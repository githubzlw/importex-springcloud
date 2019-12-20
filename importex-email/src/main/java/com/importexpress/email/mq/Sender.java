package com.importexpress.email.mq;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.email.config.Config;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;


/**
 * @author lhao
 */
@Service
public class Sender {

    private final AmqpTemplate rabbitTemplate;

    private final Config config;

    public Sender(AmqpTemplate rabbitTemplate, Config config) {
        this.rabbitTemplate = rabbitTemplate;
        this.config = config;
    }

    public void sendMQToMail(MailBean mailBean) {
        this.rabbitTemplate.convertAndSend(Config.QUEUE_MAIL, mailBean);
    }


}