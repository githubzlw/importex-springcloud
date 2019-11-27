package com.importexpress.ali1688.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.importexpress.ali1688.service.Ali1688CacheService;
import com.importexpress.ali1688.service.Ali1688Service;
import com.importexpress.ali1688.util.Config;
import com.importexpress.ali1688.util.InvalidPid;
import com.importexpress.comm.util.UrlUtil;
import com.importexpress.comm.exception.BizErrorCodeEnum;
import com.importexpress.comm.exception.BizException;
import com.importexpress.comm.pojo.Ali1688Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author luohao
 * @date 2019/11/6
 */
@Slf4j
@Service
@Configuration
public class Ali1688ServiceImpl implements Ali1688Service {

    private static final int MAX_GOODS_NUMBER = 200;
    private static final int MIN_SALES = 10;

    /**
     * 获取商品详情
     */
    private final static String URL_ITEM_GET = "%sapi_call.php?key=%s&secret=%s&num_iid=%s&api_name=item_get&lang=zh-CN";

//    private final static String URL_ITEM_GET = "%sapi_call.php?key=%s&secret=%s&num_iid=%s&cache=no&api_name=item_get&lang=zh-CN";
    /**
     * 获取店铺商品
     */
    private final static String URL_ITEM_SEARCH = "%sapi_call.php?key=%s&secret=%s&seller_nick=%s&start_price=0&end_price=0&q=&page=%d&cid=&api_name=item_search_shop&lang=zh-CN";
    private Ali1688CacheService ali1688CacheService;
    private Config config;

    @Autowired
    public Ali1688ServiceImpl(Ali1688CacheService ali1688CacheService, Config config) {
        this.ali1688CacheService = ali1688CacheService;
        this.config = config;
    }


    private JSONObject getItemByPid(Long pid) {

        JSONObject itemFromRedis = this.ali1688CacheService.getItem(pid);
        if (itemFromRedis != null) {
            checkItem(pid, itemFromRedis);
            return itemFromRedis;
        }

        try {
            JSONObject jsonObject = UrlUtil.getInstance().callUrlByGet(String.format(URL_ITEM_GET, config.API_HOST, config.API_KEY, config.API_SECRET, pid));
            String error = jsonObject.getString("error");
            //if(1==1) throw new IllegalStateException("testtesttest");
            if (StringUtils.isNotEmpty(error)) {
                if (error.contains("你的授权已经过期")) {
                    throw new BizException(BizErrorCodeEnum.EXPIRE_FAIL);
                } else if (error.contains("超过")) {
                    //TODO
                    throw new BizException(BizErrorCodeEnum.LIMIT_EXCEED_FAIL);
                } else if (error.contains("item-not-found")) {
                    throw new IllegalStateException("item-not-found");
                }
                log.warn("json's error is not empty:[{}]，pid:[{}]", error, pid);
                jsonObject = InvalidPid.of(pid, error);
            }
            this.ali1688CacheService.saveItemIntoRedis(pid, jsonObject);
            checkItem(pid, jsonObject);

            return jsonObject;
        } catch (IOException e) {
            log.error("getItem", e);
            throw new BizException(BizErrorCodeEnum.UNSPECIFIED);
        }
    }

