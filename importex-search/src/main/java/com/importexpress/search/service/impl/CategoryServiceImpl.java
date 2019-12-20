package com.importexpress.search.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.importexpress.search.mapper.CategoryMapper;
import com.importexpress.search.pojo.Category;
import com.importexpress.search.pojo.CategoryWrap;
import com.importexpress.search.pojo.SearchParam;
import com.importexpress.search.pojo.SearchWordWrap;
import com.importexpress.search.service.CategoryService;
import com.importexpress.search.service.base.UriService;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl extends UriService implements CategoryService {
	@Autowired
	private ServletContext application;
	@Autowired
	private CategoryMapper categoryMapper;


	@Override
	public List<Category> getCategories() {
		return categoryMapper.getCategories();
	}

	@Override
	public List<CategoryWrap> categorys(SearchParam param, List<FacetField> facetFields) {
		if(facetFields == null) {
			return Lists.newArrayList();
		}
		//所有类别列表全局变量
		Map<String, Category> catidList = (Map<String, Category>)application.getAttribute("categorys");

		//已选择类别
		List<String> selectedList = selectedCatid(param, catidList);

		//facet结果集
		List<CategoryWrap> categorys = facetCategory(facetFields, catidList, param);

		//子类类别集合
		List<CategoryWrap> dealCategoryChildren = dealCategoryChildren(categorys,selectedList);

		return dealCategoryChildren;
	}


	/**
	 * 已选择类别的类别树
	 * @param param
	 * @param catidList
	 * @return
	 */
	private List<String> selectedCatid(SearchParam param, Map<String, Category> catidList) {
		List<String> selectedList = Lists.newArrayList();
		if(StringUtils.isBlank(param.getCatid())) {
			return selectedList;
		}
		Category categoryBean = catidList.get(param.getCatid());
		selectedList = categoryBean != null ?
				Arrays.asList(categoryBean.getPath().split(",")) : selectedList;
		return selectedList;
	}

	/**统计类别子类集合
	 * @param categorys
	 * @return
	 */
	private List<CategoryWrap> dealCategoryChildren(List<CategoryWrap> categorys, List<String> selectedCatid){
		Map<String,List<CategoryWrap>> category_map = Maps.newHashMap();
		List<CategoryWrap> firstLevelCategory  = Lists.newArrayList();
		for(CategoryWrap c : categorys) {
			c.setSelected(selectedCatid.contains(c.getId()));
			String parentCategory = c.getParentCategory();
			List<CategoryWrap> childrenList = category_map.get(parentCategory);
			childrenList = childrenList == null ? Lists.newArrayList() : childrenList;
			childrenList.add(c);
			category_map.put(parentCategory, childrenList);
			if("0".equals(parentCategory)) {
				firstLevelCategory.add(c);
			}
		}
		List<CategoryWrap> dealCategory = dealCategory(category_map, firstLevelCategory);

		return dealCategory;
	}

	/**
	 * 递归统计子类列表
	 * @param category_map
	 * @param categorys
	 * @return
	 */
	private List<CategoryWrap> dealCategory(Map<String,List<CategoryWrap>> category_map,
											List<CategoryWrap> categorys){
		if(categorys == null) {
			return null;
		}
		for(CategoryWrap c : categorys) {
			List<CategoryWrap> childrenList = category_map.get(c.getId());
			dealCategory(category_map, childrenList);
			c.setChilden(childrenList);
		}
		return categorys;
	}


	/**统计facet类别
	 * @param facetFields
	 * @return
	 */
	private List<CategoryWrap> facetCategory(List<FacetField> facetFields,
										 Map<String,Category> catidList, SearchParam param){
		List<CategoryWrap> categorys = Lists.newArrayList();
		CategoryWrap wrap;
		//初始化
		String url = initUri(param);
		for(FacetField facet : facetFields) {
			List<Count> values = facet.getValues();
			for(Count value : values){
				String catid = value.getName();

				Category categoryBean = catidList.get(catid);
				if(categoryBean == null) {
					continue;
				}
				wrap = new CategoryWrap();
				wrap.setCount(value.getCount());
				wrap.setId(categoryBean.getCatid());
				wrap.setLevel(categoryBean.getLevel());
				wrap.setName(categoryBean.getName());
				String[] categoryPaths = categoryBean.getPath().split(",");
				wrap.setUrl(catid.equals(param.getCatid()) ? "" : url + catid);
				wrap.setParentCategory(categoryPaths.length >1?categoryPaths[categoryPaths.length-2] : "0");
				categorys.add(wrap);
			}
		}
		return categorys;
	}

	@Override
	public String initUri(SearchParam param) {
		StringBuffer sb_href = new StringBuffer(uriBase(param));
		sb_href.append("&catid=");
		return sb_href.toString();
	}


	@Override
	public List<SearchWordWrap> getRecommendedWords() {
		return categoryMapper.getRecommendedWords();
	}
}
