package com.importexpress.email.mq;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.email.config.Config;
import com.importexpress.email.service.SendMailFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author lhao
 */
@Service
@Slf4j
public class ReceiveMQ {

    private static AtomicInteger count = new AtomicInteger(0);
    private final SendMailFactory sendMailFactory;

    public ReceiveMQ(SendMailFactory sendMailFactory) {
        this.sendMailFactory = sendMailFactory;
    }

    @RabbitListener(queues = Config.QUEUE_MAIL, containerFactory = "rabbitListenerContainerFactory")
    public void receiveMail(@Payload MailBean mailBean) {

        sendMailFactory.sendMail(mailBean);
        log.info("received mq count:[{}]", count.incrementAndGet());
    }

}