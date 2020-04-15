package com.importexpress.email.mq;

import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.config.Config;
import com.importexpress.email.service.SendMailFactory;
import com.importexpress.email.service.TemplateMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final TemplateMailService templateMailService;


    public ReceiveMQ(SendMailFactory sendMailFactory, TemplateMailService templateMailService) {
        this.sendMailFactory = sendMailFactory;
        this.templateMailService = templateMailService;
    }

    @RabbitListener(queues = Config.QUEUE_MAIL, containerFactory = "rabbitListenerContainerFactory")
    public void receiveMail(@Payload MailTemplateBean mailTemplateBean) {

        try{
            sendMailFactory.sendMail(templateMailService.processTemplate(mailTemplateBean));
            log.info("received mq count:[{}]", count.incrementAndGet());
        }catch(Exception e){
            log.error("receiveMail",e);
        }
    }

}