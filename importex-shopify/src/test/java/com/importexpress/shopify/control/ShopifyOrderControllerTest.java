package com.importexpress.shopify.control;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.importexpress.shopify.pojo.orders.Line_items;
import com.importexpress.shopify.pojo.orders.Orders;
import com.importexpress.shopify.pojo.orders.Shipping_address;
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

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void getShopifyOrderListByShopifyNameTest() throws Exception {

        String shopifyName = "importxtest";
        MvcResult mvcResult = mockMvc
                .perform(get("/api/shopify/" + shopifyName))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.code").value("200")).andReturn();
        String rsStr = mvcResult.getResponse().getContentAsString();
        System.out.println(rsStr);
        JSONObject rsJson = JSON.parseObject(rsStr);

        Assert.assertNotNull("无查询结果", rsJson.getJSONArray("rows"));
        long orderNo = 1875136217122L;
        List<Orders> ordersList = JSONArray.parseArray(rsJson.getString("rows"), Orders.class);
        Assert.assertTrue("查询结果空list", CollectionUtils.isNotEmpty(ordersList));
        int count = 0;
        for (Orders orderBean : ordersList) {
            if (orderBean.getId() == orderNo) {
                count++;
            }
        }
        Assert.assertEquals("未有预测订单:" + orderNo + ",执行结果", ordersList.size(), count);

    }


    @Test
    public void genShopifyNameOrdersTest() throws Exception {

        String shopifyName = "importxtest";
        MvcResult mvcResult = mockMvc
                .perform(get("/api/shopify/" + shopifyName + "/orders"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.code").value("200")).andReturn();
        String rsStr = mvcResult.getResponse().getContentAsString();
        System.out.println(rsStr);
        JSONObject rsJson = JSON.parseObject(rsStr);

        Assert.assertNotNull("抓取shopify订单异常", rsJson.getString("data"));
        int count = rsJson.getIntValue("data");
        Assert.assertTrue("本次未抓取到shopify订单", count > 0);
    }


    @Test
    public void getDetailsByOrderNo() throws Exception {

        String shopifyName = "importxtest";
        long orderNo = 1875136217122L;
        MvcResult mvcResult = mockMvc
                .perform(get("/api/shopify/" + shopifyName + "/orders/" + orderNo))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.code").value("200")).andReturn();

        String rsStr = mvcResult.getResponse().getContentAsString();
        System.out.println(rsStr);
        JSONObject rsJson = JSON.parseObject(rsStr);
        Assert.assertNotNull("无查询结果", rsJson.getJSONObject("data"));
        JSONObject mapJson = rsJson.getJSONObject("data");

        Assert.assertNotNull("地址查询结果异常", mapJson.getString("address"));

        Assert.assertNotNull("地址Json数据格式异常", mapJson.getString("address"));
        List<Shipping_address> shipping_addressList = JSONArray.parseArray(mapJson.getString("address"), Shipping_address.class);
        Assert.assertTrue("地址Json数据格式异常", CollectionUtils.isNotEmpty(shipping_addressList));

        int count = 0;
        for (Shipping_address address : shipping_addressList) {
            if (orderNo == address.getOrder_no()) {
                count++;
            }
        }

        Assert.assertEquals("获取地址中未包含订单号:" + orderNo, shipping_addressList.size(), count);

        Assert.assertNotNull("详情查询结果异常", mapJson.getString("details"));
        List<Line_items> line_itemsList = JSONArray.parseArray(mapJson.getString("details"), Line_items.class);
        Assert.assertTrue("详情Json数据格式异常", CollectionUtils.isNotEmpty(line_itemsList));

        count = 0;
        for (Line_items lineItem : line_itemsList) {
            if (orderNo == lineItem.getOrder_no()) {
                count++;
            }
        }
        Assert.assertEquals("获取详情中未包含订单号:" + orderNo, line_itemsList.size(), count);

    }

}
