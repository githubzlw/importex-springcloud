package importexpress.email.service;


import importexpress.common.pojo.mail.MailBean;

public interface SendMail {


    void sendMail(MailBean mailBean) throws IllegalStateException;

}
