package com.importexpress.ali1688.control;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.ali1688.model.ItemDetails;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.ali1688.control
 * @date:2020/5/6
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AmazonControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
    }


    @Test
    public void getDetails() throws Exception {
        String pid = "B0714BNR2W";
        MvcResult mvcResult = mockMvc.perform(get("/amazon/details/" + pid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andReturn();
        String rs = mvcResult.getResponse().getContentAsString();
        Assert.assertNotNull("获取结果空", rs);
        JSONObject jsonObject = JSONObject.parseObject(rs);
        Assert.assertNotNull("无data数据", jsonObject.getString("data"));

        ItemDetails itemDetails = JSONObject.parseObject(jsonObject.getString("data"), ItemDetails.class);

        System.err.println(JSONObject.toJSONString(itemDetails));
    }
}
