package com.importexpress.ali1688.service;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.pojo.Ali1688Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @author jack.luo
 * @date 2019/11/6
 */
@Service
@Slf4j
public class Ali1688CacheService {

    private static final String REDIS_PID_PRE = "ali:pid:";
    private static final String REDIS_SHOP_PRE = "ali:shopid:";
    private static final int REDIS_EXPIRE_DAYS = 7;
    private StringRedisTemplate redisTemplate;

    @Autowired
    public Ali1688CacheService(StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public JSONObject getItem(Long pid) {
        Objects.requireNonNull(pid);
        String value = this.redisTemplate.opsForValue().get(REDIS_PID_PRE + pid);
        if (StringUtils.isNotEmpty(value)) {
            return JSONObject.parseObject(value);
        } else {
            return null;
        }
    }

    public void saveItemIntoRedis(Long pid, JSONObject value) {
        Objects.requireNonNull(pid);
        Objects.requireNonNull(value);
        this.redisTemplate.opsForValue().set(REDIS_PID_PRE + pid, JSONObject.toJSONString(value), REDIS_EXPIRE_DAYS, TimeUnit.DAYS);
    }

    public List<Ali1688Item> getShop(String shopId) {
        Objects.requireNonNull(shopId);
        String value = this.redisTemplate.opsForValue().get(REDIS_SHOP_PRE + shopId);
        if (StringUtils.isNotEmpty(value)) {
            Ali1688Item[] arr = JSONObject.parseObject(value, Ali1688Item[].class);
            return Arrays.asList(arr);
        } else {
            return null;
        }
    }

    public void setShop(String shopId, List<Ali1688Item> value) {
        Objects.requireNonNull(shopId);
        Objects.requireNonNull(value);
        this.redisTemplate.opsForValue().set(REDIS_SHOP_PRE + shopId, JSONObject.toJSONString(value), REDIS_EXPIRE_DAYS, TimeUnit.DAYS);
    }


    public int processNotExistItemInCache(boolean isClear) {
        int count = 0;
        Set<String> keys = this.redisTemplate.keys(REDIS_PID_PRE + "*");
        if (keys != null) {
            for (String key : keys) {
                String value = this.redisTemplate.opsForValue().get(key);
                JSONObject jsonObject = JSONObject.parseObject(value);
                if (StringUtils.isNotEmpty(jsonObject.getString("reason"))) {
                    log.warn("not data pid:[{}],reason:[{}]", key, jsonObject.getString("reason"));
                    if (isClear) {
                        if (this.redisTemplate.delete(key)) {
                            count++;
                        }
                    } else {
                        count++;
                    }

                }
            }
        }
        return count;
    }

    /**
     * 清除所有pid的缓存
     *
     * @return
     */
    public int clearAllPidInCache() {
        int count = 0;
        Set<String> keys = this.redisTemplate.keys(REDIS_PID_PRE + "*");
        if (keys != null) {
            for (String key : keys) {
                this.redisTemplate.delete(key);
                count++;
            }
        }
        return count;
    }

    /**
     * 清除所有店铺的缓存
     *
     * @return
     */
    public int clearAllShopInCache() {
        int count = 0;
        Set<String> keys = this.redisTemplate.keys(REDIS_SHOP_PRE + "*");
        if (keys != null) {
            for (String key : keys) {
                this.redisTemplate.delete(key);
                count++;
            }
        }
        return count;
    }

    /**
     * 重新设置key的过期时间
     *
     * @param days
     * @return
     */
    public void setItemsExpire(int days) {

        //商品过期时间设置
        Set<String> keys = this.redisTemplate.keys(REDIS_PID_PRE + "*");
        if (keys != null) {
            for (String key : keys) {
                this.redisTemplate.expire(key, days, TimeUnit.DAYS);
            }
        }

        //店铺过期时间设置
        keys = this.redisTemplate.keys(REDIS_SHOP_PRE + "*");
        if (keys != null) {
            for (String key : keys) {
                this.redisTemplate.expire(key, days, TimeUnit.DAYS);
            }
        }
    }

    /**
     * desc属性为空的商品数量
     *
     * @return
     */
    public Pair<List<String>, List<String>> checkDescInAllPids(boolean isClear) {

        final List<String> lstCountAll = new CopyOnWriteArrayList<>();
        final List<String> lstCountDesc = new CopyOnWriteArrayList<>();

        Set<String> keys = this.redisTemplate.keys(REDIS_PID_PRE + "*");
        if (keys != null) {
            keys.stream().parallel().forEach(key -> {

                String value = this.redisTemplate.opsForValue().get(key);
                JSONObject jsonObject = JSONObject.parseObject(value);
                JSONObject item = jsonObject.getJSONObject("item");
                if (item != null) {
                    String strDesc = item.getString("desc");
                    if (StringUtils.isEmpty(strDesc)) {
                        lstCountDesc.add(key);
                        if (isClear) {
                            this.redisTemplate.delete(key);
                        }
                    }
                    lstCountAll.add(key);
                }
            });
        }
        return Pair.of(lstCountDesc, lstCountAll);
    }


}
