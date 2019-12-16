package com.importexpress.shoppingcart.rest;

import com.importexpress.shoppingcart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author luohao
 * @date 2019/12/16
 */
@RestController
@Slf4j
public class CartControl {


    private CartService shoppingCartService;

    @Autowired
    public CartControl(CartService shoppingCartService) {

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
