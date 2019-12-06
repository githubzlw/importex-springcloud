package com.importexpress.pay.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${PAYPAL.SANDBOX}")
    public boolean isPaypalSandbox;

    @Value("${PAYPAL.CLIENT_ID}")
    public String PaypalClientId;

    @Value("${PAYPAL.CLIENT_SECRET}")
    public String PaypalClientSecret;

}