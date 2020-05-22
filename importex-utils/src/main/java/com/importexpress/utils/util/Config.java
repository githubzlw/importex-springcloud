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

    @Value("${baidu.api.url.token}")
    public String BAIDU_API_URL_TOKEN;

    @Value("${baidu.api.client_id}")
    public String BAIDU_API_CLIENT_ID;

    @Value("${baidu.api.client_secret}")
    public String BAIDU_API_CLIENT_SECRET;

    @Value("${baidu.api.url.object.detect}")
    public String BAIDU_API_URL_OBJECT_DETECT;

    @Value("${shell.path}")
    public String SHELL_PATH;
}