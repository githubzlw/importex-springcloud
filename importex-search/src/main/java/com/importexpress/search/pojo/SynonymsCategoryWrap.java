package com.importexpress.search.pojo;

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
    private String category;
    /**
     * 类别id
     */
    private String catid;
    /**
     * category字符串长度
     */
    private int length;
    /**
     * category单词数
     */
    private int num;
}
