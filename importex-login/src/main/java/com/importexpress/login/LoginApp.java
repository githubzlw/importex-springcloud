package com.importexpress.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author jack.luo
 * @create 2020/5/29 10:58
 * Description
 */
@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
public class LoginApp {

    public static void main(String[] args) {

        SpringApplication.run(LoginApp.class, args);
    }
}
