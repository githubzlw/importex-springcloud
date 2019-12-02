package com.importexpress.shopify.service;

import com.importexpress.shopify.pojo.product.ShopifyBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopifyAuthServiceTest {

    @Autowired
    private ShopifyAuthService shopifyAuthService;

    @Test
    public void saveShopifyAuth(){
        String shopName = "importglove33";
        String token = "6ca0c15496aa1eb0396608f5154ce806";
        String scope="write_orders,write_products";
        int save = shopifyAuthService.saveShopifyAuth(shopName,token,scope);
        System.err.println(save);
    }
}
