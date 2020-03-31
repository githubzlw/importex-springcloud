package com.importexpress.ali1688.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.importexpress.ali1688.mapper.PidQueueMapper;
import com.importexpress.ali1688.model.PidQueue;
import com.importexpress.ali1688.service.Ali1688CacheService;
import com.importexpress.ali1688.service.Ali1688Service;
import com.importexpress.ali1688.util.Config;
import com.importexpress.ali1688.util.InvalidPid;
import com.importexpress.comm.exception.BizErrorCodeEnum;
import com.importexpress.comm.exception.BizException;
import com.importexpress.comm.pojo.Ali1688Item;
import com.importexpress.comm.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author jack.luo
 * @date 2019/11/6
 */
@Slf4j
@Service
@Configuration
public class Ali1688ServiceImpl implements Ali1688Service {

    private static final int MAX_GOODS_NUMBER = 200;

    private static final String REDIS_CALL_COUNT = "ali:call:count";

    private static final String YYYYMMDD = "yyyyMMdd";

    private final StringRedisTemplate redisTemplate;

    /**
     * 获取商品详情
     */
    private final static String URL_ITEM_GET = "%sapi_call.php?key=%s&secret=%s&num_iid=%s&api_name=item_get&lang=zh-CN";

    /**
     * API URL
     */
    private final static String API_URL = "%sapi_call.php";

//    private final static String URL_ITEM_GET = "%sapi_call.php?key=%s&secret=%s&num_iid=%s&cache=no&api_name=item_get&lang=zh-CN";
    /**
     * 获取店铺商品
     */
    private final static String URL_ITEM_SEARCH = "%sapi_call.php?key=%s&secret=%s&seller_nick=%s&start_price=0&end_price=0&q=&page=%d&cid=&api_name=item_search_shop&lang=zh-CN";
    private Ali1688CacheService ali1688CacheService;
    private Config config;
    private PidQueueMapper pidQueueMapper;

    @Autowired
    public Ali1688ServiceImpl(Ali1688CacheService ali1688CacheService, Config config, PidQueueMapper pidQueueMapper, StringRedisTemplate redisTemplate) {
        this.ali1688CacheService = ali1688CacheService;
        this.config = config;
        this.pidQueueMapper = pidQueueMapper;
        this.redisTemplate = redisTemplate;
    }


