package com.importexpress.shopify.control;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.importexpress.shopify.pojo.GoodsBean;
import com.importexpress.shopify.pojo.ShopifyRequestWrap;
import com.importexpress.shopify.pojo.TypeBean;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopifyControllerTest {

    @Autowired
    private WebApplicationContext wac;


    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void addProduct() throws Exception {
        ShopifyRequestWrap wrap = new ShopifyRequestWrap();
        GoodsBean goods = new GoodsBean();
        goods.setPID("536420633473");
        goods.setInfo_ori("<html>\\n <head></head>\\n <body>   \\n  <br> \\n  <img src=\\\"536420633473/desc/3288438490_1777934208.jpg\\\" alt=\\\"\\\"> \\n  <br> \\n  <img src=\\\"536420633473/desc/3288444156_1777934208.jpg\\\" alt=\\\"\\\"> \\n  <br> \\n  <img src=\\\"536420633473/desc/3286987060_1777934208.jpg\\\" alt=\\\"\\\"> \\n  <br> \\n  <img src=\\\"536420633473/desc/3288441537_1777934208.jpg\\\" alt=\\\"\\\"> \\n  <br> \\n  <img src=\\\"536420633473/desc/3286970937_1777934208.jpg\\\" alt=\\\"\\\">  \\n </body>\\n</html>");
        HashMap<String, String>  details = Maps.newHashMap();
        details.put("1","Material:Plastic/resin");
        details.put("2","Style:women");
        details.put("3","Material:not");
        details.put("4","Color:Gold . Silver .KC Gold");
        details.put("5","Species:Earring");
        details.put("6","Product Dimensions:20.0 cm * 10.0 cm * 10.0 cm");
        goods.setPInfo(details);
        goods.setPName("Large And Simple Alloy Ball Dropping Alloy Earrings EZ0628");
        List<TypeBean> lstType = Lists.newArrayList();
        lstType.add(TypeBean.builder().img("536420633473/3288444156_1777934208.60x60.jpg").type("Color").value("Gold").id("32161").build());
        lstType.add(TypeBean.builder().img("536420633473/3288438490_1777934208.60x60.jpg").type("Color").value("Silver").id("32162").build());
        lstType.add(TypeBean.builder().img("536420633473/3286987060_1777934208.60x60.jpg").type("Color").value("KC gold").id("32163").build());
        goods.setType(lstType);
        List<String> lstImg = Lists.newArrayList();
        goods.setPImage(lstImg);
        goods.setSkuProducts("[{\\\"skuAttr\\\":\\\"3216:32161\\\", \\\"skuPropIds\\\":\\\"32161\\\", \\\"specId\\\":\\\"0061b739ae60e11d22170378ac121c70\\\", \\\"skuId\\\":\\\"3201894141571\\\", \\\"fianlWeight\\\":\\\"0.02\\\",\\\"volumeWeight\\\":\\\"0.02\\\", \\\"wholesalePrice\\\":\\\"[2-119 $ 5.50, ≥120 $ 5.00]\\\", \\\"skuVal\\\":{\\\"actSkuCalPrice\\\":\\\"0.96\\\", \\\"actSkuMultiCurrencyCalPrice\\\":\\\"0.96\\\", \\\"actSkuMultiCurrencyDisplayPrice\\\":\\\"0.96\\\", \\\"availQuantity\\\":0, \\\"inventory\\\":0, \\\"isActivity\\\":true, \\\"skuCalPrice\\\":\\\"0.96\\\", \\\"skuMultiCurrencyCalPrice\\\":\\\"0.96\\\", \\\"skuMultiCurrencyDisplayPrice\\\":\\\"0.96\\\", \\\"costPrice\\\":\\\"5.5\\\", \\\"freeSkuPrice\\\":\\\"1.16\\\"}}, {\\\"skuAttr\\\":\\\"3216:32163\\\", \\\"skuPropIds\\\":\\\"32163\\\", \\\"specId\\\":\\\"c9915469fda3cbd1d4a261f715388eab\\\", \\\"skuId\\\":\\\"3201894141573\\\", \\\"fianlWeight\\\":\\\"0.02\\\",\\\"volumeWeight\\\":\\\"0.02\\\", \\\"wholesalePrice\\\":\\\"[2-119 $ 5.50, ≥120 $ 5.00]\\\", \\\"skuVal\\\":{\\\"actSkuCalPrice\\\":\\\"0.96\\\", \\\"actSkuMultiCurrencyCalPrice\\\":\\\"0.96\\\", \\\"actSkuMultiCurrencyDisplayPrice\\\":\\\"0.96\\\", \\\"availQuantity\\\":6, \\\"inventory\\\":6, \\\"isActivity\\\":true, \\\"skuCalPrice\\\":\\\"0.96\\\", \\\"skuMultiCurrencyCalPrice\\\":\\\"0.96\\\", \\\"skuMultiCurrencyDisplayPrice\\\":\\\"0.96\\\", \\\"costPrice\\\":\\\"5.5\\\", \\\"freeSkuPrice\\\":\\\"1.16\\\"}}, {\\\"skuAttr\\\":\\\"3216:32162\\\", \\\"skuPropIds\\\":\\\"32162\\\", \\\"specId\\\":\\\"4872dc478000b9bf5120596432ec71fa\\\", \\\"skuId\\\":\\\"3201894141572\\\", \\\"fianlWeight\\\":\\\"0.02\\\",\\\"volumeWeight\\\":\\\"0.02\\\", \\\"wholesalePrice\\\":\\\"[2-119 $ 5.50, ≥120 $ 5.00]\\\", \\\"skuVal\\\":{\\\"actSkuCalPrice\\\":\\\"0.96\\\", \\\"actSkuMultiCurrencyCalPrice\\\":\\\"0.96\\\", \\\"actSkuMultiCurrencyDisplayPrice\\\":\\\"0.96\\\", \\\"availQuantity\\\":248, \\\"inventory\\\":248, \\\"isActivity\\\":true, \\\"skuCalPrice\\\":\\\"0.96\\\", \\\"skuMultiCurrencyCalPrice\\\":\\\"0.96\\\", \\\"skuMultiCurrencyDisplayPrice\\\":\\\"0.96\\\", \\\"costPrice\\\":\\\"5.5\\\", \\\"freeSkuPrice\\\":\\\"1.16\\\"}}]");

        wrap.setGoods(goods);
        wrap.setShopname("importxtest");
        String requestJson = JSONObject.toJSONString(wrap);
        mockMvc.perform(post("/shopify/add/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)).andExpect(status().isOk()).andDo(print());

    }

    @Test
    public void getOrders() throws Exception {
        mockMvc.perform(post("/shopify/get/order").param("shopname", "importxtest"))
                .andExpect(status().isOk()).andDo(print());
    }
}
