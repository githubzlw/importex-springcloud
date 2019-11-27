package com.importexpress.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.user
 * @date:2019/11/27
 */
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@SpringBootApplication
public class UserApp {

    public static void main(String[] args) {

        SpringApplication.run(UserApp.class, args);
    }
}
