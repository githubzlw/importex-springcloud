package com.importexpress.pay.mq;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.pay.service.PaypalService;
import com.importexpress.pay.util.Config;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RPCServer implements Runnable {

    private final Config config;

    private final PaypalService paypalService;

    public RPCServer(Config config, PaypalService paypalService) {
        this.config = config;
        this.paypalService = paypalService;
    }


    @Override
    public void run() {
        ConnectionFactory factory = getConnectionFactory();

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(config.qnameRpc, false, false, false, null);
            channel.queuePurge(config.qnameRpc);
            channel.basicQos(1);

            log.info(" [x] Awaiting RPC requests [{}]", config.qnameRpc);

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";
                try {
                    String json = new String(delivery.getBody(), "UTF-8");
                    log.info("input json:[{}]",json);
                    JSONObject jsonObject =null;
                    try {
                        jsonObject = JSONObject.parseObject(json);
                    }catch(RuntimeException re){
                        response = JSONObject.toJSONString(CommonResult.failed(re.getMessage()));
                    }
                    CommonResult refund = paypalService.refund(jsonObject.getString("captureId"), jsonObject.getDouble("amount"));
                    response = JSONObject.toJSONString(refund);
                } catch (Exception e) {
                    log.error("Exception",e);
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(config.qnameRpc, false, deliverCallback, (consumerTag -> {
            }));
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        log.error("InterruptedException", e);
                    }
                }
            }
        } catch (TimeoutException e) {
            log.error("TimeoutException", e);
        } catch (IOException e) {
            log.error("IOException", e);
        }
    }

    /**
     * getConnectionFactory
     * @return
     */
    private ConnectionFactory getConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(config.rabbitmqHost);
        factory.setPort(config.rabbitmqPort);
        factory.setUsername(config.rabbitmqUser);
        factory.setPassword(config.rabbitmqPass);
        return factory;
    }
}
