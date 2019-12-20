package com.importexpress.email.service.impl;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.email.config.Config;
import com.importexpress.email.service.SendMail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author jack.luo
 * @date 2019/12/20
 * https://docs.aws.amazon.com/zh_cn/ses/latest/DeveloperGuide/send-using-smtp-java.html
 */
@Slf4j
@Service("SendMailByAmazon")
public final class SendMailAmzImpl implements SendMail {


    private final Config config;

    public SendMailAmzImpl(Config config) {
        this.config = config;
    }

    /**
     * sendMail
     *
     * @param mailBean
     * @throws IllegalStateException
     */
    @Override
    public void sendMail(MailBean mailBean) throws IllegalStateException {

        Transport transport = null;
        try {
            Properties props = getProperties();
            Session session = Session.getDefaultInstance(props);
            MimeMessage msg = new MimeMessage(session);
            Pair<String, String> fromInfo = getFromInfo(mailBean.getSiteEnum());
            String from = fromInfo.getLeft();
            String fromName = fromInfo.getRight();
            msg.setFrom(new InternetAddress(from, fromName));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(mailBean.getTo()));
            if (StringUtils.isNotBlank(mailBean.getBcc())) {
                msg.setRecipient(Message.RecipientType.BCC, new InternetAddress(mailBean.getBcc()));
            }
            msg.setSubject(mailBean.getSubject());
            msg.setContent(mailBean.getBody(), "text/html");

            transport = session.getTransport();
            transport.connect(config.amazonMailHost, config.amazonMailUser, config.amazonMailPass);
            transport.sendMessage(msg, msg.getAllRecipients());
            log.info("邮件发送成功..ToMail:[" + mailBean.getTo() + "],Subject:[" + mailBean.getSubject() + "]");
        } catch (Exception e) {
            log.error("邮件发送失败", e);
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

    /**
     * getProperties
     *
     * @return
     */
    private Properties getProperties() {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", config.amazonMailPort);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        return props;
    }

    /**
     * getFromInfo
     *
     * @param siteEnum
     * @return
     */
    private Pair<String, String> getFromInfo(SiteEnum siteEnum) {

        String from;
        String fromName;
        if (siteEnum == SiteEnum.IMPORTX) {
            from = "service@importexpress.com";
            fromName = "Import-Express.com";
        } else if (siteEnum == SiteEnum.KIDS || siteEnum == SiteEnum.PETS || siteEnum == SiteEnum.HOME) {
            from = "service@chinawholesaleinc.com";
            fromName = "ChinaWholesaleInc.com";
        } else {
            throw new UnsupportedOperationException("siteEnum value is not support! value=" + siteEnum);
        }
        return new ImmutablePair(from, fromName);
    }

}