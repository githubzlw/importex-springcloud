package com.importexpress.email.service;


import com.google.common.collect.ImmutableMap;
import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.SiteEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * @Author jack.luo
 * @create 2019/12/20 9:43
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SendMailFactoryTest {

    @Autowired
    private SendMailFactory sendMailFactory;

    @Test(expected = IllegalArgumentException.class)
    public void sendMail1() {
        String body = String.join(
                System.getProperty("line.separator"),
                "<h1>Amazon SES SMTP Email Test</h1>",
                "<p>This email was sent with Amazon SES using the ",
                "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
                " for <a href='https://www.java.com'>Java</a>."
        );
        MailBean mailBean = new MailBean();
        mailBean.setSubject("This is a test email");
        mailBean.setSiteEnum(SiteEnum.KIDS);
        mailBean.setBody(body);
        mailBean.setTest(true);
        sendMailFactory.sendMail(mailBean);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendMail2() {
        String body = String.join(
                System.getProperty("line.separator"),
                "<h1>Amazon SES SMTP Email Test</h1>",
                "<p>This email was sent with Amazon SES using the ",
                "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
                " for <a href='https://www.java.com'>Java</a>."
        );
        MailBean mailBean = new MailBean();
        mailBean.setTo("luohao518@yeah.net");
        mailBean.setSiteEnum(SiteEnum.KIDS);
        mailBean.setBody(body);
        mailBean.setTest(true);
        sendMailFactory.sendMail(mailBean);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendMail3() {
        MailBean mailBean = new MailBean();
        mailBean.setTo("luohao518@yeah.net");
        mailBean.setSubject("This is a test email");
        mailBean.setSiteEnum(SiteEnum.KIDS);
        mailBean.setTest(true);
        sendMailFactory.sendMail(mailBean);
    }


    @Test(expected = IllegalArgumentException.class)
    public void sendMail4() {
        String body = String.join(
                System.getProperty("line.separator"),
                "<h1>Amazon SES SMTP Email Test</h1>",
                "<p>This email was sent with Amazon SES using the ",
                "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
                " for <a href='https://www.java.com'>Java</a>."
        );
        MailBean mailBean = new MailBean();
        mailBean.setTo("luohao518@yeah.net");
        mailBean.setSubject("This is a test email");
        mailBean.setSiteEnum(SiteEnum.KIDS);
        mailBean.setBody(body);
        mailBean.setModel(ImmutableMap.of("key1", "value1"));
        mailBean.setTest(true);
        sendMailFactory.sendMail(mailBean);
    }


    @Test(expected = IllegalArgumentException.class)
    public void sendMail5() {
        MailBean mailBean = new MailBean();
        mailBean.setTo("luohao518@yeah.net");
        mailBean.setSubject("This is a test email");
        mailBean.setSiteEnum(SiteEnum.KIDS);
        mailBean.setTest(true);
        sendMailFactory.sendMail(mailBean);
    }

    @Test
    public void sendMail() {
        String body = String.join(
                System.getProperty("line.separator"),
                "<h1>Amazon SES SMTP Email Test</h1>",
                "<p>This email was sent with Amazon SES using the ",
                "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
                " for <a href='https://www.java.com'>Java</a>."
        );
        MailBean mailBean = new MailBean();
        mailBean.setTo("luohao518@yeah.net");
        mailBean.setSubject("This is a test email");
        mailBean.setSiteEnum(SiteEnum.KIDS);
        mailBean.setBody(body);
        mailBean.setTest(false);
        sendMailFactory.sendMail(mailBean);
    }

}
