package com.importexpress.shopify.control;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.shopify.pojo.orders.Line_items;
import com.importexpress.shopify.pojo.orders.Orders;
import com.importexpress.shopify.pojo.orders.OrdersWraper;
import com.importexpress.shopify.pojo.orders.Shipping_address;
import com.importexpress.shopify.service.ShopifyOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.control
 * @date:2019/11/28
 */
@Slf4j
@RestController
@RequestMapping("/api/shopify")
@Api(tags = "shopify订单调用接口")
public class ShopifyOrderController {


    private final ShopifyOrderService shopifyOrderService;

    public ShopifyOrderController(ShopifyOrderService shopifyOrderService) {
        this.shopifyOrderService = shopifyOrderService;
    }


    @GetMapping("/{shopifyName}")
    @ApiOperation("该店铺下订单一览")
    public JSONObject getShopifyOrderListByShopifyName(
            @ApiParam(name = "shopifyName", value = "shopify店铺名", required = true)
            @PathVariable(value = "shopifyName") String shopifyName) {
        JSONObject jsonObject = new JSONObject();

        try {

            List<Orders> ordersList = shopifyOrderService.queryListByShopifyName(shopifyName);
            jsonObject.put("total", ordersList.size());
            jsonObject.put("rows", ordersList);
            jsonObject.put("code", 200);
        } catch (Exception e) {
            log.error("shopifyName:" + shopifyName + "error:", e);
            jsonObject.put("message", "shopifyName:" + shopifyName + "error:" + e.getMessage());
            jsonObject.put("code", 500);
            jsonObject.put("total", 0);
            jsonObject.put("rows", new ArrayList<>());
        }
        return jsonObject;
    }


    @GetMapping("/{shopifyName}/orders")
    @ApiOperation("根据shopifyName抓取店铺订单")
    public CommonResult genShopifyNameOrders(@ApiParam(name = "shopifyName", value = "shopify店铺名", required = true)
                                             @PathVariable(value = "shopifyName") String shopifyName) {

        Assert.notNull(shopifyName, "shopifyName is null");

        try {
            OrdersWraper orders = shopifyOrderService.getOrders(shopifyName);
            if (orders != null && orders.getOrders() != null) {
                int total = orders.getOrders().size();
                shopifyOrderService.genShopifyOrderInfo(shopifyName, orders);
                return CommonResult.success(total);
            } else {
                return CommonResult.success(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("shopifyName:" + shopifyName + ",error:", e);
            return CommonResult.failed("shopifyName:" + shopifyName + ",error:" + e.getMessage());
        }
    }


    @GetMapping("/{shopifyName}/orders/{orderNo}")
    @ApiOperation("根据订单号获取shopify店铺名下的订单详情信息")
    public CommonResult getDetailsByOrderNo(
            @ApiParam(name = "shopifyName", value = "shopify店铺名", required = true)
            @PathVariable(value = "shopifyName") String shopifyName,
            @ApiParam(name = "orderNo", value = "订单号", required = true)
            @PathVariable(value = "orderNo") Long orderNo) {
        Assert.notNull(orderNo, "orderNo is null");

        Map<String, Object> rsMap = new HashMap<>();
        try {
            List<Line_items> line_itemsList = shopifyOrderService.queryOrderDetailsByOrderId(orderNo);
            Shipping_address shipping_address = shopifyOrderService.queryOrderAddressByOrderId(orderNo);

            rsMap.put("details", line_itemsList);
            rsMap.put("address", shipping_address);
            return CommonResult.success(rsMap);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("shopifyName:" + shopifyName + ",orderNo:" + ",error:", e);
            return CommonResult.failed("shopifyName:" + shopifyName + ",orderNo:" + ",error:" + orderNo + "," + e.getMessage());
        }
    }

}
