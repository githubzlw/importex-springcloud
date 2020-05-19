package com.importexpress.serialport.service.impl;

import com.importexpress.serialport.service.SerialPortService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.comm.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author jack.luo
 * @create 2020/05/18
 * Description
 */
@Service
@Slf4j
public class SerialPortServiceImpl implements SerialPortService {

    private static final String COM_PORT = "COM3";
    private static final String COM_PORT_WRITER = "Com3Writer";
    private static final int SLEEP_MILLIS = 2000;

    @Override
    public void com5Writer(String[] msgs) {

        OutputStream outputStream = null;
        CommPortIdentifier com;
        SerialPort serialCom5 = null;
        try {

            //获取COM11口
            com = CommPortIdentifier.getPortIdentifier(COM_PORT);

            //打开COM11
            serialCom5 = (SerialPort) com.open(COM_PORT_WRITER, 1000);

            //设置端口参数(波特率等)
            setSerialPortParams(serialCom5);

            //往串口写数据（使用串口对应的输出流对象）
            //获取串口的输出流对象
            outputStream = serialCom5.getOutputStream();

            //通过串口的输出流向串口写数据“Hello World!”：
            //使用输出流往串口写数据的时候必须将数据转换为byte数组格式或int格式，
            //当另一个串口接收到数据之后再根据双方约定的规则，对数据进行解码。
            for (String msg : msgs) {
                outputStream.write(msg.getBytes());
                outputStream.flush();
                readData(serialCom5);
                try {
                    Thread.sleep(SLEEP_MILLIS);
                } catch (InterruptedException e) {
                    log.error("InterruptedException", e);
                }
            }


        } catch (NoSuchPortException | PortInUseException | IOException e) {
            log.error("com5Writer", e);
        } finally {

            if (outputStream != null) {
                //关闭输出流
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("outputStream.close()", e);
                }
            }

            if (serialCom5 != null) {
                //关闭串口
                serialCom5.close();
            }

        }
    }

    /**
     * 设置端口参数
     *
     * @param serialCom serialCom
     */
    private void setSerialPortParams(SerialPort serialCom) {
        try {
            serialCom.setSerialPortParams(
                    //波特率
                    9600,
                    //数据位数
                    SerialPort.DATABITS_8,
                    //停止位
                    SerialPort.STOPBITS_1,
                    //奇偶位
                    SerialPort.PARITY_NONE
            );
        } catch (UnsupportedCommOperationException e) {
            log.error("setSerialPortParams", e);
        }
    }

    /**
     * 读取串口返回的数据
     *
     * @param serialCom
     * @throws IOException
     */
    private void readData(SerialPort serialCom) throws IOException {
        InputStream inputStream = serialCom.getInputStream();
        //定义用于缓存读入数据的数组
        byte[] cache = new byte[1024];
        //获取串口COM21收到的可用字节数
        int availableBytes = inputStream.available();
        //如果可用字节数大于零则开始循环并获取数据
        while (availableBytes > 0) {
            //从串口的输入流对象中读入数据并将数据存放到缓存数组中
            inputStream.read(cache);
            //将获取到的数据进行转码并输出
            for (int j = 0; j < cache.length && j < availableBytes; j++) {
                //因为COM11口发送的是使用byte数组表示的字符串，
                //所以在此将接收到的每个字节的数据都强制装换为char对象即可，
                //这是一个简单的编码转换，读者可以根据需要进行更加复杂的编码转换。
                System.out.print((char) cache[j]);
            }
            //更新循环条件
            availableBytes = inputStream.available();
        }
        log.info("read data:[{}]", new String(cache));
        try {
            //让线程睡眠
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
        }
    }
}
