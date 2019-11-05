package com.importexpress.ali1688.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author luohao
 * @date 2019/11/5
 */
@FeignClient(value = "ali1688-service")
public interface Ali1688ServiceFeign {

    @GetMapping(value="getItem")
    JSONObject getItem(@RequestParam("pid") String pid);
}
