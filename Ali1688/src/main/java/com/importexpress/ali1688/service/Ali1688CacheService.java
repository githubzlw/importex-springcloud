package com.importexpress.ali1688.service;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.common.pojo.Ali1688Item;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author luohao
 * @date 2019/11/6
 */
@Service
public class Ali1688CacheService {

    private StringRedisTemplate redisTemplate;

    private static final String REDIS_PID_PRE = "ali:pid:";

    private static final String REDIS_SHOP_PRE = "ali:shopid:";

    private static final int REDIS_EXPIRE_DAYS = 7;

    @Autowired
    public Ali1688CacheService(StringRedisTemplate redisTemplate){

        this.redisTemplate =redisTemplate;
    }

    public JSONObject getItem(Long pid){
        String value = this.redisTemplate.opsForValue().get(REDIS_PID_PRE+pid);
        if(StringUtils.isNotEmpty(value)){
            return JSONObject.parseObject(value);
        }else{
            return null;
        }
    }

    public void setItem(Long pid,JSONObject value){
        this.redisTemplate.opsForValue().set(REDIS_PID_PRE+pid,JSONObject.toJSONString(value),REDIS_EXPIRE_DAYS, TimeUnit.DAYS);
    }

    public List<Ali1688Item> getShop(String shopId){
        String value = this.redisTemplate.opsForValue().get(REDIS_SHOP_PRE+shopId);
        if(StringUtils.isNotEmpty(value)){
            Ali1688Item[] arr=JSONObject.parseObject(value, Ali1688Item[].class);
            return Arrays.asList(arr);
        }else{
            return null;
        }
    }

    public void setShop(String shopId, List<Ali1688Item> value){
        this.redisTemplate.opsForValue().set(REDIS_SHOP_PRE+shopId,JSONObject.toJSONString(value),REDIS_EXPIRE_DAYS, TimeUnit.DAYS);
    }


    public int processNotExistItemInCache(boolean isClear){
        int count=0;
        Set<String> keys = this.redisTemplate.keys(REDIS_PID_PRE+"*");
        if(keys != null) {
            for (String key : keys) {
                String value = this.redisTemplate.opsForValue().get(key);
                JSONObject jsonObject = JSONObject.parseObject(value);
                if (StringUtils.isNotEmpty(jsonObject.getString("reason"))){
                    if(isClear){
                        if (this.redisTemplate.delete(key)) {
                            count++;
                        }
                    }else{
                        count++;
                    }

                }
            }
        }
        return count;
    }



}
