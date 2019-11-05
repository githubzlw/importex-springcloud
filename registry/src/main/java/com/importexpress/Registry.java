package com.importexpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


/**
 * @author luohao
 */
@SpringBootApplication
@EnableEurekaServer
public class Registry {
    public static void main(String[] args) {
        SpringApplication.run(Registry.class);
    }
}
