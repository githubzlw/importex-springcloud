package com.importexpress.serialport;

import com.importexpress.serialport.exception.SerialPortException;
import com.importexpress.serialport.mq.SendMQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
@Slf4j
class GlobalExceptionHandler {

    private final SendMQ sendMQ;

    public GlobalExceptionHandler(SendMQ mq) {
        this.sendMQ = mq;
    }

    @ExceptionHandler(value = SerialPortException.class)
    public void defaultErrorHandler(SerialPortException e) throws Exception {

        log.error("SerialPortException handler:", e);
        sendMQ.sendWarningMsgToMQ(e.toString());

    }
}
