package com.importexpress.email.service.impl.process;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.comm.pojo.MultiSiteUtil;
import com.importexpress.email.service.TemplateMailProcess;
import com.importexpress.email.vo.AccountUpdateMailTemplateBean;
import com.importexpress.email.vo.ReceivedMailTemplateBean;
import org.springframework.beans.BeanUtils;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static com.importexpress.email.util.Util.getHtmlContent;

/**
 * @Author jack.luo
 * @create 2020/4/16 16:05
 * Description
 */
public class ReceivedMailImpl implements TemplateMailProcess {

    @Override
    public MailBean process(MailTemplateBean mailTemplateBean, SpringTemplateEngine thymeleafEngine) {
        MailBean mailBean = new MailBean();
        BeanUtils.copyProperties(mailTemplateBean.getMailBean(),mailBean);
        //welcome mail
        ReceivedMailTemplateBean bean = (ReceivedMailTemplateBean)mailTemplateBean;
        String here = bean.getMailBean().getSiteEnum().getUrl() + "individual/getCenter";
        Map<String, Object> model = new HashMap<>();
        model.put("name", bean.getName());
        model.put("orderAddressEmailInfo", bean.getOrderAddressEmailInfo());
        model.put("orderEmailBeans", bean.getOrderEmailBeans());
        model.put("here", here);
        model.put("transport", bean.getTransport());
        model.put("shippingMethod", bean.getShippingMethod());
        model.put("estimatedShipOutDate", bean.getFormat());
        model.put("logoUrl", bean.getMailBean().getSiteEnum().getCode());
        model.put("showUrl", bean.getMailBean().getSiteEnum().getUrl());
        model.put("imgLogo", bean.getMailBean().getSiteEnum().getUrl());
        String title = "Your order is received!";
        mailBean.setSubject(title);
        mailBean.setBody(getHtmlContent(model, mailBean.getTemplateType(),thymeleafEngine));
        mailBean.setTest(mailBean.isTest());

        return mailBean;
    }




}
