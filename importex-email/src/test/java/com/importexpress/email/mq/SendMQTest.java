package com.importexpress.email.mq;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.comm.pojo.TemplateType;
import com.importexpress.email.config.Config;
import com.importexpress.email.vo.WelcomeMailTemplateBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void rabbitBeanTest() {
        String body = String.join(
                System.getProperty("line.separator"),
                "<h1>Amazon SES SMTP Email Test</h1>",
                "<p>This email was sent with Amazon SES using the ",
                "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
                " for <a href='https://www.java.com'>Java</a>."
        );
        MailBean mailBean = MailBean.builder().to("1071083166@qq.com").subject("This is a test email").siteEnum(SiteEnum.KIDS)
                .body(body).isTest(false).build();
        rabbitTemplate.convertAndSend(Config.QUEUE_MAIL, mailBean);
    }

    @Test
    public void rabbitStringTest() {
        rabbitTemplate.convertAndSend(Config.QUEUE_MAIL, "mailBean");
    }

    @Test
    public void sendMQToMail1() {

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

        sender.sendMQToMail(welcomeBean);
    }

    @Test
    public void sendMQToMail2() {
        String body = String.join(
                System.getProperty("line.separator"),
                "<h1>Amazon SES SMTP Email Test</h1>",
                "<p>This email was sent with Amazon SES using the ",
                "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
                " for <a href='https://www.java.com'>Java</a>."
        );

        MailBean mailBean = new MailBean();
        mailBean.setTest(true);
        mailBean.setSiteEnum(SiteEnum.KIDS);
        mailBean.setTo("luohao@kairong.com");
        mailBean.setSubject("test mail");
        mailBean.setBody(body);

        WelcomeMailTemplateBean welcomeBean = new WelcomeMailTemplateBean();
        welcomeBean.setMailBean(mailBean);

        sender.sendMQToMail(welcomeBean);
    }

//    @Test
//    public void sendMQToMail2() {
//
//        Map<String, Object> model = new HashMap<>();
//        model.put("logoUrl", SiteEnum.KIDS.getUrl());
//        model.put("name", "name1");
//        model.put("email", "test@gmail.com");
//        model.put("pass", "pass1");
//        model.put("activeLink", "activeLink......");
//        model.put("here", "here");
//
//        MailBean mailBean = MailBean.builder().to("luohao518@yeah.net").subject("This is a ACTIVATION email").siteEnum(SiteEnum.KIDS)
//                .model(model).templateType(TemplateType.ACTIVATION).isTest(false).build();
//        sender.sendMQToMail(mailBean);
//    }

//    @Test
//    public void sendMQToMail3() {
//
//        Map<String, Object> model = new HashMap<>();
//        model.put("logoUrl", SiteEnum.IMPORTX.getUrl());
//        model.put("name", "name1");
//        model.put("email", "test@gmail.com");
//        model.put("pass", "pass1");
//        model.put("activeLink", "activeLink......");
//        model.put("here", "here");
//        MailBean mailBean = MailBean.builder().to("luohao518@yeah.net").subject("This is a ACTIVATION email").siteEnum(SiteEnum.IMPORTX)
//                .model(model).templateType(TemplateType.ACTIVATION).isTest(false).build();
//
//        IntStream.range(1, 100).forEach(i -> {
//            sender.sendMQToMail(mailBean);
//            try {
//                Thread.currentThread().sleep(100);
//            } catch (InterruptedException e) {
//
//            }
//        });
//    }
}
