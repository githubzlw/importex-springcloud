package com.importexpress.ali1688.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.importexpress.ali1688.model.AliExpressItem;
import com.importexpress.ali1688.model.ItemResultPage;
import com.importexpress.ali1688.service.AliExpressCacheService;
import com.importexpress.ali1688.service.AliExpressService;
import com.importexpress.ali1688.util.ConfigExpress;
import com.importexpress.ali1688.util.InvalidKeyWord;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.exception.BizErrorCodeEnum;
import com.importexpress.comm.exception.BizException;
import com.importexpress.comm.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.aliexpress.service.impl
 * @date:2020/3/16
 */
@Service
@Slf4j
public class AliExpressServiceImpl implements AliExpressService {
    private final StringRedisTemplate redisTemplate;
    private final AliExpressCacheService cacheService;
    private static final String REDIS_CALL_COUNT = "aliexpress:call:count";
    private static final String YYYYMMDD = "yyyyMMdd";
    private final ConfigExpress config;

    /**
     * 获取商品详情
     */
    private final static String URL_ITEM_SEARCH = "https://api.onebound.cn/aliexpress/api_call.php?key=%s&secret=%s&q=%s&api_name=item_search&lang=en&page=%s";// &sort=_sale

    @Autowired
    public AliExpressServiceImpl(StringRedisTemplate redisTemplate, AliExpressCacheService cacheService, ConfigExpress config) {
        this.redisTemplate = redisTemplate;
        this.cacheService = cacheService;
        this.config = config;
    }

    @Override
    public CommonResult getItemByKeyWord(Integer currPage, String keyword, boolean isCache) {
        JSONObject jsonObject = searchResultByKeyWord(currPage, keyword, isCache);
        if (jsonObject == null || jsonObject.getJSONObject("items") == null
                || jsonObject.getJSONObject("items").getString("item") == null) {
            return CommonResult.failed("no data");
        } else {
            List<AliExpressItem> aliExpressItems = JSONArray.parseArray(jsonObject.getJSONObject("items").getString("item"), AliExpressItem.class);
            Integer totalNum = jsonObject.getJSONObject("items").getInteger("total_results");
            Integer rsPage = jsonObject.getJSONObject("items").getInteger("page");
            Integer rsPageSize = jsonObject.getJSONObject("items").getInteger("page_size");
            Integer totalPage = totalNum / rsPageSize;
            if (totalNum % rsPageSize > 0) {
                totalPage++;
            }
            ItemResultPage resultPage = new ItemResultPage(aliExpressItems, currPage, rsPageSize, totalPage, totalNum);
            return CommonResult.success(resultPage);
        }
    }


    private JSONObject searchResultByKeyWord(Integer page, String keyword, boolean isCache) {
        /**
         * api.onebound.cn/aliexpress/api_call.php?
         * q=shoe&start_price=&end_price=&page=&cat=&discount_only=&sort=&page_size=&seller_info=&nick=&ppath=&api_name=item_search&lang=zh-CN&key=tel13222738797&secret=20200316
         */
        Objects.requireNonNull(keyword);
        Callable<JSONObject> callable = new Callable<JSONObject>() {

            @Override
            public JSONObject call() {
                return getItemByKeyword(page, keyword, isCache);

            }
        };

        Retryer<JSONObject> retryer = RetryerBuilder.<JSONObject>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(IllegalStateException.class)
                .withWaitStrategy(WaitStrategies.fixedWait(2000, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();
        try {
            return retryer.call(callable);
        } catch (RetryException | ExecutionException e) {
            throw new BizException(e.getMessage());
        }
    }


    private JSONObject getItemByKeyword(Integer page, String keyword, boolean isCache) {
        Objects.requireNonNull(keyword);
        if (isCache) {
            JSONObject itemFromRedis = this.cacheService.getItemByKeyword(page, keyword);
            if (itemFromRedis != null) {
                checkKeyWord(page, keyword, itemFromRedis);
                return itemFromRedis;
            }
        }

        try {
            // "https://api.onebound.cn/aliexpress/api_call.php?key=%s&secret=%s&q=%s&api_name=item_search&lang=zh-CN&page_size=%s&page=%s"
            JSONObject jsonObject = UrlUtil.getInstance().callUrlByGet(String.format(URL_ITEM_SEARCH, config.API_KEY, config.API_SECRET, keyword, page));
            String strYmd = LocalDate.now().format(DateTimeFormatter.ofPattern(YYYYMMDD));
            this.redisTemplate.opsForHash().increment(REDIS_CALL_COUNT, "keyword_" + strYmd, 1);
            String error = jsonObject.getString("error");
            if (StringUtils.isNotEmpty(error)) {
                if (error.contains("你的授权已经过期")) {
                    throw new BizException(BizErrorCodeEnum.EXPIRE_FAIL);
                } else if (error.contains("超过")) {
                    //TODO
                    throw new BizException(BizErrorCodeEnum.LIMIT_EXCEED_FAIL);
                } else if (error.contains("item-not-found")) {
                    throw new IllegalStateException("item-not-found");
                }
                log.warn("json's error is not empty:[{}]，keyword:[{}]", error, keyword);
                jsonObject = InvalidKeyWord.of(keyword, error);
            }
            this.cacheService.saveItemByKeyword(page, keyword, jsonObject);
            checkKeyWord(page, keyword, jsonObject);

            return jsonObject;
        } catch (IOException e) {
            log.error("getItemByKeyword,keyword[{}]", keyword, e);
            throw new BizException(BizErrorCodeEnum.UNSPECIFIED);
        }
    }

    private void checkKeyWord(Integer page, String keyword, JSONObject jsonObject) {
        Objects.requireNonNull(keyword);
        Objects.requireNonNull(jsonObject);
        JSONObject items = jsonObject.getJSONObject("items");
        if (items != null) {
            if (StringUtils.isEmpty(items.getString("item"))) {
                // this.cacheService.deleteKeyword(page, keyword);
                log.warn("item is null ,keyword:[{}]", keyword);
                throw new BizException(BizErrorCodeEnum.ITEM_IS_NULL);
            }
        }
    }

}
