package com.importexpress.email.controller;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MultiSiteUtil;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.comm.pojo.TemplateType;
import com.importexpress.email.util.RestTemplateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.email.controller
 * @date:2019/12/23
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SendEmailControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }


//    @Test
//    public void testSendEmail() throws Exception {
//        Map<String, Object> model = new HashMap<>();
//        // String email = "kairong404report@hotmail.com";
//        String email = "1071083166@qq.com";
//        model.put("date", LocalDateTime.now());
//        model.put("param", "{}");
//        String title = "400 Error!";
//        model.put("title", title);
//        String url = MultiSiteUtil.siteEnum.getUrl();
//        model.put("requestURL", url);
//        MailBean mailBean = MailBean.builder().to(email).subject(title).model(model).templateType(TemplateType.SEND_ERROR)
//                .siteEnum(MultiSiteUtil.siteEnum).build();
//        mailBean.setTest(true);
//        /*MvcResult mvcResult = mockMvc.perform(post("/sendMail/mailBean")
//                .params(MapAndBeanUtil.bean2map(mailBean)))
//                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value("200")).andReturn();
//        String rsStr = mvcResult.getResponse().getContentAsString();
//
//        Assert.assertTrue("发送异常", rsStr.contains(email));*/
//
//        RestTemplate restTemplate = RestTemplateUtil.getInstance("utf-8");
//
//        url = "http://localhost:18001/sendMail/mailBean";
//        String result = restTemplate.postForObject(url, mailBean, String.class);
//        Assert.assertTrue("发送异常", result.contains(email));
//    }


    @Test
    public void genWelcomeBodyAndSend() throws Exception {
        String email = "1071083166@qq.com";
        String name = "name";
        String pass = "123123";
        String from = "/Goods/getShopCar";
        SiteEnum siteEnum = SiteEnum.KIDS;
        MvcResult mvcResult = mockMvc.perform(post("/sendMail/genWelcomeBodyAndSend")
                .param("email", email).param("name", name).param("pass", pass)
                .param("from", from).param("siteEnum", siteEnum.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value("200")).andReturn();
        String rsStr = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("发送异常", rsStr.contains(email));
    }


    @Test
    public void genReceivedBodyAndSend() throws Exception {
        String orderNo = "2191225K517";
        int userId = 15937;
        SiteEnum siteEnum = SiteEnum.KIDS;
        MvcResult mvcResult = mockMvc.perform(post("/sendMail/genReceivedBodyAndSend")
                .param("orderNo", orderNo).param("userId", String.valueOf(userId)).param("siteEnum", siteEnum.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value("200")).andReturn();
        String rsStr = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("发送异常", rsStr.contains(String.valueOf(userId)));
    }


    @Test
    public void genNewPasswordBodyAndSend() throws Exception {
        String email = "1071083166@qq.com";
        String passWord = "123123";
        String businessName = "businessName";
        String businessIntroduction = "businessIntroduction";
        SiteEnum siteEnum = SiteEnum.KIDS;
        MvcResult mvcResult = mockMvc.perform(post("/sendMail/genNewPasswordBodyAndSend")
                .param("email", email)
                .param("passWord", passWord)
                .param("businessName", businessName)
                .param("businessIntroduction", businessIntroduction)
                .param("siteEnum", siteEnum.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value("200")).andReturn();
        String rsStr = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("发送异常", rsStr.contains(email));
    }


    @Test
    public void genActivationBodyAndSend() throws Exception {
        String email = "1071083166@qq.com";
        String name = "name";
        String pass = "123123";
        String fromWhere = "/Goods/getShopCar";
        SiteEnum siteEnum = SiteEnum.KIDS;
        MvcResult mvcResult = mockMvc.perform(post("/sendMail/genActivationBodyAndSend")
                .param("email", email)
                .param("name", name)
                .param("pass", pass)
                .param("fromWhere", fromWhere)
                .param("siteEnum", siteEnum.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value("200")).andReturn();
        String rsStr = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("发送异常", rsStr.contains(email));
    }


    @Test
    public void genAccountUpdateBodyAndSend() throws Exception {
        String email = "1071083166@qq.com";
        SiteEnum siteEnum = SiteEnum.KIDS;
        MvcResult mvcResult = mockMvc.perform(post("/sendMail/genAccountUpdateBodyAndSend")
                .param("email", email)
                .param("siteEnum", siteEnum.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value("200")).andReturn();
        String rsStr = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("发送异常", rsStr.contains(email));
    }

    @Test
    public void justSend() throws Exception {
        String email = "1071083166@qq.com";
        String content = "content";
        String title = "title";
        SiteEnum siteEnum = SiteEnum.KIDS;
        MvcResult mvcResult = mockMvc.perform(post("/sendMail/justSend")
                .param("toEmail", email)
                .param("content", content)
                .param("title", title)
                .param("siteEnum", siteEnum.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value("200")).andReturn();
        String rsStr = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("发送异常", rsStr.contains(email));
    }
}
