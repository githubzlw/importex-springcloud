package com.importexpress.cart.service;

import com.google.gson.Gson;
import com.importexpress.cart.pojo.Cart;
import com.importexpress.comm.pojo.SiteEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;


/**
 * @Author jack.luo
 * @create 2019/12/17 11:15
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CartServiceTest2 {

    public static final long USER_ID = 100001L;

    private static final SiteEnum SITE = SiteEnum.KIDS;
    @Autowired
    private CartService cartService;

    /**
     * addCart
     */
    @Test
    public void cart1() {
            Assert.assertEquals
                    (1, cartService.addCartItem(SITE, USER_ID, "1005786125:32166", 1));
        Cart cart = cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(1,cart.getItems().size());
    }

    @Test
    public void cart2() {
        for(String str : getItemIds()){
            Assert.assertEquals
                    (1, cartService.addCartItem(SITE, USER_ID, str, 1));
        }
    }

    @Test
    public void cart3() {
        Cart cart = cartService.getCart(SITE, USER_ID);
        Assert.assertEquals(getItemIds().length+1,cart.getItems().size());

    }

    @Test
    public void cart4() {
        cartService.checkAll(SITE, USER_ID,1);
        Assert.assertEquals(1,cartService.delChecked(SITE, USER_ID));

    }


    @Test
    public void convert() {
        //sample: [{"skuAttr":"3216:32168", "skuPropIds":"32168", "specId":"3757601142926", "skuId":"3757601142926", "fianlWeight":"0.12","volumeWeight":"0.12", "wholesalePrice":"[≥1 $ 7.0-14.0]", "skuVal":{"actSkuCalPrice":"2.76", "actSkuMultiCurrencyCalPrice":"2.76", "actSkuMultiCurrencyDisplayPrice":"2.76", "availQuantity":0, "inventory":0, "isActivity":true, "skuCalPrice":"2.76", "skuMultiCurrencyCalPrice":"2.76", "skuMultiCurrencyDisplayPrice":"2.76", "costPrice":"14.0", "freeSkuPrice":"3.96"}]
        String sku = "[{\"skuAttr\":\"3216:32168\", \"skuPropIds\":\"32168\", \"specId\":\"3757601142926\", \"skuId\":\"3757601142926\", \"fianlWeight\":\"0.12\",\"volumeWeight\":\"0.12\", \"wholesalePrice\":\"[≥1 $ 7.0-14.0]\", \"skuVal\":{\"actSkuCalPrice\":\"2.76\", \"actSkuMultiCurrencyCalPrice\":\"2.76\", \"actSkuMultiCurrencyDisplayPrice\":\"2.76\", \"availQuantity\":0, \"inventory\":0, \"isActivity\":true, \"skuCalPrice\":\"2.76\", \"skuMultiCurrencyCalPrice\":\"2.76\", \"skuMultiCurrencyDisplayPrice\":\"2.76\", \"costPrice\":\"14.0\", \"freeSkuPrice\":\"3.96\"}}, {\"skuAttr\":\"3216:321611\", \"skuPropIds\":\"321611\", \"specId\":\"3757601142918\", \"skuId\":\"3757601142918\", \"fianlWeight\":\"0.12\",\"volumeWeight\":\"0.12\", \"wholesalePrice\":\"[≥1 $ 7.0-14.0]\", \"skuVal\":{\"actSkuCalPrice\":\"2.76\", \"actSkuMultiCurrencyCalPrice\":\"2.76\", \"actSkuMultiCurrencyDisplayPrice\":\"2.76\", \"availQuantity\":0, \"inventory\":0, \"isActivity\":true, \"skuCalPrice\":\"2.76\", \"skuMultiCurrencyCalPrice\":\"2.76\", \"skuMultiCurrencyDisplayPrice\":\"2.76\", \"costPrice\":\"14.0\", \"freeSkuPrice\":\"3.96\"}}, {\"skuAttr\":\"3216:321612\", \"skuPropIds\":\"321612\", \"specId\":\"3757601142920\", \"skuId\":\"3757601142920\", \"fianlWeight\":\"0.12\",\"volumeWeight\":\"0.12\", \"wholesalePrice\":\"[≥1 $ 7.0-14.0]\", \"skuVal\":{\"actSkuCalPrice\":\"2.76\", \"actSkuMultiCurrencyCalPrice\":\"2.76\", \"actSkuMultiCurrencyDisplayPrice\":\"2.76\", \"availQuantity\":0, \"inventory\":0, \"isActivity\":true, \"skuCalPrice\":\"2.76\", \"skuMultiCurrencyCalPrice\":\"2.76\", \"skuMultiCurrencyDisplayPrice\":\"2.76\", \"costPrice\":\"14.0\", \"freeSkuPrice\":\"3.96\"}}, {\"skuAttr\":\"3216:321610\", \"skuPropIds\":\"321610\", \"specId\":\"3757601142919\", \"skuId\":\"3757601142919\", \"fianlWeight\":\"0.12\",\"volumeWeight\":\"0.12\", \"wholesalePrice\":\"[≥1 $ 7.0-14.0]\", \"skuVal\":{\"actSkuCalPrice\":\"2.76\", \"actSkuMultiCurrencyCalPrice\":\"2.76\", \"actSkuMultiCurrencyDisplayPrice\":\"2.76\", \"availQuantity\":0, \"inventory\":0, \"isActivity\":true, \"skuCalPrice\":\"2.76\", \"skuMultiCurrencyCalPrice\":\"2.76\", \"skuMultiCurrencyDisplayPrice\":\"2.76\", \"costPrice\":\"14.0\", \"freeSkuPrice\":\"3.96\"}}, {\"skuAttr\":\"3216:32166\", \"skuPropIds\":\"32166\", \"specId\":\"3757601142925\", \"skuId\":\"3757601142925\", \"fianlWeight\":\"0.12\",\"volumeWeight\":\"0.12\", \"wholesalePrice\":\"[≥1 $ 7.0-14.0]\", \"skuVal\":{\"actSkuCalPrice\":\"2.17\", \"actSkuMultiCurrencyCalPrice\":\"2.17\", \"actSkuMultiCurrencyDisplayPrice\":\"2.17\", \"availQuantity\":0, \"inventory\":0, \"isActivity\":true, \"skuCalPrice\":\"2.17\", \"skuMultiCurrencyCalPrice\":\"2.17\", \"skuMultiCurrencyDisplayPrice\":\"2.17\", \"costPrice\":\"11.0\", \"freeSkuPrice\":\"3.37\"}}, {\"skuAttr\":\"3216:32167\", \"skuPropIds\":\"32167\", \"specId\":\"3757601142922\", \"skuId\":\"3757601142922\", \"fianlWeight\":\"0.12\",\"volumeWeight\":\"0.12\", \"wholesalePrice\":\"[≥1 $ 7.0-14.0]\", \"skuVal\":{\"actSkuCalPrice\":\"2.17\", \"actSkuMultiCurrencyCalPrice\":\"2.17\", \"actSkuMultiCurrencyDisplayPrice\":\"2.17\", \"availQuantity\":13, \"inventory\":13, \"isActivity\":true, \"skuCalPrice\":\"2.17\", \"skuMultiCurrencyCalPrice\":\"2.17\", \"skuMultiCurrencyDisplayPrice\":\"2.17\", \"costPrice\":\"11.0\", \"freeSkuPrice\":\"3.37\"}}, {\"skuAttr\":\"3216:32162\", \"skuPropIds\":\"32162\", \"specId\":\"3757601142923\", \"skuId\":\"3757601142923\", \"fianlWeight\":\"0.12\",\"volumeWeight\":\"0.12\", \"wholesalePrice\":\"[≥1 $ 7.0-14.0]\", \"skuVal\":{\"actSkuCalPrice\":\"1.38\", \"actSkuMultiCurrencyCalPrice\":\"1.38\", \"actSkuMultiCurrencyDisplayPrice\":\"1.38\", \"availQuantity\":5, \"inventory\":5, \"isActivity\":true, \"skuCalPrice\":\"1.38\", \"skuMultiCurrencyCalPrice\":\"1.38\", \"skuMultiCurrencyDisplayPrice\":\"1.38\", \"costPrice\":\"7.0\", \"freeSkuPrice\":\"2.58\"}}, {\"skuAttr\":\"3216:32164\", \"skuPropIds\":\"32164\", \"specId\":\"3757601142924\", \"skuId\":\"3757601142924\", \"fianlWeight\":\"0.12\",\"volumeWeight\":\"0.12\", \"wholesalePrice\":\"[≥1 $ 7.0-14.0]\", \"skuVal\":{\"actSkuCalPrice\":\"1.38\", \"actSkuMultiCurrencyCalPrice\":\"1.38\", \"actSkuMultiCurrencyDisplayPrice\":\"1.38\", \"availQuantity\":12, \"inventory\":12, \"isActivity\":true, \"skuCalPrice\":\"1.38\", \"skuMultiCurrencyCalPrice\":\"1.38\", \"skuMultiCurrencyDisplayPrice\":\"1.38\", \"costPrice\":\"7.0\", \"freeSkuPrice\":\"2.58\"}}, {\"skuAttr\":\"3216:32169\", \"skuPropIds\":\"32169\", \"specId\":\"3757601142921\", \"skuId\":\"3757601142921\", \"fianlWeight\":\"0.12\",\"volumeWeight\":\"0.12\", \"wholesalePrice\":\"[≥1 $ 7.0-14.0]\", \"skuVal\":{\"actSkuCalPrice\":\"2.17\", \"actSkuMultiCurrencyCalPrice\":\"2.17\", \"actSkuMultiCurrencyDisplayPrice\":\"2.17\", \"availQuantity\":0, \"inventory\":0, \"isActivity\":true, \"skuCalPrice\":\"2.17\", \"skuMultiCurrencyCalPrice\":\"2.17\", \"skuMultiCurrencyDisplayPrice\":\"2.17\", \"costPrice\":\"11.0\", \"freeSkuPrice\":\"3.37\"}}, {\"skuAttr\":\"3216:32161\", \"skuPropIds\":\"32161\", \"specId\":\"3757601142928\", \"skuId\":\"3757601142928\", \"fianlWeight\":\"0.12\",\"volumeWeight\":\"0.12\", \"wholesalePrice\":\"[≥1 $ 7.0-14.0]\", \"skuVal\":{\"actSkuCalPrice\":\"1.38\", \"actSkuMultiCurrencyCalPrice\":\"1.38\", \"actSkuMultiCurrencyDisplayPrice\":\"1.38\", \"availQuantity\":0, \"inventory\":0, \"isActivity\":true, \"skuCalPrice\":\"1.38\", \"skuMultiCurrencyCalPrice\":\"1.38\", \"skuMultiCurrencyDisplayPrice\":\"1.38\", \"costPrice\":\"7.0\", \"freeSkuPrice\":\"2.58\"}}, {\"skuAttr\":\"3216:32165\", \"skuPropIds\":\"32165\", \"specId\":\"3757601142917\", \"skuId\":\"3757601142917\", \"fianlWeight\":\"0.12\",\"volumeWeight\":\"0.12\", \"wholesalePrice\":\"[≥1 $ 7.0-14.0]\", \"skuVal\":{\"actSkuCalPrice\":\"2.17\", \"actSkuMultiCurrencyCalPrice\":\"2.17\", \"actSkuMultiCurrencyDisplayPrice\":\"2.17\", \"availQuantity\":0, \"inventory\":0, \"isActivity\":true, \"skuCalPrice\":\"2.17\", \"skuMultiCurrencyCalPrice\":\"2.17\", \"skuMultiCurrencyDisplayPrice\":\"2.17\", \"costPrice\":\"11.0\", \"freeSkuPrice\":\"3.37\"}}, {\"skuAttr\":\"3216:32163\", \"skuPropIds\":\"32163\", \"specId\":\"3757601142927\", \"skuId\":\"3757601142927\", \"fianlWeight\":\"0.12\",\"volumeWeight\":\"0.12\", \"wholesalePrice\":\"[≥1 $ 7.0-14.0]\", \"skuVal\":{\"actSkuCalPrice\":\"1.38\", \"actSkuMultiCurrencyCalPrice\":\"1.38\", \"actSkuMultiCurrencyDisplayPrice\":\"1.38\", \"availQuantity\":8, \"inventory\":8, \"isActivity\":true, \"skuCalPrice\":\"1.38\", \"skuMultiCurrencyCalPrice\":\"1.38\", \"skuMultiCurrencyDisplayPrice\":\"1.38\", \"costPrice\":\"7.0\", \"freeSkuPrice\":\"2.58\"}}]";
        Map[] mapsType = new Map[0];
        Map[] maps = new Gson().fromJson(sku, mapsType.getClass());
        for (Map map: maps) {
            System.out.println(map.keySet());
        }

    }

    private String[] getPids(){
        String[] strs={"1003032520", "1004781188", "1005786125", "1006954507", "1008069787", "1010519189", "1013662758", "1014747217", "1015678191", "1016749470", "1017057298", "1018515657", "1019756286", "1021140389", "1022140035", "1023904980", "1024067979", "1024111041", "1024770999", "1025164195", "1027048047", "1029124531", "1032228495", "1033849308", "1033859096", "1038069441", "1039567830", "1041129992", "1041134151", "1041168995", "1041171574", "1041926319", "1041952983", "1043862285", "1044601028", "1047021155", "1050626672", "1050948924", "1051320695", "1051729524", "1054853831", "1056055205", "1056992803", "1057223017", "1058692954", "1059258923", "1059675127", "1059833386", "1060968202", "1063006052", "1063755939", "1064519196", "1065398217", "1067323984", "1069460667", "1070175847", "1071563012", "1074775356", "1075069601", "1076585962", "1079567971", "1080513398", "1081052994", "1082222583", "1082241130", "1082274070", "1082578687", "1083063773", "1083308284", "1083310282", "1083344591", "1083440279", "1084049147", "1084477973", "1084492048", "1084618354", "1084621656", "1084623723", "1084873681", "1086323239", "1092491446", "1093287465", "1094517538", "1095654393", "1096208979", "1096891833", "1096911187", "1097216519", "1097673794", "1100323058", "1100608208", "1102725673", "1104614488", "1107926208", "1109123695", "1109778128", "1110477669", "1110565327", "1113334079", "1113567786"};
            return strs;

    }

    private String[] getItemIds(){
        String[] strs={"1003032520:32168", "1004781188:32161:324511", "1005786125:32168", "1006954507:32166:4501"};
        return strs;

    }


}
