package com.importexpress.email.service;


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
