package com.importexpress.ali1688.control;

import com.importexpress.ali1688.service.AliExpressService;
import com.importexpress.comm.domain.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.aliexpress.controller
 * @date:2020/3/16
 */
@Slf4j
@RestController
@RequestMapping("/aliExpress")
public class AliExpressController {


    private AliExpressService expressService;


    public AliExpressController(AliExpressService expressService) {
        this.expressService = expressService;
    }


    @GetMapping("/search/{page}/{keyword}")
    public CommonResult searchItem(@PathVariable("page") Integer page, @PathVariable("keyword") String keyword,
                                   @RequestParam(value = "isCache", required = false, defaultValue = "true") boolean isCache) {
        return expressService.getItemByKeyWord(page, keyword, isCache);
    }

}
