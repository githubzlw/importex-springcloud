package com.importexpress.pay.rest;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.pay.service.OrderService;
import com.importexpress.pay.service.enumc.ClientTypeEnum;
import com.importexpress.pay.service.enumc.TradeTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author jack.luo
 * @create 2020/4/8 12:03
 * Description
 */
@RestController
@Slf4j
@Api("订单接口")
@RequestMapping("/order")
public class OrderControl {

    private OrderService orderNoGenerator;

    @Autowired
    public OrderControl(OrderService orderNoGenerator) {

        this.orderNoGenerator = orderNoGenerator;
    }

    @GetMapping("/{site}/create")
    @ApiOperation("支付创建")
    public CommonResult createPayment(@PathVariable(value = "site") SiteEnum site,
                                      @RequestParam ClientTypeEnum clientTypeEnum, @RequestParam TradeTypeEnum tradeTypeEnum) {

        try {
            String orderNumber = orderNoGenerator.generateOrderNumber(site,clientTypeEnum, tradeTypeEnum);
            return CommonResult.success(orderNumber);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return CommonResult.failed(e.getMessage());
        }
    }
}
