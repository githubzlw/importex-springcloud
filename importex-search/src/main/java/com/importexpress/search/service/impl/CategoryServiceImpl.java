package com.importexpress.search.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletContext;
import java.util.ArrayList;
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
		if (facetFields == null) {
			return Lists.newArrayList();
		}
		//所有类别列表全局变量
		Map<String, Category> catidList = (Map<String, Category>) application.getAttribute("categorys");

		catidList.remove("9110051");

		//已选择类别
		List<String> selectedList = selectedCatid(param, catidList);

		//facet结果集
		List<CategoryWrap> categorys = facetCategory(facetFields, catidList, param);

		if ("1813,311,1501,125386001,201161703,125372003".equals(param.getCatid())) {
			param.setCatid("1818031101501");
		}
		if ("122584001".equals(param.getCatid())
				|| "9210134".equals(param.getCatid())
				|| "9110131".equals(param.getCatid())
				|| "121776006".equals(param.getCatid())
				|| "1818031101501".equals(param.getCatid())) {
			if (CollectionUtils.isEmpty(selectedList)) {
				selectedList = new ArrayList<>();
			}
			selectedList.add(param.getCatid());
		}
		List<CategoryWrap> categoryWrapList = getOtherCategories(param, selectedList);
		categorys.addAll(categoryWrapList);


		//子类类别集合
		List<CategoryWrap> dealCategoryChildren = dealCategoryChildren(categorys, selectedList);
		for (CategoryWrap categoryWrap : dealCategoryChildren) {
			if ("122584001".equals(categoryWrap.getId())) {
				categoryWrap.setName("Cat Litter");
			} else if ("9210134".equals(categoryWrap.getId())) {
				categoryWrap.setName("Shoes & Socks");
			} else if ("9110131".equals(categoryWrap.getId())) {
				categoryWrap.setName("Toys");
			} else if ("121776006".equals(categoryWrap.getId())) {
				categoryWrap.setName("Others");
			} else if ("1818031101501".equals(categoryWrap.getId())) {
				categoryWrap.setName("Toys, Kids & Babies");
			}

			if (!CollectionUtils.isEmpty(categoryWrap.getChilden())) {
				for (CategoryWrap categoryWrap1 : categoryWrap.getChilden()) {
					if ("122584001".equals(categoryWrap1.getId())) {
						categoryWrap1.setName("Cat Litter");
					} else if ("9210134".equals(categoryWrap1.getId())) {
						categoryWrap1.setName("Shoes & Socks");
					} else if ("9110131".equals(categoryWrap1.getId())) {
						categoryWrap1.setName("Toys");
					} else if ("121776006".equals(categoryWrap1.getId())) {
						categoryWrap1.setName("Others");
					} else if ("1818031101501".equals(categoryWrap1.getId())) {
						categoryWrap1.setName("Toys, Kids & Babies");
					}
					if (!CollectionUtils.isEmpty(categoryWrap1.getChilden())) {
						for (CategoryWrap categoryWrap2 : categoryWrap1.getChilden()) {
							if ("122584001".equals(categoryWrap2.getId())) {
								categoryWrap2.setName("Cat Litter");
							} else if ("9210134".equals(categoryWrap2.getId())) {
								categoryWrap2.setName("Shoes & Socks");
							} else if ("9110131".equals(categoryWrap2.getId())) {
								categoryWrap2.setName("Toys");
							} else if ("121776006".equals(categoryWrap2.getId())) {
								categoryWrap2.setName("Others");
							} else if ("1818031101501".equals(categoryWrap2.getId())) {
								categoryWrap2.setName("Toys, Kids & Babies");
							}
						}
					}
				}

			}
		}

		if (selectedList.isEmpty()) {
			return dealCategoryChildren;
		}

		//新品日志列表
		newArrivalDate(param, dealCategoryChildren);

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
				if(param.getSite() == 2){
					String name = categoryBean.getName();
					name = name.replace("Children's","").replace("Children","");
					wrap.setName(name);
				}else{
					wrap.setName(categoryBean.getName());
				}
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
		if (addCategory) {
			productsCate.append("<a class=\"searcwor\" style='color:deeppink'  href=\"")
					.append("/apa/clothing.html").append("\">")
					.append("wholesale clothing").append("</a>");
		}
		return productsCate.toString();
	}

	private List<Category> getCategoriesByIds(int site) {
		List<String> list = new ArrayList();
		// pet
		if (site == 4) {
			list.add("122584001");
			list.add("9210034");
			//list.add("9110035");
			list.add("9210036");
			//list.add("121802003");
			//list.add("9110037");
			//list.add("9110038");
			list.add("9110039");
			//list.add("122586001");
			//list.add("121840001");
			//list.add("9210040");
			//list.add("9110041");
			//list.add("9110042");
			//list.add("9110043");
			list.add("9210044");
			//list.add("9110045");
			//list.add("9110046");
			//list.add("121786003");
			list.add("121776006");
		}
		// kids
		else if (site == 2) {
			list.add("9410061");
			list.add("9410062");
			list.add("9410063");
			list.add("9410064");
			list.add("9410065");
			list.add("9410066");
			list.add("9410067");
			list.add("9410068");
			//list.add("9210052");
			list.add("9410069");
			list.add("9410070");
			list.add("9410071");
			list.add("9410072");
			list.add("9410073");
			list.add("9410074");
			list.add("9410075");
			list.add("9410076");
			list.add("9410077");
			list.add("9410078");
			list.add("9410079");
			list.add("9410080");
			list.add("9410081");
			list.add("9410082");
			list.add("9410083");
			list.add("9410084");
			list.add("9410085");
			list.add("9410086");
			list.add("9410087");
			list.add("9410088");
			list.add("9410089");
			list.add("9410090");
			list.add("9410091");
			list.add("9410092");
			list.add("9410093");
			list.add("9410094");
			list.add("9410095");
			list.add("9410096");
			list.add("9410097");
			list.add("9310121");
			list.add("9410123");

			list.add("9410124");
			list.add("9410125");
			list.add("9410127");
			list.add("9410128");
			list.add("9210134");
			list.add("9410117");
			list.add("9410120");
			list.add("9410114");
			list.add("9410126");

			list.add("9410118");
			list.add("9410119");
			list.add("9410116");
			list.add("125386001");
			list.add("1813");

			list.add("9210054");
			list.add("9210053");


		}
		return categoryMapper.getCategoriesByIds(list);
	}

	private List getOtherCategories(SearchParam param, List<String> catidList) {

		List<CategoryWrap> categorys = new ArrayList<>();
		if (param.getSite() == 2 || param.getSite() == 4) {
			List<Category> categoryList = getCategoriesByIds(param.getSite());
			for (Category category : categoryList) {
				CategoryWrap categoryWrap = new CategoryWrap();
				categoryWrap.setId(category.getCatid());
				categoryWrap.setName(category.getName());
				//categoryWrap.setParentCategory("121828001");
				categoryWrap.setLevel(category.getLevel());
				categoryWrap.setUrl("keyword=&srt=default&isFreeShip=2&catid=" + category.getCatid());
				if (catidList.contains(category.getCatid())) {
					categoryWrap.setSelected(1);
				} else {
					categoryWrap.setSelected(0);
				}
				if ("9410061".equals(category.getCatid())
						|| "9410062".equals(category.getCatid())
						|| "9410063".equals(category.getCatid())
						|| "9410064".equals(category.getCatid())
						|| "9410065".equals(category.getCatid())
						|| "9410066".equals(category.getCatid())
						|| "9410067".equals(category.getCatid())
						|| "9410068".equals(category.getCatid())
				) {
					categoryWrap.setParentCategory("9210054");
					categoryWrap.setLevel(2);
				} else if ("9410069".equals(category.getCatid())
						|| "9410070".equals(category.getCatid())
						|| "9410071".equals(category.getCatid())
						|| "9410072".equals(category.getCatid())
						|| "9410073".equals(category.getCatid())
						|| "9410074".equals(category.getCatid())
						|| "9410075".equals(category.getCatid())
						|| "9410076".equals(category.getCatid())
						|| "9410077".equals(category.getCatid())
						|| "9410078".equals(category.getCatid())
						|| "9410079".equals(category.getCatid())
						|| "9410080".equals(category.getCatid())
						|| "9410081".equals(category.getCatid())
						|| "9410082".equals(category.getCatid())
						|| "9410083".equals(category.getCatid())
				) {
					categoryWrap.setParentCategory("9210052");
					categoryWrap.setLevel(2);
				} else if ("9410084".equals(category.getCatid())
						|| "9410085".equals(category.getCatid())
						|| "9410086".equals(category.getCatid())
						|| "9410087".equals(category.getCatid())
						|| "9410088".equals(category.getCatid())
						|| "9410089".equals(category.getCatid())
						|| "9410090".equals(category.getCatid())
						|| "9410091".equals(category.getCatid())
						|| "9410092".equals(category.getCatid())
						|| "9410093".equals(category.getCatid())
						|| "9410094".equals(category.getCatid())
						|| "9410095".equals(category.getCatid())
						|| "9410096".equals(category.getCatid())
						|| "9410097".equals(category.getCatid())
				) {
					categoryWrap.setParentCategory("9210053");
					categoryWrap.setLevel(2);
				} else if ("9410123".equals(category.getCatid())
						|| "9410124".equals(category.getCatid())
						|| "9410125".equals(category.getCatid())
						|| "9410127".equals(category.getCatid())
						|| "9410128".equals(category.getCatid())

				) {
					categoryWrap.setParentCategory("9310121");
					categoryWrap.setLevel(2);
				} else if ("9410117".equals(category.getCatid())
						|| "9410120".equals(category.getCatid())
						|| "9410114".equals(category.getCatid())
						|| "9410126".equals(category.getCatid())
						|| "9410118".equals(category.getCatid())
						|| "9410119".equals(category.getCatid())
						|| "9410116".equals(category.getCatid())

				) {
					categoryWrap.setParentCategory("9210134");
					categoryWrap.setLevel(2);
				} else if ("9210054".equals(category.getCatid())
						|| "9210052".equals(category.getCatid())
						|| "9210053".equals(category.getCatid())
						|| "9310121".equals(category.getCatid())
						|| "9210134".equals(category.getCatid())
						|| "9110131".equals(category.getCatid())
				) {
					categoryWrap.setParentCategory("0");
					categoryWrap.setLevel(1);
				} else {
					String path = category.getPath();
					if (StringUtils.isNotBlank(path)) {
						String[] pathString = path.split(",");
						if (pathString.length == 1) {
							categoryWrap.setParentCategory("0");
						} else if (pathString.length == 2 || pathString.length == 3) {
							categoryWrap.setParentCategory(pathString[pathString.length - 2]);
						} else {
							categoryWrap.setParentCategory("0");
						}
					}
				}
//			/categoryWrap.setCount(20);

				categorys.add(categoryWrap);
			}
		}

		CategoryWrap categoryWrap = new CategoryWrap();
		// 自定义catid
		if (param.getSite() == 4) {

			categoryWrap.setId("122584001");
			categoryWrap.setName("Cat Litter ");
			categoryWrap.setParentCategory("0");
			categoryWrap.setLevel(1);
			categoryWrap.setUrl("keyword=&srt=default&isFreeShip=2&catid=122584001");
			if (catidList.contains("122584001")) {
				categoryWrap.setSelected(1);
			} else {
				categoryWrap.setSelected(0);
			}
			categorys.add(categoryWrap);


		} else if (param.getSite() == 2) {
			categoryWrap = new CategoryWrap();
			categoryWrap.setId("9210134");
			categoryWrap.setName("Shoes & Socks");
			categoryWrap.setParentCategory("0");
			categoryWrap.setLevel(1);
			categoryWrap.setUrl("keyword=&srt=default&isFreeShip=2&catid=9210134");
			if (catidList.contains("9210134")) {
				categoryWrap.setSelected(1);
			} else {
				categoryWrap.setSelected(0);
			}
			categorys.add(categoryWrap);
			categoryWrap = new CategoryWrap();
			categoryWrap.setId("9110131");
			categoryWrap.setName("Toys");
			categoryWrap.setParentCategory("0");
			categoryWrap.setLevel(1);
			categoryWrap.setUrl("keyword=&srt=default&isFreeShip=2&catid=9110131");
			if (catidList.contains("9110131")) {
				categoryWrap.setSelected(1);
			} else {
				categoryWrap.setSelected(0);
			}
			categorys.add(categoryWrap);
		} else if (param.getSite() == 1) {
			categoryWrap = new CategoryWrap();
			categoryWrap.setId("1818031101501");
			categoryWrap.setName("Toys, Kids & Babies");
			categoryWrap.setParentCategory("0");
			categoryWrap.setLevel(1);
			categoryWrap.setUrl("keyword=&srt=default&isFreeShip=2&catid=1818031101501");
			if (catidList.contains("1818031101501")
					|| catidList.contains("9210054")
					|| catidList.contains("9210052")
					|| catidList.contains("9210053")
					|| catidList.contains("9210107")
					|| catidList.contains("9210134")
					|| catidList.contains("9110131")) {
				categoryWrap.setSelected(1);
			} else {
				categoryWrap.setSelected(0);
			}
			categorys.add(categoryWrap);
			categoryWrap = new CategoryWrap();
			categoryWrap.setId("9210054");
			categoryWrap.setName("Baby(0-12M)");
			categoryWrap.setParentCategory("1818031101501");
			categoryWrap.setLevel(2);
			categoryWrap.setUrl("keyword=&srt=default&isFreeShip=2&catid=9210054");
			if (catidList.contains("9210054")) {
				categoryWrap.setSelected(1);
			} else {
				categoryWrap.setSelected(0);
			}
			categorys.add(categoryWrap);
			categoryWrap = new CategoryWrap();
			categoryWrap.setId("9210052");
			categoryWrap.setName("Toddler(1-6T)");
			categoryWrap.setParentCategory("1818031101501");
			categoryWrap.setLevel(2);
			categoryWrap.setUrl("keyword=&srt=default&isFreeShip=2&catid=9210052");
			if (catidList.contains("9210052")) {
				categoryWrap.setSelected(1);
			} else {
				categoryWrap.setSelected(0);
			}
			categorys.add(categoryWrap);
			categoryWrap = new CategoryWrap();
			categoryWrap.setId("9210053");
			categoryWrap.setName("Kids(6-14T)");
			categoryWrap.setParentCategory("1818031101501");
			categoryWrap.setLevel(2);
			categoryWrap.setUrl("keyword=&srt=default&isFreeShip=2&catid=9210053");
			if (catidList.contains("9210053")) {
				categoryWrap.setSelected(1);
			} else {
				categoryWrap.setSelected(0);
			}
			categorys.add(categoryWrap);
			categoryWrap = new CategoryWrap();
			categoryWrap.setId("9210107");
			categoryWrap.setName("Baby Product");
			categoryWrap.setParentCategory("1818031101501");
			categoryWrap.setLevel(2);
			categoryWrap.setUrl("keyword=&srt=default&isFreeShip=2&catid=9210107");
			if (catidList.contains("9210107")) {
				categoryWrap.setSelected(1);
			} else {
				categoryWrap.setSelected(0);
			}
			categorys.add(categoryWrap);
			categoryWrap = new CategoryWrap();
			categoryWrap.setId("9210134");
			categoryWrap.setName("Shoes & Socks");
			categoryWrap.setParentCategory("1818031101501");
			categoryWrap.setLevel(2);
			categoryWrap.setUrl("keyword=&srt=default&isFreeShip=2&catid=9210134");
			if (catidList.contains("9210134")) {
				categoryWrap.setSelected(1);
			} else {
				categoryWrap.setSelected(0);
			}
			categorys.add(categoryWrap);
			categoryWrap = new CategoryWrap();
			categoryWrap.setId("9110131");
			categoryWrap.setName("Toys");
			categoryWrap.setParentCategory("1818031101501");
			categoryWrap.setLevel(2);
			categoryWrap.setUrl("keyword=&srt=default&isFreeShip=2&catid=9110131");
			if (catidList.contains("9110131")) {
				categoryWrap.setSelected(1);
			} else {
				categoryWrap.setSelected(0);
			}
			categorys.add(categoryWrap);

		}
		return categorys;

	}
}
