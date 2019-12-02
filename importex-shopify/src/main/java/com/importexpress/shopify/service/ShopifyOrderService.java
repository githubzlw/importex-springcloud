package com.importexpress.shopify.service;

import com.importexpress.shopify.pojo.orders.Line_items;
import com.importexpress.shopify.pojo.orders.Orders;
import com.importexpress.shopify.pojo.orders.OrdersWraper;
import com.importexpress.shopify.pojo.orders.Shipping_address;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.service
 * @date:2019/11/28
 */
public interface ShopifyOrderService {
    /**获取shopify订单
     * @param shopName
     * @return
     */
    OrdersWraper getOrders(String shopName);


    /**
     * 查询订单数据
     *
     * @param shopifyName
     * @return
     */
    List<Orders> queryListByShopifyName(String shopifyName);

    /**
     * 插入订单数据
     *
     * @param orderInfo
     * @return
     */
    int insertOrderInfoSingle(Orders orderInfo);


    /**
     * 查询订单详情数据
     *
     * @param orderId
     * @return
     */
    List<Line_items> queryOrderDetailsByOrderId(long orderId);

    /**
     * 插入订单详情数据
     *
     * @param item
     * @return
     */
    int insertOrderDetails(Line_items item);

    /**
     * 查询订单地址数据
     *
     * @param orderId
     * @return
     */
    Shipping_address queryOrderAddressByOrderId(long orderId);

    /**
     * 插入订单地址数据
     *
     * @param address
     * @return
     */
    int insertIntoOrderAddress(Shipping_address address);

}
