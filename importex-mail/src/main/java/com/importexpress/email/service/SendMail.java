package com.importexpress.email.service;

import com.importexpress.common.pojo.mail.MailBean;

public interface SendMail {


    void sendMail(MailBean mailBean) throws IllegalStateException;
}
