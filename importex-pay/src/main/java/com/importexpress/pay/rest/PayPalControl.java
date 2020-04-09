package com.importexpress.pay.rest;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.pay.service.PayPalService;
import com.importexpress.pay.service.enumc.PayPalPaymentIntentEnum;
import com.importexpress.pay.service.enumc.PayPalPaymentMethodEnum;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author jack.luo
 * @create 2020/4/7 18:07
 * Description
 */
@RestController
@Slf4j
@Api("PayPal支付接口")
@RequestMapping("/paypal")
public class PayPalControl {

    private PayPalService payPalService;

    @Autowired
    public PayPalControl(PayPalService payPalService) {

        this.payPalService = payPalService;
    }

    @PostMapping("/{site}/create")
    @ApiOperation("支付创建")
    public CommonResult createPayment(@PathVariable(value = "site") SiteEnum site,
                                      @RequestParam Double total,@RequestParam String orderNo,@RequestParam(value = "customMsg", required = false, defaultValue = "") String customMsg,@RequestParam(value = "cancelUrlType", defaultValue = "0") int cancelUrlType,
   @RequestParam(value = "successUrl", required = false, defaultValue = "") String successUrl,
   @RequestParam(value = "cancelUrl", required = false, defaultValue = "") String cancelUrl) {


        String strApprovalUrl=null;
        try {
            if(StringUtils.isEmpty(successUrl)){
                successUrl = site.getUrl() + "/doPayment/pay";
            }
            if(StringUtils.isEmpty(cancelUrl)){
                if(cancelUrlType==1){
                    //返回购物车
                    cancelUrl = site.getUrl() + "/Goods/getShopCar?from=pay";
                }else{
                    //返回个人中心
                    cancelUrl = site.getUrl() + "/myaccount";
                }
            }

            Payment payment = payPalService.createPayment(total,
                    "USD",
                    PayPalPaymentMethodEnum.paypal,
                    PayPalPaymentIntentEnum.sale,
                    "",
                    cancelUrl,
                    successUrl,
                    orderNo, customMsg);
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    strApprovalUrl=links.getHref();
                    break;
                }
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
        return CommonResult.success(strApprovalUrl);
    }

    @PostMapping("/{site}/execute")
    @ApiOperation("支付执行")
    public CommonResult execute(@PathVariable(value = "site") SiteEnum site,@RequestParam String paymentId,@RequestParam String payerId) {

        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                return CommonResult.success(payment.toJSON());
            } else {
                return CommonResult.failed(payment.getState());
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage(), e);
            return CommonResult.failed(e.getMessage());
        }
    }
}
