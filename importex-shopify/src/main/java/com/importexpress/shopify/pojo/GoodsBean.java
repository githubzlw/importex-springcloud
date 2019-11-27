package com.importexpress.shopify.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


/**商品类
 * @author abc
 */
@Data
public class GoodsBean implements Serializable,Cloneable {

	private static final long serialVersionUID = -4952290032355580137L;
	private String ip;

	@ApiModelProperty("//数据状态 6：下架，不能添加购物车  0：页面商品数据 1：正常")
	private int valid = 1;
	private int cid;
	@ApiModelProperty("//数据入库时间")
	private String ctime;
	@ApiModelProperty("//网页title")
	private String title;
	@ApiModelProperty("//商品名称")
	private String pName;
	@ApiModelProperty("//商品id")
	private String pID;
	@ApiModelProperty("//卖家id")
	private String sID;
	@ApiModelProperty("//卖家名称（可能为公司名）")
	private String sName;
	@ApiModelProperty("//商品批发价格")
	private List<String> pWprice;
	@ApiModelProperty("//商品原价")
	private String pOprice;
	@ApiModelProperty("//商品现价")
	private String pSprice;
	@ApiModelProperty("//货币单位")
	private String pPriceUnit;
	@ApiModelProperty("//商品单位")
	private String pGoodsUnit;
	@ApiModelProperty("//最小订单")
	private String minOrder;
	@ApiModelProperty("//已经售出数量")
	private String sell;
	@ApiModelProperty("//商品类别")
	private String category;
	@ApiModelProperty("//window.runParams.categoryId")
	private String categoryId;
	@ApiModelProperty("//免邮标志，为“1”是免邮,2:去请求免邮信息，“3”非免邮")
	private String free;
	@ApiModelProperty("//免邮的快递方式")
	private String method;
	@ApiModelProperty("//快递时间")
	private String time;
	@ApiModelProperty("//packing 重量")
	private String weight;
	@ApiModelProperty("//packing 长*宽*高")
	private String width;
	@ApiModelProperty("//计算后的商品体积")
	private String volume;
	@ApiModelProperty("//packing 计数单位")
	private String sellUnits;
	private String perWeight;
	@ApiModelProperty("//图片后缀，小图在前  大图在后")
	private String[] imgSize;
	@ApiModelProperty("//商品规格")
	private List<TypeBean> type;
	@ApiModelProperty("//商品图片")
	private List<String> pImage;
	@ApiModelProperty("//订单处理时间")
	private String pTime;
	@ApiModelProperty("//运费")
	private String pFreight;
	@ApiModelProperty("//商品详细信息")
	private HashMap<String, String> pInfo;
	@ApiModelProperty("//")
	private List<String> pDetail;
	@ApiModelProperty("//商品详细（图片+文字）")
	private String info_ori;
	@ApiModelProperty("//数据来源(web or sql)")
	private String com;
	@ApiModelProperty("//包装信息")
	private String packages;
	@ApiModelProperty("//详情url")
	private String infourl;
	@ApiModelProperty("//详情标志")
	private String flag="1";
	private String skuProducts;
	@ApiModelProperty("//去除免邮运费后的商品价格  改成了拿样费")
	private String fprice;
	@ApiModelProperty("//用户反馈")
	private String noteUrl;
	@ApiModelProperty("//头部导航")
	private String categps;
	@ApiModelProperty("//分级类别id，最小级别  ")
	private String catid1;
	private String catid2;
	private String catid3;
	private String catid4;
	private String catid5;
	@ApiModelProperty("//")
	private String catid6;
	@ApiModelProperty("//规格明细id")
	private String pvid;
	@ApiModelProperty("//价格校正倍数 2016-11-02")
	private double rate=1;
	@ApiModelProperty("//定制标志 2017-01-11")
	private  int customFlag;
	@ApiModelProperty("//seo主标题特征 2017-01-11")
	private String feature;
	@ApiModelProperty("//seo主标题")
	private String seoTitle;
	@ApiModelProperty("//最近一次的历史价格")
	private String historyPrice;
	@ApiModelProperty("//新抓取的价格或者goodsdata数据表记录的价格")
	private String currentPrice;
	@ApiModelProperty("//产品单页seo用 产品链接")
	private String seoUrl;
	@ApiModelProperty("//工厂价")
	private String notFreePrice;
	@ApiModelProperty("//产品单页初次显示免邮价格")
	private String  lastPrice;
	@ApiModelProperty("//单位（100piece/lot）")
	private String goodsUnitTemp;
	@ApiModelProperty("//是否检查过有货源 1-检查过  0-未检查")
	private int isStock;
	@ApiModelProperty("//汉明距离check")
	private int imgCheck;
	@ApiModelProperty("//类别 (类似15,2103,45320)")
	private String catids;
	@ApiModelProperty("//产品itemid的uuid")
	private String goodsUuid;
	@ApiModelProperty("//0-aliexpress 1-货源替换商品 2-1688上传商品，3-亚马逊商品  4-图片搜索商品")
	private int isSource;
	@ApiModelProperty("//参数价格,搜索页面传过来的")
	private String paramPrice;
	@ApiModelProperty("//使用参数价格 1-使用 0-不适用")
	private int userParam;
	@ApiModelProperty("//显示批量采购价格")
	private String batchPrice;
	@ApiModelProperty("//显示批量采购数量")
	private int batchCount;
	@ApiModelProperty("//批量工厂价")
	private String batchFactoryPrice;
	@ApiModelProperty("//批量运费")
	private String batchFeight;
	@ApiModelProperty("//产品原链接")
	private String pUrl;
	private boolean pFreightChange;
	@ApiModelProperty("//aliexpress重量")
	private String aliWeight;

