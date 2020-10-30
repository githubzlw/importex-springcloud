package com.importexpress.product.rest;

import com.importexpress.product.mongo.CatidGroup;
import com.importexpress.product.mongo.MongoProduct;
import com.importexpress.product.pojo.SearchParam;
import com.importexpress.product.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author jack.luo
 * @date 2019/12/13
 */
@RestController
@Slf4j
@Api(tags = "mongodb产品表")
public class ProductControl {


    private ProductService productService;

    @Autowired
    public ProductControl(ProductService productService) {

        this.productService = productService;
    }

    @GetMapping("/hello")
    @ApiOperation("hello(test)")
    public String hello() {

        return "hello world!";
    }

    @GetMapping("/pid/{pid}")
    @ApiOperation("查询单个pid")
    public MongoProduct findProduct(@ApiParam(name = "pid", value = "pid", required = true)  @PathVariable("pid") long pid) {

        return productService.findProduct(pid);
    }

    @PostMapping("/pid/{pid}")
    @ApiOperation("更新单个pid")
    public int updateProduct(@ApiParam(name = "pid", value = "pid", required = true)     @PathVariable("pid") long pid,
                             @ApiParam(name = "valid", value = "valid", required = true) @RequestParam(value = "valid") int valid) {

        return productService.updateProduct(pid, valid);
    }

    @GetMapping("/pids/{pids}")
    @ApiOperation("查询多个pid")
    public List<MongoProduct> findProducts(@ApiParam(name = "pids", value = "pids", required = true) @PathVariable("pids") long[] pids,
                                           @ApiParam(name = "valid", value = "valid", defaultValue = "-1")  @RequestParam(value = "valid", required = false, defaultValue = "-1") int valid) {

        return productService.findProducts(pids, valid);
    }

    @GetMapping("/shop/{shopid}")
    @ApiOperation("通过店铺id搜索")
    public List<MongoProduct> findProductByShopId(@ApiParam(name = "shopid", value = "shopid", required = true) @PathVariable("shopid") String shopid) {

        return productService.findProductByShopId(shopid);
    }

    @PostMapping("/findProductByCatid")
    @ApiOperation("通过店铺id搜索")
    public List<MongoProduct> findProductByCatid(@ApiParam(name = "param", value = "param", required = true) @RequestBody SearchParam param) {

        return productService.findProductByCatid(param);
    }

    @PostMapping("/findProductByCatidCount")
    @ApiOperation("通过店铺id搜索")
    public Long findProductByCatidCount(@ApiParam(name = "param", value = "param", required = true) @RequestBody SearchParam param) {

        return productService.findProductByCatidCount(param);
    }

    @PostMapping("/findCatidGroup")
    @ApiOperation("通过店铺id搜索")
    public List<CatidGroup> findCatidGroup(@ApiParam(name = "list", value = "list", required = true)  @RequestBody List<String> list) {

        return productService.findCatidGroup(list);
    }

}
