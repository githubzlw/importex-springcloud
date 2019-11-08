package com.importexpress.ali1688.service;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.common.pojo.Ali1688Item;

import java.util.List;

/**
 * @author luohao
 * @date 2019/11/6
 */
public interface Ali1688Service {

    /**
     * 1688商品详情查询
     *
     * @param pid
     * @return
     */
    JSONObject getItem(Long pid);

    /**
     * get items by pid array
     *
     * @param pids
     * @return
     */
    List<JSONObject> getItems(Long[] pids);

    /**
     * get Items In Shop
     *
     * @param shopid
     * @return
     */
    List<Ali1688Item> getItemsInShop(String shopid);

    /**
     * 清除redis缓存里面下架商品
     * @return
     */
    int clearNotExistItemInCache();

    /**
     * 下架商品数量统计
     * @return
     */
    int getNotExistItemInCache();
}
