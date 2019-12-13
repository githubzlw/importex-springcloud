package com.importexpress.pay.service;


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
    private PaypalService paypalService;

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
}