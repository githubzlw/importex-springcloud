package com.importexpress.message.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${MESSAGE_PRE}")
    public String MESSAGE_PRE;

}