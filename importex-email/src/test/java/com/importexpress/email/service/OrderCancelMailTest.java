package com.importexpress.email.service;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.comm.pojo.TemplateType;
import com.importexpress.email.vo.OrderCancelMail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.email.service
 * @date:2020/4/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderCancelMailTest {

    @Autowired
    private TemplateMailService mailService;

    @Autowired
    private SendMailFactory sendMailFactory;

    @Test
    public void sendEmail() {

        String confirmEmail = "1071083166@qq.com";
        String toEmail = "1071083166@qq.com";
        String orderNo = "orderNo";
        String accountLink = SiteEnum.KIDS.getUrl() + "/myaccount";
        String cancelOrderLink = SiteEnum.KIDS.getUrl() + "/orderInfo/getOrders?state=6&orderNo=&timeFrom=&timeTo=&page=1";
        OrderCancelMail build = OrderCancelMail.builder().email(confirmEmail).name(toEmail)
                .websiteType(SiteEnum.KIDS.getCode()).orderNo(orderNo).accountLink(accountLink).cancelOrderLink(cancelOrderLink).build();

        MailBean mailBean = MailBean.builder().to(toEmail).bcc(confirmEmail).siteEnum(SiteEnum.KIDS)
                .subject("Your "+SiteEnum.KIDS.getName()+" Order " + orderNo + " transaction is closed!").type(1)
                .templateType(TemplateType.CANCEL_ORDER).isTest(true).build();

        build.setMailBean(mailBean);
        sendMailFactory.sendMail(mailService.processTemplate(build));
    }
}
