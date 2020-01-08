package com.importexpress.search.service;

import java.util.List;
import java.util.Map;

public interface KeywordRecordService {

	/**获取关键词类别列表
	 * @return
	 * @data 2018年1月8日
	 * @author user4
	 */
	List<Map<String,String>> getKeywordCatidList();

	/**
	 * 初始化搜索优先类别列表
	 * @return
	 */
	List<Map<String,String>> getPriorityCategoryList();

	/**限定类别搜索类别列表
	 * @return
	 */
	List<Map<String,Object>> getSpecialCategoryList();

	/**限定类别搜索类别列表
	 * @return
	 */
	Map<Integer,List<String>> getSpecialCategory();

	/**
	 *初始化搜索词对应的最低价和最高价
	 * @return
	 */
	List<Map<String,String>> getCategoryPriceList();
}
