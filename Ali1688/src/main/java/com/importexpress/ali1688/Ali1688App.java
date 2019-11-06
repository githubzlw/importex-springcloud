package com.importexpress.ali1688;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author luohao
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class Ali1688App {

    public static void main(String[] args) {

        SpringApplication.run(Ali1688App.class, args);
    }
}
