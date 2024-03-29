package com.importexpress.comm.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author jack.luo
 * @date 2019/9/5
 */
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class MailBean {

    /**
     * to
     */
    private String to;

    /**
     * bcc
     */
    private String bcc;

    /**
     * subject
     */
    private String subject;

    /**
     * body
     */
    private String body;

    /**
     * 邮件类型
     */
    private TemplateType templateType;

    /**
     * 1:线上请求    2:线下请求
     */
    private int type = 1;

    /**
     * true:测试模板（不实际发送邮件）
     */
    private boolean isTest = true;

    /**
     * 区分网站
     */
    private SiteEnum siteEnum;

}
