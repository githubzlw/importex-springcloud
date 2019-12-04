package com.importexpress.shopify.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**商品类
 * @author abc
 */
@Data
public class ShopifyData implements Serializable {

	private static final long serialVersionUID = -4952290032355580137L;
	@ApiModelProperty(value = "商品名称",required = true)
	private String name;
	@ApiModelProperty(value ="商品id",required = true)
	private String pid;
	@ApiModelProperty(value ="商品类别",required = true)
	private String category;
	@ApiModelProperty(value ="重量",required = true)
	private String perWeight;
	@ApiModelProperty(value ="商品规格",required = true)
	private List<TypeBean> type;
	@ApiModelProperty(value ="商品图片",required = true)
	private List<String> image;
	@ApiModelProperty(value ="商品详细信息",required = true)
	private List<String> info;
	@ApiModelProperty(value ="商品详细(图片+文字)",required = true)
	private String infoHtml;
	@ApiModelProperty(value ="sku",required = true)
	private String skuProducts;
	@ApiModelProperty(value ="价格",required = true)
	private String price;
	@ApiModelProperty(value ="网站",required = true)
	private String vendor;
}
