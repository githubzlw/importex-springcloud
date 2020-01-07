package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 产品描述
 * @author sj
 *
 */
@Data
public class Product implements Serializable {
	private static final long serialVersionUID = 986818736352041314L;

	@ApiModelProperty(value="商品id")
	private String id;

	@ApiModelProperty(value="商品链接")
	private String url;

	@ApiModelProperty(value="商品名")
	private String name;

	@ApiModelProperty(value="商品图片")
	private String image;

	@ApiModelProperty(value="商品最小订单")
	private String minOrder;

	@ApiModelProperty(value="已经售出数量")
	private String sold;

	@ApiModelProperty(value="订量单位")
	private String moqUnit;

	@ApiModelProperty(value="价格后面单位")
	private String priceUnit;

	@ApiModelProperty(value="订量单位2  100 pieces/lot")
	private String goodsUnit;

	@ApiModelProperty(value="产品重量")
	private String weight;

	@ApiModelProperty(value="价格")
	private String price;

	@ApiModelProperty(value="产品类别")
	private String category;

	@ApiModelProperty(value="多批量批发价格")
	private List<Price> wholesalePrice;

	@ApiModelProperty(value="多批量批发价格-非免邮中间价格")
	private String wholesaleMiddlePrice;

	@ApiModelProperty(value="货币单位")
	private String currencySymbol = "USD";

	@ApiModelProperty(value="商品id")
	private String shopId;

	@ApiModelProperty(value="是否有视频 1-有视频  0-无视频")
	private int isVideo;

	@ApiModelProperty(value="商品库存标识  0没有库存  1有库存  hot")
	private int isStock;
}
