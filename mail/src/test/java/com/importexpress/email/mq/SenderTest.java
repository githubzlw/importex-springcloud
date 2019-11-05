package com.importexpress.email.mq;

import com.importexpress.common.pojo.SiteEnum;
import com.importexpress.common.pojo.mail.MailBean;
import com.importexpress.common.pojo.mail.TemplateType;
import com.importexpress.email.service.SendMail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SenderTest {

    @Autowired
    private SendMail sender;

    @Test
    public void sendMQ() {
        Map<String, String> model =new HashMap<>(9);
        model.put("logoUrl","logoUrl");
        model.put("name", "name");
        model.put("email", "luohao518@yeah.net");
        model.put("pass", "pass2");
        model.put("activeLink", "activeLink");
        model.put("here", "here");

        MailBean mailBean = new MailBean();
        mailBean.setTo("luohao518@yeah.net");
        mailBean.setSiteEnum(SiteEnum.KIDS);
        mailBean.setSubject("You've successfully created an ChinaWholesaleInc account. We Welcome You!");
        mailBean.setTest(false);
        mailBean.setModel(model);
        mailBean.setTemplateType(TemplateType.WELCOME);

        sender.sendMail(mailBean);

    }

}
