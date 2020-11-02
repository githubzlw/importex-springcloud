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

	/**
	 * 商品链接
	 */
	@ApiModelProperty(value="商品链接")
	private String id;

	/**
	 * 商品链接
	 */
	@ApiModelProperty(value="商品链接")
	private String url;

	/**
	 *商品名
	 */
	@ApiModelProperty(value="商品名")
	private String name;

	/**
	 *商品图片
	 */
	@ApiModelProperty(value="商品图片")
	private String image;

	/**
	 *商品最小订单
	 */
	@ApiModelProperty(value="商品最小订单")
	private String minOrder;

	/**
	 *已经售出数量
	 */
	@ApiModelProperty(value="已经售出数量")
	private String sold;

	/**
	 *订量单位
	 */
	@ApiModelProperty(value="订量单位")
	private String moqUnit;

	/**
	 *价格后面单位
	 */
	@ApiModelProperty(value="价格后面单位")
	private String priceUnit;

	/**
	 *订量单位2  100 pieces/lot
	 */
	@ApiModelProperty(value="订量单位2  100 pieces/lot")
	private String goodsUnit;

	/**
	 *产品重量
	 */
	@ApiModelProperty(value="产品重量")
	private String weight;

	/**
	 *价格
	 */
	@ApiModelProperty(value="价格")
	private String price;

	/**
	 *产品类别
	 */
	@ApiModelProperty(value="产品类别")
	private String category;

	/**
	 *多批量批发价格
	 */
	@ApiModelProperty(value="多批量批发价格")
	private List<Price> wholesalePrice;

	/**
	 *多批量批发价格-非免邮中间价格
	 */
	@ApiModelProperty(value="多批量批发价格-非免邮中间价格")
	private String wholesaleMiddlePrice;

	/**
	 *货币单位
	 */
	@ApiModelProperty(value="货币单位")
	private String currencySymbol = "USD";

	/**
	 *店铺id
	 */
	@ApiModelProperty(value="店铺id")
	private String shopId;

	/**
	 *是否有视频 1-有视频  0-无视频
	 */
	@ApiModelProperty(value="是否有视频 1-有视频  0-无视频")
	private int isVideo;

	/**
	 *商品库存标识  0没有库存  1有库存  hot
	 */
	@ApiModelProperty(value="商品库存标识  0没有库存  1有库存  hot")
	private int isStock;

	/**
	 *商品重量
	 */
	@ApiModelProperty(value="商品重量")
	private String final_weight;

	/**
	 *非免邮价格
	 */
	@ApiModelProperty(value="非免邮价格")
	private String wprice;

	/**
	 *免邮价格
	 */
	@ApiModelProperty(value="免邮价格")
	private String free_price_new;

	/**
	 *非免邮价格区间
	 */
	@ApiModelProperty(value="非免邮价格区间")
	private String range_price;

	/**
	 *免邮价格区间
	 */
	@ApiModelProperty(value="免邮价格区间")
	private String range_price_free_new;

	/**
	 *体积重量
	 */
	@ApiModelProperty(value="体积重量")
	private String volume_weight;

	/**
	 *是否免邮
	 */
	@ApiModelProperty(value="是否免邮")
	private String img_check;

}
