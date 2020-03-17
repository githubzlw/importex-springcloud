package com.importexpress.ali1688.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigExpress {

    @Value("${aliexpressapi.API_KEY}")
    public String API_KEY;

    @Value("${aliexpressapi.API_SECRET}")
    public String API_SECRET;

}