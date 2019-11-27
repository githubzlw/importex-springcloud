package com.importexpress.shopify.control;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

        Map<String, Object> sessionAttrs = new HashMap<>();
//        UserBean userBean = new UserBean();
//        userBean.setId(15937);
//        userBean.setShopifyName("importxtest");
//        sessionAttrs.put("userInfo", userBean);
//        sessionAttrs.put("IS_LOGIN", true);

        mockMvc.perform(post("/shopify/add/product").param("itemid", "599236401419").sessionAttrs(sessionAttrs)).andExpect(status().isOk()).andDo(print());

    }

    @Test
    public void getOrders() throws Exception {
        mockMvc.perform(post("/shopify/get/order").param("shopname", "importxtest"))
                .andExpect(status().isOk()).andDo(print());
    }
}
