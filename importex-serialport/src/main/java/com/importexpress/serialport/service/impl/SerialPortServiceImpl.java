package com.importexpress.serialport.service.impl;

import com.importexpress.serialport.service.SerialPortService;
import com.importexpress.serialport.util.Config;
import com.importexpress.serialport.util.SerialTool;
import gnu.io.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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

    private final AiImageServiceImpl aiImageService;

    public SerialPortServiceImpl(Config config, AiImageServiceImpl aiImageService) {
        this.config = config;
        this.aiImageService = aiImageService;
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
        this.sendData(x,y,0,false);

        String picUrlFrom = null;
        try {
            picUrlFrom = this.aiImageService.captureImage();
        } catch (IOException e) {
            log.error("moveGoods",e);
        }

        //伸Z
        if(synchronousQueue.take()==1) {
            log.debug("take 0(伸Z)");
            this.sendData(x, y, z, false);
        }

        //吸取物品
        if(synchronousQueue.take()==1){
            log.debug("take 1(吸取物品)");
            this.execMagNet(x,y,z);
        }

        //缩Z
        if(synchronousQueue.take()==1) {
            log.debug("take 2(缩Z)");
            this.sendData(x, y, 0, true);
        }

        //移动到托盘区域
        if(synchronousQueue.take()==1) {
            log.debug("take 3(移动到托盘区域)");
            this.moveToCart();
        }

        //释放物品
        if(synchronousQueue.take()==1) {
            log.debug("take 4(释放物品)");
            this.execMagoff(config.MOVE_TO_CART_MAGOFF_POSI);
        }

        //回到零点
        if(synchronousQueue.take()==1) {
            log.debug("take 5(回到零点)");
            this.returnZeroPosi();
        }

        //计算是否移动成功
        try {
            String picUrlTo = this.aiImageService.captureImage();
            List<String> lstFrom = this.aiImageService.callCMD(picUrlFrom);
            List<String> lstTo = this.aiImageService.callCMD(picUrlTo);
            if(this.aiImageService.compareTwoList(lstFrom, lstTo)){
                log.info("aiImage result:move succeed");
            }else{
                log.error("aiImage result:move failed");
            }
        } catch (IOException e) {
            log.error("moveGoods",e);
        }

        //执行完毕，返回
        if(synchronousQueue.take()==1){
            log.debug("take 6(执行完毕，返回)");
            Thread.sleep(MAX_SLEEP*5);
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
                                        } catch (InterruptedException ignored) {
                                        }
                                    } while (inputStream.available() > 0);
                                    log.debug("receivd data:[{}]",sb.toString());
                                    try {
                                        if(sb.toString().contains("LimitSwitch")){
                                            log.debug("put queue");
                                            synchronousQueue.put(1);
                                        }
                                    } catch (InterruptedException ignored) {
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

    /**
     * 地毯式扫描货物
     * @param hmGoods
     */
    @Override
    public Map<String,Integer> findGoodsByGrid(Map<String, String> hmGoods)  {

        int stepGap=config.STEP_VALUE;
        int count=0;

        Map<String, Integer> result = new HashMap<>(hmGoods.size());
        if(hmGoods.size()==0){
            return result;
        }
        for(int x=0;x*stepGap<=config.MAX_VALUE_X;x++){
            for(int y=0;y*stepGap<=config.MAX_VALUE_X;y++){
                log.debug("x:[{}],y:[{}]",x*stepGap,y*stepGap);
                ++count;
                try {
                    ///this.sendData(x*stepGap,y*stepGap,config.MAX_VALUE_Z,false);
                    String strGoodsId = this.readGoodsId();
                    String strDest = hmGoods.get(strGoodsId);
                    if(StringUtils.isNotEmpty(strDest)){
                        //匹配到了需要搬动的货物
                        ///this.moveGoods(x*stepGap,y*stepGap,config.MAX_VALUE_Z);
                        result.put(strGoodsId, 1);
                        if(result.size()==hmGoods.size()){
                            //全部找到指定的货物,提前退出程序
                            log.info("move count:[{}]",count);
                            return result;
                        }
                    }
                } catch (Exception e) {
                    log.error("findGoodsByGrid",e);
                    return result;
                }
            }
        }
        log.info("move count:[{}]",count);

        return result;
    }

    @Override
    public String readGoodsId() throws IOException {
        //TODO 扫描条形码
        String[] random = {"20200619144110070","20200624093705854"};
        return random[new Random().nextInt(random.length)];
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
