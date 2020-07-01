package com.importexpress.serialport.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.importexpress.serialport.bean.ActionTypeEnum;
import com.importexpress.serialport.bean.GoodsBean;
import com.importexpress.serialport.service.SerialPort2Service;
import com.importexpress.serialport.util.Config;
import com.importexpress.serialport.util.SerialTool;
import gnu.io.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.SynchronousQueue;

import static com.importexpress.serialport.bean.ActionTypeEnum.LIGHT;
import static com.importexpress.serialport.bean.ActionTypeEnum.MAGI;


/**
 * 串口通信
 * @Author jack.luo
 * @create 2020/05/18
 * Description
 */
@Service
@Slf4j
public class SerialPort2ServiceImpl implements SerialPort2Service {

    /**当前位置设为零位 */
    private static final String ZERO_POSI = "#000000#000000#000000#X0Y0Z0#360";

    /**普通的回到零点指令 */
    private static final String RETURN_ZERO_POSI = "#000000#000000#000000#000000#360";

//    /**读取光电信号判断是否有物体 */
//    private static final String READ_LIGHT_DATA = "#000000#000000#000000#LIGHT#360";

    private static SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue();

    /** 光电操作同步queue*/
    private static SynchronousQueue<String> synchronousLightQueue = new SynchronousQueue();

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

    public SerialPort2ServiceImpl(Config config, AiImageServiceImpl aiImageService) {
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

        String strSendData = buildSendString(x, y, z, MAGI, isMagi);
        sendData(strSendData);
    }

    /**
     * 读取光电信号
     * @param x
     * @param y
     * @param z
     * @throws PortInUseException
     * @throws NoSuchPortException
     * @throws InterruptedException
     * @throws UnsupportedCommOperationException
     */
    @Override
    public String readLight(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        String strSendData = buildSendString(x, y, z,LIGHT,false);
        sendData(strSendData);
        return strSendData;
    }

    /**
     * build send string to serial port
     * @param x
     * @param y
     * @param z
     * @param type
     * @param isMagi
     * @throws PortInUseException
     * @throws NoSuchPortException
     * @throws InterruptedException
     * @throws UnsupportedCommOperationException
     */
    private String buildSendString(int x, int y, int z, ActionTypeEnum type, boolean isMagi) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {
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

            switch(type){
                case MAGI:
                    if(isMagi){
                        sb.append("#MAGNET");
                    }else{
                        sb.append("#MAGOFF");
                    }
                    break;
                case LIGHT:
                    sb.append("#LIGHT");
                    break;
                default:
                    throw new IllegalArgumentException("type is invalid");
            }
            sb.append("#360");

            return sb.toString();
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
            log.debug("begin open serial : [{}]",config.SERIAL_PORT2);
            serialPort = SerialTool.openSerialPort(config.SERIAL_PORT2);
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
                                        }else if(sb.toString().contains("LIGHT")){
                                            //光电操作
                                            log.debug("put light queue");
                                            synchronousLightQueue.put(sb.toString());
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
     * 移动货物（前期已经地毯式扫描过货物，取得了货物坐标）
     * @param hmGoods
     */
    @Override
    public Map<String,Integer> moveGoodsByFinder(Map<String, String> hmGoods)  {

        Map<String, Integer> result = new HashMap<>(hmGoods.size());
        if(hmGoods.size()==0){
            return result;
        }

        String json = null;
        try {
            json = getAllGoodsFromJsonFile(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

            List<GoodsBean> lstGoodsBean =new Gson().fromJson(json,new TypeToken<List<GoodsBean>>(){}.getType());
            for(GoodsBean goodsBean : lstGoodsBean){
                if(hmGoods.get(goodsBean.getGoodsId()) !=null){
                    //匹配到需要搬动的货物
                    ///this.moveGoods(goodsBean.getX(),goodsBean.getY(),config.MAX_VALUE_Z);
                    result.put(goodsBean.getGoodsId(), 1);
                }
            }
        } catch (Exception e) {
            log.error("moveGoodsByFinder",e);
        }

        return result;
    }

    /**
     * 读取指定日期的json文件（定时任务生成）
     * @param yyyyMMdd
     * @return
     * @throws IOException
     */
    @Override
    public String getAllGoodsFromJsonFile(String yyyyMMdd) throws IOException {

        StringBuilder fileName = getJsonFileName(yyyyMMdd);
        return FileUtils.readFileToString(new File(fileName.toString()));

    }

    /**
     * 获取json文件名称
     * @param yyyyMMdd
     * @return
     */
    @Override
    public StringBuilder getJsonFileName(String yyyyMMdd) {
        StringBuilder fileName = new StringBuilder();

        fileName.append(config.SAVE_FINDER_PATH);
        fileName.append("finder_").append(yyyyMMdd).append(".json");
        return fileName;
    }


    /**
     * 条形码读取
     * @param mapTmp
     * @return
     */
    @Override
    public String readGoodsId(Map<Integer, Integer> mapTmp) {
        //TODO 扫描条形码
        String[] random = {"20200619144110070","20200624093705854"};
        int index = new Random().nextInt(random.length);
        if(mapTmp.get(index)==null){
            mapTmp.put(index, 1);
            return random[index];
        }else{
            return null;
        }
    }

    /**
     * 地毯式扫描货物(定时任务执行），进行入库操作准备
     */
    @Override
    public List<GoodsBean> findAllGoodsByGrid()  {

        int stepGap=config.STEP_VALUE;
        int count=0;
        List<GoodsBean> lstFinderGoods = new ArrayList<>();
        Map<Integer, Integer> mapTmp = new HashMap<>();
        for(int x=0;x*stepGap<=config.MAX_VALUE_X;x++){
            for(int y=0;y*stepGap<=config.MAX_VALUE_X;y++){
                log.debug("x:[{}],y:[{}]",x*stepGap,y*stepGap);
                ++count;
                try {

                    String strSendData=this.readLight(x*stepGap,y*stepGap,config.MAX_VALUE_Z);
                    String strReturnData = synchronousLightQueue.take();
                    if(strReturnData.contains(strSendData)){
                        log.debug("光电识别结果返回:[{}]",strReturnData);
                        if(strReturnData.endsWith("000")){
                            continue;
                        }
                    }

                    String strGoodsId = this.readGoodsId(mapTmp);
                    if(StringUtils.isNotEmpty(strGoodsId)){
                        log.info("find goods (x,y):[{},{}]",x*stepGap,y*stepGap);
                        lstFinderGoods.add(
                                GoodsBean.builder().x(x * stepGap).y(y * stepGap).goodsId(strGoodsId).build());
                    }
                } catch (Exception e) {
                    log.error("findAllGoodsByGrid",e);
                }
            }
        }
        log.info("move count:[{}]",count);

        return lstFinderGoods;
    }



}
