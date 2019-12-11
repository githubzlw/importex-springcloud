package com.importexpress.search.service;

import com.importexpress.search.pojo.SynonymsCategoryWrap;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SynonymService {
    /**
     * 初始化同义词列表
     * @return
     */
    Map<String,Set<String>> getSynonymKeyword();
    /**类别同义词
     * @return
     */
    List<SynonymsCategoryWrap> getSynonymsCategory();
}
