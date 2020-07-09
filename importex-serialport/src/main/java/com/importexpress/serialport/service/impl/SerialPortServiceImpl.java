package com.importexpress.serialport.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.serialport.bean.ActionTypeEnum;
import com.importexpress.serialport.bean.GoodsBean;
import com.importexpress.serialport.bean.ReturnMoveBean;
import com.importexpress.serialport.service.SerialPort2Service;
import com.importexpress.serialport.service.SerialPortService;
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
public class SerialPortServiceImpl implements SerialPortService {

    /**当前位置设为零位 */
    private static final String ZERO_POSI = "#000000#000000#000000#X0Y0Z0#360";

    /**普通的回到零点指令 */
    private static final String RETURN_ZERO_POSI = "#000000#000000#000000#000000#360";

    /**条形码扫描 */
    private static final String DO_SCAN = "#000000#000000#000000#SCAN#360";

    /** 同步queues使用的存放内容*/
    private static final int PUT_ONE = 10000;

    /** 同步queue*/
    private static final SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();

    /** 光电操作同步queue*/
    private static final SynchronousQueue<String> synchronousLightQueue = new SynchronousQueue<>();

    /** 条形码扫描同步queue*/
    private static final SynchronousQueue<String> synchronousScanQueue = new SynchronousQueue<>();

//    /**释放物品（消磁） */
//    private static final String EXEC_MAGOFF = "#000000#000000#000000#MAGOFF#360";
//
//    /**吸取物品（吸磁） */
//    private static final String EXEC_MAGNET = "#000000#000000#000000#MAGNET#360";

    /**操作之间间隔时间 */
    public static final int MAX_SLEEP = 3000;

    /**出库商品再入库的空位置的最大数量 */
    public static final int RETURN_MOVE_SIZE = 10;

    /**读取配置 */
    private final Config config;

    /**串口 */
    private static SerialPort serialPort =null;

    /**图片识别service */
    private final AiImageServiceImpl aiImageService;

    private final SerialPort2Service serialPort2Service;
    /**
     * 构造函数
     * @param config
     * @param aiImageService
     */
    public SerialPortServiceImpl(Config config, AiImageServiceImpl aiImageService, SerialPort2Service serialPort2Service) {
        this.config = config;
        this.aiImageService = aiImageService;
        this.serialPort2Service = serialPort2Service;
    }

