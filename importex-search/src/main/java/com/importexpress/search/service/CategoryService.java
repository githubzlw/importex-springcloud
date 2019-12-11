package com.importexpress.search.service;

import com.importexpress.search.pojo.Category;
import com.importexpress.search.pojo.CategoryWrap;
import com.importexpress.search.pojo.SearchParam;
import org.apache.solr.client.solrj.response.FacetField;

import java.util.List;

/**
 * 类别
 * @author Administrator
 *
 */
public interface CategoryService {
	/**
	 *
	 * @Title
	 * @Description 查询全部的数据
	 * @return List<Category>
	 */
	List<Category> getCategories();
	/**
	 * 搜索类别列表
	 * @param param 搜索参数
	 * @param facetFields solr统计的类别结果
	 * @return
	 */
	List<CategoryWrap> categorys(SearchParam param, List<FacetField> facetFields);
}
