package com.importexpress.shopify.control;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.importexpress.shopify.mapper.ShopifyAuthMapper;
import com.importexpress.shopify.pojo.ShopifyAuth;
import com.importexpress.shopify.pojo.ShopifyData;
import com.importexpress.shopify.pojo.ShopifyRequestWrap;
import com.importexpress.shopify.pojo.TypeBean;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopifyAuthControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    /**
     * 需要正式环境测试
     * @throws Exception
     */
    @Test
    public void auth() throws Exception{
        Map<String,String> mapParam = Maps.newHashMap();
        mapParam.put("code","");
        mapParam.put("hmac","");
        mapParam.put("timestamp","");
        mapParam.put("state","");
        mapParam.put("shop","");
//        mapParam.put("itemId","1003055874");
        String requestJson = JSONObject.toJSONString(mapParam);
        mockMvc.perform(post("/shopifyAuth/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)).andExpect(status().isOk()).andDo(print());
    }


}
