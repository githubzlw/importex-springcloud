package com.importexpress.ali1688.control;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.importexpress.ali1688.model.AliExpressItem;
import com.importexpress.ali1688.model.ItemDetails;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        String keyword = "shoes";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("keyword", keyword);
        params.add("page", "1");
        params.add("start_price", null);
        params.add("end_price", null);
        params.add("sort", getSearchParam("0"));

        MvcResult mvcResult = mockMvc.perform(post("/aliExpress/search").params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andReturn();
        String rs = mvcResult.getResponse().getContentAsString();
        Assert.assertNotNull("获取结果空", rs);
        JSONObject jsonObject = JSONObject.parseObject(rs);
        Assert.assertNotNull("无data数据", jsonObject.getString("data"));
        List<AliExpressItem> aliExpressItems = JSONArray.parseArray(jsonObject.getJSONObject("data").getString("itemList"), AliExpressItem.class);
        Assert.assertTrue("获取数据空", CollectionUtils.isNotEmpty(aliExpressItems));
        aliExpressItems.forEach(System.err::println);
    }


    @Test
    public void searchItem1() throws Exception {
        String keyword = "shoes";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("keyword", keyword);
        params.add("page", "1");
        params.add("start_price", "3");
        params.add("end_price", null);
        params.add("sort", getSearchParam("1"));

        MvcResult mvcResult = mockMvc.perform(post("/aliExpress/search").params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andReturn();
        String rs = mvcResult.getResponse().getContentAsString();
        Assert.assertNotNull("获取结果空", rs);
        JSONObject jsonObject = JSONObject.parseObject(rs);
        Assert.assertNotNull("无data数据", jsonObject.getString("data"));
        List<AliExpressItem> aliExpressItems = JSONArray.parseArray(jsonObject.getJSONObject("data").getString("itemList"), AliExpressItem.class);
        Assert.assertTrue("获取数据空", CollectionUtils.isNotEmpty(aliExpressItems));
        aliExpressItems.forEach(System.err::println);
    }


    @Test
    public void searchItem2() throws Exception {
        String keyword = "shoes";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("keyword", keyword);
        params.add("page", "1");
        params.add("start_price", null);
        params.add("end_price", "9");
        params.add("sort", getSearchParam("2"));

        MvcResult mvcResult = mockMvc.perform(post("/aliExpress/search").params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andReturn();
        String rs = mvcResult.getResponse().getContentAsString();
        Assert.assertNotNull("获取结果空", rs);
        JSONObject jsonObject = JSONObject.parseObject(rs);
        Assert.assertNotNull("无data数据", jsonObject.getString("data"));
        List<AliExpressItem> aliExpressItems = JSONArray.parseArray(jsonObject.getJSONObject("data").getString("itemList"), AliExpressItem.class);
        Assert.assertTrue("获取数据空", CollectionUtils.isNotEmpty(aliExpressItems));
        aliExpressItems.forEach(System.err::println);
    }


    @Test
    public void getDetails() throws Exception {
        String pid = "32821682345";
        MvcResult mvcResult = mockMvc.perform(get("/aliExpress/details/" + pid))
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


    private static String getSearchParam(String sort) {
        /**
         * 0 Best Match, 1 Price Low To High,
         * 2 Best Selling 3 New Arrivals
         *
         * sort:排序[bid,_bid,_sale,_new]
         *   (bid:总价,sale:销量,new上架时间,加_前缀为从大到小排序)
         */
        switch (sort) {
            case "0":
                return "";
            case "1":
                return "bid";
            case "2":
                return "_sale";
            case "3":
                return "_new";
            default:
                return "";
        }
    }
}