    /**
     * 1688商品详情查询
     *
     * @param pid
     * @return
     */
    @Override
    public JSONObject getItem(Long pid) {

        Callable<JSONObject> callable = new Callable<JSONObject>() {

            @Override
            public JSONObject call() {
                return getItemByPid(pid);

            }
        };

        Retryer<JSONObject> retryer = RetryerBuilder.<JSONObject>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(IllegalStateException.class)
                .withWaitStrategy(WaitStrategies.fixedWait(2000, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();
        try {
            return retryer.call(callable);
        } catch (RetryException | ExecutionException e) {
            throw new BizException(e.getMessage());
        }
    }

    /**
     * get items by pid array
     *
     * @param pids
     * @return
     */
    @Override
    public List<JSONObject> getItems(Long[] pids) {

        List<JSONObject> lstResult = new CopyOnWriteArrayList<JSONObject>();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (long pid : pids) {
            executorService.execute(() -> {
                try {
                    JSONObject item = getItem(pid);
                    if (item != null) {
                        lstResult.add(item);
                    } else {
                        lstResult.add(InvalidPid.of(pid, "no data"));
                    }
                } catch (BizException be) {
                    if (be.getErrorCode() == BizErrorCodeEnum.DESC_IS_NULL) {
                        lstResult.add(InvalidPid.of(pid, BizErrorCodeEnum.DESC_IS_NULL.getDescription()));
                    }
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
        }

        return lstResult;
    }

    /**
     * get Items In Shop
     *
     * @param shopid
     * @return
     */
    @Override
    public List<Ali1688Item> getItemsInShop(String shopid) {

        List<Ali1688Item> itemFromRedis = this.ali1688CacheService.getShop(shopid);
        if (itemFromRedis != null) {
            return itemFromRedis;
        }

        try {
            List<Ali1688Item> result = new ArrayList<>(100);

            Map<String, Integer> mapSum = fillItems(result, shopid, 1);
            if (mapSum == null) {
                //无数据
                return result;
            }
            int count = mapSum.get("pagecount") < MAX_GOODS_NUMBER ? mapSum.get("pagecount") : MAX_GOODS_NUMBER;
            int total = mapSum.get("total_results");

            if (count > 1) {
                for (int i = 2; i <= count; i++) {
                    fillItems(result, shopid, i);
                }
            }

//            if(result.size() != total){
//                log.warn("filled items's size is not same,need retry! : [{}]:[{}]",result.size(),total);
//                throw new IllegalStateException("filled items's size is not same,need retry!");
//            }

            //过滤掉销量=0的商品
            List<Ali1688Item> haveSaleItems = result.stream().filter(item -> item.getSalesOfParse() >= MIN_SALES).sorted(Comparator.comparing(Ali1688Item::getSalesOfParse, Comparator.reverseOrder())).collect(Collectors.toList());
            this.ali1688CacheService.setShop(shopid, haveSaleItems);
            return haveSaleItems;

        } catch (IOException e) {
            log.error("getItemsInShop", e);
            throw new IllegalStateException("io exception,need retry!");
        }
    }

    @Override
    public int clearNotExistItemInCache() {
        return this.ali1688CacheService.processNotExistItemInCache(true);
    }

    @Override
    public int getNotExistItemInCache() {
        return this.ali1688CacheService.processNotExistItemInCache(false);
    }

    @Override
    public void setItemsExpire(int days) {
        this.ali1688CacheService.setItemsExpire(days);
    }


    /**
     * fillItems
     *
     * @param lstAllItems
     * @param shopid
     * @param page
     * @return
     * @throws IOException
     */
    private Map<String, Integer> fillItems(List<Ali1688Item> lstAllItems, String shopid, int page) throws IOException {

        log.info("begin fillItems: shopid:[{}] page:[{}]", shopid, page);

        Map<String, Integer> result = new HashMap<>(3);

        JSONObject jsonObject = UrlUtil.getInstance().callUrlByGet(String.format(URL_ITEM_SEARCH, config.API_HOST, config.API_KEY, config.API_SECRET, shopid, page));

        checkReturnJson(jsonObject);

        JSONObject items = jsonObject.getJSONObject("items");
        Ali1688Item[] ali1688Items = JSON.parseObject(items.getJSONArray("item").toJSONString(), Ali1688Item[].class);
        log.info("end fillItems: size:[{}] ", ali1688Items.length);
        lstAllItems.addAll(Arrays.asList(ali1688Items));

        result.put("pagecount", Integer.parseInt(items.getString("pagecount")));
        result.put("total_results", Integer.parseInt(items.getString("total_results")));
        result.put("page_size", Integer.parseInt(items.getString("page_size")));

        return result;
    }

    /**
     * isHaveData
     *
     * @param jsonObject
     * @return
     */
    private void checkReturnJson(JSONObject jsonObject) {

        String error = jsonObject.getString("error");
        if (!StringUtils.isEmpty(error)) {
            throw new BizException(BizErrorCodeEnum.FAIL, error);
        }
    }


    private void checkItem(Long pid, JSONObject jsonObject) {
        JSONObject item = jsonObject.getJSONObject("item");
        if (item != null) {
            if (StringUtils.isEmpty(item.getString("desc"))) {
                log.warn("desc is null ,pid:[{}]", pid);
                throw new BizException(BizErrorCodeEnum.DESC_IS_NULL);
            }
        }
    }

}
