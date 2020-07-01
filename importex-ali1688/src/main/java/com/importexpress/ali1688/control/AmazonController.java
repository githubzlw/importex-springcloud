package com.importexpress.ali1688.control;

import com.importexpress.ali1688.service.AmazonService;
import com.importexpress.comm.domain.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.ali1688.control
 * @date:2020/5/6
 */
@Controller
@Slf4j
@RequestMapping("/amazon")
public class AmazonController {

    private final AmazonService amazonService;

    public AmazonController(AmazonService amazonService) {
        this.amazonService = amazonService;
    }

    @GetMapping("/details/{pid}")
    @ResponseBody
    public CommonResult getDetails(@PathVariable(name = "pid") String pid) {
        return amazonService.getDetails(pid);
    }
}
