package com.importexpress.cart.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${CART_PRE}")
    public String CART_PRE;

    @Value("${SAVE_CART_PATH}")
    public String SAVE_CART_PATH;

    @Value("${SFTP_REMOTEHOST}")
    public String SFTP_REMOTEHOST;

    @Value("${SFTP_USERNAME}")
    public String SFTP_USERNAME;

    @Value("${SFTP_PASSWORD}")
    public String SFTP_PASSWORD;

    @Value("${SFTP_SAVE_CART_PATH}")
    public String SFTP_SAVE_CART_PATH;

}