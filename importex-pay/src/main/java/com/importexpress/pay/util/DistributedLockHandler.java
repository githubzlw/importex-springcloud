package com.importexpress.pay.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author jack.luo
 * @create 2020/4/9 16:22
 * Description
 */
@Service
@Slf4j
public class DistributedLockHandler {

    private static final int LOCK_TIMEOUT = 1000;

    private final StringRedisTemplate redisTemplate;

    public DistributedLockHandler(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 外部调用加锁的方法
     * @param lockKey 锁的名字
     * @param timeout 超时时间（放置时间长度，如：5L）
     * @return
     */
    public boolean tryLock(String lockKey, long timeout) {
        try {
            //开始加锁的时间
            Long currentTime = System.currentTimeMillis();
            boolean result = false;

            while (true) {
                if ((System.currentTimeMillis() - currentTime) / 1000 > timeout) {
                    //当前时间超过了设定的超时时间
                    log.info("Execute DistributedLockHandler.tryLock method, Time out.");
                    break;
                } else {
                    result = innerTryLock(lockKey);
                    if (result) {
                        break;
                    } else {
                        log.info("Try to get the Lock,and wait 100 millisecond....");
                        Thread.sleep(100);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.info("Failed to run DistributedLockHandler.getLock method.",e);
            return false;
        }
    }

    /**
     * 释放锁
     * @param lockKey 锁的名字
     */
    public void realseLock(String lockKey) {
        if(!checkIfLockTimeout(System.currentTimeMillis(), lockKey)){
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 内部获取锁的实现方法
     * @param lockKey 锁的名字
     * @return
     */
    private boolean innerTryLock(String lockKey) {

        //当前时间
        long currentTime = System.currentTimeMillis();
        //锁的持续时间
        String lockTimeDuration = String.valueOf(currentTime + LOCK_TIMEOUT + 1);
        boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockTimeDuration);
        if (result) {
            return true;
        } else {
            if (checkIfLockTimeout(currentTime, lockKey)) {
                //锁已过期

                //设置新锁时间
                String preLockTimeDuration = redisTemplate.opsForValue().getAndSet(lockKey, lockTimeDuration);
                if (currentTime > Long.valueOf(preLockTimeDuration)) {
                    return true;
                }
            }
            return false;
        }

    }

    /**
     * 判断加锁是否超时
     * @param currentTime 当前时间
     * @param lockKey 锁的名字
     * @return
     */
    private boolean checkIfLockTimeout(Long currentTime, String lockKey) {
        if (currentTime > Long.valueOf(redisTemplate.opsForValue().get(lockKey))) {
            //当前时间超过锁的持续时间
            return true;
        } else {
            return false;
        }
    }

}
