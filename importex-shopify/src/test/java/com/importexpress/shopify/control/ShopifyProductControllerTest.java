package com.importexpress.shopify.control;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.importexpress.shopify.mapper.ShopifyAuthMapper;
import com.importexpress.shopify.pojo.ShopifyAuth;
import com.importexpress.shopify.pojo.ShopifyData;
import com.importexpress.shopify.pojo.ShopifyRequestWrap;
import com.importexpress.shopify.pojo.TypeBean;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopifyProductControllerTest {
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
        wrap.setData(data());
        wrap.setShopname("importxtest");
        String requestJson = JSONObject.toJSONString(wrap);
        mockMvc.perform(post("/shopifyProduct/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)).andExpect(status().isOk()).andDo(print());

    }
    @Test
    public void addProductNoParam() throws Exception {
        String jsonWrap = JSONObject.toJSONString(null);
        MockHttpServletResponse response = mockMvc.perform(post("/shopifyProduct/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWrap)).andExpect(status().isOk())
                .andReturn().getResponse();
        Assert.assertEquals("error",
                "{\"code\":500,\"message\":\"request parameter is null\",\"data\":null}",response.getContentAsString());
    }
    @Test
    public void addProductNoShop() throws Exception {
        ShopifyRequestWrap wrap = new ShopifyRequestWrap();

        String requestJson = JSONObject.toJSONString(wrap);
        MockHttpServletResponse response = mockMvc.perform(post("/shopifyProduct/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)).andExpect(status().isOk())
                .andReturn().getResponse();
        Assert.assertEquals("error",
                "{\"code\":500,\"message\":\"shopname is null\",\"data\":null}",response.getContentAsString());
    }
    @Test
    public void addProductNoProduct() throws Exception {
        ShopifyRequestWrap wrap = new ShopifyRequestWrap();
        wrap.setData(null);
        wrap.setShopname("importxtest");
        String requestJson = JSONObject.toJSONString(wrap);
        MockHttpServletResponse response = mockMvc.perform(post("/shopifyProduct/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)).andExpect(status().isOk())
                .andReturn().getResponse();
        Assert.assertEquals("error",
                "{\"code\":500,\"message\":\"product is null\",\"data\":null}",response.getContentAsString());
    }
    @Test
    public void addProductOtherError() throws Exception {
        ShopifyRequestWrap wrap = new ShopifyRequestWrap();
        //设置data()方法: goods.setType(null);
        wrap.setData(data());
        wrap.setShopname("importxtest");
        String requestJson = JSONObject.toJSONString(wrap);
        MockHttpServletResponse response = mockMvc.perform(post("/shopifyProduct/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)).andExpect(status().isOk())
                .andReturn().getResponse();
        Assert.assertEquals("error",
                "{\"code\":500,\"message\":\"Product options has something wrong\",\"data\":null}",response.getContentAsString());
    }

    @Test
    public void getOrders() throws Exception {
        mockMvc.perform(post("/shopify/get/order").param("shopname", "importxtest"))
                .andExpect(status().isOk()).andDo(print());
    }

    private ShopifyData data(){
        ShopifyData goods = new ShopifyData();
        goods.setPid("536420633473");
        goods.setInfoHtml("");
        HashMap<String, String>  details = Maps.newHashMap();
        details.put("1","Material:Plastic/resin");
        details.put("2","Style:women");
        details.put("3","Material:not");
        details.put("4","Color:Gold . Silver .KC Gold");
        details.put("5","Species:Earring");
        details.put("6","Product Dimensions:20.0 cm * 10.0 cm * 10.0 cm");
        goods.setInfo(details);
        goods.setName("update Large And Simple Alloy Ball Dropping Alloy Earrings EZ0628");
        List<TypeBean> lstType = Lists.newArrayList();
        lstType.add(TypeBean.builder().img("https://img.import-express.com/importcsvimg/shopimg/536420633473/3288444156_1777934208.60x60.jpg").type("Color").value("Gold").id("32161").build());
        lstType.add(TypeBean.builder().img("https://img.import-express.com/importcsvimg/shopimg/536420633473/3288438490_1777934208.60x60.jpg").type("Color").value("Silver").id("32162").build());
        lstType.add(TypeBean.builder().img("https://img.import-express.com/importcsvimg/shopimg/536420633473/3286987060_1777934208.60x60.jpg").type("Color").value("KC gold").id("32163").build());
        goods.setType(lstType);
        List<String> lstImg = Lists.newArrayList();
        lstImg.add("https://img.import-express.com/importcsvimg/shopimg/536420633473/3288438490_1777934208.60x60.jpg");
        lstImg.add("https://img.import-express.com/importcsvimg/shopimg/536420633473/3288444156_1777934208.60x60.jpg");
        lstImg.add("https://img.import-express.com/importcsvimg/shopimg/536420633473/3286987060_1777934208.60x60.jpg");
        lstImg.add("https://img.import-express.com/importcsvimg/shopimg/536420633473/3288441537_1777934208.60x60.jpg");
        lstImg.add("https://img.import-express.com/importcsvimg/shopimg/536420633473/3286970937_1777934208.60x60.jpg");
        goods.setImage(lstImg);
        goods.setSkuProducts("[{\"skuAttr\":\"3216:32161\", \"skuPropIds\":\"32161\", \"specId\":\"0061b739ae60e11d22170378ac121c70\", \"skuId\":\"3201894141571\", \"fianlWeight\":\"0.02\",\"volumeWeight\":\"0.02\", \"wholesalePrice\":\"[2-119 $ 5.50, ≥120 $ 5.00]\", \"skuVal\":{\"actSkuCalPrice\":\"0.96\", \"actSkuMultiCurrencyCalPrice\":\"0.96\", \"actSkuMultiCurrencyDisplayPrice\":\"0.96\", \"availQuantity\":0, \"inventory\":0, \"isActivity\":true, \"skuCalPrice\":\"0.96\", \"skuMultiCurrencyCalPrice\":\"0.96\", \"skuMultiCurrencyDisplayPrice\":\"0.96\", \"costPrice\":\"5.5\", \"freeSkuPrice\":\"1.16\"}}, {\"skuAttr\":\"3216:32163\", \"skuPropIds\":\"32163\", \"specId\":\"c9915469fda3cbd1d4a261f715388eab\", \"skuId\":\"3201894141573\", \"fianlWeight\":\"0.02\",\"volumeWeight\":\"0.02\", \"wholesalePrice\":\"[2-119 $ 5.50, ≥120 $ 5.00]\", \"skuVal\":{\"actSkuCalPrice\":\"0.96\", \"actSkuMultiCurrencyCalPrice\":\"0.96\", \"actSkuMultiCurrencyDisplayPrice\":\"0.96\", \"availQuantity\":6, \"inventory\":6, \"isActivity\":true, \"skuCalPrice\":\"0.96\", \"skuMultiCurrencyCalPrice\":\"0.96\", \"skuMultiCurrencyDisplayPrice\":\"0.96\", \"costPrice\":\"5.5\", \"freeSkuPrice\":\"1.16\"}}, {\"skuAttr\":\"3216:32162\", \"skuPropIds\":\"32162\", \"specId\":\"4872dc478000b9bf5120596432ec71fa\", \"skuId\":\"3201894141572\", \"fianlWeight\":\"0.02\",\"volumeWeight\":\"0.02\", \"wholesalePrice\":\"[2-119 $ 5.50, ≥120 $ 5.00]\", \"skuVal\":{\"actSkuCalPrice\":\"0.96\", \"actSkuMultiCurrencyCalPrice\":\"0.96\", \"actSkuMultiCurrencyDisplayPrice\":\"0.96\", \"availQuantity\":248, \"inventory\":248, \"isActivity\":true, \"skuCalPrice\":\"0.96\", \"skuMultiCurrencyCalPrice\":\"0.96\", \"skuMultiCurrencyDisplayPrice\":\"0.96\", \"costPrice\":\"5.5\", \"freeSkuPrice\":\"1.16\"}}]");
        return goods;
    }
}