    private JSONObject getItemByPid(Long pid, boolean isCache) {
        Objects.requireNonNull(pid);
        if (isCache) {
            JSONObject itemFromRedis = this.ali1688CacheService.getItem(pid);
            if (itemFromRedis != null) {
                checkItem(pid, itemFromRedis);
                return itemFromRedis;
            }
        }

        try {
            JSONObject jsonObject = UrlUtil.getInstance().callUrlByGet(String.format(URL_ITEM_GET, config.API_HOST, config.API_KEY, config.API_SECRET, pid));
            String strYmd = LocalDate.now().format(DateTimeFormatter.ofPattern(YYYYMMDD));
            this.redisTemplate.opsForHash().increment(REDIS_CALL_COUNT, "pid_" + strYmd, 1);
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
     * 1688商品详情查询（单个）
     *
     * @param pid
     * @return
     */
    @Override
    public JSONObject getItem(Long pid, boolean isCache) {
        Objects.requireNonNull(pid);

        Callable<JSONObject> callable = new Callable<JSONObject>() {

            @Override
            public JSONObject call() {
                return getItemByPid(pid, isCache);

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
     * 1688商品详情查询（多个）
     *
     * @param pids
     * @return
     */
    @Override
    public List<JSONObject> getItems(Long[] pids, boolean isCache) {
        Objects.requireNonNull(pids);
        List<JSONObject> lstResult = new CopyOnWriteArrayList<JSONObject>();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (long pid : pids) {
            executorService.execute(() -> {
                try {
                    JSONObject item = getItem(pid, isCache);
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
     * 获得店铺商品
     *
     * @param shopid
     * @return
     */
    @Override
    public List<Ali1688Item> getItemsInShop(String shopid) {
        Objects.requireNonNull(shopid);
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
            List<Ali1688Item> haveSaleItems = result.stream().filter(item -> item.getSalesOfParse() >= config.minSales).sorted(Comparator.comparing(Ali1688Item::getSalesOfParse, Comparator.reverseOrder())).collect(Collectors.toList());
            this.ali1688CacheService.setShop(shopid, haveSaleItems);
            return haveSaleItems;

        } catch (IOException e) {
            log.error("getItemsInShop", e);
            throw new IllegalStateException("io exception,need retry!");
        }
    }

    /**
     * 上传图片到1688
     *
     * @param file
     * @return
     */
    @Override
    public String uploadImgTo1688(byte[] file){

        String url="";
        //imgcode=111111111&api_name=upload_img&lang=zh-CN&key=tel13661551662&secret=20200331
        Map<String, Object> params = new HashMap<>(3);
        params.put("api_name", "upload_img");
        params.put("lang", "zh-CN");
        params.put("key", config.API_KEY);
        params.put("secret", config.API_SECRET);
        params.put("imgcode", file);

            try {
                JSONObject jsonObject = UrlUtil.getInstance().doPut(String.format(API_URL, config.API_HOST),params);
                if(jsonObject !=null){
                    log.info("result:[{}]",jsonObject);
                    //TODO
                }
            } catch (IOException e) {
                log.error("uploadImgTo1688",e);
        }
        return url;
    }
    /**
     * 清除redis缓存里面下架商品
     * @return
     */
    @Override
    public int clearNotExistItemInCache() {
        return this.ali1688CacheService.processNotExistItemInCache(true);
    }

    /**
     * 清除redis缓存里面所有商品
     * @return
     */
    @Override
    public int clearAllPidInCache() {
        return this.ali1688CacheService.clearAllPidInCache();
    }

    /**
     * 清除redis缓存里面所有店铺
     * @return
     */
    @Override
    public int clearAllShopInCache() {
        return this.ali1688CacheService.clearAllShopInCache();
    }

    /**
     * 下架商品数量统计
     * @return
     */
    @Override
    public int getNotExistItemInCache() {
        return this.ali1688CacheService.processNotExistItemInCache(false);
    }

    /**
     * 设置key的过期时间
     * @param days
     */
    @Override
    public void setItemsExpire(int days) {
        this.ali1688CacheService.setItemsExpire(days);
    }

    /**
     * pid_queue表：获得pid（分页）
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<PidQueue> getAllPids(int page, int pageSize) {
        int offset = (page - 1) * pageSize;

        Example example = new Example(PidQueue.class);
        example.setOrderByClause("update_time DESC");
        return this.pidQueueMapper.selectByExampleAndRowBounds(example, new RowBounds(offset, pageSize));
    }

    /**
     * pid_queue表：获得所有pid
     * @return
     */
    @Override
    public List<PidQueue> getAllPids() {

        Example example = new Example(PidQueue.class);
        example.setOrderByClause("update_time DESC");
        return this.pidQueueMapper.selectByExample(example);
    }


    /**
     * pid_queue表：获得UnStartpid
     * @return
     */
    @Override
    public List<PidQueue> getAllUnStartPids() {

        PidQueue pidQueue = new PidQueue();
        pidQueue.setStatus((byte) 0);
        return this.pidQueueMapper.select(pidQueue);
    }

    /**
     * pid_queue表：更新状态
     * @param id
     * @param status
     * @return
     */
    @Override
    public int updatePidQueue(int id, int status) {

        PidQueue pidQueue = new PidQueue();
        pidQueue.setId(id);
        pidQueue.setStatus((byte) status);
        pidQueue.setUpdateTime(new Date());
        return this.pidQueueMapper.updateByPrimaryKeySelective(pidQueue);
    }

    /**
     * pid_queue表：增加pid
     * @param shopId
     * @param pid
     * @return
     */
    @Override
    public int pushPid(String shopId, int pid) {
        PidQueue pidQueue = new PidQueue();
        pidQueue.setPid(pid);

        PidQueue select = this.pidQueueMapper.selectOne(pidQueue);
        if (select != null) {
            //update
            select.setShopId(shopId);
            select.setUpdateTime(new Date());
            return this.pidQueueMapper.updateByPrimaryKey(select);
        } else {
            //insert
            pidQueue.setShopId(shopId);
            pidQueue.setStatus((byte) 0);
            Date now = new Date();
            pidQueue.setCreateTime(now);
            pidQueue.setUpdateTime(now);
            return this.pidQueueMapper.insert(pidQueue);
        }
    }

    /**
     * pid_queue表：删除pid
     * @param id
     * @return
     */
    @Override
    public int deleteIdInQueue(int id) {

        PidQueue pidQueue = new PidQueue();
        pidQueue.setId(id);
        return this.pidQueueMapper.deleteByPrimaryKey(pidQueue);
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

        Objects.requireNonNull(lstAllItems);
        Objects.requireNonNull(shopid);
        log.info("begin fillItems: shopid:[{}] page:[{}]", shopid, page);

        Map<String, Integer> result = new HashMap<>(3);

        JSONObject jsonObject = UrlUtil.getInstance().callUrlByGet(String.format(URL_ITEM_SEARCH, config.API_HOST, config.API_KEY, config.API_SECRET, shopid, page));

        String strYmd = LocalDate.now().format(DateTimeFormatter.ofPattern(YYYYMMDD));
        this.redisTemplate.opsForHash().increment(REDIS_CALL_COUNT, "shop_" + strYmd, 1);

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
        Objects.requireNonNull(jsonObject);
        String error = jsonObject.getString("error");
        if (!StringUtils.isEmpty(error)) {
            throw new BizException(BizErrorCodeEnum.FAIL, error);
        }
    }


    private void checkItem(Long pid, JSONObject jsonObject) {
        Objects.requireNonNull(pid);
        Objects.requireNonNull(jsonObject);
        JSONObject item = jsonObject.getJSONObject("item");
        if (item != null) {
            if (StringUtils.isEmpty(item.getString("desc"))) {
                log.warn("desc is null ,pid:[{}]", pid);
                throw new BizException(BizErrorCodeEnum.DESC_IS_NULL);
            }
        }
    }

}
