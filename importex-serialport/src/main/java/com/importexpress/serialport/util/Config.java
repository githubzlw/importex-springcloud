package com.importexpress.serialport.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {


    @Value("${SERIAL.PORT}")
    public String SERIAL_PORT;

    @Value("${MOVE_TO_CART_POSI}")
    public String MOVE_TO_CART_POSI;

}