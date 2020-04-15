package com.importexpress.email.service;

import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.email.vo.WelcomeBean;
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
public class SendMailTest {

    @Autowired
    private SendEmailService sendMail;

    @Test
    public void welcome() {


        sendMail.genWelcomeBodyAndSend(WelcomeBean.builder().siteEnum(SiteEnum.KIDS).toEmail("luohao518@yeah.net").name("jack").pass("pass").from("kids@mail.com").activationCode("aaaaaaaabbbbbbbbbcccccccccc").test(true).build());
    }
}
