package com.importexpress.search.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.common.SwitchDomainUtil;
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

		if(selectedList.isEmpty()){
			return dealCategoryChildren;
		}

		//新品日志列表
		newArrivalDate(param,dealCategoryChildren);

		return dealCategoryChildren;
	}

	/**设置新品
	 * @param param
	 * @param dealCategoryChildren
	 */
	private void newArrivalDate(SearchParam param, List<CategoryWrap> dealCategoryChildren){
		//新品日期
		Map<String,List<CategoryWrap>> dateMap = SwitchDomainUtil.getSiteEnum(param.getSite()).dateMap(application);
		//日期
		for(int i=0;i<dealCategoryChildren.size();i++){
			if(i != 0 || dealCategoryChildren.get(i).getLevel() != 1){
				continue;
			}
			CategoryWrap categoryWrap = dealCategoryChildren.get(0);
			List<CategoryWrap> lstDate = dateMap.get(categoryWrap.getId());
			if(lstDate != null && !lstDate.isEmpty()){
				CategoryWrap wrap = new CategoryWrap();
				wrap.setSelected(param.getCollection() == 8 ? 1 : 0);
				wrap.setName("New Arrivals");
				wrap.setUrl("keyword=&srt=default&collection=8&catid="+categoryWrap.getId());
				wrap.setChilden(setDateSelected(param,lstDate));
				List<CategoryWrap> newChilden = Lists.newArrayList();
				newChilden.add(wrap);
				if(categoryWrap.getChilden() != null){
					newChilden.addAll(categoryWrap.getChilden());
				}
				categoryWrap.setChilden(newChilden);
			}
		}
	}

	/**所选日期设置选中模式
	 * @param param
	 * @param lstDate
	 * @return
	 */
	private List<CategoryWrap> setDateSelected(SearchParam param,List<CategoryWrap> lstDate){
		lstDate.stream().forEach(l->{
			if(StringUtils.isBlank(param.getNewArrivalDate())){
				l.setSelected( 0);
			}else{
				l.setSelected(l.getName().equalsIgnoreCase(param.getNewArrivalDate()) ? 1 : 0);
			}
		});
		return lstDate;
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
		CategoryWrap selected = null;
		for(CategoryWrap c : categorys) {
			c.setSelected(selectedCatid.contains(c.getId()) ? 1 :0);
			String parentCategory = c.getParentCategory();
			List<CategoryWrap> childrenList = category_map.get(parentCategory);
			childrenList = childrenList == null ? Lists.newArrayList() : childrenList;
			childrenList.add(c);
			category_map.put(parentCategory, childrenList);
			if("0".equals(parentCategory)) {
				if(selectedCatid.contains(c.getId())){
					selected = c;
				}else{
					firstLevelCategory.add(c);
				}
			}
		}
		if(selected != null){
			firstLevelCategory.add(0,selected);
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
			return Lists.newArrayList();
		}
		for(CategoryWrap c : categorys) {
			List<CategoryWrap> childrenList = category_map.get(c.getId());
			dealCategory(category_map, childrenList);
			c.setChilden(childrenList == null ? Lists.newArrayList() : childrenList);
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

	@Override
	public String productsCate(List<CategoryWrap> rootTree){
		if (rootTree == null || rootTree.isEmpty()) {
			return "";
		}
		StringBuilder productsCate = new StringBuilder();
		productsCate.append("<span class='add_seemore'>See more products in category:</span>");
		boolean  addCategory = false;

		for (int i = 0; i < rootTree.size() && i <= 4; i++) {
			String category=rootTree.get(i).getName().toLowerCase().replace("'","")
					.replaceAll("\\s+","");
			if("childrensclothing".equals(category)) {
				productsCate.append("<a class=\"searcwor\"  href=\"")
						.append("/goodslist?catid=311")
						.append("\">").append("Wholesale Children's Clothing").append("</a>");
			}else{
				productsCate.append("<a class=\"searcwor\"  href=\"/goodslist?catid=")
						.append(rootTree.get(i).getId()).append("\">")
						.append(rootTree.get(i).getName()).append("</a>");

			}
			addCategory = "mensclothing".equals(category) || "womensclothing".equals(category) ? true : addCategory;
		}
		if(addCategory){
			productsCate.append("<a class=\"searcwor\" style='color:deeppink'  href=\"")
					.append("/apa/clothing.html").append("\">")
					.append("wholesale clothing").append("</a>");
		}
		return productsCate.toString();
	}
}
