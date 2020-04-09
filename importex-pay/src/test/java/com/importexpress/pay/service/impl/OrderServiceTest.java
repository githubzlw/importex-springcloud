package com.importexpress.pay.service.impl;


import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.pay.service.OrderService;
import com.importexpress.pay.service.enumc.ClientTypeEnum;
import com.importexpress.pay.service.enumc.TradeTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author jack.luo
 * @create 2020/4/8 17:10
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void generateOrderNumber() throws Exception {
        orderService.clearRedisCache();
        String result = orderService.generateOrderNumber(SiteEnum.IMPORTX, ClientTypeEnum.PC, TradeTypeEnum.NORMAL);
        System.out.println(result);
    }

    @Test
    public void generateOrderNumberForLarge() throws Exception {
        orderService.clearRedisCache();
        ExecutorService es = Executors.newFixedThreadPool(5);
        for(int i=0;i<30;i++){
            es.submit(() -> {
                String result = null;
                try {
                    result = orderService.generateOrderNumber(SiteEnum.IMPORTX, ClientTypeEnum.PC, TradeTypeEnum.NORMAL);
                    Assert.isTrue(!StringUtils.isEmpty(result));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(result);
            });
        }
        es.shutdown();
        es.awaitTermination(60, TimeUnit.SECONDS);

    }

}
