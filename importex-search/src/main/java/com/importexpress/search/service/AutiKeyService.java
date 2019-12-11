package com.importexpress.search.service;

import java.util.Map;

public interface AutiKeyService {

    /**获取搜索词对应的反关键词
     * @return
     */
    Map<String,String> getAutiKey();
}
