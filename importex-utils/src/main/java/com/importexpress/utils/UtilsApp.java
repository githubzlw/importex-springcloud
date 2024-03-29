package com.importexpress.utils;

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
public class UtilsApp {

    public static void main(String[] args) {

        SpringApplication.run(UtilsApp.class, args);
    }
}
