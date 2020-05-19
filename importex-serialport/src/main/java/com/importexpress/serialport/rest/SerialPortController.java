package com.importexpress.serialport.rest;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.serialport.service.SerialPortService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jack.luo
 */
@RestController
@Slf4j
@Api(tags = "串口通信")
public class SerialPortController {

    private final SerialPortService serialPortService;

    public SerialPortController(SerialPortService serialPortService) {
        this.serialPortService = serialPortService;
    }

    @GetMapping("/com5Writer")
    @ApiOperation("com5口通信")
    public CommonResult com5Writer(@RequestParam String[] msgs) {

        serialPortService.com5Writer(msgs);
        return CommonResult.success();
    }
}
