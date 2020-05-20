package com.importexpress.serialport.service.impl;

import com.importexpress.serialport.service.SerialPortService;
import com.importexpress.serialport.util.SerialTool;
import gnu.io.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @Author jack.luo
 * @create 2020/05/18
 * Description
 */
@Service
@Slf4j
public class SerialPortServiceImpl implements SerialPortService {

    private static final String COM_PORT = "COM3";

    private static SerialPort serialPort =null;
//    private static final String COM_PORT_WRITER = "Com3Writer";
//    private static final int SLEEP_MILLIS = 2000;
//
//    private static final int TIME_OUT = 2000;
//    private static final int DATA_RATE = 9600;

    @Override
    public void comWriter(String msg) {

        try {
            if(serialPort ==null){
                serialPort = SerialTool.openSerialPort(COM_PORT);
                Thread.sleep(5000);
            }
            SerialTool.sendData(serialPort, (msg+"\n").getBytes());
            log.info("read data from serial port:[{}]",SerialTool.readData(serialPort));
        } catch (Exception e) {
            log.error("comWriter",e);
        }
    }

//    @Override
//    public void comWriter(String msg) {
//
//        OutputStream output = null;
//        SerialPort serialPort =null;
//        try {
//
//            //获取COM口
//            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(COM_PORT);
//            if (portId == null) {
//                log.error("Could not find COM port[{}].",COM_PORT);
//                return;
//            }
//
//            serialPort = (SerialPort) portId.open(this.getClass().getName(),
//                    TIME_OUT);
//
//            serialPort.setSerialPortParams(DATA_RATE,
//                    SerialPort.DATABITS_8,
//                    SerialPort.STOPBITS_1,
//                    SerialPort.PARITY_NONE);
//
//
//            //往串口写数据（使用串口对应的输出流对象）
//            output = serialPort.getOutputStream();
//
//            //使用输出流往串口写数据的时候必须将数据转换为byte数组格式或int格式，
//            //当另一个串口接收到数据之后再根据双方约定的规则，对数据进行解码。
//            output.write(msg.getBytes());
//            output.flush();
//            try {
//                Thread.sleep(SLEEP_MILLIS);
//            } catch (InterruptedException e) {
//                log.error("InterruptedException", e);
//            }
//        } catch (Exception e) {
//            log.error("comWriter", e);
//        } finally {
//            if (output != null) {
//                //关闭输出流
//                try {
//                    output.close();
//                } catch (IOException e) {
//                    log.error("outputStream.close()", e);
//                }
//            }
//            if (serialPort != null) {
//                //关闭串口
//                serialPort.close();
//            }
//
//        }
//    }

    @Override
    public void callCMD(String msg) {

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("python serial2.py", null,new File("D:\\work"));
        } catch (IOException e) {
            log.error("callCMD",e);
            throw new IllegalStateException("callCMD error");
        }
        int status = 0;
        try {
//            OutputStream output = process.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
//            writer.write(msg);
//            writer.flush();
//            writer.close();
//            output.close();

            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
            status = process.waitFor();
            is.close();
            reader.close();
            process.destroy();
        } catch (Exception e) {
            log.error("callCMD",e);
        }
        if (status != 0) {
            log.error("Failed to call shell's command and the return status's is: " + status);
        }

    }


//    /**
//     * 读取串口返回的数据
//     *
//     * @param serialCom
//     * @throws IOException
//     */
//    private void readData(SerialPort serialCom) throws IOException {
//        InputStream inputStream = serialCom.getInputStream();
//        //定义用于缓存读入数据的数组
//        byte[] cache = new byte[1024];
//        //获取串口COM21收到的可用字节数
//        int availableBytes = inputStream.available();
//        //如果可用字节数大于零则开始循环并获取数据
//        while (availableBytes > 0) {
//            //从串口的输入流对象中读入数据并将数据存放到缓存数组中
//            inputStream.read(cache);
//            //将获取到的数据进行转码并输出
//            for (int j = 0; j < cache.length && j < availableBytes; j++) {
//                //因为COM口发送的是使用byte数组表示的字符串，
//                //所以在此将接收到的每个字节的数据都强制装换为char对象即可，
//                //这是一个简单的编码转换，读者可以根据需要进行更加复杂的编码转换。
//                System.out.print((char) cache[j]);
//            }
//            //更新循环条件
//            availableBytes = inputStream.available();
//        }
//        log.info("read data:[{}]", new String(cache));
//        try {
//            //让线程睡眠
//            Thread.sleep(SLEEP_MILLIS);
//        } catch (InterruptedException e) {
//            log.error("InterruptedException", e);
//        }
//    }
}
