package com.importexpress.shopify.service;

import com.importexpress.shopify.feign.ProductServiceFeign;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author luohao
 * @date 2019/12/13
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignTest {

    @Autowired
    private ProductServiceFeign productServiceFeign;

    public void testFeign(){
        System.out.println(productServiceFeign.hello());
    }
}
