package com.importexpress.email.service;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.comm.pojo.TemplateType;
import com.importexpress.email.vo.AccountUpdateMailTemplateBean;
import com.importexpress.email.vo.ActivationMailTemplateBean;
import com.importexpress.email.vo.NewPasswordMailTemplateBean;
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
public class MailTemplateTest {

    @Autowired
    private TemplateMailService mailService;

    @Autowired
    private SendMailFactory sendMailFactory;

    @Test
    public void welcome() {

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

    @Test
    public void newPassword() {

        MailBean mailBean = new MailBean();
        mailBean.setTest(true);
        mailBean.setSiteEnum(SiteEnum.IMPORTX);
        mailBean.setTo("luohao@kairong.com");
        mailBean.setTemplateType(TemplateType.NEW_PASSWORD);

        NewPasswordMailTemplateBean bean = new NewPasswordMailTemplateBean();
        bean.setBusinessName("busName");
        bean.setBusinessIntroduction("bus introduct");
        bean.setName("luohao");
        bean.setPass("pass");
        bean.setMailBean(mailBean);

        sendMailFactory.sendMail(mailService.processTemplate(bean));
    }

    @Test
    public void activation() {

        MailBean mailBean = new MailBean();
        mailBean.setTest(true);
        mailBean.setSiteEnum(SiteEnum.PETS);
        mailBean.setTo("luohao@kairong.com");
        mailBean.setTemplateType(TemplateType.ACTIVATION);

        ActivationMailTemplateBean bean = new ActivationMailTemplateBean();
        bean.setActivationCode("abcdef");
        bean.setName("luohao");
        bean.setPass("pass");
        bean.setFrom("from");
        bean.setMailBean(mailBean);

        sendMailFactory.sendMail(mailService.processTemplate(bean));
    }

    @Test
    public void accountUpdate() {

        MailBean mailBean = new MailBean();
        mailBean.setTest(true);
        mailBean.setSiteEnum(SiteEnum.KIDS);
        mailBean.setTo("luohao@kairong.com");
        mailBean.setTemplateType(TemplateType.ACCOUNT_UPDATE);

        AccountUpdateMailTemplateBean bean = new AccountUpdateMailTemplateBean();
        bean.setActivationCode("abcdef");
        bean.setName("luohao");
        bean.setPass("pass");
        bean.setMailBean(mailBean);

        sendMailFactory.sendMail(mailService.processTemplate(bean));
    }
}
