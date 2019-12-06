package com.importexpress.pay.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${PAYPAL.SANDBOX}")
    public boolean isPaypalSandbox;

    @Value("${PAYPAL.MODE}")
    public String paypalMode;


    @Value("${PAYPAL.CLIENT_ID}")
    public String PaypalClientId;

    @Value("${PAYPAL.CLIENT_SECRET}")
    public String PaypalClientSecret;

    @Value("${rabbitmq.host}")
    public String rabbitmqHost;

    @Value("${rabbitmq.port}")
    public int rabbitmqPort;

    @Value("${rabbitmq.username}")
    public String rabbitmqUser;

    @Value("${rabbitmq.password}")
    public String rabbitmqPass;

    @Value("${rabbitmq.rpc.qname}")
    public String qnameRpc;


}