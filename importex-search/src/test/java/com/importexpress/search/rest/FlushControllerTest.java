package com.importexpress.search.rest;

import com.google.gson.Gson;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.search.common.InitApplicationParameter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FlushControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Autowired
    private InitApplicationParameter init;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        init.init(wac.getServletContext());
    }

    /**
     * 全部1688Category的数据导入
     */
    @Test
    public void category() throws Exception {
        String contentAsString = mockMvc.perform(get("/flush/category"))
                .andReturn().getResponse().getContentAsString();

        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("结果:" + result.getData().toString());
    }

    /**
     * 获取规格属性表信息
     */
    @Test
    public void newPvid() throws Exception {
        String contentAsString = mockMvc.perform(get("/flush/atrrid"))
                .andReturn().getResponse().getContentAsString();

        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("结果:" + result.getData().toString());
    }

    /**
     * 初始化同义词列表
     */
    @Test
    public void synonyms() throws Exception {
        String contentAsString = mockMvc.perform(get("/flush/synonyms/key"))
                .andReturn().getResponse().getContentAsString();

        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("结果:" + result.getData().toString());
    }

    /**
     * 初始化类别同义词列表
     */
    @Test
    public void synonymsCategory() throws Exception {
        String contentAsString = mockMvc.perform(get("/flush/synonyms/category"))
                .andReturn().getResponse().getContentAsString();

        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("结果:" + result.getData().toString());
    }

    /**
     * 初始化搜索词对应的最低价和最高价
     */
    @Test
    public void categoryPrice() throws Exception {
        String contentAsString = mockMvc.perform(get("/flush/category/price"))
                .andReturn().getResponse().getContentAsString();
        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("结果:" + result.getData().toString());
    }

    /**
     * 初始化盲搜类别列表
     */
    @Test
    public void blindSearchCategory() throws Exception {
        String contentAsString = mockMvc.perform(get("/flush/blind/category"))
                .andReturn().getResponse().getContentAsString();

        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("结果:" + result.getData().toString());
    }

    /**
     * 初始化类别限制列表
     */
    @Test
    public void specialCatid() throws Exception {
        String contentAsString = mockMvc.perform(get("/flush/special/category"))
                .andReturn().getResponse().getContentAsString();

        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("结果:" + result.getData().toString());
    }

    /**
     * 初始化优先类别列表
     */
    @Test
    public void priorityCategory() throws Exception {
        String contentAsString = mockMvc.perform(get("/flush/priority/category"))
                .andReturn().getResponse().getContentAsString();

        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("结果:" + result.getData().toString());
    }

    /**
     * 初始化反关键词集合
     */
    @Test
    public void autiKey() throws Exception {
        String contentAsString = mockMvc.perform(get("/flush/autikey"))
                .andReturn().getResponse().getContentAsString();

        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("结果:" + result.getData().toString());
    }

    /**
     * 获取搜索页面底部推荐词 whj
     */
    @Test
    public void recommendedWords() throws Exception {
        String contentAsString = mockMvc.perform(get("/flush/recommen"))
                .andReturn().getResponse().getContentAsString();

        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("结果:" + result.getData().toString());
    }
}