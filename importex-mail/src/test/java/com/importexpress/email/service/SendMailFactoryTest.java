package com.importexpress.email.service;

//import com.importexpress.common.pojo.mail.MailBean;
//import com.importexpress.common.pojo.SiteEnum;
//import com.importexpress.common.pojo.mail.TemplateType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SendMailFactoryTest {

//    @Autowired
//    private SendMailFactory sendMailFactory;
//
//    @Test
//    public void sendWelcomeMail() {
//
//        Map<String, String> model =new HashMap<>(9);
//        model.put("logoUrl","logoUrl");
//        model.put("name", "name");
//        model.put("email", "luohao518@yeah.net");
//        model.put("pass", "pass");
//        model.put("activeLink", "activeLink");
//        model.put("here", "here");
//
//        MailBean mailBean = new MailBean();
//        mailBean.setTo("luohao518@yeah.net");
//        mailBean.setSiteEnum(SiteEnum.KIDS);
//        mailBean.setSubject("You've successfully created an ChinaWholesaleInc account. We Welcome You!");
//        mailBean.setTest(true);
//        mailBean.setModel(model);
//        mailBean.setTemplateType(TemplateType.WELCOME);
//
//        sendMailFactory.sendMail(mailBean);
//
//    }
//
//    @Test
//    public void sendWelcomeMailByTrue() {
//
//        Map<String, String> model =new HashMap<>(9);
//        model.put("logoUrl","logoUrl");
//        model.put("name", "name");
//        model.put("email", "luohao518@yeah.net");
//        model.put("pass", "pass");
//        model.put("activeLink", "activeLink");
//        model.put("here", "here");
//
//        MailBean mailBean = new MailBean();
//        mailBean.setTo("luohao518@yeah.net");
//        mailBean.setSiteEnum(SiteEnum.KIDS);
//        mailBean.setSubject("You've successfully created an ChinaWholesaleInc account. We Welcome You!");
//        mailBean.setTest(false);
//        mailBean.setModel(model);
//        mailBean.setTemplateType(TemplateType.WELCOME);
//
//        sendMailFactory.sendMail(mailBean);
//
//    }
//
//    @Test
//    public void sendActivationMail() {
//
//        Map<String, String> model =new HashMap<>(9);
//        model.put("logoUrl","logoUrl");
//        model.put("name", "name");
//        model.put("email", "luohao518@yeah.net");
//        model.put("pass", "pass");
//        model.put("activeLink", "activeLink");
//        model.put("here", "here");
//
//        MailBean mailBean = new MailBean();
//        mailBean.setTo("luohao518@yeah.net");
//        mailBean.setSiteEnum(SiteEnum.KIDS);
//        mailBean.setSubject("You've successfully created an ChinaWholesaleInc account. We Welcome You!");
//        mailBean.setTest(true);
//        mailBean.setModel(model);
//        mailBean.setTemplateType(TemplateType.ACTIVATION);
//
//        sendMailFactory.sendMail(mailBean);
//
//    }


}