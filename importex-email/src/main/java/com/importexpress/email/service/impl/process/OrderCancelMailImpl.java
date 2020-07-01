package com.importexpress.email.service.impl.process;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.service.TemplateMailProcess;
import com.importexpress.email.vo.OrderCancelMail;
import org.springframework.beans.BeanUtils;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static com.importexpress.email.util.Util.getHtmlContent;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.email.service.impl.process
 * @date:2020/4/22
 */
public class OrderCancelMailImpl implements TemplateMailProcess {
    @Override
    public MailBean process(MailTemplateBean mailTemplateBean, SpringTemplateEngine thymeleafEngine) {

        MailBean mailBean = new MailBean();
        BeanUtils.copyProperties(mailTemplateBean.getMailBean(), mailBean);

        OrderCancelMail carListMail = (OrderCancelMail) mailTemplateBean;

        Map<String, Object> model = new HashMap<String, Object>();

        model.put("email", carListMail.getEmail());
        model.put("name", carListMail.getName());
        model.put("websiteType", carListMail.getWebsiteType());
        model.put("accountLink", carListMail.getAccountLink());
        model.put("cancelOrderLink", carListMail.getCancelOrderLink());
        model.put("orderNo", carListMail.getOrderNo());

        mailBean.setBody(getHtmlContent(model, mailBean.getTemplateType(), thymeleafEngine));
        mailBean.setTest(mailBean.isTest());
        return mailBean;
    }
}
