package com.importexpress.search.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LimitKey implements Serializable {
    private int id;
    /**
     * 广告词
     */
    private String key;
    /**
     * 触发搜索词
     */
    private String triggerKey;
    /**
     * 触发搜索类别
     */
    private String triggerCatid;
    /**
     * 广告id
     */
    private String adgroupid;
}
