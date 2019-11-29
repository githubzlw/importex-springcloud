package com.importexpress.shopify.service;

import com.importexpress.shopify.pojo.product.ShopifyBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopifyServiceTest {

    @Autowired
    private ShopifyService shopifyService;

    @Test
    public void saveShopifyAuth(){
        String shopName = "importglove33";
        String token = "6ca0c15496aa1eb0396608f5154ce806";
        String scope="write_orders,write_products";
        int save = shopifyService.saveShopifyAuth(shopName,token,scope);
        System.err.println(save);
    }
    @Test
    public void insertShopifyIdWithPid(){
        ShopifyBean shopifyBean = new ShopifyBean();
        shopifyBean.setPid("599236401419");
        shopifyBean.setShopifyInfo("");
        shopifyBean.setShopifyName("importxtest");
        shopifyBean.setShopifyPid("4354622750756");
        int save = shopifyService.insertShopifyIdWithPid(shopifyBean);
        System.err.println(save);
    }
}
