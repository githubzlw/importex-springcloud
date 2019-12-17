package com.importexpress.product.rest;

import com.importexpress.product.mongo.MongoProduct;
import com.importexpress.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author jack.luo
 * @date 2019/12/13
 */
@RestController
@Slf4j
public class ProductControl {


    private ProductService productService;

    @Autowired
    public ProductControl(ProductService productService) {

        this.productService = productService;
    }

    @GetMapping("/hello")
    public String hello() {

        return "hello world!";
    }

    @GetMapping("/pid/{pid}")
    public MongoProduct findProduct(@PathVariable("pid") long pid) {

        return productService.findProduct(pid);
    }

    @GetMapping("/pids/{pids}")
    public List<MongoProduct> findProducts(@PathVariable("pids") long[] pids, @RequestParam(value = "valid", required = false, defaultValue = "-1") int valid) {

        return productService.findProducts(pids, valid);
    }
}
