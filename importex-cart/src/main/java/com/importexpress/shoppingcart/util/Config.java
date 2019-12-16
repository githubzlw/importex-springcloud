package com.importexpress.shoppingcart.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${CART_PRE}")
    public String CART_PRE;

}