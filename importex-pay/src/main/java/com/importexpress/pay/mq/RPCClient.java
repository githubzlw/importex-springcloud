package com.importexpress.pay.mq;

import com.importexpress.pay.util.Config;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@Slf4j
@Service
public class RPCClient implements AutoCloseable {



    private Connection connection;
    private Channel channel;
    private final static String refund_rpc = "refund_rpc";


    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.98");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public String call(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", refund_rpc, props, message.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        //超过指定时间后强制结束
        String result = response.poll(60, TimeUnit.SECONDS);
        channel.basicCancel(ctag);
        return result;
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }

    public static void main(String[] argv) {
        try (RPCClient rpcClient = new RPCClient()) {
            System.out.println(rpcClient.call("{'captureId':'aaaa','amount':1.1}"));
            System.out.println(rpcClient.call("{'captureId':'74Y59251KF272460A','amount':1.1}"));
            System.out.println(rpcClient.call("abc"));
        } catch (IOException | TimeoutException | InterruptedException e) {
            log.error("main",e);
        }
    }


}

