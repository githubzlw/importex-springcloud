package com.importexpress.serialport.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {


    @Value("${SERIAL.PORT}")
    public String SERIAL_PORT;

    @Value("${SERIAL.PORT2}")
    public String SERIAL_PORT2;

    @Value("${MOVE_TO_CART_MAGNET_POSI}")
    public String MOVE_TO_CART_MAGNET_POSI;

    @Value("${MOVE_TO_CART_MAGOFF_POSI}")
    public String MOVE_TO_CART_MAGOFF_POSI;

    @Value("${CART_X}")
    public int CART_X;

    @Value("${CART_Y}")
    public int CART_Y;

    @Value("${CART_Z}")
    public int CART_Z;

    @Value("${MAX_VALUE_X}")
    public int MAX_VALUE_X;

    @Value("${MAX_VALUE_Y}")
    public int MAX_VALUE_Y;

    @Value("${MAX_VALUE_Z}")
    public int MAX_VALUE_Z;

    @Value("${STEP_VALUE}")
    public int STEP_VALUE;

    @Value("${RETURN_VALUE_X}")
    public int RETURN_VALUE_X;

    @Value("${RETURN_VALUE_Y}")
    public int RETURN_VALUE_Y;

    @Value("${RETURN_STEP_VALUE}")
    public int RETURN_STEP_VALUE;

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

//    @Value("${baidu.api.url.token}")
//    public String BAIDU_API_URL_TOKEN;
//
//    @Value("${baidu.api.client_id}")
//    public String BAIDU_API_CLIENT_ID;
//
//    @Value("${baidu.api.client_secret}")
//    public String BAIDU_API_CLIENT_SECRET;
//
//    @Value("${baidu.api.url.object.detect}")
//    public String BAIDU_API_URL_OBJECT_DETECT;

    @Value("${shell.path}")
    public String SHELL_PATH;

    @Value("${SAVE_FINDER_PATH}")
    public String SAVE_FINDER_PATH;

}