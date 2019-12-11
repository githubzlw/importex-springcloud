package com.importexpress.search.pojo;

import lombok.Data;

import java.util.List;

/**
 * 搜索结果
 * @author sj
 *
 */
@Data
public class SearchResultWrap {
	/**
	 * 商品
	 */
	private List<Product> products;
	/**
	 * 类别
	 */
	private List<CategoryWrap> categorys;
	/**
	 * 页码
	 */
	private Page page;

	/**
	 * 属性集合
	 */
	private List<AttributeWrap> attributeWraps;


	/**
	 * 已选择属性集合
	 */
	private List<Attribute> selectedAttr;


}
