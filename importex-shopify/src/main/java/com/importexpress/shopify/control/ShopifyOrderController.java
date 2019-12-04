package com.importexpress.shopify.control;

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
import org.springframework.web.bind.annotation.*;

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
    @ApiOperation("店铺一览")
    public CommonResult getShopifyOrderByShopifyName(
            @ApiParam(name="shopifyName",value="shopify店铺名",required=true) @PathVariable(value = "shopifyName") String shopifyName) {
        try {

            List<Orders> ordersList = shopifyOrderService.queryListByShopifyName(shopifyName);
            return CommonResult.success(ordersList);
        } catch (Exception e) {
            log.error("shopifyName:" + shopifyName + "error:", e);
            return CommonResult.failed("shopifyName:" + shopifyName + "error:" + e.getMessage());
        }
    }


    @GetMapping("/{shopifyName}/orders")
    public CommonResult genOrderByByShopifyName(@PathVariable(value = "shopifyName") String shopifyName) {

        Assert.notNull(shopifyName, "shopifyName is null");

        try {
            OrdersWraper orders = shopifyOrderService.getOrders(shopifyName);
            if (orders != null && orders.getOrders() != null) {
                shopifyOrderService.genShopifyOrderInfo(shopifyName, orders);
                return CommonResult.success(orders.getOrders().size());
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
    public CommonResult getDetailsByOrderNo(@PathVariable(value = "shopifyName") String shopifyName,
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
