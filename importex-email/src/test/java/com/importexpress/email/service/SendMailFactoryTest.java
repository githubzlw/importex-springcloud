package com.importexpress.email.service;


import com.google.common.collect.ImmutableMap;
import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.SiteEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


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
        MailBean mailBean = MailBean.builder().subject("This is a test email").siteEnum(SiteEnum.KIDS).body(body).isTest(true).build();
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
        MailBean mailBean = MailBean.builder().to("luohao518@yeah.net").siteEnum(SiteEnum.KIDS).body(body).isTest(true).build();
        sendMailFactory.sendMail(mailBean);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendMail3() {
        MailBean mailBean = MailBean.builder().to("luohao518@yeah.net").subject("This is a test email").siteEnum(SiteEnum.KIDS).isTest(true).build();
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
        MailBean mailBean = MailBean.builder().to("luohao518@yeah.net").subject("This is a test email").siteEnum(SiteEnum.KIDS).body(body).model(ImmutableMap.of("key1", "value1")).isTest(true).build();
        sendMailFactory.sendMail(mailBean);
    }


    @Test(expected = IllegalArgumentException.class)
    public void sendMail5() {
        MailBean mailBean = MailBean.builder().to("luohao518@yeah.net").subject("This is a test email").siteEnum(SiteEnum.KIDS).isTest(true).build();
        sendMailFactory.sendMail(mailBean);
    }

    @Test
    public void sendMail6() {
        String body = String.join(
                System.getProperty("line.separator"),
                "<h1>Amazon SES SMTP Email Test</h1>",
                "<p>This email was sent with Amazon SES using the ",
                "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
                " for <a href='https://www.java.com'>Java</a>."
        );
        MailBean mailBean = MailBean.builder().type(1).to("luohao518@yeah.net").subject("This is a test email").siteEnum(SiteEnum.KIDS).body(body).isTest(false).build();
        sendMailFactory.sendMail(mailBean);
    }

}
