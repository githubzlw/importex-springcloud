package com.importexpress.shopify.pojo;

import lombok.Data;

/**
 * shopify请求参数
 */
@Data
public class ShopifyRequestWrap {

    /**
     * 店铺名称
     */
    private String shopname;

    /**
     * 数据
     */
    private ShopifyData data;
}
