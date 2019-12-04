package com.importexpress.shopify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.aop.config
 * @date:2019/12/4
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {


    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor getThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int poolSize = Runtime.getRuntime().availableProcessors();
        // 设置核心线程数
        executor.setCorePoolSize(poolSize);
        // 设置最大线程数
        executor.setMaxPoolSize(poolSize * 3);
        // 设置队列容量
        executor.setQueueCapacity(20000);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(120);
        // 设置默认线程名称
        executor.setThreadNamePrefix("get shopify order");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
