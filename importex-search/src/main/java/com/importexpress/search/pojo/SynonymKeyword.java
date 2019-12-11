package com.importexpress.search.pojo;

import lombok.Data;

import java.util.Set;

/**
 * 关键词同义词数据bean
 */
@Data
public class SynonymKeyword {
    /**
     * 关键词
     */
    private String keyword;
    /**
     * 同义词
     */
    private Set<String> synonym;
}
