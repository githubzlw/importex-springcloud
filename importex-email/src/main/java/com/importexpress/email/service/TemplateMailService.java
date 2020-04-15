package com.importexpress.email.service;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;

/**
 * TemplateMailService
 * @author jack.luo
 */
public interface TemplateMailService {


    MailBean processTemplate(MailTemplateBean mailBean);

}
