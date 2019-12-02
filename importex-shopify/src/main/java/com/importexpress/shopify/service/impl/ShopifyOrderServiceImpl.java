package com.importexpress.shopify.service.impl;

import com.importexpress.shopify.mapper.ShopifyOrderMapper;
import com.importexpress.shopify.pojo.orders.Line_items;
import com.importexpress.shopify.pojo.orders.Orders;
import com.importexpress.shopify.pojo.orders.OrdersWraper;
import com.importexpress.shopify.pojo.orders.Shipping_address;
import com.importexpress.shopify.service.ShopifyOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.service.impl
 * @date:2019/11/28
 */
@Slf4j
@Service
public class ShopifyOrderServiceImpl implements ShopifyOrderService {

    @Autowired
    private ShopifyOrderMapper shopifyOrderMapper;

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

    @Override
    public void genShopifyOrderInfo(String shopifyName, OrdersWraper orders) {
        List<Orders> shopifyOrderList = orders.getOrders();

        List<Orders> existList = shopifyOrderMapper.queryListByShopifyName(shopifyName);

        List<Orders> insertList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(existList)) {
            // 过滤已经存在的订单
            Map<Long, Orders> idSet = new HashMap<>(existList.size() * 2);
            existList.stream().forEach(e -> idSet.put(e.getId(), e));
            insertList = shopifyOrderList.stream().filter(e -> {
                if (idSet.containsKey(e.getId())) {
                    Orders tempOrder = idSet.get(e.getId());
                    if (!tempOrder.getTotal_price_usd().equalsIgnoreCase(e.getTotal_price_usd())
                            || !tempOrder.getFinancial_status().equalsIgnoreCase(e.getFinancial_status())) {
                        return true;
                    }
                    return false;
                } else {
                    return true;
                }
            }).collect(Collectors.toList());
            idSet.clear();
        } else {
            insertList = new ArrayList<>(shopifyOrderList);
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            for (Orders orderInfo : insertList) {
                try {
                    orderInfo.setShopify_name(shopifyName);
                    shopifyOrderMapper.insertOrderInfoSingle(orderInfo);
                    if (CollectionUtils.isNotEmpty(orderInfo.getLine_items())) {
                        for (Line_items item : orderInfo.getLine_items()) {
                            item.setOrder_no(orderInfo.getId());
                            shopifyOrderMapper.insertOrderDetails(item);
                        }
                    }
                    if (orderInfo.getShipping_address() != null) {
                        orderInfo.getShipping_address().setOrder_no(orderInfo.getId());
                        shopifyOrderMapper.insertIntoOrderAddress(orderInfo.getShipping_address());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("shopifyName:" + shopifyName + ",genShopifyOrderInfo error:", e);
                }
            }
        }
        shopifyOrderList.clear();
    }

}
