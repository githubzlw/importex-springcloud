package com.importexpress.pay.service.impl;

import com.importexpress.pay.util.DistributedLockHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 订单
 *
 * @author luohao
 */
@Service
@Slf4j
public class OrderNoHelp {

    private static final String ORDERNO = "ORDERNO:";

    private static final String lockKey = "ORDERNO_LOCK_KEY";

    private static final DateTimeFormatter fmtMMdd = DateTimeFormatter.ofPattern("MMdd");


    private final StringRedisTemplate redisTemplate;

    private final DistributedLockHandler distributedLockHandler;

    public OrderNoHelp(StringRedisTemplate redisTemplate, DistributedLockHandler distributedLockHandler) {
        this.redisTemplate = redisTemplate;
        this.distributedLockHandler = distributedLockHandler;
    }

    /**
     * 产生3位数的随机数组（范围：100---999）
     *
     * @return
     */
    private List<String> generatorArrays() {

        List<Integer> list = IntStream.range(100, 1000).boxed().collect(Collectors.toList());
        Collections.shuffle(list);

        List<String> lstResult = new ArrayList<>(list.size());
        list.stream().forEach(i -> lstResult.add(i.toString()));

        return lstResult;
    }

    /**
     * 获取当天日期
     *
     * @return
     */
    private String getToday() {

        return LocalDate.now().format(fmtMMdd);
    }

    /**
     * 一次性生产当天的所有订单号
     *
     * @param lstOrders
     */
    private void pushToRedis(String key,List<String> lstOrders) {


        //execute a transaction

        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {

            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.watch(key);
                operations.multi();
                log.debug("begin pushToRedis multi()");
                operations.opsForList().leftPushAll(key, lstOrders);
                operations.expire(key, 7, TimeUnit.DAYS);
                return operations.exec();
            }
        });

        log.info("redis txResults:[{}]", txResults);
    }

    /**
     * 检查redis中key是否存在
     *
     * @return
     */
    private boolean checkRedis(String key) {

        return redisTemplate.hasKey(key);
    }

    /**
     * 清理redis缓存
     * @return
     */
    public boolean clearRedisCache() {

        return redisTemplate.delete(ORDERNO + getToday());
    }

    /**
     * 获得订单号(分布式部署)
     *
     * @return
     */
    protected String getOrderNoFromRedis() {

        String key = ORDERNO + getToday();
        String keyHis = ORDERNO + getToday() + ":his";

        try{
            if(distributedLockHandler.tryLock(lockKey, 3)){
                if (!checkRedis(key)) {
                    pushToRedis(key,generatorArrays());
                }
                return redisTemplate.opsForList().rightPopAndLeftPush(key, keyHis);
            }else {
                log.warn("can't get lock.");
                return null;
            }
        }catch(Exception e){
            log.error("getOrderNoFromRedis",e);
            return null;
        }finally {
            distributedLockHandler.realseLock(lockKey);
        }
    }

}
