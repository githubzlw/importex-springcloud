package com.importexpress.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author jack.luo
 * @date 2019/12/16
 */
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@SpringBootApplication
public class CartApp {

    public static void main(String[] args) {

        SpringApplication.run(CartApp.class, args);
    }
}
