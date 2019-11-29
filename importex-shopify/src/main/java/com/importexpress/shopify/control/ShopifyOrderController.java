package com.importexpress.shopify.control;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.shopify.pojo.orders.Line_items;
import com.importexpress.shopify.pojo.orders.Orders;
import com.importexpress.shopify.pojo.orders.OrdersWraper;
import com.importexpress.shopify.pojo.orders.Shipping_address;
import com.importexpress.shopify.service.ShopifyOrderService;
import com.importexpress.shopify.service.ShopifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.control
 * @date:2019/11/28
 */
@Slf4j
@RestController
@RequestMapping("/shopifyOrder")
public class ShopifyOrderController {

    @Autowired
    private ShopifyService shopifyService;

    @Autowired
    private ShopifyOrderService shopifyOrderService;


    @RequestMapping("/list/{shopifyName}")
    public CommonResult getShopifyOrderByShopifyName(
            @PathVariable(value = "shopifyName") String shopifyName, Long pageNum, Long limitNum) {
        CommonResult rs = new CommonResult();

        if (pageNum == null || pageNum < 1L) {
            pageNum = 1L;
        }
        if (limitNum == null || limitNum < 1L) {
            limitNum = 10L;
        }
        try {

            List<Orders> ordersList = shopifyOrderService.queryListByShopifyName(shopifyName);
            List<Orders> rsList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(ordersList)) {
                rsList = ordersList.stream().skip((pageNum - 1) * limitNum).limit(limitNum).collect(Collectors.toList());
            }
            rs.success(rsList);
        } catch (Exception e) {
            rs.failed("shopifyName:" + shopifyName + "error:" + e.getMessage());
            e.printStackTrace();
            log.error("shopifyName:" + shopifyName + "error:", e);
        }
        return rs;
    }


    @RequestMapping("/getOrder/{shopifyName}")
    public CommonResult genOrderByByShopifyName(@PathVariable(value = "shopifyName") String shopifyName) {
        CommonResult rs = new CommonResult();
        Assert.notNull(shopifyName, "shopifyName is null");

        try {
            OrdersWraper orders = shopifyService.getOrders(shopifyName);
            if (orders != null && orders.getOrders() != null) {
                genShopifyOrderInfo(shopifyName, orders);
                rs.success(orders.getOrders().size());
            } else {
                rs.success(0);
            }

        } catch (Exception e) {
            rs.failed("shopifyName:" + shopifyName + ",error:" + e.getMessage());
            e.printStackTrace();
            log.error("shopifyName:" + shopifyName + ",error:", e);
        }
        return rs;
    }


    @RequestMapping("/getDetailsByOrderNo/{orderNo}")
    public CommonResult getDetailsByOrderNo(@PathVariable(value = "orderNo") Long orderNo) {
        CommonResult rs = new CommonResult();
        Assert.notNull(orderNo, "orderNo is null");

        Map<String, Object> rsMap = new HashMap<>();
        try {
            List<Line_items> line_itemsList = shopifyOrderService.queryOrderDetailsByOrderId(orderNo);
            Shipping_address shipping_address = shopifyOrderService.queryOrderAddressByOrderId(orderNo);

            rsMap.put("details", line_itemsList);
            rsMap.put("address", shipping_address);
            rs.success(rsMap);
        } catch (Exception e) {
            rs.failed("shopifyName:" + orderNo + ",error:" + e.getMessage());
            e.printStackTrace();
            log.error("shopifyName:" + orderNo + ",error:", e);
        }
        return rs;
    }


    private void genShopifyOrderInfo(String shopifyName, OrdersWraper orders) {
        List<Orders> shopifyOrderList = orders.getOrders();

        List<Orders> existList = shopifyOrderService.queryListByShopifyName(shopifyName);

        List<Orders> insertList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(existList)) {
            // 过滤已经存在的订单
            Map<Long,Orders> idSet = new HashMap<>(existList.size() * 2);
            existList.stream().forEach(e -> idSet.put(e.getId(),e));
            insertList = shopifyOrderList.stream().filter(e -> {
                if(idSet.containsKey(e.getId())){
                     Orders tempOrder = idSet.get(e.getId());
                     if(!tempOrder.getTotal_price_usd().equalsIgnoreCase(e.getTotal_price_usd())
                             || !tempOrder.getFinancial_status().equalsIgnoreCase(e.getFinancial_status())){
                         return true;
                     }
                     return false;
                }else{
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
                    shopifyOrderService.insertOrderInfoSingle(orderInfo);
                    if (CollectionUtils.isNotEmpty(orderInfo.getLine_items())) {
                        for (Line_items item : orderInfo.getLine_items()) {
                            item.setOrder_no(orderInfo.getId());
                            shopifyOrderService.insertOrderDetails(item);
                        }
                    }
                    if (orderInfo.getShipping_address() != null) {
                        orderInfo.getShipping_address().setOrder_no(orderInfo.getId());
                        shopifyOrderService.insertIntoOrderAddress(orderInfo.getShipping_address());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("genShopifyOrderInfo error:", e);
                }
            }
        }
        shopifyOrderList.clear();
    }

}
