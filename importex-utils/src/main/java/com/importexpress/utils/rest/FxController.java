package com.importexpress.utils.rest;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.utils.service.ExchangeRateServiceFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author jack.luo
 */
@RestController
@Slf4j
@Api(tags = "汇率查询接口")
public class FxController {

    public static final String REDIS_FX = "fx";

    private ExchangeRateServiceFactory serviceFactory;

    private StringRedisTemplate redisTemplate;

    @Autowired
    public FxController(ExchangeRateServiceFactory serviceFactory, StringRedisTemplate redisTemplate) {
        this.serviceFactory = serviceFactory;
        this.redisTemplate = redisTemplate;
    }


    @GetMapping("/fx")
    @ApiOperation("汇率查询")
    public Map<String, BigDecimal> fx() {

        //from cache read
        String fxJson = this.redisTemplate.opsForValue().get(REDIS_FX);
        if (fxJson != null) {
            return JSONObject.parseObject(fxJson, Map.class);
        } else {
            //from url read
            try {
                final Map<String, BigDecimal> exchangeRate = serviceFactory.getExchangeRate();
                this.redisTemplate.opsForValue().set(REDIS_FX, JSONObject.toJSONString(exchangeRate));
                return exchangeRate;
            } catch (IOException e) {
                log.error("do fx() get", e);
                return null;
            }
        }

    }

}
