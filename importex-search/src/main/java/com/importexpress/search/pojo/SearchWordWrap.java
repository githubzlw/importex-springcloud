package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 类别推荐搜索词
 */
@Data
public class SearchWordWrap implements Serializable,Cloneable {
    private static final long serialVersionUID = -7448833575209315621L;

    /**
     *关键词
     */
    @ApiModelProperty(value="关键词")
    private String keyWord;

    /**
     *路径
     */
    @ApiModelProperty(value="路径")
    private String path;

    @Override
    public SearchWordWrap clone()  {
        SearchWordWrap bean = null;
        try {
            bean = (SearchWordWrap)super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }
}