    /**
     * 直接发送指令
     * @param msg
     * @throws PortInUseException
     * @throws NoSuchPortException
     * @throws InterruptedException
     * @throws UnsupportedCommOperationException
     */
    @Override
    public void sendData(String msg) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        try {
            openSerial();
            SerialTool.sendData(serialPort, (msg+"\n").getBytes());
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 直接发送指令
     * @param x
     * @param y
     * @param z
     * @param isMagi
     * @throws PortInUseException
     * @throws NoSuchPortException
     * @throws InterruptedException
     * @throws UnsupportedCommOperationException
     */
    @Override
    public void sendData(int x, int y, int z, boolean isMagi) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        String strSendData = buildSendString(x, y, z, MAGI, isMagi);
        sendData(strSendData);
        assert synchronousQueue.take()==PUT_ONE;
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
    public boolean readLight(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        String strSendData = buildSendString(x, y, z,LIGHT,false);
        sendData(strSendData);

        String strReturnData = synchronousLightQueue.take();
        log.info("take:[{}]",strReturnData);
        if(strReturnData.contains(strSendData)){
            log.debug("光电识别结果返回:[{}]",strReturnData);
            return strReturnData.endsWith("001");
        }
        return false;
    }

    /**
     * build send string to serial port
     * @param x
     * @param y
     * @param z
     * @param type
     * @param isMagi
     */
    private String buildSendString(int x, int y, int z, ActionTypeEnum type, boolean isMagi) {

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

    }

    /**
     * 普通的回到零点指令
     */
    @Override
    public void returnZeroPosi() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {
        sendData(RETURN_ZERO_POSI);
        assert synchronousQueue.take()==PUT_ONE;
    }

    /**
     * 当前位置设为零位
     */
    @Override
    public void setZeroPosi() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {
        sendData(ZERO_POSI);
        assert synchronousQueue.take()==PUT_ONE;
    }


    /**
     * 释放物品（消磁）
     */
    @Override
    public void execMagoff(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {
        sendData(x,y,z,false);
        assert synchronousQueue.take()==PUT_ONE;
    }

    /**
     * 托盘区释放物品（消磁）
     */
    @Override
    public void execMagoff(String msg) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {
        sendData(msg);
        assert synchronousQueue.take()==PUT_ONE;
    }

    /**
     * 吸取物品（吸磁）
     */
    @Override
    public void execMagNet(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {
        sendData(x,y,z,true);
        assert synchronousQueue.take()==PUT_ONE;
    }

    /**
     * 移动到托盘区
     */
    @Override
    public void moveToCart() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        sendData(config.MOVE_TO_CART_MAGNET_POSI);
        assert synchronousQueue.take()==PUT_ONE;
    }

    /**
     * 移动物品到托盘区并且释放,再回到零点
     */
    @Override
    public void moveGoods(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        //移动到指定地点
        this.sendData(x,y,0,false);

//        String picUrlFrom = null;
//        try {
//            picUrlFrom = this.aiImageService.captureImage();
//        } catch (IOException e) {
//            log.error("moveGoods",e);
//        }

        //伸Z
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("take 0(伸Z)");
        this.sendData(x, y, z, false);

        //吸取物品
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("take 1(吸取物品)");
        this.execMagNet(x,y,z);


        //缩Z
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("take 2(缩Z)");
        this.sendData(x, y, 0, true);

        //移动到托盘区域
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("take 3(移动到托盘区域)");
        this.moveToCart();

        //释放物品
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("take 4(释放物品)");
        this.execMagoff(config.MOVE_TO_CART_MAGOFF_POSI);

        //回到零点
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("take 5(回到零点)");
        this.returnZeroPosi();

//        //计算是否移动成功
//        try {
//            String picUrlTo = this.aiImageService.captureImage();
//            List<String> lstFrom = this.aiImageService.callCMD(picUrlFrom);
//            List<String> lstTo = this.aiImageService.callCMD(picUrlTo);
//            if(this.aiImageService.compareTwoList(lstFrom, lstTo)){
//                log.info("aiImage result:move succeed");
//            }else{
//                log.error("aiImage result:move failed");
//            }
//        } catch (IOException e) {
//            log.error("moveGoods",e);
//        }

        //执行完毕，返回
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("take 6(执行完毕，返回)");
        Thread.sleep(MAX_SLEEP*5);
    }

    /**
     * 移动物品从托盘区放到仓库区,再回到零点
     */
    @Override
    public void returnMoveGoods(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        //移动到托盘区域
        log.debug("移动到托盘区域");
        this.moveToCart();

        //伸Z
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("伸Z");
        this.sendData(config.CART_X, config.CART_Y, config.CART_Z, false);

        //吸取物品
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("吸取物品");
        this.execMagNet(config.CART_X, config.CART_Y, config.CART_Z);

        //缩Z
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("缩Z");
        this.sendData(config.CART_X, config.CART_Y, 0, true);

        //移动到指定地点
        assert synchronousQueue.take() == PUT_ONE;
        this.sendData(x,y,0,true);

        //释放物品
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("释放物品");
        this.execMagoff(x,y,0);

        //回到零点
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("回到零点");
        this.returnZeroPosi();

        //执行完毕，返回
        assert synchronousQueue.take() == PUT_ONE;
        log.debug("执行完毕，返回");
        Thread.sleep(MAX_SLEEP*5);
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
                                            synchronousQueue.put(PUT_ONE);
                                        }else if(sb.toString().contains("LIGHT")){
                                            //光电操作
                                            log.debug("put light queue");
                                            synchronousLightQueue.put(sb.toString());
                                        }else if(sb.toString().contains("SCAN")){
                                            //条形码扫描
                                            log.debug("put scan queue");
                                            synchronousScanQueue.put(sb.toString());
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
                String value = hmGoods.get(goodsBean.getGoodsId());
                if(StringUtils.isNotEmpty(value)){
                    //匹配到需要搬动的货物
                    String[] split = value.split("-");
                    assert split.length ==2;
                    CommonResult commonResult = serialPort2Service.outOfStock(split[0], split[1], "0");
                    if(commonResult.getCode()==CommonResult.SUCCESS){
                        this.moveGoods(goodsBean.getX(),goodsBean.getY(),config.MAX_VALUE_Z);
                        result.put(goodsBean.getGoodsId(), 1);
                    }else{
                        log.error("serialPort2Service.outOfStock return result is error");
                    }
                }
            }
        } catch (Exception e) {
            log.error("moveGoodsByFinder",e);
        }

        return result;
    }

