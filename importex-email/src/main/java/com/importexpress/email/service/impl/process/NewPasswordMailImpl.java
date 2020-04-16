package com.importexpress.email.service.impl.process;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.service.TemplateMailProcess;
import com.importexpress.email.vo.NewPasswordMailTemplateBean;
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
public class NewPasswordMailImpl implements TemplateMailProcess {

    @Override
    public MailBean process(MailTemplateBean mailTemplateBean, SpringTemplateEngine thymeleafEngine) {
        MailBean mailBean = new MailBean();
        BeanUtils.copyProperties(mailTemplateBean.getMailBean(),mailBean);
        //welcome mail
        NewPasswordMailTemplateBean npBean = (NewPasswordMailTemplateBean)mailTemplateBean;
        String here = mailBean.getSiteEnum().getUrl() + "/individual/getCenter";
        Map<String, Object> model = new HashMap<>();
        model.put("businessIntroduction", npBean.getBusinessIntroduction());
        model.put("businessName", npBean.getBusinessName());
        model.put("name", npBean.getName());
        model.put("email", npBean.getMailBean().getTo());
        model.put("pass", npBean.getPass());
        model.put("here", here);
        model.put("logoUrl", String.valueOf(npBean.getMailBean().getSiteEnum().getCode()));
        String title = "You've successfully complete your info!";
        mailBean.setSubject(title);
        mailBean.setBody(getHtmlContent(model, mailBean.getTemplateType(),thymeleafEngine));
        mailBean.setTest(mailBean.isTest());
        return mailBean;
    }




}
