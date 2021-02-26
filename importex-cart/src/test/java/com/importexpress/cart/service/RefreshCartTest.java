package com.importexpress.cart.service;

import com.importexpress.cart.feign.ProductServiceFeign;
import com.importexpress.comm.pojo.SiteEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author jack.luo
 * @create 2020/3/4 16:00
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RefreshCartTest {


    public static final long USER_ID = 100001L;
    public static final String ITEM_ID1 = "559803042434:321631:324518";
    public static final String ITEM_ID2 = "560676334685:32161:324513";
    public static final String ITEM_ID3 = "530333452003:32164:324512";
    public static final String ITEM_ID4 = "547188149310:32161:324514";

    private static final SiteEnum SITE = SiteEnum.KIDS;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductServiceFeign productServiceFeign;

    /**
     * refreshCart 无下架商品情况
     */
    @Test
    public void refreshCart1() {
        Assert.assertEquals
                (1, cartService.delAllCartItem(SITE, USER_ID));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID1, 1,1));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID4, 2,1));
        Assert.assertEquals
                (0, cartService.refreshCart(SITE, USER_ID));
        Assert.assertEquals
                (1, cartService.delAllCartItem(SITE, USER_ID));
    }

    /**
     * refreshCart 有下架商品情况
     */
    @Test
    public void refreshCart2() {
        Assert.assertEquals
                (1, cartService.delAllCartItem(SITE, USER_ID));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID1, 1,1));
        productServiceFeign.updateProduct(Long.parseLong(ITEM_ID2.substring(0, ITEM_ID2.indexOf(":"))), 0);
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID2, 2,1));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID3, 3,1));
        //判断无变更
        Assert.assertEquals
                (1, cartService.refreshCart(SITE, USER_ID));
        //复原
        productServiceFeign.updateProduct(Long.parseLong(ITEM_ID2.substring(0, ITEM_ID2.indexOf(":"))), 1);
        Assert.assertEquals
                (1, cartService.delAllCartItem(SITE, USER_ID));
    }

    /**
     * refreshCart 途中新增下架商品情况
     */
    @Test
    public void refreshCart3() {
        Assert.assertEquals
                (1, cartService.delAllCartItem(SITE, USER_ID));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID1, 1,1));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID2, 2,1));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID3, 3,1));

        productServiceFeign.updateProduct(Long.parseLong(ITEM_ID2.substring(0, ITEM_ID2.indexOf(":"))), 0);
        Assert.assertEquals
                (1, cartService.refreshCart(SITE, USER_ID));
        productServiceFeign.updateProduct(Long.parseLong(ITEM_ID2.substring(0, ITEM_ID2.indexOf(":"))), 1);
        Assert.assertEquals
                (1, cartService.delAllCartItem(SITE, USER_ID));
    }
}
