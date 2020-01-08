package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 类别同义词数据bean
 */
@Data
@Builder
@Accessors(chain = true)
public class SynonymsCategoryWrap {
    /**
     * 类别
     */
    @ApiModelProperty(value="类别")
    private String category;
    /**
     * 类别id
     */
    @ApiModelProperty(value="类别id")
    private String catid;
    /**
     * category字符串长度
     */
    @ApiModelProperty(value="category字符串长度")
    private int length;
    /**
     * category单词数
     */
    @ApiModelProperty(value="category单词数")
    private int num;
}
