package com.importexpress.ali1688.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author luohao
 * @date 2019/9/5
 */
@FeignClient(value = "mail-service")
public interface DemoServiceFeign {

    @GetMapping(value="demo")
    String demo(@RequestParam("str") String str);
}
