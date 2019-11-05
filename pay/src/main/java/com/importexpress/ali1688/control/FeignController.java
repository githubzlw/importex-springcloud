package com.importexpress.ali1688.control;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.ali1688.service.feign.Ali1688ServiceFeign;
import com.importexpress.ali1688.service.feign.DemoServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FeignController {

    @Autowired
    private DemoServiceFeign client;

    @Autowired
    private Ali1688ServiceFeign ali1688ServiceFeign;

    @GetMapping("/feign")
    public String callDemoByFeign(){

        return client.demo("jack");
    }

    @GetMapping("/getItem")
    public JSONObject callGetItem(String pid){

        return ali1688ServiceFeign.getItem(pid);
    }
}
