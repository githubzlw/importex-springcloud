package com.importexpress.shopify.service.impl;

import com.google.gson.Gson;
import com.importexpress.shopify.mapper.ShopifyAuthMapper;
import com.importexpress.shopify.mapper.ShopifyOrderMapper;
import com.importexpress.shopify.pojo.orders.Line_items;
import com.importexpress.shopify.pojo.orders.Orders;
import com.importexpress.shopify.pojo.orders.OrdersWraper;
import com.importexpress.shopify.pojo.orders.Shipping_address;
import com.importexpress.shopify.service.ShopifyAuthService;
import com.importexpress.shopify.service.ShopifyOrderService;
import com.importexpress.shopify.util.Config;
import com.importexpress.shopify.util.ShopifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.service.impl
 * @date:2019/11/28
 */
@Service
public class ShopifyOrderServiceImpl implements ShopifyOrderService {

    @Autowired
    private ShopifyOrderMapper shopifyOrderMapper;
    @Autowired
    private Config config;
    @Autowired
    private ShopifyUtil shopifyUtil;
    @Autowired
    private ShopifyAuthService shopifyAuthService;

    @Override
    public List<Orders> queryListByShopifyName(String shopifyName) {
        return shopifyOrderMapper.queryListByShopifyName(shopifyName);
    }

    @Override
    public int insertOrderInfoSingle(Orders orderInfo) {
        return shopifyOrderMapper.insertOrderInfoSingle(orderInfo);
    }

    @Override
    public List<Line_items> queryOrderDetailsByOrderId(long orderId) {
        return shopifyOrderMapper.queryOrderDetailsByOrderId(orderId);
    }

    @Override
    public int insertOrderDetails(Line_items item) {
        return shopifyOrderMapper.insertOrderDetails(item);
    }

    @Override
    public Shipping_address queryOrderAddressByOrderId(long orderId) {
        return shopifyOrderMapper.queryOrderAddressByOrderId(orderId);
    }

    @Override
    public int insertIntoOrderAddress(Shipping_address address) {
        return shopifyOrderMapper.insertIntoOrderAddress(address);
    }
    /**
     * 获取所有订单
     * @param shopName
     */
    @Override
    public OrdersWraper getOrders(String shopName) {

        String url = String.format(config.SHOPIFY_URI_ORDERS, shopName);
        String json = shopifyUtil.exchange(url, shopifyAuthService.getShopifyToken(shopName));
        OrdersWraper result = new Gson().fromJson(json, OrdersWraper.class);
        return result;
    }

}
