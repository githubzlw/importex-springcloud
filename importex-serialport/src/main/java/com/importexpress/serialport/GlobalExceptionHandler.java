package com.importexpress.serialport;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.serialport.exception.SerialPortException;
import com.importexpress.serialport.mq.SendMQ;
import com.importexpress.serialport.service.SerialPort2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final SendMQ sendMQ;

    private final SerialPort2Service serialPort2Service;

    public GlobalExceptionHandler(SendMQ mq, SerialPort2Service serialPort2Service) {
        this.sendMQ = mq;
        this.serialPort2Service = serialPort2Service;
    }

    @ExceptionHandler(value = SerialPortException.class)
    public CommonResult serialPortExceptionHandler(SerialPortException e) throws Exception {

        log.error("SerialPortException handler:", e);
        //点亮报警灯
        serialPort2Service.warningLight(true);
        //send mq
        sendMQ.sendWarningMsgToMQ(e.toString());

        return CommonResult.failed(e.toString());

    }
}
