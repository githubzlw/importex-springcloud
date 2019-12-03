package com.importexpress.ali1688.control;

import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.importexpress.ali1688.service.Ali1688Service;
import com.importexpress.ali1688.util.Config;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.exception.BizException;
import com.importexpress.comm.pojo.Ali1688Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author luohao
 */
@RestController
@Slf4j
public class Ali1688Controller {

    private Ali1688Service ali1688Service;

    private Config config;

    @Autowired
    public Ali1688Controller(Ali1688Service ali1688Service,Config config) {

        this.ali1688Service = ali1688Service;
        this.config = config;
    }

    @GetMapping("/hello")
    public String hello() {

        return "hello world!";
    }

    @GetMapping("/pids/{pids}")
    public List<JSONObject> pid(@PathVariable("pids") Long[] pids, @RequestParam(value = "isCache", required = false, defaultValue = "true") boolean isCache) {

        if(!isRunnable()){
            return null;
        }

        if (pids != null && pids.length == 1) {

            List<JSONObject> lstResult = new ArrayList<JSONObject>(1);
            lstResult.add(ali1688Service.getItem(pids[0], isCache));
            return lstResult;
        } else {
            return ali1688Service.getItems(pids, isCache);
        }
    }


    @GetMapping("/shop/{shopid}")
    public CommonResult getItemsInShop(@PathVariable("shopid") String shopid) {

        if(!isRunnable()){
            return new CommonResult().failed("非运行期间");
        }

        List<Ali1688Item> lstItems = null;

        Callable<List<Ali1688Item>> callable = new Callable<List<Ali1688Item>>() {

            @Override
            public List<Ali1688Item> call() {
                return ali1688Service.getItemsInShop(shopid);

            }
        };

        Retryer<List<Ali1688Item>> retryer = RetryerBuilder.<List<Ali1688Item>>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(IllegalStateException.class)
                .retryIfExceptionOfType(BizException.class)
                .withWaitStrategy(WaitStrategies.randomWait(1000, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(2))
                .build();
        try {
            return new CommonResult().success(retryer.call(callable));
        } catch (RetryException | ExecutionException e) {
            log.error("getItemsInShop", e);
            return new CommonResult().failed(e.getMessage());
        }
    }

    @GetMapping("/pids/clearNotExistItemInCache")
    public int clearNotExistItemInCache() {

        return ali1688Service.clearNotExistItemInCache();
    }

    @GetMapping("/pids/clearAllPidInCache")
    public int clearAllPidInCache() {

        return ali1688Service.clearAllPidInCache();
    }

    @GetMapping("/pids/clearAllShopInCache")
    public int clearAllShopInCache() {

        return ali1688Service.clearAllShopInCache();
    }

    @GetMapping("/pids/getNotExistItemInCache")
    public int getNotExistItemInCache() {

        return ali1688Service.getNotExistItemInCache();
    }

    @GetMapping("/pids/setItemsExpire")
    public CommonResult setItemsExpire(@Param("days") int days) {

        if (days <= 0) {
            return new CommonResult().failed("input params is invalid.");
        } else {
            ali1688Service.setItemsExpire(days);
            return new CommonResult().success(null);
        }
    }

    /**
     * 是否是可以运行的日期
     * @return
     */
    private boolean isRunnable(){
        String[] split = StringUtils.split(config.dates, ',');
        Assert.notNull(split,"config.dates is null");
        Assert.isTrue(split.length>0,"config.dates is empty");
        boolean result = Arrays.asList(split).contains(Integer.toString(LocalDate.now().getDayOfWeek().getValue()));
        if (result && LocalDateTime.now().getHour()>23 && LocalDateTime.now().getMinute()>30) {
            //23:30开始不执行程序
            return false;
        }else{
            return result;
        }
    }


}
