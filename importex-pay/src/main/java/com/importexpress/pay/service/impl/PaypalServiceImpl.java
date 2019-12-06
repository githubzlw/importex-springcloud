package com.importexpress.pay.service.impl;

import com.braintreepayments.http.HttpResponse;
import com.importexpress.pay.service.PaypalService;
import com.importexpress.pay.util.Config;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.payments.CapturesRefundRequest;
import com.paypal.payments.Money;
import com.paypal.payments.Refund;
import com.paypal.payments.RefundRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Slf4j
public class PaypalServiceImpl implements PaypalService {

    private final Config config;

    public PaypalServiceImpl(Config config) {
        this.config = config;
    }

    @Override
    public boolean refund(String captureId, Double amount) throws IOException {

        CapturesRefundRequest request = new CapturesRefundRequest(captureId);
        request.prefer("return=representation");

        Money money = new Money();
        money.currencyCode("USD");
        money.value(amount.toString());
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.amount(money);
        request.requestBody(refundRequest);

        HttpResponse<Refund> response = this.getPayPalHttpClient().execute(request);
        log.info("response: [{}]", response);
        if (response.statusCode() == 200) {
            //TODO 成功
            return true;
        } else {
            return false;
        }
    }

    private PayPalHttpClient getPayPalHttpClient() {

        PayPalEnvironment env;
        if (config.isPaypalSandbox) {
            env = new PayPalEnvironment.Sandbox(
                    config.PaypalClientId, config.PaypalClientSecret);
        } else {
            env = new PayPalEnvironment.Live(
                    config.PaypalClientId, config.PaypalClientSecret);
        }
        return new PayPalHttpClient(env);
    }

}