package com.importexpress.utils.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${yingshi.api.url.token}")
    public String YINGSHI_API_URL_TOKEN;

    @Value("${yingshi.api.appKey}")
    public String YINGSHI_API_APPKEY;

    @Value("${yingshi.api.appSecret}")
    public String YINGSHI_API_APPSECRET;

    @Value("${yingshi.api.url.capture}")
    public String YINGSHI_API_URL_CAPTURE;

    @Value("${yingshi.api.deviceSerial}")
    public String YINGSHI_API_DEVICESERIAL;

}