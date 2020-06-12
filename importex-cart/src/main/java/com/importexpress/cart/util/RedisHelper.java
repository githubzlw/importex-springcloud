package com.importexpress.cart.util;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @Author jack.luo
 * @create 2020/6/12 11:35
 * Description
 */
public class RedisHelper {


    /**
     * scan 实现
     * @param pattern    表达式
     * @param consumer    对迭代到的key进行操作
     */
    private static void scan(StringRedisTemplate stringRedisTemplate,String pattern, Consumer<byte[]> consumer) {

        stringRedisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
                cursor.forEachRemaining(consumer);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 获取符合条件的key
     * @param pattern    表达式
     * @return
     */
    public static List<String> keys(StringRedisTemplate stringRedisTemplate,String pattern) {
        List<String> keys = new ArrayList<>();
        scan(stringRedisTemplate,pattern, item -> {
            //符合条件的key
            String key = new String(item, StandardCharsets.UTF_8);
            keys.add(key);
        });
        return keys;
    }
}

