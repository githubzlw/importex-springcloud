package com.importexpress.serialport.mq;


import com.importexpress.serialport.exception.SerialPortException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.importexpress.serialport.exception.SerialPortException.SERIAL_PORT_EXCEPTION_NOT_SAME;


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

    @Test(expected = SerialPortException.class)
    public void SerialPortException()  {
        throw new SerialPortException(SERIAL_PORT_EXCEPTION_NOT_SAME);
    }


}