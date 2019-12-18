package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 搜索结果
 * @author sj
 *
 */
@Data
public class SearchResultWrap {
	@ApiModelProperty(value="商品")
	private List<Product> products;

	@ApiModelProperty(value="类别列表")
	private List<CategoryWrap> categorys;

	@ApiModelProperty(value="页码")
	private Page page;

	@ApiModelProperty(value="属性集合")
	private List<AttributeWrap> attributeWraps;

	@ApiModelProperty(value="已选择属性集合")
	private List<Attribute> selectedAttr;

	@ApiModelProperty(value="是否推荐联想词")
	private boolean isSuggest;

	@ApiModelProperty(value="推荐联想词")
	private List<AssociateWrap> associates;

}
