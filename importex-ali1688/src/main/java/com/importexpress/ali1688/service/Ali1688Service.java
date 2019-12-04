package com.importexpress.ali1688.service;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.ali1688.model.PidQueue;
import com.importexpress.comm.pojo.Ali1688Item;

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
    JSONObject getItem(Long pid, boolean isCache);

    /**
     * get items by pid array
     *
     * @param pids
     * @return
     */
    List<JSONObject> getItems(Long[] pids, boolean isCache);

    /**
     * get Items In Shop
     *
     * @param shopid
     * @return
     */
    List<Ali1688Item> getItemsInShop(String shopid);

    /**
     * 清除redis缓存里面下架商品
     *
     * @return
     */
    int clearNotExistItemInCache();

    int clearAllPidInCache();

    int clearAllShopInCache();

    /**
     * 下架商品数量统计
     *
     * @return
     */
    int getNotExistItemInCache();

    /**
     * 设置key的过期时间
     *
     * @param days
     */
    void setItemsExpire(int days);

    List<PidQueue> getAllPids(int page, int pageSize);

    List<PidQueue> getAllPids();

    List<PidQueue> getAllUnStartPids();

    int updatePidQueue(int id, int status);

    int pushPid(String shopId, int pid);

    int deleteIdInQueue(int id);
}
