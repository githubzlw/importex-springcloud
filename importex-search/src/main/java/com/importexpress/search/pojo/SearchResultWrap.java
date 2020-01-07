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
	@ApiModelProperty(value="商品")
	private List<Product> products;

	@ApiModelProperty(value="类别列表")
	private List<CategoryWrap> categorys;

	@ApiModelProperty(value="页码")
	private PageWrap page;

	@ApiModelProperty(value="属性集合")
	private List<AttributeWrap> attributes;

	@ApiModelProperty(value="已选择属性集合")
	private List<Attribute> selectedAttr;

	@ApiModelProperty(value="是否推荐联想词")
	private boolean isSuggest;

	@ApiModelProperty(value="推荐联想词")
	private List<AssociateWrap> associates;

	@ApiModelProperty(value="搜索页增加面包屑导航")
	private Map<String, String> searchNavigation;

	@ApiModelProperty(value="搜索参数")
	private SearchParam param;

}
