package com.importexpress.cart.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @Author jack.luo
 * @create 2020/6/12 11:35
 * Description
 */
@Slf4j
public class RedisHelper {


//    /**
//     * scan 实现
//     * @param pattern    表达式
//     * @param consumer    对迭代到的key进行操作
//     */
//    private static void scan(StringRedisTemplate stringRedisTemplate,String pattern, Consumer<byte[]> consumer) {
//
//        stringRedisTemplate.execute((RedisConnection connection) -> {
//            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
//                cursor.forEachRemaining(consumer);
//                return null;
//            } catch (IOException e) {
//                log.error("scan",e);
//                throw new RuntimeException(e);
//            }
//        });
//    }
//
//    /**
//     * 获取符合条件的key
//     * @param pattern    表达式
//     * @return
//     */
//    public static List<String> keys(StringRedisTemplate stringRedisTemplate,String pattern) {
//        log.info("pattern:[{}]",pattern);
//        List<String> keys = new ArrayList<>();
//        scan(stringRedisTemplate,pattern, item -> {
//            //符合条件的key
//            String key = new String(item, StandardCharsets.UTF_8);
//            keys.add(key);
//        });
//        return keys;
//    }

    public static Set<String> scan(StringRedisTemplate redisTemplate,String matchKey) {
        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysTmp = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match("*" + matchKey + "*").count(1000).build());
            while (cursor.hasNext()) {
                keysTmp.add(new String(cursor.next()));
            }
            return keysTmp;
        });

        return keys;
    }
}

