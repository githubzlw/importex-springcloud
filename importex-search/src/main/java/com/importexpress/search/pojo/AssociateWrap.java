package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 搜索词联想推荐词+及其产品数量
 */
@Data
public class AssociateWrap implements Serializable {
    private static final long serialVersionUID = 986818736352041314L;

    /**
     * 联想词
     */
    @ApiModelProperty(value="联想词")
    private String key;

    /**
     * 联想词产品数量
     */
    @ApiModelProperty(value="联想词产品数量")
    private long count;
}
