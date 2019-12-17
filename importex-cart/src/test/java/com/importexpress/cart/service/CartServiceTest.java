package com.importexpress.cart.service;

import com.importexpress.cart.pojo.CartProduct;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @Author jack.luo
 * @create 2019/12/17 11:15
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Test
    public void addCart() {
        Assert.assertEquals
                (1,cartService.addCart('K',100001L, 1001178017L, 50));
    }

    @Test
    public void getCartList() {
        List<CartProduct> lst = cartService.getCartList('K', 100001L);
        System.out.println(lst);
        Assert.assertEquals(1,lst.size());
        System.out.println(lst.get(0).getPrice());
    }

    @Test
    public void updateCartNum() {

        Assert.assertEquals
                (1,cartService.updateCartNum('K',12345L, 111111L, 100,"true"));

    }

    @Test
    public void deleteCartItem() {
        Assert.assertEquals
                (1,cartService.deleteCartItem('K',12345L, 111111L));
    }

    @Test
    public void checkAll() {

    }

    @Test
    public void delChecked() {

    }
}
