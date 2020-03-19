package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 价格区间分布
 */
@Data
public class GoodsPriceRange implements Serializable {
    private static final long serialVersionUID = 986818736352041314L;

    /**
     * 分类id
     */
    @ApiModelProperty(value = "分类id")
    private String catid;

    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词")
    private String keyword;

    /**
     * 同义词
     */
    @ApiModelProperty(value = "同义词")
    private String otherkeyword;

    /**
     * 第一区段最高价格
     */
    @ApiModelProperty(value = "第一区段最高价格")
    private Double sectionOnePrice;

    /**
     * 第一区段数量
     */
    @ApiModelProperty(value = "第一区段数量")
    private int sectionOneCount=0;

    /**
     * 第二区段最高价格
     */
    @ApiModelProperty(value = "第二区段最高价格")
    private Double sectionTwoPrice;

    /**
     * 第二区段数量
     */
    @ApiModelProperty(value = "第二区段数量")
    private int sectionTwoCount=0;

    /**
     * 第三区段最高价格
     */
    @ApiModelProperty(value = "第三区段最高价格")
    private Double sectionThreePrice;

    /**
     * 第四区段数量
     */
    @ApiModelProperty(value = "第四区段数量")
    private int sectionThreeCount=0;

    /**
     * 第四区段数量
     */
    @ApiModelProperty(value = "第四区段数量")
    private int sectionFourCount=0;

    /**
     * 状态标识 0 正常  1 删除
     */
    @ApiModelProperty(value = "状态标识 0 正常  1 删除")
    private int state;
}
