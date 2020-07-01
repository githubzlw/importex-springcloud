package com.importexpress.email.service;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import org.thymeleaf.spring5.SpringTemplateEngine;

/**
 * @Author jack.luo
 * @create 2020/4/15 16:04
 * Description
 */
public interface TemplateMailProcess {

    MailBean process(MailTemplateBean mailTemplateBean, SpringTemplateEngine thymeleafEngine);
}
