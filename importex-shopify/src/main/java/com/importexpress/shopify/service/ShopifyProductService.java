package com.importexpress.shopify.service;


import com.importexpress.shopify.exception.ShopifyException;
import com.importexpress.shopify.pojo.ProductRequestWrap;
import com.importexpress.shopify.pojo.ShopifyData;
import com.importexpress.shopify.pojo.product.ProductWraper;
import com.importexpress.shopify.pojo.product.ShopifyBean;

import java.util.List;

public interface ShopifyProductService {

    /**下架
     * @param shopname
     * @param id
     * @return
     */
    int delete(String shopname, String id);

    /**铺货到shopify
     * @param shopname
     * @param productWraper
     * @return
     */
    ProductWraper addProduct(String shopname, ProductWraper productWraper);
    /**
     * 绑定shopify铺货的ID与我司网站的PID关联
     *
     * @param  shopifyBean
     * @return
     */
    int insertShopifyIdWithPid(ShopifyBean shopifyBean);

    /**获取shopify铺货的ID与我司网站的PID关联的数据
     * @param shopifyBean
     * @return
     */
    ShopifyBean selectShopifyId(ShopifyBean shopifyBean);

    /**铺货
     * @param shopname
     * @param goods
     * @return
     */
    ProductWraper onlineProduct(String shopname, ShopifyData goods) throws ShopifyException;
    /**铺货
     * @param wrap 铺货请求参数
     * @return
     */
    ProductWraper pushProduct(ProductRequestWrap wrap) throws ShopifyException;
    /**铺货校验
     * @param shopname
     * @param itemId
     * @return
     */
    ShopifyBean checkProduct(String shopname, String itemId) throws ShopifyException;
    /**批量铺货
     * @param shopname
     * @param ids
     * @param site
     * @return
     */
    List<ProductWraper> onlineProducts(String shopname, String[] ids, int site,boolean published,boolean bodyHtml)
            throws ShopifyException;


}
