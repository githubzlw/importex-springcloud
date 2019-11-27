package com.importexpress.shopify.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**商品类
 * @author abc
 */
@Data
public class GoodsBean implements Serializable {

	private static final long serialVersionUID = -4952290032355580137L;
	@ApiModelProperty("//商品名称")
	private String name;
	@ApiModelProperty("//商品id")
	private String pid;
	@ApiModelProperty("//商品类别")
	private String category;
	@ApiModelProperty("//packing 重量")
	private String perWeight;
	@ApiModelProperty("//商品规格")
	private List<TypeBean> type;
	@ApiModelProperty("//商品图片")
	private List<String> image;
	@ApiModelProperty("//商品详细信息")
	private Map<String, String> info;
	@ApiModelProperty("//商品详细（图片+文字）")
	private String infoHtml;
	private String skuProducts;
}
