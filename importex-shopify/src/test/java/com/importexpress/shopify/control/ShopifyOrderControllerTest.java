package com.importexpress.shopify.control;

import com.importexpress.shopify.mapper.ShopifyAuthMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.control
 * @date:2019/11/28
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopifyOrderControllerTest {

    @Autowired
    private WebApplicationContext wac;


    @Autowired
    private ShopifyAuthMapper shopifyAuthMapper;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }


    @Test
    public void getOrderByByShopifyName() throws Exception {

        String shopifyName = "importxtest";
        MvcResult mvcResult = mockMvc
                .perform(get("/shopifyOrder/getOrder/" + shopifyName))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.code").value("200")).andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }


    @Test
    public void getDetailsByOrderNo() throws Exception {

        long orderNo = 1;
        MvcResult mvcResult = mockMvc
                .perform(get("/shopifyOrder/getDetailsByOrderNo/" + orderNo))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.code").value("200")).andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

}
