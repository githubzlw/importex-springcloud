package com.importexpress.email.service;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.comm.pojo.TemplateType;
import com.importexpress.email.vo.WelcomeMailTemplateBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author jack.luo
 * @create 2020/4/14 18:03
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WelcomeMailTest {

    @Autowired
    private TemplateMailService mailService;

    @Autowired
    private SendMailFactory sendMailFactory;

    @Test
    public void sendMail() {

        MailBean mailBean = new MailBean();
        mailBean.setTest(true);
        mailBean.setSiteEnum(SiteEnum.KIDS);
        mailBean.setTo("luohao@kairong.com");
        mailBean.setTemplateType(TemplateType.WELCOME);

        WelcomeMailTemplateBean welcomeBean = new WelcomeMailTemplateBean();
        welcomeBean.setActivationCode("aabbcc");
        welcomeBean.setFrom("kairong");
        welcomeBean.setName("luohao");
        welcomeBean.setPass("pass");
        welcomeBean.setMailBean(mailBean);

        sendMailFactory.sendMail(mailService.processTemplate(welcomeBean));
    }
}
