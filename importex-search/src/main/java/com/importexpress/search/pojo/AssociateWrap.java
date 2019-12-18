package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 搜索词联想推荐词+及其产品数量
 */
@Data
public class AssociateWrap implements Serializable {
    @ApiModelProperty(value="联想词")
    private String key;
    @ApiModelProperty(value="联想词产品数量")
    private long count;
}
