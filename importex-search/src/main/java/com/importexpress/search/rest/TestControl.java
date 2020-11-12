package com.importexpress.search.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author jack.luo
 * @date 2019/12/13
 */
@RestController
@Slf4j
@Api(tags = "test")
public class TestControl {

    @GetMapping("/hello")
    @ApiOperation("hello")
    public String hello() {

        return "hello world!";
    }

}
