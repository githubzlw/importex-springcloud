package com.importexpress.pay.service.impl;

import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.pay.service.OrderNoGenerator;
import com.importexpress.pay.service.enumc.ClientTypeEnum;
import com.importexpress.pay.service.enumc.TradeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisSystemException;
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
 * 订单号生成器(redis版）
 *
 * @author luohao
 * @date 2019/8/30
 */
@Service
@Slf4j
public class OrderNoGeneratorByRedis implements OrderNoGenerator {

    private static final String ORDERNO = "ORDERNO:";
    private static final int ONE_DAY = 60 * 60 * 24 + 60;
    private static ThreadLocal<Integer> retryCount = new ThreadLocal() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
    private static final DateTimeFormatter fmtMMdd = DateTimeFormatter.ofPattern("MMdd");
    private static final DateTimeFormatter fmtyyMMdd = DateTimeFormatter.ofPattern("yyMMdd");

    private final StringRedisTemplate redisTemplate;

    public OrderNoGeneratorByRedis(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
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
    private void pushToRedis(List<String> lstOrders) {


        //execute a transaction

        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {

            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                String key = ORDERNO + getToday();
                operations.watch(key);
                if (operations.hasKey(key)) {
                    log.info("redis key:[{}] is exist", key);
                    return null;
                } else {
                    operations.multi();
                    operations.opsForList().leftPushAll(key, lstOrders);
                    operations.expire(key, ONE_DAY, TimeUnit.SECONDS);
                    return operations.exec();
                }
            }
        });

        log.info("redis txResults:[{}]", txResults);
    }

    /**
     * 检查redis中key是否存在
     *
     * @return
     */
    private boolean checkRedis() {

        return redisTemplate.hasKey(ORDERNO + getToday());
    }

    /**
     * 获得订单号(分布式部署)
     *
     * @return
     */
    private String getOrderNoFromRedis() throws Exception {

        String key = ORDERNO + getToday();
        String keyHis = ORDERNO + getToday() + ":his";
        if (checkRedis()) {
            return redisTemplate.opsForList().rightPopAndLeftPush(key, keyHis);
        } else {
            //今日首次订单
            try {
                pushToRedis(generatorArrays());
                return redisTemplate.opsForList().rightPopAndLeftPush(key, keyHis);
            } catch (RedisSystemException rse) {
                try {
                    log.info("begin retry...");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error("getOrderNoFromRedis", e);
                }
                if (retryCount.get() < 3) {
                    //最多重试3次
                    retryCount.set(retryCount.get() + 1);
                    log.info("This is {} times", retryCount.get());
                    return getOrderNoFromRedis();
                } else {
                    retryCount.set(0);
                    throw new Exception("generator OrderNo fault");
                }
            }

        }
    }

    /**
     * 新版订单号编制
     *
     * @param clientTypeEnum 终端类型
     * @param tradeTypeEnum  交易类型
     * @return
     */
    @Override
    public String generateOrderNumber(SiteEnum site, ClientTypeEnum clientTypeEnum, TradeTypeEnum tradeTypeEnum) throws Exception {
        char clientType = '0';
        if (clientTypeEnum.equals(ClientTypeEnum.MOBILE)) {
            clientType = '1';
        } else if (clientTypeEnum.equals(ClientTypeEnum.PC)) {
            clientType = '2';
        }
        char tradeType = '0';
        if (tradeTypeEnum.equals(TradeTypeEnum.NORMAL)) {
            tradeType = '1';
        } else if (tradeTypeEnum.equals(TradeTypeEnum.DROPSHIP)) {
            tradeType = '2';
        } else if (tradeTypeEnum.equals(TradeTypeEnum.BUYFORME)) {
            tradeType = '3';
        } else if (tradeTypeEnum.equals(TradeTypeEnum.REORDER)) {
            tradeType = '4';
        } else if (tradeTypeEnum.equals(TradeTypeEnum.RECHARGE)) {
            tradeType = '5';
        } else if (tradeTypeEnum.equals(TradeTypeEnum.OTHER)) {
            tradeType = '9';
        } else if(tradeTypeEnum.equals(TradeTypeEnum.MEMBERRENWAL)){
            tradeType = '8';
        }
        String yyMMdd = LocalDate.now().format(fmtyyMMdd);
        if(site == SiteEnum.IMPORTX){
            //10位（兼容老格式）
            return String.valueOf(clientType) +
                    tradeType +
                    yyMMdd.substring(1, 6) +
                    getOrderNoFromRedis();
        }else{
            //11位
            return String.valueOf(clientType) +
                    tradeType +
                    yyMMdd.substring(1, 6) +
                    site.getName().charAt(0)+
                    getOrderNoFromRedis();
        }
    }

}
