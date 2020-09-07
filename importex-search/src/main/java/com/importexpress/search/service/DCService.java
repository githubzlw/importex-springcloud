package com.importexpress.search.service;

import com.importexpress.search.pojo.SearchParam;

import java.util.Map;

public interface DCService {
    /**
     *
     * @Title getSearchNavigation
     * @Description TODO
     * @return
     * @return String
     */
    Map<String,String> getSearchNavigation(SearchParam param);
}
