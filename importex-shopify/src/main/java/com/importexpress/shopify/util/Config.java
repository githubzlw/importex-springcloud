package com.importexpress.shopify.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${shopify.client_id}")
    public String SHOPIFY_CLIENT_ID;

    @Value("${shopify.client_secret}")
    public String SHOPIFY_CLIENT_SECRET;

    @Value("${shopify.scope}")
    public String SHOPIFY_SCOPE;

    @Value("${shopify.redirect_uri}")
    public String SHOPIFY_REDIRECT_URI;

    @Value("${shopify.uri_products}")
    public String SHOPIFY_URI_PRODUCTS;

    @Value("${shopify.uri_orders}")
    public String SHOPIFY_URI_ORDERS;

}