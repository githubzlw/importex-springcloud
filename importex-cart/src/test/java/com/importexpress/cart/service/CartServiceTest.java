package com.importexpress.cart.service;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.importexpress.cart.pojo.Cart;
import com.importexpress.comm.pojo.SiteEnum;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    public static final String ITEM_ID1 = "559803042434:321631:324518";
    public static final String ITEM_ID2 = "560676334685:32161:324513";
    public static final String ITEM_ID3 = "530333452003:32164:324512";
    public static final String ITEM_ID4 = "547188149310:32161:324514";

    private static final SiteEnum SITE = SiteEnum.KIDS;
    @Autowired
    private CartService cartService;

    /**
     * addCart
     */
    @Test
    public void cart1() {
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID1, 1));
    }

    /**
     * getCartList
     */
    @Test
    public void cart2() {
        Cart cart = cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(1, cart.getItems().size());
        Assert.assertEquals(368, cart.getItems().get(0).getPri());
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
                (1, cartService.updateCartItem(SITE, USER_ID, ITEM_ID1, 99, 1));
        Cart cart =  cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(1, cart.getItems().size());
        Assert.assertEquals(335, cart.getItems().get(0).getPri());

    }

    /**
     * addCart
     */
    @Test
    public void cart4() {
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID1, 1));
        Cart cart =  cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(1, cart.getItems().size());
        Assert.assertEquals(314, cart.getItems().get(0).getPri());
    }

    /**
     * delete
     */
    @Test
    public void cart5() {
        Assert.assertEquals
                (1, cartService.delCartItem(SITE, USER_ID, ITEM_ID1));
    }

    /**
     * checkAll
     */
    @Test
    public void cart6() {
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID1, 1));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID2, 2));
        Cart cart =  cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(3, cart.getTotalAmount());
        Assert.assertEquals(1005, cart.getTotalPrice());
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

        // ITEM_ID1 = "560676334685:32162:324514";
        // ITEM_ID2 = "560676334685:32161:324513";
        // ITEM_ID3 = "530333452003:32164:324512";
        // ITEM_ID4 = "547188149310:32161:324514";
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID1, 1));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID3, 3));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID2, 2));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID4, 4));


        Cart cart =  cartService.getCart(SITE, USER_ID);

        Assert.assertEquals(10, cart.getTotalAmount());
        Assert.assertEquals(4, cart.getItems().size());

        Assert.assertEquals(ITEM_ID1, cart.getItems().get(0).getItemId());
        Assert.assertEquals(ITEM_ID2, cart.getItems().get(1).getItemId());
        Assert.assertEquals(ITEM_ID3, cart.getItems().get(2).getItemId());
        Assert.assertEquals(ITEM_ID4, cart.getItems().get(3).getItemId());


        Assert.assertEquals
                (1, cartService.checkAll(SITE, USER_ID, 1));
        Assert.assertEquals
                (1, cartService.delChecked(SITE, USER_ID));

    }

    @Test
    public void cart9() {

        // ITEM_ID1 = "560676334685:32162:324514";
        // ITEM_ID2 = "560676334685:32161:324513";
        // ITEM_ID3 = "530333452003:32164:324512";
        // ITEM_ID4 = "547188149310:32161:324514";
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID2, 1));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID1, 2));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID4, 3));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID3, 4));


        Cart cart =  cartService.getCart(SITE, USER_ID);

        Assert.assertEquals(10, cart.getTotalAmount());
        Assert.assertEquals(4, cart.getItems().size());

        Assert.assertEquals(ITEM_ID2, cart.getItems().get(0).getItemId());
        Assert.assertEquals(ITEM_ID1, cart.getItems().get(1).getItemId());
        Assert.assertEquals(ITEM_ID4, cart.getItems().get(2).getItemId());
        Assert.assertEquals(ITEM_ID3, cart.getItems().get(3).getItemId());


        Assert.assertEquals
                (1, cartService.checkAll(SITE, USER_ID, 1));
        Assert.assertEquals
                (1, cartService.delChecked(SITE, USER_ID));

    }


    @Test
    public void cart10() {

        // ITEM_ID1 = "560676334685:32162:324514";
        // ITEM_ID2 = "560676334685:32161:324513";
        // ITEM_ID3 = "530333452003:32164:324512";
        // ITEM_ID4 = "547188149310:32161:324514";
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID3, 1));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID4, 2));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID1, 3));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID2, 4));


        Cart cart =  cartService.getCart(SITE, USER_ID);

        Assert.assertEquals(10, cart.getTotalAmount());
        Assert.assertEquals(4, cart.getItems().size());

        Assert.assertEquals(ITEM_ID3, cart.getItems().get(0).getItemId());
        Assert.assertEquals(ITEM_ID4, cart.getItems().get(1).getItemId());
        Assert.assertEquals(ITEM_ID1, cart.getItems().get(2).getItemId());
        Assert.assertEquals(ITEM_ID2, cart.getItems().get(3).getItemId());


        Assert.assertEquals
                (1, cartService.checkAll(SITE, USER_ID, 1));
        Assert.assertEquals
                (1, cartService.delChecked(SITE, USER_ID));

    }

    /**
     * 单规格情况下的pid
     */
    @Test
    public void cart11() {
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, "100473434:999999", 199));

        Cart cart = cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(1, cart.getItems().size());
        //[1-199 $ 4.92, 200-2999 $ 4.23, ≥3000 $ 3.73]
        Assert.assertEquals(492, cart.getItems().get(0).getPri());
        Assert.assertTrue(StringUtils.isEmpty(cart.getItems().get(0).getTn()));

        Assert.assertEquals
                (1, cartService.updateCartItem(SITE, USER_ID, "100473434:999999", 200,1));
        cart = cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(423, cart.getItems().get(0).getPri());

        Assert.assertEquals
                (1, cartService.updateCartItem(SITE, USER_ID, "100473434:999999", 3000,1));
        cart = cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(373, cart.getItems().get(0).getPri());
        Assert.assertEquals
                (1, cartService.delCartItem(SITE, USER_ID, "100473434:999999"));

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

    @Test
    public void generateTouristId(){

        for(int i=0;i<10;i++){
            long result = this.cartService.generateTouristId(SiteEnum.KIDS);
            Assert.assertTrue(result>0);
        }

        for(int i=0;i<10;i++){
            long result = this.cartService.generateTouristId(SiteEnum.MEDIC);
            Assert.assertTrue(result>0);
        }
    }

    @Test
    public void mergeCarts(){

        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID1, 1));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, USER_ID, ITEM_ID2, 2));
        long touristId = this.cartService.generateTouristId(SITE);
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, touristId, ITEM_ID2, 3));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, touristId, ITEM_ID1, 4));
        Assert.assertEquals
                (1, cartService.addCartItem(SITE, touristId, ITEM_ID3, 10));

        Assert.assertEquals(2, this.cartService.getCart(SITE, USER_ID).getItems().size());
        Assert.assertEquals(3, this.cartService.getCart(SITE, touristId).getItems().size());

        Assert.assertEquals(1, this.cartService.mergeCarts(SITE,USER_ID,touristId));

        Assert.assertEquals(0, this.cartService.getCart(SITE, touristId).getItems().size());
        Cart cart = this.cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(3, cart.getItems().size());
        Assert.assertEquals(20, cart.getTotalAmount());

        this.cartService.delAllCartItem(SITE, USER_ID);

    }
}
