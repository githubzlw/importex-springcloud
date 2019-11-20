package com.importexpress.ali1688.control;

import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.importexpress.ali1688.service.Ali1688Service;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.exception.BizException;
import com.importexpress.comm.pojo.Ali1688Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
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

    @Autowired
    public Ali1688Controller(Ali1688Service ali1688Service){
        this.ali1688Service=ali1688Service;
    }

    @GetMapping("/hello")
    public String hello() {

        return "hello world!";
    }

    @GetMapping("/pids/{pids}")
    public List<JSONObject> pid(@PathVariable("pids") Long[] pids) {

        if(pids !=null && pids.length==1){

            List<JSONObject> lstResult = new ArrayList<JSONObject>(1);
            lstResult.add(ali1688Service.getItem(pids[0]));
            return lstResult;
        }else{
            return ali1688Service.getItems(pids);
        }
    }


    @GetMapping("/shop/{shopid}")
    public CommonResult getItemsInShop(@PathVariable("shopid") String shopid) {

        List<Ali1688Item> lstItems = null;

        Callable<List<Ali1688Item>> callable = new Callable<List<Ali1688Item>>() {

            @Override
            public List<Ali1688Item> call()  {
                return ali1688Service.getItemsInShop(shopid);

            }
        };

        Retryer<List<Ali1688Item>> retryer = RetryerBuilder.<List<Ali1688Item>>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(IllegalStateException.class)
                .withWaitStrategy(WaitStrategies.fixedWait(2000, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();
        try {
            return new CommonResult().success(retryer.call(callable));
        } catch (RetryException | ExecutionException e) {
            log.error("getItemsInShop",e);
            return new CommonResult().failed(e.getMessage());
        }
    }

    @GetMapping("/pids/clearNotExistItemInCache")
    public int clearNotExistItemInCache() {

            return ali1688Service.clearNotExistItemInCache();
    }

    @GetMapping("/pids/getNotExistItemInCache")
    public int getNotExistItemInCache() {

            return ali1688Service.getNotExistItemInCache();
    }

    @GetMapping("/pids/setItemsExpire")
    public CommonResult setItemsExpire(@Param("days")int days) {

        if(days <=0 ){
            return new CommonResult().failed("input params is invalid.");
        }else{
            ali1688Service.setItemsExpire(days);
            return new CommonResult().success(null);
        }
    }


}
