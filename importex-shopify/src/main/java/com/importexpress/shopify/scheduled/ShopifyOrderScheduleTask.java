package com.importexpress.shopify.scheduled;

import com.google.common.collect.ImmutableList;
import com.importexpress.shopify.pojo.orders.OrdersWraper;
import com.importexpress.shopify.service.ShopifyOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Configuration
@EnableScheduling
public class ShopifyOrderScheduleTask {

    private final ShopifyOrderService shopifyOrderService;

    public ShopifyOrderScheduleTask(ShopifyOrderService shopifyOrderService) {
        this.shopifyOrderService = shopifyOrderService;
    }


    @Scheduled(cron = "0 0 0/6 * * ?")
    public void getOrdersByShopifyNameTask() {
        // 获取订单列表
        ImmutableList<String> shopifyNameList = ImmutableList.copyOf(shopifyOrderService.queryShopifyNameFromUser());
            for (String shopifyName : shopifyNameList) {
                log.info("shopifyName:" + shopifyName + ",getOrdersByShopifyNameTask begin get orders");
                try {
                    // 获取订单
                    OrdersWraper orders = shopifyOrderService.getOrders(shopifyName);
                    if (orders != null) {
                        // 执行插入数据
                        shopifyOrderService.genShopifyOrderInfo(shopifyName, orders);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("shopifyName:" + shopifyName + ",getOrdersByShopifyNameTask error:", e);
                }
            }
    }

}