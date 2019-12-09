package com.importexpress.pay.mq;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.pay.service.PaypalService;
import com.importexpress.pay.util.Config;
import com.importexpress.utils.util.MD5Util;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RPCServer implements Runnable {

    private final Config config;

    private final PaypalService paypalService;

    /**
     * 保存生成出来的UUID
     */
    private final static Map<String,Long> mapUUID = new HashMap<>();

    /**
     * 设置最大的2次请求的超时时间（30秒）
     */
    private final static int MAX_TIMEOUT = 30000;

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
                    try {
                        String json = new String(delivery.getBody(), "UTF-8");
                        log.info("input :[{}]",json);
                        JSONObject jsonObject = JSONObject.parseObject(json);
                        //"{'step':1}")
                        //"{'step':2,'uuid':'xxxxxxx','captureId':'74Y59251KF272460A','amount':2.0}")
                        int intStep = jsonObject.getIntValue("step");
                        Assert.isTrue(intStep ==1 || intStep ==2,"The input step is invalid");
                        CommonResult refund;
                        if(intStep==1){
                            String randomUUID = paypalService.getRandomUUID();
                            refund=CommonResult.success(randomUUID);
                            mapUUID.put(randomUUID, System.currentTimeMillis());
                        }else{
                            String strUUID = jsonObject.getString("uuid");
                            Assert.isTrue(StringUtils.isNotEmpty(strUUID),"The input uuid is invalid");
                            if(System.currentTimeMillis()-mapUUID.get(strUUID)<MAX_TIMEOUT){

                                String captureId = jsonObject.getString("captureId");
                                String amount = jsonObject.getString("amount");
                                String md5 = jsonObject.getString("md5");


                                if(MD5Util.verify(strUUID + captureId + amount,md5 )){

                                    refund = paypalService.refund(jsonObject.getString("captureId"), jsonObject.getDouble("amount"));
                                }else{
                                    //md5校验不通过
                                    refund=CommonResult.failed("md5 verify false");
                                }
                            }else{
                                //二次请求超时
                                refund=CommonResult.failed("step 2 is timeout");
                            }
                            mapUUID.remove(strUUID);
                        }
                        response = JSONObject.toJSONString(refund);
                    }catch(Exception re){
                        response = JSONObject.toJSONString(CommonResult.failed(re.getMessage()));
                    }
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
