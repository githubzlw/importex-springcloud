package com.importexpress.pay.service;


import com.google.gson.Gson;
import com.importexpress.comm.domain.CommonResult;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaypalServiceTest {

    @Autowired
    private PayPalService paypalService;

    @Test
    public void createPayment() throws PayPalRESTException {
        Payment payment = paypalService.createPayment(100.05, "http://localhost/cancel", "http://localhost/success", "ORD20191206001", "msg1");
        System.out.println(payment);
    }

    @Test
    public void refundFault() throws IOException {
        CommonResult refund = paypalService.refund("test", 1.0);
        Assert.assertEquals(500,refund.getCode());
    }

    @Test
    public void refundSuccess1() throws IOException {
        CommonResult refund = paypalService.refund("74Y59251KF272460A", 1.0);
        Assert.assertEquals(200,refund.getCode());
    }

    @Test
    public void refundSuccess2() throws IOException {
        CommonResult refund = paypalService.refund("0G915709VH258105T", 1.0);
        Assert.assertEquals(200,refund.getCode());
    }

    @Test
    public void parseJsonStr(){
        String json = "{\"id\":\"PAYID-L2HOTYI5BX72629HN597014A\",\"intent\":\"sale\",\"payer\":{\"paymentMethod\":\"paypal\",\"status\":\"UNVERIFIED\",\"payerInfo\":{\"email\":\"luohao518-buyer-1@yeah.net\",\"firstName\":\"luo\",\"lastName\":\"hao\",\"payerId\":\"7P3NMPRZKEFGN\",\"countryCode\":\"US\",\"shippingAddress\":{\"recipientName\":\"luo hao\",\"line1\":\"1 Main St\",\"city\":\"San Jose\",\"countryCode\":\"US\",\"postalCode\":\"95131\",\"state\":\"CA\"}}},\"cart\":\"01A3972214797990N\",\"transactions\":[{\"relatedResources\":[{\"sale\":{\"id\":\"26T76732XC0092236\",\"amount\":{\"currency\":\"USD\",\"total\":\"1.00\",\"details\":{\"subtotal\":\"1.00\",\"shipping\":\"0.00\",\"tax\":\"0.00\",\"handlingFee\":\"0.00\",\"shippingDiscount\":\"0.00\",\"insurance\":\"0.00\"}},\"paymentMode\":\"INSTANT_TRANSFER\",\"state\":\"completed\",\"protectionEligibility\":\"ELIGIBLE\",\"protectionEligibilityType\":\"ITEM_NOT_RECEIVED_ELIGIBLE,UNAUTHORIZED_PAYMENT_ELIGIBLE\",\"transactionFee\":{\"currency\":\"USD\",\"value\":\"0.33\"},\"parentPayment\":\"PAYID-L2HOTYI5BX72629HN597014A\",\"createTime\":\"2020-04-09T09:25:35Z\",\"updateTime\":\"2020-04-09T09:25:35Z\",\"links\":[{\"href\":\"https://api.sandbox.paypal.com/v1/payments/sale/26T76732XC0092236\",\"rel\":\"self\",\"method\":\"GET\"},{\"href\":\"https://api.sandbox.paypal.com/v1/payments/sale/26T76732XC0092236/refund\",\"rel\":\"refund\",\"method\":\"POST\"},{\"href\":\"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-L2HOTYI5BX72629HN597014A\",\"rel\":\"parent_payment\",\"method\":\"GET\"}]}}],\"amount\":{\"currency\":\"USD\",\"total\":\"1.00\",\"details\":{\"subtotal\":\"1.00\",\"shipping\":\"0.00\",\"tax\":\"0.00\",\"handlingFee\":\"0.00\",\"shippingDiscount\":\"0.00\",\"insurance\":\"0.00\"}},\"payee\":{\"email\":\"contact-facilitator@china-synergy.org\",\"merchantId\":\"KB5KKMFF7M2MJ\"},\"description\":\"1234\",\"itemList\":{\"items\":[{\"name\":\"1234\",\"quantity\":\"1\",\"price\":\"1.00\",\"currency\":\"USD\",\"tax\":\"0.00\"}],\"shippingAddress\":{\"recipientName\":\"luo hao\",\"line1\":\"1 Main St\",\"city\":\"San Jose\",\"countryCode\":\"US\",\"postalCode\":\"95131\",\"state\":\"CA\"}}}],\"failedTransactions\":[],\"state\":\"approved\",\"createTime\":\"2020-04-09T09:24:49Z\",\"updateTime\":\"2020-04-09T09:25:35Z\",\"links\":[{\"href\":\"https://api.sandbox.paypal.com/v1/payments/payment/PAYID-L2HOTYI5BX72629HN597014A\",\"rel\":\"self\",\"method\":\"GET\"}]}";

        Gson g = new Gson();
        Payment payment = g.fromJson(json, Payment.class);
        System.out.println(payment);
    }
}