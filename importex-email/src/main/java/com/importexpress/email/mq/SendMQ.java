package com.importexpress.email.mq;

import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.config.Config;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;


/**
 * @author lhao
 */
@Service
public class SendMQ {

    private final AmqpTemplate rabbitTemplate;

    private final Config config;

    public SendMQ(AmqpTemplate rabbitTemplate, Config config) {
        this.rabbitTemplate = rabbitTemplate;
        this.config = config;
    }

    public void sendMQToMail(MailTemplateBean mailTemplateBean) {
        this.rabbitTemplate.convertAndSend(Config.QUEUE_MAIL, mailTemplateBean);
    }


}