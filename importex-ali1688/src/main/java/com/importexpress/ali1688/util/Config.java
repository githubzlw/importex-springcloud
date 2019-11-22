package com.importexpress.ali1688.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${ali1688api.API_HOST}")
    public String API_HOST;


    @Value("${ali1688api.API_KEY}")
    public String API_KEY;

    @Value("${ali1688api.API_SECRET}")
    public String API_SECRET;

}