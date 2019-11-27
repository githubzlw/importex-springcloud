package com.importexpress.shopify.service;


import com.importexpress.shopify.pojo.orders.OrdersWraper;
import com.importexpress.shopify.pojo.product.ProductWraper;
import com.importexpress.shopify.pojo.product.ProductsWraper;
import com.importexpress.shopify.pojo.product.ShopifyBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface ShopifyService {

    HashMap<String, String> getAccessToken(String shopname, String code) throws IOException;

    ProductWraper addProduct(String shopname, ProductWraper productWraper);

    ProductsWraper getProduct(String shopname);

    int saveShopifyAuth(String shopName,String access_token, String scope);

    OrdersWraper getOrders(String shopName);

    /**
     * 绑定shopify铺货的ID与我司网站的PID关联
     *
     * @param  shopifyBean
     * @return
     */
    int insertShopifyIdWithPid(ShopifyBean shopifyBean);

    /**
     *  根据shopify店铺名称获取所有对应的PID
     *
     * @param shopifyName : shopify店铺名
     * @return
     */
    List<ShopifyBean> queryPidbyShopifyName(String shopifyName);
}
