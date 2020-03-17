package com.importexpress.ali1688.control;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.importexpress.ali1688.model.AliExpressItem;
import org.apache.commons.collections.CollectionUtils;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.aliexpress.controller
 * @date:2020/3/16
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AliExpressControllerTest {

    @Autowired
    private WebApplicationContext wac;


    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void searchItem() throws Exception {
        String keyword = "shoe";
        int page = 2;
        MvcResult mvcResult = mockMvc.perform(get("/aliExpress/search/" + page + "/" + keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andReturn();
        String rs = mvcResult.getResponse().getContentAsString();
        Assert.assertNotNull("获取结果空", rs);
        JSONObject jsonObject = JSONObject.parseObject(rs);
        Assert.assertNotNull("无data数据", jsonObject.getString("data"));
        List<AliExpressItem> aliExpressItems = JSONArray.parseArray(jsonObject.getJSONObject("data").getString("itemList"), AliExpressItem.class);
        Assert.assertTrue("获取数据空", CollectionUtils.isNotEmpty(aliExpressItems));
    }
}
