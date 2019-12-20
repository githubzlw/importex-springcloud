package com.importexpress.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author jack.luo
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class EmailApp {

    public static void main(String[] args) {

        SpringApplication.run(EmailApp.class, args);
    }
}
