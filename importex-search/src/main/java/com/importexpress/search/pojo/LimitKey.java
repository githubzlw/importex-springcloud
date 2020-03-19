package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 广告词
 */
@Data
public class LimitKey implements Serializable {

    private int id;
    /**
     * 广告词
     */
    @ApiModelProperty(value="广告词")
    private String key;

    /**
     * 触发搜索词
     */
    @ApiModelProperty(value="触发搜索词")
    private String triggerKey;

    /**
     * 触发搜索类别
     */
    @ApiModelProperty(value="触发搜索类别")
    private String triggerCatid;

    /**
     * 广告id
     */
    @ApiModelProperty(value="广告id")
    private String adgroupid;
}
