package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 关键词触发的类别同义词列表
 */
@Data
@Accessors(chain = true)
@Builder
public class KeyToCategoryWrap {

    /**
     * 类别列表
     */
    @ApiModelProperty(value="类别列表")
    private List<String> lstCatid;

    /**
     * 关键词
     */
    @ApiModelProperty(value="关键词")
    private String keyword;

}
