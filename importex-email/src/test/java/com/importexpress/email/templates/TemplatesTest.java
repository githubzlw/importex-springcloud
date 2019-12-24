package com.importexpress.email.templates;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MultiSiteUtil;
import com.importexpress.comm.pojo.TemplateType;
import com.importexpress.email.TestBeanUtils;
import com.importexpress.email.pojo.OrderAddressEmailBean;
import com.importexpress.email.pojo.OrderEmailBean;
import com.importexpress.email.service.SendMailFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.email.templates
 * @date:2019/12/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TemplatesTest {
    @Autowired
    private SendMailFactory sendMailFactory;


    @Test
    public void accountUpdateTest() {

        String email = "1071083166@qq.com";
        String activationPassCode = Md5Util.encoder(email + System.currentTimeMillis());
        String activeLink = MultiSiteUtil.siteEnum.getUrl() + "/forgotPassword/passActivate?email=" + email
                + "&validateCode=" + activationPassCode;
        String here = MultiSiteUtil.siteEnum.getUrl() + "individual/getCenter";
        Map<String, Object> model = new HashMap<>();
        model.put("name", email);
        model.put("email", email);
        model.put("activeLink", activeLink);
        model.put("here", here);
        int site = MultiSiteUtil.site;
        model.put("logoUrl", String.valueOf(site));
        String title;
        switch (site) {
            case 2:
                title = "Reset Your Password At KidsProductWholesale";
                break;
            case 4:
                title = "Reset Your Password At PetStoreInc";
                break;
            default:
                title = "Reset Your Password At ImportExpress";
        }
        MailBean mailBean = MailBean.builder().to(email).subject(title).model(model).templateType(TemplateType.ACCOUNT_UPDATE).build();
        mailBean.setTest(true);
        sendMailFactory.sendMail(mailBean);
    }

    @Test
    public void activationTest() {
        String email = "1071083166@qq.com";
        String name = "name";
        String pass = "pass";
        String activationCode = Md5Util.encoder(email + UUID.randomUUID().toString().replaceAll("-", ""));
        String activeLink = MultiSiteUtil.siteEnum.getUrl() + "userController/upUserState?code=" + activationCode + "&email=" + email + "&from=";
        String here = MultiSiteUtil.siteEnum.getUrl() + "individual/getCenter";
        Map<String, Object> model = new HashMap<>();
        int site = MultiSiteUtil.site;
        model.put("logoUrl", String.valueOf(site));
        model.put("name", name);
        model.put("email", email);
        model.put("pass", pass);
        model.put("activeLink", activeLink);
        model.put("here", here);
        String title;
        switch (site) {
            case 2:
                title = "Reset Your Password KidsProductWholesale account.";
                break;
            case 4:
                title = "Reset Your Password PetStoreInc account.";
                break;
            default:
                title = "Reset Your Password ImportExpress account.";
        }
        MailBean mailBean = MailBean.builder().to(email).subject(title).model(model).templateType(TemplateType.ACTIVATION).build();
        mailBean.setTest(true);
        sendMailFactory.sendMail(mailBean);
    }


    @Test
    public void newPassWordTest() {

        String email = "1071083166@qq.com";
        String passWord = "passWord";
        String businessName = "businessName";
        String businessIntroduction = "businessIntroduction";

        String here = MultiSiteUtil.siteEnum.getUrl() + "individual/getCenter";
        Map<String, Object> model = new HashMap<>();
        model.put("businessIntroduction", businessIntroduction);
        model.put("businessName", businessName);
        model.put("name", email);
        model.put("email", email);
        model.put("pass", passWord);
        model.put("here", here);
        int site = MultiSiteUtil.site;
        model.put("logoUrl", String.valueOf(site));
        String title = "You've successfully complete your info!";
        MailBean mailBean = MailBean.builder().to(email).subject(title).model(model).templateType(TemplateType.NEW_PASSWORD).build();
        mailBean.setTest(true);
        sendMailFactory.sendMail(mailBean);
    }


    @Test
    public void receivedTest() {
        String email = "1071083166@qq.com";
        String name = "name";
        String valueFromResourceFile = MultiSiteUtil.siteEnum.getUrl();
        String here = valueFromResourceFile + "individual/getCenter";
        Map<String, Object> model = new HashMap<>();
        model.put("name", StringUtils.isNotBlank(name) ? name : "Valued Customer");

        OrderAddressEmailBean orderAddressEmailInfo = TestBeanUtils.getOrderAddressEmailBean();
        model.put("orderAddressEmailInfo", orderAddressEmailInfo);

        List<OrderEmailBean> orderEmailBeans = TestBeanUtils.getOrderEmailBeans();
        model.put("orderEmailBeans", orderEmailBeans);
        model.put("here", here);

        String transport = "transport";
        model.put("transport", transport);
        String shippingMethod = "shippingMethod";
        model.put("shippingMethod", shippingMethod);
        String format = "format";
        model.put("estimatedShipOutDate", format);
        int site = MultiSiteUtil.site;
        model.put("logoUrl", site);
        model.put("showUrl", valueFromResourceFile);
        model.put("imgLogo", MultiSiteUtil.siteEnum.getUrl());

        String title = "Your order is received!";
        MailBean mailBean = MailBean.builder().to(email).subject(title).model(model).templateType(TemplateType.RECEIVED).build();
        mailBean.setTest(true);
        sendMailFactory.sendMail(mailBean);
    }


    @Test
    public void welcomeTest() {
        String email = "1071083166@qq.com";
        String name = "name";
        String pass = "pass";
        String activationCode = Md5Util.encoder(email + UUID.randomUUID().toString().replaceAll("-", ""));
        String activeLink = MultiSiteUtil.siteEnum.getUrl() + "userController/upUserState?code=" + activationCode + "&email=" + email + "&from=";
        String here = MultiSiteUtil.siteEnum.getUrl() + "individual/getCenter";
        int site = MultiSiteUtil.site;
        Map<String, Object> model = new HashMap<>(9);
        model.put("logoUrl", String.valueOf(site));
        model.put("name", name);
        model.put("email", email);
        model.put("pass", pass);
        model.put("activeLink", activeLink);
        model.put("here", here);
        String title;
        switch (site) {
            case 2:
                title = "You've successfully created an KidsProductWholesale account. We Welcome You!";
                break;
            case 4:
                title = "You've successfully created an PetStoreInc account. We Welcome You!";
                break;
            default:
                title = "You've successfully created an ImportExpress account. We Welcome You!";
        }
        MailBean mailBean = MailBean.builder().to(email).subject(title).model(model).templateType(TemplateType.WELCOME).build();
        mailBean.setTest(true);
        sendMailFactory.sendMail(mailBean);
    }

    @Test
    public void sendErrorTest() {
        Map<String, Object> model = new HashMap<>();
        // String email = "kairong404report@hotmail.com";
        String email = "1071083166@qq.com";
        model.put("date", LocalDateTime.now());
        model.put("param", "{}");
        String title = "400 Error!";
        model.put("title", title);
        String url = MultiSiteUtil.siteEnum.getUrl();
        model.put("requestURL", url);
        MailBean mailBean = MailBean.builder().to(email).subject(title).model(model).templateType(TemplateType.SEND_ERROR).build();
        mailBean.setTest(true);
        sendMailFactory.sendMail(mailBean);
    }
}
