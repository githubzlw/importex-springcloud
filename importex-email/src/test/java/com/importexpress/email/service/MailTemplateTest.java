package com.importexpress.email.service;

import com.importexpress.comm.pojo.*;
import com.importexpress.email.vo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

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
        mailBean.setTo("china@gmail.com");
        mailBean.setTemplateType(TemplateType.WELCOME);

        WelcomeMailTemplateBean welcomeBean = new WelcomeMailTemplateBean();
        welcomeBean.setActivationCode("aabbcc");
        welcomeBean.setFrom("kairong");
        welcomeBean.setName("china@gmail.com");
        welcomeBean.setPass("pass");
        welcomeBean.setMailBean(mailBean);

        sendMailFactory.sendMail(mailService.processTemplate(welcomeBean));
    }

    @Test
    public void newPassword() {

        MailBean mailBean = new MailBean();
        mailBean.setTest(true);
        mailBean.setSiteEnum(SiteEnum.IMPORTX);
        mailBean.setTo("china@gmail.com");
        mailBean.setTemplateType(TemplateType.NEW_PASSWORD);

        NewPasswordMailTemplateBean bean = new NewPasswordMailTemplateBean();
        bean.setBusinessName("busName");
        bean.setBusinessIntroduction("bus introduct");
        bean.setName("china@gmail.com");
        bean.setPass("pass");
        bean.setMailBean(mailBean);

        sendMailFactory.sendMail(mailService.processTemplate(bean));
    }

    @Test
    public void activation() {

        MailBean mailBean = new MailBean();
        mailBean.setTest(true);
        mailBean.setSiteEnum(SiteEnum.PETS);
        mailBean.setTo("china@gmail.com");
        mailBean.setTemplateType(TemplateType.ACTIVATION);

        ActivationMailTemplateBean bean = new ActivationMailTemplateBean();
        bean.setActivationCode("abcdef");
        bean.setName("china@gmail.com");
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
        mailBean.setTo("china@gmail.com");
        mailBean.setTemplateType(TemplateType.ACCOUNT_UPDATE);

        AccountUpdateMailTemplateBean bean = new AccountUpdateMailTemplateBean();
        bean.setActivationCode("abcdef");
        bean.setName("china@gmail.com");
        bean.setPass("pass");
        bean.setMailBean(mailBean);

        sendMailFactory.sendMail(mailService.processTemplate(bean));
    }

    @Test
    public void received() {

        MailBean mailBean = new MailBean();
        mailBean.setTest(true);
        mailBean.setSiteEnum(SiteEnum.IMPORTX);
        mailBean.setTo("china@gmail.com");
        mailBean.setTemplateType(TemplateType.RECEIVED);

        ReceivedMailTemplateBean bean = new ReceivedMailTemplateBean();
        bean.setFormat("2019-09-04");
        bean.setName("china@gmail.com");
        OrderAddressEmailBean oae = new OrderAddressEmailBean();
        oae.setRecipients("rigoberto arciga carapia");
        oae.setAddress("inchatiro # 286 colinia vista bella");
        oae.setStreet("");
        oae.setCountry("MEXICO");
        oae.setPhoneNumber("014433159817");
        oae.setOrderno("2190828580");
        oae.setPay_price("300.33");
        bean.setOrderAddressEmailInfo(oae);

        List<OrderEmailBean> oebs = new ArrayList<>();
        OrderEmailBean oeb;
        for(int i=0;i<10;i++){
            oeb = new OrderEmailBean();
            oeb.setGoodsname("Diy Simple"+i);
            oeb.setGoodsprice("10.0"+i);
            oeb.setYourorder(i+1);
            oeb.setCar_img("https://img.import-express.com/importcsvimg/coreimg1/535955960203/3241446445_2118088041.60x60.jpg");
            oebs.add(oeb);
        }
        bean.setOrderEmailBeans(oebs);


        bean.setShippingMethod("CNE");
        bean.setTransport("13-19 days");
        bean.setMailBean(mailBean);

        sendMailFactory.sendMail(mailService.processTemplate(bean));
    }
}
