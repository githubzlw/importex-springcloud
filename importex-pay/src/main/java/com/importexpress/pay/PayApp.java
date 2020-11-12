package com.importexpress.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author jack.luo
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableSwagger2
public class PayApp {

    public static void main(String[] args)  {

        SpringApplication.run(PayApp.class, args);
    }
}