    /**
     * 出库商品再入库
     *
     */
    @Override
    public int returnMoveGoodsByFinder(String turnTable, String box, String goodsId)  {

        try {

            File file = new File(config.SAVE_FINDER_PATH + "returnMove.txt");
            String strReturnMove;
            List<ReturnMoveBean> lstBean;
            if(file.exists()){
                strReturnMove =FileUtils.readFileToString(file);
                lstBean =
                        new Gson().fromJson(strReturnMove,new TypeToken<List<ReturnMoveBean>>(){}.getType());
            }else{
                //初次
                lstBean = new ArrayList<>();
                ReturnMoveBean item;
                for(int i = 0; i< RETURN_MOVE_SIZE; i++){
                    item = new ReturnMoveBean();
                    item.setIndex(i);
                    lstBean.add(item);
                }
            }

            //查找空位,找到后移动物体
            for(ReturnMoveBean item : lstBean){
                if(!item.isHave()){
                    //找到空位
                    int x = config.RETURN_VALUE_X;
                    int y = config.RETURN_VALUE_Y * config.RETURN_STEP_VALUE * (item.getIndex()+1);
                    CommonResult commonResult = serialPort2Service.outOfStock(turnTable, box, "0");
                    if(commonResult.getCode()==CommonResult.SUCCESS){
                        this.returnMoveGoods(x,y,config.MAX_VALUE_Z);
                    }else{
                        log.error("serialPort2Service.outOfStock return result is error");
                        return -2;
                    }
                }
            }

        } catch (Exception e) {
            log.error("moveGoodsByFinder",e);
            return -1;
        }

        return 0;
    }

    /**
     * 读取指定日期的json文件（定时任务生成）
     * @param yyyyMMdd
     * @return
     * @throws IOException
     */
    @Override
    public String getAllGoodsFromJsonFile(String yyyyMMdd) throws IOException {

        String fileName = getJsonFileName(yyyyMMdd);
        return FileUtils.readFileToString(new File(fileName));

    }

    /**
     * 获取json文件名称
     * @param yyyyMMdd
     * @return
     */
    @Override
    public String getJsonFileName(String yyyyMMdd) {
        StringBuilder fileName = new StringBuilder();

        fileName.append(config.SAVE_FINDER_PATH);
        fileName.append("finder_").append(yyyyMMdd).append(".json");
        return fileName.toString();
    }


    /**
     * 条形码读取
     * @return
     */
    @Override
    public String readGoodsId() {
        try {
            this.sendData(DO_SCAN);
            String result = synchronousScanQueue.take();//6970194002330#SCAN#360
            log.info("条形码扫描结果:{}",result);
            String[] split = result.split("#");
            assert split.length == 3;
            return split[0];
        } catch (Exception e) {
            log.error("readGoodsId",e);
            return "";
        }

    }

    /**
     * 地毯式扫描货物(定时任务执行），进行入库操作准备
     */
    @Override
    public List<GoodsBean> findAllGoodsByGrid()  {

        long start = System.currentTimeMillis();
        int stepGap=config.STEP_VALUE;
        int count=0;
        List<GoodsBean> lstFinderGoods = new ArrayList<>();
        for(int x=1;x*stepGap<=config.MAX_VALUE_X;x++){
            for(int y=1;y*stepGap<=config.MAX_VALUE_X;y++){
                log.debug("x:[{}],y:[{}]",x*stepGap,y*stepGap);
                ++count;
                try {

                    if(!this.readLight(x*stepGap,y*stepGap,config.MAX_VALUE_Z)){
                        continue;
                    }

                    String strGoodsId = this.readGoodsId();
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
        log.info("move count:[{}],spend time:[{}]s",count,(System.currentTimeMillis() - start)/1000);

        //回到零位
        try {
            this.setZeroPosi();
        } catch (Exception e) {
            log.error("setZeroPosi",e);
        }

        return lstFinderGoods;
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
