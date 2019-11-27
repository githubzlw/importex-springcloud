package com.importexpress.shopify.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${SHOPIFY.CLIENT_ID}")
    public String SHOPIFY_CLIENT_ID;

    @Value("${SHOPIFY.CLIENT_SECRET}")
    public String SHOPIFY_CLIENT_SECRET;

    @Value("${SHOPIFY.SCOPE}")
    public String SHOPIFY_SCOPE;

    @Value("${SHOPIFY.REDIRECT_URI}")
    public String SHOPIFY_REDIRECT_URI;

    @Value("${SHOPIFY.URI_PRODUCTS}")
    public String SHOPIFY_URI_PRODUCTS;

    @Value("${SHOPIFY.URI_ORDERS}")
    public String SHOPIFY_URI_ORDERS;

}