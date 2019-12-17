package com.importexpress.shopify.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.pojo.ImportProductBean;
import com.importexpress.shopify.feign.ProductServiceFeign;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author jack.luo
 * @date 2019/12/13
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignTest {

    @Autowired
    private WebApplicationContext wac;


    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }


    @Test
    public void feign1() throws Exception {
        mockMvc.perform(get("/overSea/helloFeign"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andDo(print());
    }

    @Test
    public void feign2() throws Exception {
        mockMvc.perform(get("/overSea/findProductFeign").param("pid", "556860707964"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andDo(print());
    }


}
