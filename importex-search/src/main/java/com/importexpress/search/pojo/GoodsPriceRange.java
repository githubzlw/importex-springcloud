package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GoodsPriceRange implements Serializable {
    private static final long serialVersionUID = 986818736352041314L;
    @ApiModelProperty(value = "分类id")
    private String catid;

    @ApiModelProperty(value = "关键词")
    private String keyword;

    @ApiModelProperty(value = "同义词")
    private String otherkeyword;

    @ApiModelProperty(value = "第一区段最高价格")
    private Double sectionOnePrice;

    @ApiModelProperty(value = "第一区段数量")
    private int sectionOneCount;

    @ApiModelProperty(value = "第二区段最高价格")
    private Double sectionTwoPrice;

    @ApiModelProperty(value = "第二区段数量")
    private int sectionTwoCount;

    @ApiModelProperty(value = "第三区段最高价格")
    private Double sectionThreePrice;

    @ApiModelProperty(value = "第三区段数量")
    private int sectionThreeCount;

    @ApiModelProperty(value = "第四区段数量")
    private int sectionFourCount;

    @ApiModelProperty(value = "创建时间")
    private Date datetime;

    @ApiModelProperty(value = "状态标识 0 正常  1 删除")
    private int state;
}
