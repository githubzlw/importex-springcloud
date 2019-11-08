package com.importexpress.common.pojo.mail;

import com.importexpress.common.pojo.SiteEnum;

import java.util.Map;

/**
 * @author luohao
 * @date 2019/9/5
 */
public class MailBean {

    private String to;
    private String bcc;
    private String subject;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    public String body;

    /** 1:线上请求    2:线下请求 */
    public int type=1;

    /** true:测试模板（不实际发送邮件） */
    private boolean isTest=false;

    private SiteEnum siteEnum;

    public TemplateType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(TemplateType templateType) {
        this.templateType = templateType;
    }

    public SiteEnum getSiteEnum() {
        return siteEnum;
    }

    public void setSiteEnum(SiteEnum siteEnum) {
        this.siteEnum = siteEnum;
    }

    public Map<String, String> getModel() {
        return model;
    }

    public void setModel(Map<String, String> model) {
        this.model = model;
    }

    /** 填充类型*/
    private Map<String, String> model;

    /** 邮件类型 */
    private TemplateType templateType;
}
