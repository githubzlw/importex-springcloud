package com.importexpress.serialport.mq;

import com.importexpress.serialport.util.Config;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;


/**
 * @author lhao
 */
@Service
public class SendMQ {

    private final AmqpTemplate rabbitTemplate;


    public SendMQ(AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendWarningMsgToMQ(String warningMsg) {
        this.rabbitTemplate.convertAndSend(Config.QUEUE_SERIAL_PORT, warningMsg);
    }


}