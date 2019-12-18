package com.importexpress.search.service;

import com.importexpress.search.pojo.PageWrap;
import com.importexpress.search.pojo.SearchParam;

/**数据分页
 * @author Administrator
 *
 */
public interface PageService {

	 /**
	  * 搜索结果数据分页
	 * @param param  搜索参数
	 * @param recordCount 搜索结果数量
	 * @return
	 */
	 PageWrap paging(SearchParam param, long recordCount);
}
