package com.importexpress.ali1688.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.importexpress.ali1688.service.Ali1688CacheService;
import com.importexpress.ali1688.service.Ali1688Service;
import com.importexpress.ali1688.util.Config;
import com.importexpress.ali1688.util.UrlUtil;
import com.importexpress.common.pojo.Ali1688Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author luohao
 * @date 2019/11/6
 */
@Slf4j
@Service
@Configuration
public class Ali1688ServiceImpl implements Ali1688Service {

    private Ali1688CacheService ali1688CacheService;

    private Config config;

    @Autowired
    public Ali1688ServiceImpl(Ali1688CacheService ali1688CacheService,Config config){
        this.ali1688CacheService = ali1688CacheService;
        this.config = config;
    }

    /**
     * 获取商品详情
     */
    private final static String URL_ITEM_GET = "http://api.onebound.cn/1688/api_call.php?key=%s&secret=%s&num_iid=%s&cache=no&api_name=item_get&lang=zh-CN";

    /**
     * 获取店铺商品
     */
    private final static String URL_ITEM_SEARCH = "http://api.onebound.cn/1688/api_call.php?key=%s&secret=%s&seller_nick=%s&start_price=0&end_price=0&q=&page=%d&cid=&cache=no&api_name=item_search_shop&lang=zh-CN";


    /**
     * 1688商品详情查询
     *
     * @param pid
     * @return
     */
    @Override
    public JSONObject getItem(Long pid) {

        JSONObject itemFromRedis = this.ali1688CacheService.getItem(pid);
        if(itemFromRedis != null){
            return itemFromRedis;
        }

        try {
            JSONObject jsonObject = UrlUtil.getInstance().callUrlByGet(String.format(URL_ITEM_GET, config.API_KEY,config.API_SECRET,pid));
            String error = jsonObject.getString("error");
            if (StringUtils.isNotEmpty(error)) {
                jsonObject = getNotExistPid(pid);
            }
            this.ali1688CacheService.setItem(pid,jsonObject);
            return jsonObject;
        } catch (IOException e) {
            log.error("getItem", e);
            return null;
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
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (long pid : pids) {
            executorService.execute(() -> {
                JSONObject item = getItem(pid);
                if (item != null) {
                    lstResult.add(item);
                } else {
                    lstResult.add(getNotExistPid(pid));
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
        if(itemFromRedis != null){
            return itemFromRedis;
        }

        try {
            List<Ali1688Item> result = new ArrayList<>(100);

            Map<String,Integer> mapSum = fillItems(result, shopid, 1);
            if(mapSum == null ){
                //无数据
                return result;
            }
            int count = mapSum.get("pagecount");
            int total = mapSum.get("total_results");

            if (count > 1) {
                for (int i = 2; i <= count; i++) {
                    fillItems(result, shopid, i);
                }
            }
            if(result.size() != total){
                log.warn("filled items's size is not same,need retry! : [{}]:[{}]",result.size(),total);
                throw new IllegalStateException("filled items's size is not same,need retry!");
            }
            this.ali1688CacheService.setShop(shopid,result);
            return result;

        } catch (IOException e) {
            log.error("getItemsInShop", e);
            throw new IllegalStateException("io exception,need retry!");
        }
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
    private Map<String,Integer> fillItems(List<Ali1688Item> lstAllItems, String shopid, int page) throws IOException {

        log.info("begin fillItems: shopid:[{}] page:[{}]", shopid, page);

        Map<String,Integer> result = new HashMap<>(3);

        JSONObject jsonObject = UrlUtil.getInstance().callUrlByGet(String.format(URL_ITEM_SEARCH, config.API_KEY,config.API_SECRET,shopid, page));
        if (!isHaveData(jsonObject)) {
            return null;
        }

        JSONObject items = jsonObject.getJSONObject("items");
        Ali1688Item[] ali1688Items = JSON.parseObject(items.getJSONArray("item").toJSONString(), Ali1688Item[].class);
        log.info("end fillItems: size:[{}] ",ali1688Items.length);
        lstAllItems.addAll(Arrays.asList(ali1688Items));

        result.put("pagecount",Integer.parseInt(items.getString("pagecount")));
        result.put("total_results",Integer.parseInt(items.getString("total_results")));
        result.put("page_size",Integer.parseInt(items.getString("page_size")));

        return result;
    }

    /**
     * isHaveData
     *
     * @param jsonObject
     * @return
     */
    private boolean isHaveData(JSONObject jsonObject) {

        String error = jsonObject.getString("error");
        boolean result = StringUtils.isEmpty(error);
        if(!result){
            log.warn("catch return result error: [{}]",error);
        }
        return result;
    }

    /**
     * return offline pid json object
     *
     * @return
     */
    private JSONObject getNotExistPid(Long pid) {
        JSONObject jsonObject = new JSONObject();
        LocalDateTime now = LocalDateTime.now();
        jsonObject.put("secache_date", now);
        jsonObject.put("server_time", now);
        jsonObject.put("reason", "no data");
        jsonObject.put("pid", pid);
        return jsonObject;
    }

}
