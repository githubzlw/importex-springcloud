package com.importexpress.serialport;

import com.importexpress.serialport.exception.SerialPortException;
import com.importexpress.serialport.mq.SendMQ;
import com.importexpress.serialport.service.SerialPort2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
@Slf4j
class GlobalExceptionHandler {

    private final SendMQ sendMQ;

    @Autowired
    SerialPort2Service serialPort2Service;

    public GlobalExceptionHandler(SendMQ mq) {
        this.sendMQ = mq;
    }

    @ExceptionHandler(value = SerialPortException.class)
    public void defaultErrorHandler(SerialPortException e) throws Exception {

        log.error("SerialPortException handler:", e);
        //点亮报警灯
        serialPort2Service.warningLight(true);
        sendMQ.sendWarningMsgToMQ(e.toString());

    }
}
