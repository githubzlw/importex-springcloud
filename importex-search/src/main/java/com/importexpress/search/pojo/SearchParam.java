package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 搜索请求参数
 * @author sj
 *
 */
@Data
public class SearchParam implements Serializable {
	private static final long serialVersionUID = 986818736352041314L;

	@ApiModelProperty(value = "关键词")
	private String keyword;

	@ApiModelProperty(value = "search within")
	private String fKey;

	@ApiModelProperty(value = "反关键词")
	private String unkey;

	@ApiModelProperty(value = "类别反关键词")
	private String reverseKeywords;

	@ApiModelProperty(value = "最小订量")
	private String minq;

	@ApiModelProperty(value = "最大订量")
	private String maxq;

	@ApiModelProperty(value = "排序")
	private String sort = "default";

	@ApiModelProperty(value = "最小价格")
	private String minPrice;

	@ApiModelProperty(value = "最大价格")
	private String maxPrice;

	@ApiModelProperty(value = "类别ID")
	private String catid;

	@ApiModelProperty(value = "属性id")
	private String attrId;

	@ApiModelProperty(value = "请求页数")
	private int page = 1;

	@ApiModelProperty(value = "每页数据数量")
	private int pageSize = 60;

	@ApiModelProperty(value = "是否免邮 (1-默认免邮;2-选择免邮;0-取消免邮)")
	private int freeShipping = 1;

	@ApiModelProperty(value = "请求网站(1-importexpress; 2-kids; 4-pets)")
	private int site;

	@ApiModelProperty(value = "请求接口")
	private String uriRequest;

	@ApiModelProperty(value = "货币")
	private Currency currency;

	@ApiModelProperty(value = "是否注册版搜索")
	private boolean isImportExpress = false;

	@ApiModelProperty(value = "filter=0 默认全部可搜 1-描述很精彩   2-卖过的   3-精选店铺")
	private int importType = 0;

	@ApiModelProperty(value = "用户类型 0-未授权 1-授权")
	private int  userType = 1;

	@ApiModelProperty(value = "是否搜索的同时统计类别列表,默认统计")
	private boolean isFactCategory = true;

	@ApiModelProperty(value = "是否搜索的同时统计规格属性,默认统计")
	private boolean isFactPvid = true;

	@ApiModelProperty(value = "精品（2019-10-11-注册版搜索描述很精彩）默认不是")
	private boolean boutique = false;

	@ApiModelProperty(value = "是否New Arrival，默认1")
	private int collection;

	@ApiModelProperty(value = "店铺id")
	private String storied;

	@ApiModelProperty(value = "新品日期")
	private String newArrivalDate;

	@ApiModelProperty(value = "是否移动版")
	private boolean isMobile = false;

	@ApiModelProperty(value = "是否排序，默认排序")
	private boolean isOrder = true;

	@ApiModelProperty(value = "区间价格条选择")
	private  String prices;

	@ApiModelProperty(value = "限制的产品id")
	private String pid;

	@ApiModelProperty(value = "价格区间统计-第几区间")
	private int selectedInterval;

	@ApiModelProperty(value = "价格区间统计-是否从区间图点击过来")
	private boolean isRange;

}
