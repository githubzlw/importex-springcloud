package com.importexpress.common.pojo.mail;

import com.importexpress.common.pojo.SiteEnum;
import lombok.Data;

import java.util.Map;

/**
 * @author luohao
 * @date 2019/9/5
 */
@Data
public class MailBean {

    public String to;
    public String bcc;
    public String subject;
    public String body;

    /** 1:线上请求    2:线下请求 */
    public int type=1;

    /** true:测试模板（不实际发送邮件） */
    public boolean isTest=false;

    public SiteEnum siteEnum;

    /** 填充类型*/
    public Map<String, String> model;

    /** 邮件类型 */
    public TemplateType templateType;
}
