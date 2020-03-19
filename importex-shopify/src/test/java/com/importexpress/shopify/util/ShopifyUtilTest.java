package com.importexpress.shopify.util;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author jack.luo
 * @create 2020/3/3 11:01
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopifyUtilTest {

    @Autowired
    private ShopifyUtil shopifyUtil;

    @Test
    public void getForObjectByBAI() {
        System.out.println(shopifyUtil.getObject("https://kr-cart-test.myshopify.com/admin/api/2020-01/shop.json"));
    }
}

