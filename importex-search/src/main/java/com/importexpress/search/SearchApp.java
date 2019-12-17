package com.importexpress.search;

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
public class SearchApp {

    public static void main(String[] args) {

        SpringApplication.run(SearchApp.class, args);
    }
}
