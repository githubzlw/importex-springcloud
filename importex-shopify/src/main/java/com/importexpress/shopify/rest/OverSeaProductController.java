package com.importexpress.shopify.rest;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.pojo.ImportProductBean;
import com.importexpress.shopify.feign.ProductServiceFeign;
import com.importexpress.shopify.service.OverSeaProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.rest
 * @date:2019/12/10
 */
@Slf4j
@RestController
@RequestMapping("/overSea")
@Api("海外仓商品接口")
public class OverSeaProductController {

    private final ProductServiceFeign productServiceFeign;

    private final OverSeaProductService overSeaProductService;

    @GetMapping("/helloFeign")
    @ApiOperation("feign test")
    public CommonResult helloFeign() {
        return CommonResult.success(productServiceFeign.hello());
    }

    @GetMapping("/findProductFeign")
    @ApiOperation("feign test(findProduct)")
    public CommonResult findProduct(long pid) {
        return CommonResult.success(productServiceFeign.findProduct(pid));
    }


    public OverSeaProductController(OverSeaProductService overSeaProductService, ProductServiceFeign productServiceFeign) {
        this.overSeaProductService = overSeaProductService;

        this.productServiceFeign = productServiceFeign;
    }


    @GetMapping("/productList.json")
    @ApiOperation("获取全部海外仓产品")
    public CommonResult queryForList() {

        try {
            List<ImportProductBean> productBeans = overSeaProductService.queryOverSeaProductList();
            return CommonResult.success(productBeans);
        } catch (Exception e) {
            log.error("queryForList error:", e);
            return CommonResult.failed(e.getMessage());
        }
    }


}
