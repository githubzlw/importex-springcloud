package com.importexpress.serialport;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.serialport.exception.SerialPortException;
import com.importexpress.serialport.mq.SendMQ;
import com.importexpress.serialport.service.SerialPort2Service;
import com.importexpress.serialport.service.SerialPortService;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final SendMQ sendMQ;

    private final SerialPortService serialPortService;

    private final SerialPort2Service serialPort2Service;

    public GlobalExceptionHandler(SendMQ mq, SerialPortService serialPortService, SerialPort2Service serialPort2Service) {
        this.sendMQ = mq;
        this.serialPortService = serialPortService;
        this.serialPort2Service = serialPort2Service;
    }

    @ExceptionHandler(value = SerialPortException.class)
    public CommonResult serialPortExceptionHandler(SerialPortException e) throws Exception {

        //点亮报警灯
        serialPort2Service.warningLight(true);
        //send mq
        sendMQ.sendWarningMsgToMQ(e.toString());
        //回到零点
        serialPortService.returnZeroPosi();

        return CommonResult.failed(e.toString());

    }

    @ExceptionHandler(value = PortInUseException.class)
    public CommonResult portInUseExceptionHandler(SerialPortException e) throws Exception {

        //点亮报警灯
        serialPort2Service.warningLight(true);
        //send mq
        sendMQ.sendWarningMsgToMQ("com端口被占用");
        //回到零点
        serialPortService.returnZeroPosi();

        return CommonResult.failed(e.toString());

    }

    @ExceptionHandler(value = NoSuchPortException.class)
    public CommonResult noSuchPortException(SerialPortException e) throws Exception {

        //点亮报警灯
        serialPort2Service.warningLight(true);
        //send mq
        sendMQ.sendWarningMsgToMQ("没有这个com端口");
        //回到零点
        serialPortService.returnZeroPosi();

        return CommonResult.failed(e.toString());

    }

}
