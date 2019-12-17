package importexpress.email.service.impl;

import com.importexpress.comm.pojo.SiteEnum;
import importexpress.common.pojo.mail.MailBean;
import importexpress.email.service.SendMail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author jack.luo
 * @date 2018/10/23
 * https://docs.aws.amazon.com/zh_cn/ses/latest/DeveloperGuide/send-using-smtp-java.html
 */
@Slf4j
public class SendMailByAmazon implements SendMail {


    private static final String HOST = "email-smtp.us-west-2.amazonaws.com";
    private static final String SMTP_USERNAME = "AKIAIO7TWKGGFXB5WY2A";
    private static final String SMTP_PASSWORD = "AuYzbo9jZAUkWX35u5mwPdFeUJVdKI6K2sqTHCXZyiK6";
    private static final int PORT = 587;

    public SendMailByAmazon() {
    }

    @Override
    public void sendMail(MailBean mailBean) throws IllegalStateException {

        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);

        MimeMessage msg = new MimeMessage(session);
        Transport transport = null;
        try {

            String from;
            String fromName;
            if (mailBean.getSiteEnum() == SiteEnum.IMPORTX) {
                from = "service@importexpress.com";
                fromName = "Import-Express.com";
            } else {
                from = "service@chinawholesaleinc.com";
                fromName = "ChinaWholesaleInc.com";
            }
            msg.setFrom(new InternetAddress(from, fromName));

            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(mailBean.getTo()));
            if (StringUtils.isNotBlank(mailBean.getBcc())) {
                msg.setRecipient(Message.RecipientType.BCC, new InternetAddress(mailBean.getBcc()));
            }
            msg.setSubject(mailBean.getSubject());
            msg.setContent(mailBean.getBody(), "text/html");

            transport = session.getTransport();
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
            transport.sendMessage(msg, msg.getAllRecipients());
            log.info("邮件发送成功..ToMail:[" + mailBean.getTo() + "],Subject:[" + mailBean.getSubject() + "]");
        } catch (Exception e) {
            log.error("sendMail", e);
            throw new IllegalStateException("sendMail error");
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    log.error("transport.close()", e);
                }
            }
        }
    }
}
