package com.importexpress.email.service.impl.process;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.service.TemplateMailProcess;
import com.importexpress.email.vo.CouponMail;
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
public class CouponImpl implements TemplateMailProcess {
    @Override
    public MailBean process(MailTemplateBean mailTemplateBean, SpringTemplateEngine thymeleafEngine) {

        MailBean mailBean = new MailBean();
        BeanUtils.copyProperties(mailTemplateBean.getMailBean(), mailBean);

        CouponMail couponMail = (CouponMail) mailTemplateBean;

        Map<String, Object> model = new HashMap<String, Object>();

        model.put("firstName", couponMail.getFirstName());
        model.put("description", couponMail.getDescription());
        model.put("codeId", couponMail.getCodeId());
        model.put("validityPeriod", couponMail.getValidityPeriod());
        model.put("codeValue", couponMail.getCodeValue());

        model.put("email", couponMail.getEmail());
        model.put("title", couponMail.getTitle());
        model.put("websiteType", couponMail.getWebsiteType());

        mailBean.setBody(getHtmlContent(model, mailBean.getTemplateType(), thymeleafEngine));
        mailBean.setTest(mailBean.isTest());
        return mailBean;
    }
}
