package com.importexpress.utils.scheduled;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.utils.rest.FxController;
import com.importexpress.utils.service.ExchangeRateServiceFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;


/**
 * @author luohao
 * @date 2019/11/18
 */
@Component
public class ScheduledTasks {

    private final StringRedisTemplate redisTemplate;

    private final ExchangeRateServiceFactory serviceFactory;

    public ScheduledTasks(StringRedisTemplate redisTemplate, ExchangeRateServiceFactory serviceFactory) {
        this.redisTemplate = redisTemplate;
        this.serviceFactory = serviceFactory;
    }

    /**
     * 每3时刷新一次redis缓存
     * @throws IOException
     */
    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void getExchangeRate() throws IOException {
        final Map<String, BigDecimal> exchangeRate = serviceFactory.getExchangeRate();
        this.redisTemplate.opsForValue().set(FxController.REDIS_FX, JSONObject.toJSONString(exchangeRate));
    }

}