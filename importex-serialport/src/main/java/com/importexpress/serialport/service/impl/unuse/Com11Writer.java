package com.importexpress.serialport.service.impl.unuse;

import java.io.IOException;
import java.io.OutputStream;
import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;

/**
 * Com11Writer类的功能是向COM11串口发送字符串“Hello World!”
 */
public class Com11Writer {

    public static void main(String[] args) {
        //1.定义变量
        CommPortIdentifier com11 = null;//用于记录本地串口
        SerialPort serialCom11 = null;//用于标识打开的串口

        try {
            //2.获取COM11口
            com11 = CommPortIdentifier.getPortIdentifier("COM11");

            //3.打开COM11
            serialCom11 = (SerialPort) com11.open("Com11Writer", 1000);

            //4.往串口写数据（使用串口对应的输出流对象）
            //4.1.获取串口的输出流对象
            OutputStream outputStream = serialCom11.getOutputStream();

            //4.2.通过串口的输出流向串口写数据“Hello World!”：
            //使用输出流往串口写数据的时候必须将数据转换为byte数组格式或int格式，
            //当另一个串口接收到数据之后再根据双方约定的规则，对数据进行解码。
            outputStream.write(new byte[]{'H','e','l','l','o',
                    ' ','W','o','r','l','d','!'});
            outputStream.flush();
            //4.3.关闭输出流
            outputStream.close();

            //5.关闭串口
            serialCom11.close();
        } catch (NoSuchPortException e) {
            //找不到串口的情况下抛出该异常
            e.printStackTrace();
        } catch (PortInUseException e) {
            //如果因为端口被占用而导致打开失败，则抛出该异常
            e.printStackTrace();
        } catch (IOException e) {
            //如果获取输出流失败，则抛出该异常
            e.printStackTrace();
        }
    }
}


