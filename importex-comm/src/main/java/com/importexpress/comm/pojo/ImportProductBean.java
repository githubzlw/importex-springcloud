package com.importexpress.comm.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.pojo.product
 * @date:2019/12/10
 */
@Data
@AllArgsConstructor
@Builder
public class ImportProductBean {

    private String pid;

    private String custom_main_image;
    private String valid;
    private String remotpath;
    private String catid;
    private String shop_id;

     private String enname;

    private String price;
    private String wprice;
    private String feeprice;
    private String range_price;
    private String range_price_free;
    private String fprice_str;
    private String sellunit;

    private String morder;
    private String entype;
    private String entype_new;

    private String sold;
    private String final_weight;

    private String finalName;

    private String goodsUrl;
    /**
	 * 海外仓
	 */
	private String overSeaPrice;



}
