package com.importexpress.shopify.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.pojo.ImportProductBean;
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
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.rest
 * @date:2019/12/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OverSeaProductControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }


    @Test
    public void queryForListTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(get("/overSea/productList.json"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.code").value("200")).andReturn();
        String rsStr = mvcResult.getResponse().getContentAsString();
        System.out.println(rsStr);
        JSONObject rsJson = JSON.parseObject(rsStr);

        Assert.assertNotNull("无查询结果", rsJson.getJSONArray("data"));
        List<ImportProductBean> productList = JSONArray.parseArray(rsJson.getString("data"), ImportProductBean.class);
        Assert.assertTrue("查询结果空list", CollectionUtils.isNotEmpty(productList));
        int count = 0;
        for (ImportProductBean product : productList) {
            if (StringUtils.isNotBlank(product.getPid())) {
                count++;
            }
        }
        Assert.assertEquals("pid不是全部存在", productList.size(), count);

    }

}
