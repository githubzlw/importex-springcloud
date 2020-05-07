package com.importexpress.ali1688.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.ali1688.service
 * @date:2020/5/6
 */
@Service
@Slf4j
public class AmazonCacheService {

    private static final String REDIS_KEYWORD_PRE = "amazon:keyword:";
    private static final String REDIS_PID_PRE = "amazon:pid:";
    private static final int REDIS_EXPIRE_DAYS = 7;
    private final StringRedisTemplate redisTemplate;

    public AmazonCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setItemInfo(String pid, JSONObject jsonObject) {
        Objects.requireNonNull(jsonObject);
        this.redisTemplate.opsForValue().set(REDIS_PID_PRE + pid,
                JSONObject.toJSONString(jsonObject), REDIS_EXPIRE_DAYS, TimeUnit.DAYS);
    }

    public void setItemInfoTime(String pid, JSONObject jsonObject, int expireTime) {
        Objects.requireNonNull(jsonObject);
        this.redisTemplate.opsForValue().set(REDIS_PID_PRE + pid,
                JSONObject.toJSONString(jsonObject), expireTime, TimeUnit.HOURS);
    }

    public JSONObject getItemInfo(String pid) {
        Objects.requireNonNull(pid);

        String value = this.redisTemplate.opsForValue().get(REDIS_PID_PRE + pid);
        if (StringUtils.isNotEmpty(value)) {
            return JSONObject.parseObject(value);
        } else {
            return null;
        }
    }
}
