package com.importexpress.ali1688.service;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.ali1688.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.aliexpress.service
 * @date:2020/3/16
 */
@Service
@Slf4j
public class AliExpressCacheService {
    private static final String REDIS_KEYWORD_PRE = "ali:keyword:";
    private static final int REDIS_EXPIRE_DAYS = 7;
    private final StringRedisTemplate redisTemplate;

    public AliExpressCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public JSONObject getItemByKeyword(Integer page, String keyword) {
        Objects.requireNonNull(page);
        Objects.requireNonNull(keyword);

        String value = this.redisTemplate.opsForValue().get(REDIS_KEYWORD_PRE
                + StringUtil.checkAndChangeSpace(keyword, "_") + "_" + page);
        if (StringUtils.isNotEmpty(value)) {
            return JSONObject.parseObject(value);
        } else {
            return null;
        }
    }

    public void saveItemByKeyword(Integer page, String keyword, JSONObject jsonObject) {
        Objects.requireNonNull(page);
        Objects.requireNonNull(keyword);
        Objects.requireNonNull(jsonObject);
        this.redisTemplate.opsForValue().set(REDIS_KEYWORD_PRE + StringUtil.checkAndChangeSpace(keyword, "_") + "_" + page,
                JSONObject.toJSONString(jsonObject), REDIS_EXPIRE_DAYS, TimeUnit.DAYS);
    }


    public Boolean deleteKeyword(Integer page, String keyword) {
        Objects.requireNonNull(page);
        Objects.requireNonNull(keyword);
        return this.redisTemplate.delete(REDIS_KEYWORD_PRE + StringUtil.checkAndChangeSpace(keyword, "_") + "_" + page);
    }


}
