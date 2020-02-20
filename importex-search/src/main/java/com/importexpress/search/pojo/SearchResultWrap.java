package com.importexpress.search.pojo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 搜索结果
 * @author sj
 *
 */
@Data
public class SearchResultWrap implements Serializable {
	public SearchResultWrap() {
		this.page = new PageWrap();
		this.page.setRecordCount(0L);
		this.products = Lists.newArrayList();
		this.categorys = Lists.newArrayList();
		this.attributes = Lists.newArrayList();
		this.selectedAttr = Lists.newArrayList();
		this.associates = Lists.newArrayList();
		this.searchNavigation = Maps.newHashMap();
	}

	private static final long serialVersionUID = 986818736352041314L;
	/**
	 * 商品
	 */
	@ApiModelProperty(value="商品")
	private List<Product> products;

	/**
	 * 类别列表
	 */
	@ApiModelProperty(value="类别列表")
	private List<CategoryWrap> categorys;

	/**
	 * 页码
	 */
	@ApiModelProperty(value="页码")
	private PageWrap page;

	/**
	 * 属性集合
	 */
	@ApiModelProperty(value="属性集合")
	private List<AttributeWrap> attributes;

	/**
	 * 已选择属性集合
	 */
	@ApiModelProperty(value="已选择属性集合")
	private List<Attribute> selectedAttr;

	/**
	 * 是否推荐联想词 1-是 0-否
	 */
	@ApiModelProperty(value="是否推荐联想词 1-是 0-否")
	private int suggest;

	/**
	 * 推荐联想词
	 */
	@ApiModelProperty(value="推荐联想词")
	private List<AssociateWrap> associates;

	/**
	 * 搜索页增加面包屑导航
	 */
	@ApiModelProperty(value="搜索页增加面包屑导航")
	private Map<String, String> searchNavigation;

	/**
	 * 搜索参数
	 */
	@ApiModelProperty(value="搜索参数")
	private SearchParam param;

	/**
	 * See more products in category
	 */
	@ApiModelProperty(value="See more products in category")
	private String productsCate;

}
