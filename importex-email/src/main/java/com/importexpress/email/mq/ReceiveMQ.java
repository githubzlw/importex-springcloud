package com.importexpress.email.mq;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.email.config.Config;
import com.importexpress.email.service.SendMailFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


/**
 * @author lhao
 */
@Service
public class ReceiveMQ {

    private final SendMailFactory sendMailFactory;


    public ReceiveMQ(SendMailFactory sendMailFactory) {
        this.sendMailFactory = sendMailFactory;
    }


    @RabbitListener(queues = Config.QUEUE_MAIL, containerFactory = "rabbitListenerContainerFactory")
    public void receiveMail(@Payload MailBean mailBean) {

        sendMailFactory.sendMail(mailBean);
    }

}