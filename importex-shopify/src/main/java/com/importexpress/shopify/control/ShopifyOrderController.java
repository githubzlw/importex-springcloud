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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.control
 * @date:2019/11/28
 */
@Slf4j
@RestController
@RequestMapping("/shopify")
public class ShopifyOrderController {

    @Autowired
    private ShopifyService shopifyService;

    @Autowired
    private ShopifyOrderService shopifyOrderService;


    @GetMapping("/{shopifyName}")
    public CommonResult getShopifyOrderByShopifyName(
            @PathVariable(value = "shopifyName") String shopifyName, @RequestParam(value = "pageNum", required = false, defaultValue = "1") Long pageNum, @RequestParam(value = "limitNum", required = false, defaultValue = "10") Long limitNum) {
        CommonResult rs = new CommonResult();

        try {

            List<Orders> ordersList = shopifyOrderService.queryListByShopifyName(shopifyName);
            /*List<Orders> rsList = new ArrayList<>();
            暂不分页
            if (CollectionUtils.isNotEmpty(ordersList)) {
                rsList = ordersList.stream().skip((pageNum - 1) * limitNum).limit(limitNum).collect(Collectors.toList());
            }
            rs.success(rsList);
            */
            rs.success(ordersList);
        } catch (Exception e) {
            rs.failed("shopifyName:" + shopifyName + "error:" + e.getMessage());
            e.printStackTrace();
            log.error("shopifyName:" + shopifyName + "error:", e);
        }
        return rs;
    }


    @RequestMapping("/{shopifyName}/orders")
    public CommonResult genOrderByByShopifyName(@PathVariable(value = "shopifyName") String shopifyName) {
        CommonResult rs = new CommonResult();
        Assert.notNull(shopifyName, "shopifyName is null");

        try {
            OrdersWraper orders = shopifyService.getOrders(shopifyName);
            if (orders != null && orders.getOrders() != null) {
                shopifyOrderService.genShopifyOrderInfo(shopifyName, orders);
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


    @RequestMapping("/{shopifyName}/orders/{orderNo}")
    public CommonResult getDetailsByOrderNo(@PathVariable(value = "shopifyName") String shopifyName,
                                            @PathVariable(value = "orderNo") Long orderNo) {
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
            rs.failed("shopifyName:" + shopifyName + ",orderNo:" + ",error:" + orderNo + "," + e.getMessage());
            e.printStackTrace();
            log.error("shopifyName:" + shopifyName + ",orderNo:" + ",error:", e);
        }
        return rs;
    }

}
