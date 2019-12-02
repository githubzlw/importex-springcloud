package com.importexpress.shopify.service;

import com.importexpress.shopify.pojo.product.ShopifyBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Configuration
public class ShopifyProductServiceTest {

    @Autowired
    private ShopifyProductService shopifyProductService;

    @Test
    public void insertShopifyIdWithPid(){
        ShopifyBean shopifyBean = new ShopifyBean();
        shopifyBean.setPid("599236401419");
        shopifyBean.setShopifyInfo("");
        shopifyBean.setShopifyName("importxtest");
        shopifyBean.setShopifyPid("4354622750756");
        int save = shopifyProductService.insertShopifyIdWithPid(shopifyBean);
        System.out.println(save);
    }
    @Test
    public void selectShopifyId(){
        ShopifyBean shopifyBean = new ShopifyBean();
        shopifyBean.setPid("44525827299");
        shopifyBean.setShopifyInfo("");
        shopifyBean.setShopifyName("importxtest");
        ShopifyBean shopifyBean1 = shopifyProductService.selectShopifyId(shopifyBean);
        if(shopifyBean1 != null){
            System.out.println(shopifyBean1.toString());
        }else{
            System.err.println("出错啦");

        }
    }

}
