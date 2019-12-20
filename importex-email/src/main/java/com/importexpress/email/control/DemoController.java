package com.importexpress.email.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DemoController {

    @GetMapping("/demo")
    public String demo(String str){
        log.info("step into the demo(),input:[{}]",str);
        return "hello " + str;
    }
}
