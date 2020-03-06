package com.importexpress.product.rest;

import com.importexpress.product.mongo.MongoProduct;
import com.importexpress.product.service.ProductService;
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

    @PostMapping("/pid/{pid}")
    public int updateProduct(@PathVariable("pid") long pid, @RequestParam(value = "valid") int valid) {

        return productService.updateProduct(pid, valid);
    }

    @GetMapping("/pids/{pids}")
    public List<MongoProduct> findProducts(@PathVariable("pids") long[] pids, @RequestParam(value = "valid", required = false, defaultValue = "-1") int valid) {

        return productService.findProducts(pids, valid);
    }

}
