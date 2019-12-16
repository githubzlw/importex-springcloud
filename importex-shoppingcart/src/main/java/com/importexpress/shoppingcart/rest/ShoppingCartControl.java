package com.importexpress.shoppingcart.rest;

import com.importexpress.shoppingcart.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author luohao
 * @date 2019/12/16
 */
@RestController
@Slf4j
public class ShoppingCartControl {


    private ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartControl(ShoppingCartService shoppingCartService) {

        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/hello")
    public String hello() {

        return "hello world!";
    }

//    @GetMapping("/pid/{pid}")
//    public MongoProduct findProduct(@PathVariable("pid") long pid) {
//
//        return shoppingCartService.findProduct(pid);
//    }


}
