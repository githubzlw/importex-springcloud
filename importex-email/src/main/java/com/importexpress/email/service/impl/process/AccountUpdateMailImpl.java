package com.importexpress.email.service.impl.process;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.service.TemplateMailProcess;
import com.importexpress.email.vo.AccountUpdateMailTemplateBean;
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
public class AccountUpdateMailImpl implements TemplateMailProcess {

    @Override
    public MailBean process(MailTemplateBean mailTemplateBean, SpringTemplateEngine thymeleafEngine) {
        MailBean mailBean = new MailBean();
        BeanUtils.copyProperties(mailTemplateBean.getMailBean(),mailBean);
        //welcome mail
        AccountUpdateMailTemplateBean bean = (AccountUpdateMailTemplateBean)mailTemplateBean;
        String here = mailBean.getSiteEnum().getUrl() + "/individual/getCenter";
        String activeLink = bean.getMailBean().getSiteEnum().getUrl() + "/forgotPassword/passActivate?email=" + mailBean.getTo() + "&validateCode=" + bean.getActivationCode();
        Map<String, Object> model = new HashMap<>();
        model.put("activeLink", activeLink);
        model.put("name", bean.getName());
        model.put("email", bean.getMailBean().getTo());
        model.put("pass", bean.getPass());
        model.put("here", here);
        model.put("logoUrl", String.valueOf(bean.getMailBean().getSiteEnum().getCode()));
        String title = "Reset Your Password " + mailBean.getSiteEnum().getName() + " account.";
        mailBean.setSubject(title);
        mailBean.setBody(getHtmlContent(model, mailBean.getTemplateType(),thymeleafEngine));
        mailBean.setTest(mailBean.isTest());
        return mailBean;
    }




}
