package com.importexpress.serialport.mq;


import com.importexpress.serialport.exception.SerialPortException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @Author jack.luo
 * @create 2020/7/13 12:04
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SendMQTest {

    @Autowired
    private SendMQ sendMQ;

    @Test
    public void sendWarningMsgToMQ()  {
        sendMQ.sendWarningMsgToMQ(new SerialPortException(SerialPortException.SERIAL_PORT_EXCEPTION_EXISTS_GOODS).toString());
    }

}