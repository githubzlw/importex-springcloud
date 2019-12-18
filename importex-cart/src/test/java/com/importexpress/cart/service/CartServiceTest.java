package com.importexpress.cart.service;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.importexpress.cart.pojo.Cart;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;


/**
 * @Author jack.luo
 * @create 2019/12/17 11:15
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CartServiceTest {

    public static final long USER_ID = 100001L;
    public static final String ITEM_ID1 = "560676334685:32162:324514";
    public static final String ITEM_ID2 = "560676334685:32161:324513";
    public static final String ITEM_ID3 = "530333452003:32164:324512";
    public static final String ITEM_ID4 = "547188149310:32161:324514";

    public static final char SITE = 'K';
    @Autowired
    private CartService cartService;

    /**
     * addCart
     */
    @Test
    public void cart1() {
        Assert.assertEquals
                (1, cartService.addCart(SITE, USER_ID, ITEM_ID1, 1));
    }

    /**
     * getCartList
     */
    @Test
    public void cart2() {
        Cart cart = cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(1, cart.getItems().size());
        Assert.assertEquals(new BigDecimal("3.68"), cart.getItems().get(0).getPri());
        Assert.assertEquals("greybeard 80cm(31 inch | age 9-12M)", cart.getItems().get(0).getTn());
        Assert.assertEquals("560676334685/9192394532_2128907802.60x60.jpg", cart.getItems().get(0).getImg());
        Assert.assertTrue(cart.getItems().get(0).getCt()< Instant.now().toEpochMilli());
        Assert.assertTrue(cart.getItems().get(0).getUt()< Instant.now().toEpochMilli());

    }

    /**
     * updateCartNum
     */
    @Test
    public void cart3() {

        Assert.assertEquals
                (1, cartService.updateCartNum(SITE, USER_ID, ITEM_ID1, 99, 1));
        Cart cart =  cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(1, cart.getItems().size());
        Assert.assertEquals(new BigDecimal("3.35"), cart.getItems().get(0).getPri());

    }

    /**
     * addCart
     */
    @Test
    public void cart4() {
        Assert.assertEquals
                (1, cartService.addCart(SITE, USER_ID, ITEM_ID1, 1));
        Cart cart =  cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(1, cart.getItems().size());
        Assert.assertEquals(new BigDecimal("3.14"), cart.getItems().get(0).getPri());
    }

    /**
     * delete
     */
    @Test
    public void cart5() {
        Assert.assertEquals
                (1, cartService.deleteCartItem(SITE, USER_ID, ITEM_ID1));
    }

    /**
     * checkAll
     */
    @Test
    public void cart6() {
        Assert.assertEquals
                (1, cartService.addCart(SITE, USER_ID, ITEM_ID1, 1));
        Assert.assertEquals
                (1, cartService.addCart(SITE, USER_ID, ITEM_ID2, 2));
        Cart cart =  cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(3, cart.getTotalAmount());
        Assert.assertEquals(BigDecimal.valueOf(3.35d * 3), cart.getTotalPrice());
        Assert.assertEquals(0.11f*3, cart.getTotalWeight(),0.0001f);
        Assert.assertEquals(2, cart.getItems().size());
        Assert.assertEquals(1, cart.getItems().get(0).getChk());

        Assert.assertEquals
                (1, cartService.checkAll(SITE, USER_ID, 0));
        cart = cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(2, cart.getItems().size());
        Assert.assertEquals(0, cart.getItems().get(0).getChk());
        Assert.assertEquals(0, cart.getItems().get(1).getChk());
    }

    /**
     * delChecked
     */
    @Test
    public void cart7() {
        Assert.assertEquals
                (1, cartService.checkAll(SITE, USER_ID, 1));
        Assert.assertEquals
                (1, cartService.delChecked(SITE, USER_ID));
        Cart cart =  cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(0, cart.getItems().size());
    }

    /**
     * sort
     */
    @Test
    public void cart8() {

        Assert.assertEquals
                (1, cartService.addCart(SITE, USER_ID, ITEM_ID1, 1));
        Assert.assertEquals
                (1, cartService.addCart(SITE, USER_ID, ITEM_ID3, 3));
        Assert.assertEquals
                (1, cartService.addCart(SITE, USER_ID, ITEM_ID2, 2));
        Assert.assertEquals
                (1, cartService.addCart(SITE, USER_ID, ITEM_ID4, 4));


        Cart cart =  cartService.getCart(SITE, USER_ID);

        Assert.assertEquals(10, cart.getTotalAmount());
        Assert.assertEquals(4, cart.getItems().size());

        Assert.assertEquals(ITEM_ID4, cart.getItems().get(0).getItemId());
        Assert.assertEquals(ITEM_ID1, cart.getItems().get(1).getItemId());
        Assert.assertEquals(ITEM_ID2, cart.getItems().get(2).getItemId());
        Assert.assertEquals(ITEM_ID3, cart.getItems().get(3).getItemId());


        Assert.assertEquals
                (1, cartService.checkAll(SITE, USER_ID, 1));
        Assert.assertEquals
                (1, cartService.delChecked(SITE, USER_ID));

    }

    @Test
    public void convert(){
        String str = "[[id=32161, type=Color, value=White beard, img=560676334685/9168867283_2128907802.60x60.jpg], [id=32162, type=Color, value=greybeard, img=560676334685/9192394532_2128907802.60x60.jpg], [id=32163, type=Color, value=Blue wave point, img=560676334685/9210989827_2128907802.60x60.jpg], [id=32164, type=Color, value=Powder point, img=560676334685/9210995840_2128907802.60x60.jpg], [id=324511, type=Spec, value=59cm(23 inch | age 0-3M), img=], [id=324512, type=Spec, value=66cm(26 inch | age 3-6M), img=], [id=324513, type=Spec, value=73cm(29 inch | age 6-9M), img=], [id=324514, type=Spec, value=80cm(31 inch | age 9-12M), img=], [id=324515, type=Spec, value=85cm(33 inch | age 9-12M), img=], [id=324516, type=Spec, value=90cm(35 inch | age 1-2T), img=], [id=324517, type=Spec, value=95cm(37 inch | age 1-2T), img=]]";

        ImmutableList<String> lst = ImmutableList.copyOf(Splitter.on("],").split(str));
        for(String item:lst){
            String cleanStr = CharMatcher.anyOf("[]").removeFrom(item).trim();
            if(StringUtils.contains(cleanStr,"id=324517")){
                String str1 = "value=";
                int beginIndex = cleanStr.indexOf(str1);
                cleanStr=cleanStr.substring(beginIndex+str1.length(), cleanStr.indexOf(',', beginIndex));
                System.out.println(cleanStr);
            }
        }
    }
}
