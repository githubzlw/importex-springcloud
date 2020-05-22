package com.importexpress.serialport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author jack.luo
 */
@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
public class SerialPortApp {

    public static void main(String[] args) {

        SpringApplication.run(SerialPortApp.class, args);
    }
}
