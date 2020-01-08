package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * 关键词同义词数据bean
 */
@Data
public class SynonymKey {
    /**
     * 关键词
     */
    @ApiModelProperty(value="关键词")
    private String keyword;
    /**
     * 同义词
     */
    @ApiModelProperty(value="同义词")
    private Set<String> synonym;
}
