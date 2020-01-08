package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsPriceRangeWrap implements Serializable {
    private static final long serialVersionUID = 986818736352041314L;

    /**
     * 搜索参数
     */
    @ApiModelProperty(value="搜索参数")
    private SearchParam param;

    /**
     *价格区间
     */
    @ApiModelProperty(value="价格区间")
    private GoodsPriceRange range;

    /**
     *数量
     */
    @ApiModelProperty(value="数量")
    private int total;

    /**
     *最小价格
     */
    @ApiModelProperty(value="最小价格")
    private String minPrice;

    /**
     *最大价格
     */
    @ApiModelProperty(value="最大价格")
    private String maxPrice;

    /**
     *是否Update按钮触发 1-是 0-否
     */
    @ApiModelProperty(value="是否Update按钮触发 1-是 0-否")
    private int backDiv;

}
