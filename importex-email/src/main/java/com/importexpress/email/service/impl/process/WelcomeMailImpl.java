package com.importexpress.email.service.impl.process;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.comm.pojo.MultiSiteUtil;
import com.importexpress.email.service.TemplateMailProcess;
import com.importexpress.email.vo.WelcomeMailTemplateBean;
import org.springframework.beans.BeanUtils;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static com.importexpress.email.util.Util.getHtmlContent;

/**
 * @Author jack.luo
 * @create 2020/4/15 16:05
 * Description
 */
public class WelcomeMailImpl implements TemplateMailProcess {

    @Override
    public MailBean process(MailTemplateBean mailTemplateBean, SpringTemplateEngine thymeleafEngine) {
        MailBean mailBean = new MailBean();
        BeanUtils.copyProperties(mailTemplateBean.getMailBean(),mailBean);
        //welcome mail
        WelcomeMailTemplateBean welcomeBean = (WelcomeMailTemplateBean)mailTemplateBean;
        String activeLink = mailBean.getSiteEnum().getUrl() + "/userController/upUserState?code="
                + welcomeBean.getActivationCode() + "&toEmail=" + mailBean.getTo() + "&from=" + welcomeBean.getFrom();
        String here = mailBean.getSiteEnum().getUrl() + "/individual/getCenter";
        int site = MultiSiteUtil.site;
        Map<String, Object> model = new HashMap<>();
        model.put("logoUrl", String.valueOf(site));
        model.put("name", welcomeBean.getName());
        model.put("email", mailBean.getTo());
        model.put("pass", welcomeBean.getPass());
        model.put("activeLink", activeLink);
        model.put("here", here);
        String title = "You've successfully created an " + mailBean.getSiteEnum().getName() + " account. We Welcome You!";
        mailBean.setSubject(title);
        mailBean.setBody(getHtmlContent(model, mailBean.getTemplateType(),thymeleafEngine));
        mailBean.setTest(mailBean.isTest());
        return mailBean;
    }



}
