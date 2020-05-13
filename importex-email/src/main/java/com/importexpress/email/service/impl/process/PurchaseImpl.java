package com.importexpress.email.service.impl.process;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.service.TemplateMailProcess;
import com.importexpress.email.vo.PurchaseMail;
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
public class PurchaseImpl implements TemplateMailProcess {
    @Override
    public MailBean process(MailTemplateBean mailTemplateBean, SpringTemplateEngine thymeleafEngine) {

        MailBean mailBean = new MailBean();
        BeanUtils.copyProperties(mailTemplateBean.getMailBean(), mailBean);

        PurchaseMail purchaseMail = (PurchaseMail) mailTemplateBean;

        Map<String, Object> modelM = new HashMap<String, Object>();

        modelM.put("name", purchaseMail.getName());
        modelM.put("orderid", purchaseMail.getOrderid());
        modelM.put("recipients", purchaseMail.getRecipients());
        modelM.put("street", purchaseMail.getStreet());
        modelM.put("street1", purchaseMail.getStreet1());
        modelM.put("city", purchaseMail.getCity());
        modelM.put("state", purchaseMail.getState());
        modelM.put("country", purchaseMail.getCountry());
        modelM.put("zipCode", purchaseMail.getZipCode());
        modelM.put("phone", purchaseMail.getPhone());
        modelM.put("websiteType", purchaseMail.getWebsiteType());
        modelM.put("toHref", purchaseMail.getToHref());

        mailBean.setBody(getHtmlContent(modelM, mailBean.getTemplateType(), thymeleafEngine));
        mailBean.setTest(mailBean.isTest());
        return mailBean;
    }
}
