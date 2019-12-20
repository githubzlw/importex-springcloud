package com.importexpress.email.mq;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.comm.pojo.TemplateType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @Author jack.luo
 * @create 2019/12/20 12:58
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SendMQTest {

    @Autowired
    private SendMQ sender;

    @Test
    public void sendMQToMail1() {
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
        sender.sendMQToMail(mailBean);
    }

    @Test
    public void sendMQToMail2() {

        MailBean mailBean = new MailBean();
        mailBean.setTo("luohao518@yeah.net");
        mailBean.setSubject("This is a ACTIVATION email");
        mailBean.setSiteEnum(SiteEnum.KIDS);
        mailBean.setTest(false);
        Map<String, String> model = new HashMap<>();
        model.put("logoUrl", SiteEnum.KIDS.getUrl());
        model.put("name", "name1");
        model.put("email", "test@gmail.com");
        model.put("pass", "pass1");
        model.put("activeLink", "activeLink......");
        model.put("here", "here");
        mailBean.setModel(model);
        mailBean.setTemplateType(TemplateType.ACTIVATION);
        sender.sendMQToMail(mailBean);
    }

    @Test
    public void sendMQToMail3() {

        MailBean mailBean = new MailBean();
        mailBean.setTo("luohao518@yeah.net");
        mailBean.setSubject("This is a ACTIVATION email");
        mailBean.setSiteEnum(SiteEnum.IMPORTX);
        mailBean.setTest(true);
        Map<String, String> model = new HashMap<>();
        model.put("logoUrl", SiteEnum.IMPORTX.getUrl());
        model.put("name", "name1");
        model.put("email", "test@gmail.com");
        model.put("pass", "pass1");
        model.put("activeLink", "activeLink......");
        model.put("here", "here");
        mailBean.setModel(model);
        mailBean.setTemplateType(TemplateType.ACTIVATION);
        IntStream.range(1, 100).forEach(i -> {
            sender.sendMQToMail(mailBean);
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {

            }
        });
    }
}
