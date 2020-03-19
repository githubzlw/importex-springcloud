package com.importexpress.search.service;

import com.importexpress.search.pojo.CategoryWrap;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;

/**
 * 不同网站对应操作
 */
public interface SiteOperation {
    Map<String,List<CategoryWrap>> dateMap(ServletContext application);
}