	@ApiModelProperty("//产品类别折扣价格")
	private String bizPrice;
	@ApiModelProperty("//是否显示类别折扣价格")
	private int isShowBizPrice;
	private List<PriceBean> priceList;
	@ApiModelProperty("//店铺链接")
	private String storeUrl;
	@ApiModelProperty("//1688产品源价格")
	private String wholesalePrice;
	@ApiModelProperty("//ali商品名称")
	private String otherinfo;
	@ApiModelProperty("//库存标志  0 没有库存 1有库存")
	private String is_stock_flag;
	private String shopname;

	@ApiModelProperty("//海运价格")
	private  double oceanPrice;
	@ApiModelProperty("//产品单页视频展示路径")
	private String video_url;

	@ApiModelProperty("//低库存标识默认 0 低库存3")
	private  int  unsellableReason;
	@ApiModelProperty("//0-与速卖通对标的商品；1-与亚马逊对标的商品")
	private int matchSource;
	@ApiModelProperty("//店铺描述")
	private String shopDescription;
	@ApiModelProperty("//类别描述")
	private String catDescription;
	@ApiModelProperty("//关键词")
	private String keyWord;
	@ApiModelProperty("//中文属性")
	private String cnDetail;
	@ApiModelProperty("//最小类别名")
	private String catNameMin;

	@ApiModelProperty("//此产品的 1688sku id,这个数据用来关联 custom_bench_sku表行")
	private String skuid_1688;

	@ApiModelProperty("//ali对标价格，或者是 amazon对标价格")
	private String ali_price;

	@ApiModelProperty("//此产品的免邮价。格式与 wprice一致，是这个产品的  免邮价的 wprice")
	private String feeprice;

	@ApiModelProperty("//此产品是否有免邮价。0-无免邮价；1-老客户订单商品（强制要有免邮价）；2-有免邮价商品")
	private int is_sold_flag;

	@ApiModelProperty("* 那样moq 选择的数量低于这个 则需要使用那样费")

	private int source_used_flag;


	@ApiModelProperty("* 店铺商品数量")

	private Integer shopCount;

	@ApiModelProperty("* businessPrice 改为 Sample price")

	private  String businessPrice;


	@ApiModelProperty("* dp专用价格 都用非免邮价格")

	private List<PriceBean> dpPriceList;

	@ApiModelProperty("* dp专用价格 都用非免单价")

	private String dpNowPrice;


	@ApiModelProperty("* 主图")

	private String mainImg;

	private double volumeWeight;

	@ApiModelProperty("* 判断是否是区间价商品")

	private String rangPrice;


	@ApiModelProperty("* 按照moq计算最初buessiness sipping cost")

	private double buessinessShippingCost;

	@ApiModelProperty("* 国内交期时间")

	private  String processingTime;

	@ApiModelProperty("* min pirce")

	private String minPrice;
	private String promotionFlag;

	@Override
	public GoodsBean clone()  {
		GoodsBean bean = null;
        try {
        	bean = (GoodsBean) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return bean;
	}

}
