package importexpress.email.service;


import com.importexpress.comm.pojo.MailBean;

public interface SendMail {


    void sendMail(MailBean mailBean) throws IllegalStateException;

}
