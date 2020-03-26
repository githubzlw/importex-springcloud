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


    @PostMapping("/search")
    @ResponseBody
    public CommonResult searchItem(Integer page, String keyword, String start_price, String end_price, String sort,
                                   @RequestParam(value = "isCache", required = false, defaultValue = "true") boolean isCache) {
        return expressService.getItemByKeyWord(page, keyword,start_price, end_price, sort, isCache);
    }

}
