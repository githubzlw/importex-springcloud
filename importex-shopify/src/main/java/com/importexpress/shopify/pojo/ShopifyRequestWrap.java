package com.importexpress.shopify.pojo;

import lombok.Data;

@Data
public class ShopifyRequestWrap {

    private String shopname;

    private GoodsBean goods;
}
