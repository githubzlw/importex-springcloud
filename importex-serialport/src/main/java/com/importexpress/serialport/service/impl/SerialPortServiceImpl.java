package com.importexpress.serialport.service.impl;

import com.importexpress.serialport.service.SerialPortService;
import com.importexpress.serialport.util.Config;
import com.importexpress.serialport.util.SerialTool;
import gnu.io.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;
import java.util.concurrent.SynchronousQueue;


/**
 * 串口通信
 * @Author jack.luo
 * @create 2020/05/18
 * Description
 */
@Service
@Slf4j
public class SerialPortServiceImpl implements SerialPortService {

    /**当前位置设为零位 */
    private static final String ZERO_POSI = "#000000#000000#000000#X0Y0Z0#360";

    /**普通的回到零点指令 */
    private static final String RETURN_ZERO_POSI = "#000000#000000#000000#000000#360";

    private static SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue();

//    /**释放物品（消磁） */
//    private static final String EXEC_MAGOFF = "#000000#000000#000000#MAGOFF#360";
//
//    /**吸取物品（吸磁） */
//    private static final String EXEC_MAGNET = "#000000#000000#000000#MAGNET#360";


    /**操作之间间隔时间 */
    public static final int MAX_SLEEP = 3000;

    private final Config config;

    private static SerialPort serialPort =null;

    public SerialPortServiceImpl(Config config) {
        this.config = config;
    }

    @Override
    public void sendData(String msg) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        try {
            openSerial();
            SerialTool.sendData(serialPort, (msg+"\n").getBytes());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void sendData(int x, int y, int z, boolean isMagi) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        try {
            if(x <0 || y <0 || z <0){
                throw new IllegalArgumentException("input xyz is not right.");
            }

            if(x >config.MAX_VALUE_X || y >config.MAX_VALUE_Y || z >config.MAX_VALUE_Z){
                throw new IllegalArgumentException("input xyz is not right.");
            }

            //sample: #000000#000000#000000#MAGOFF#360
            StringBuilder sb = new StringBuilder();
            sb.append('#').append(StringUtils.leftPad(String.valueOf(x),6,'0'));
            sb.append('#').append(StringUtils.leftPad(String.valueOf(y),6,'0'));
            sb.append('#').append(StringUtils.leftPad(String.valueOf(z),6,'0'));
            if(isMagi){
                sb.append('#').append("MAGNET");
            }else{
                sb.append('#').append("MAGOFF");
            }
            sb.append("#360");

            sendData(sb.toString());
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 普通的回到零点指令
     */
    @Override
    public void returnZeroPosi() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {
        sendData(RETURN_ZERO_POSI);
    }

    /**
     * 当前位置设为零位
     */
    @Override
    public void setZeroPosi() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {
        sendData(ZERO_POSI);
    }


    /**
     * 释放物品（消磁）
     */
    @Override
    public void execMagoff(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {
        sendData(x,y,z,false);
    }

    /**
     * 托盘区释放物品（消磁）
     */
    @Override
    public void execMagoff(String msg) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {
        sendData(msg);
    }

    /**
     * 吸取物品（吸磁）
     */
    @Override
    public void execMagNet(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {
        sendData(x,y,z,true);
    }

    /**
     * 移动到托盘区
     */
    @Override
    public void moveToCart() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        sendData(config.MOVE_TO_CART_MAGNET_POSI);
    }

    /**
     * 移动物品到托盘区并且释放,再回到零点
     */
    @Override
    public void moveGoods(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        //移动到指定地点
        this.sendData(x,y,z,false);

        //吸取物品
        if(synchronousQueue.take()==1){
            log.debug("take 1");
            this.execMagNet(x,y,z);
        }

        //收缩Y
        if(synchronousQueue.take()==1) {
            log.debug("take 2");
            this.sendData(x, y, 0, true);
        }

        //移动到托盘区域
        if(synchronousQueue.take()==1) {
            log.debug("take 3");
            this.moveToCart();
        }

        //释放物品
        if(synchronousQueue.take()==1) {
            log.debug("take 4");
            this.execMagoff(config.MOVE_TO_CART_MAGOFF_POSI);
        }

        //回到零点
        if(synchronousQueue.take()==1) {
            log.debug("take 5");
            this.returnZeroPosi();
        }

        if(synchronousQueue.take()==1){
            Thread.sleep(MAX_SLEEP*5);
            return;
        }
    }

    /**
     * 关闭串口（长时间不用需要关闭）
     */
    @Override
    public void closeSerial() {
        serialPort.notifyOnDataAvailable(false);
        serialPort.removeEventListener();
        SerialTool.closeSerialPort(serialPort);
        serialPort=null;
    }

    /**
     * 打开串口并监听
     * @throws NoSuchPortException
     * @throws PortInUseException
     * @throws UnsupportedCommOperationException
     * @throws InterruptedException
     */
    private void openSerial() throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, InterruptedException {
        if (serialPort == null) {
            log.debug("begin open serial : [{}]",config.SERIAL_PORT);
            serialPort = SerialTool.openSerialPort(config.SERIAL_PORT);
            Thread.sleep(MAX_SLEEP);
            try {
                serialPort.notifyOnDataAvailable(true);
                SerialTool.setListenerToSerialPort(serialPort, serialPortEvent -> {
                            if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                                // we get here if data has been received
                                final StringBuilder sb = new StringBuilder();
                                final byte[] readBuffer = new byte[20];
                                try (InputStream inputStream = serialPort.getInputStream()){
                                    do {
                                        // read data from serial device
                                        while (inputStream.available() > 0) {
                                            final int bytes = inputStream.read(readBuffer);
                                            sb.append(new String(readBuffer, 0, bytes));
                                        }
                                        try {
                                            // add wait states around reading the stream, so that interrupted transmissions are
                                            // merged
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                        }
                                    } while (inputStream.available() > 0);
                                    log.debug("receivd data:[{}]",sb.toString());
                                    try {
                                        if(sb.toString().contains("LimitSwitch")){
                                            log.debug("put queue");
                                            synchronousQueue.put(1);
                                        }
                                    } catch (InterruptedException e) {
                                    }
                                } catch (IOException e) {
                                    log.error("Error receiving data on serial port", e);
                                }
                            }
                        }
                );
            } catch (TooManyListenersException e) {
                throw new IllegalStateException("TooManyListenersException");
            }
        }
    }




//    @Override
//    public void callCMD(String msg) {
//
//        Process process = null;
//        try {
//            process = Runtime.getRuntime().exec("python serial2.py", null,new File("D:\\work"));
//        } catch (IOException e) {
//            log.error("callCMD",e);
//            throw new IllegalStateException("callCMD error");
//        }
//        int status = 0;
//        try {
////            OutputStream output = process.getOutputStream();
////            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
////            writer.write(msg);
////            writer.flush();
////            writer.close();
////            output.close();
//
//            InputStream is = process.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                log.info(line);
//            }
//            status = process.waitFor();
//            is.close();
//            reader.close();
//            process.destroy();
//        } catch (Exception e) {
//            log.error("callCMD",e);
//        }
//        if (status != 0) {
//            log.error("Failed to call shell's command and the return status's is: " + status);
//        }
//
//    }

}
