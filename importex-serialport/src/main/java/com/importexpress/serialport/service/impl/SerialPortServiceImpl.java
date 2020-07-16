package com.importexpress.serialport.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.importexpress.serialport.bean.ActionTypeEnum;
import com.importexpress.serialport.bean.GoodsBean;
import com.importexpress.serialport.bean.ReturnMoveBean;
import com.importexpress.serialport.exception.SerialPortException;
import com.importexpress.serialport.service.SerialPort2Service;
import com.importexpress.serialport.service.SerialPortService;
import com.importexpress.serialport.util.Config;
import com.importexpress.serialport.util.SerialTool;
import gnu.io.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import static com.importexpress.serialport.bean.ActionTypeEnum.LIGHT;
import static com.importexpress.serialport.bean.ActionTypeEnum.MAGI;
import static com.importexpress.serialport.exception.SerialPortException.*;


/**
 * 串口通信
 *
 * @Author jack.luo
 * @create 2020/05/18
 * Description
 */
@Service
@Slf4j
public class SerialPortServiceImpl implements SerialPortService {

    /**
     * 当前位置设为零位
     */
    private static final String ZERO_POSI = "#000000#000000#000000#X0Y0Z0#000";

    /**
     * 普通的回到零点指令
     */
    private static final String RETURN_ZERO_POSI = "#000000#000000#000000#000000#000";

    /**
     * 条形码扫描
     */
    private static final String DO_SCAN = "#000000#000000#000000#SCAN#000";

    /**
     * 同步queues使用的存放内容
     */
    private static final String PUT_ONE = "PUT_ONE";

    /**
     * 同步queue
     */
    private final SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();

    /** 光电操作同步queue*/
    private final SynchronousQueue<String> synchronousLightQueue = new SynchronousQueue<>();

    /** 条形码扫描同步queue*/
    private final SynchronousQueue<String> synchronousScanQueue = new SynchronousQueue<>();

    /**
     * 操作之间间隔时间
     */
    private static final int MAX_SLEEP = 3000;

    /**
     * 出库商品再入库的空位置的最大数量
     */
    private static final int RETURN_MOVE_SIZE = 10;

    /**
     * 退货区货物列表
     */
    private static final String FILE_RETURN_MOVE = "returnMove.txt";
    /**
     * 串口
     */
    private static SerialPort serialPort = null;
    /**
     * 读取配置
     */
    private final Config config;
    /**
     * 图片识别service
     */
    private final AiImageServiceImpl aiImageService;

    private final SerialPort2Service serialPort2Service;

    /**
     * 构造函数
     *
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
     *
     * @param msg
     * @throws PortInUseException
     * @throws NoSuchPortException
     * @throws InterruptedException
     * @throws UnsupportedCommOperationException
     */
    @Override
    public void sendData(String msg) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        openSerial();
        log.info("sendData:{}", msg);
        SerialTool.sendData(serialPort, (msg + "\n").getBytes());

    }

    /**
     * 直接发送指令
     *
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
    }

    /**
     * 读取光电信号
     *
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

        String strSendData = buildSendString(x, y, z, LIGHT, false);
        sendData(strSendData);

        String strReturnData = synchronousLightQueue.take();
        log.info("take:[{}]", strReturnData);
        if (strReturnData.contains(strSendData)) {
            log.debug("光电识别结果返回:[{}]", strReturnData);
            return strReturnData.endsWith("001");
        }
        return false;
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
        sendData(x, y, z, false);
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
        sendData(x, y, z, true);
    }

    /**
     * 移动到托盘区
     */
    @Override
    public void moveToCart() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        sendData(buildSendString(config.CART_X, config.CART_Y, config.CART_Z_1, MAGI, true));
    }

    /**
     * 移动物品到托盘区并且释放,再回到零点
     */
    @Override
    public void moveGoods(int x, int y, int z, String goodsId) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        //移动到指定地点
        this.sendData(x, y, 0, false);
        log.info("开始移动到指定地点");
//        String picUrlFrom = null;
//        try {
//            picUrlFrom = this.aiImageService.captureImage();
//        } catch (IOException e) {
//            log.error("moveGoods",e);
//        }

        //伸Z
        if (PUT_ONE.equals(synchronousQueue.take())) {
            log.info("开始伸Z");
            this.sendData(x, y, z, false);
        }

        //吸取物品
        if (PUT_ONE.equals(synchronousQueue.take())) {

            log.info("开始条形码扫描");
            //扫描条形码核对物体时候一致
            String readGoodsId = this.readScan();
            if (!goodsId.equals(readGoodsId)) {
                log.warn("扫描条形码核对物体时候发现不一致:{}:{}", goodsId, readGoodsId);
                throw new SerialPortException(SERIAL_PORT_EXCEPTION_NOT_SAME);
            }

            log.info("开始吸取物品");
            this.execMagNet(x, y, z);
        }

        //缩Z
        if (PUT_ONE.equals(synchronousQueue.take())) {
            log.info("开始缩Z");
            this.sendData(x, y, 0, true);
        }

        //移动到托盘区域
        if (PUT_ONE.equals(synchronousQueue.take())) {

            //判断是否吊起成功
            if (!this.readLight(x, y, 0)) {
                log.warn("吊起物体失败");
                throw new SerialPortException(SERIAL_PORT_EXCEPTION_PULL_GOODS);
            }

            log.info("开始移动到托盘区域");
            this.moveToCart();
        }

        //释放物品
        if (PUT_ONE.equals(synchronousQueue.take())) {

            //判断托盘区的孔中是否为空
            if (this.serialPort2Service.getNearSignal()) {
                log.info("判断托盘区的孔中是否为空");
                throw new SerialPortException(SERIAL_PORT_EXCEPTION_EXISTS_GOODS);
            }

            log.info("开始释放物品");
            this.execMagoff(buildSendString(config.CART_X, config.CART_Y, config.CART_Z_1, MAGI, false));
        }

        //回到零点
        if (PUT_ONE.equals(synchronousQueue.take())) {

            //判断托盘区的孔中是否有物体
            if (!this.serialPort2Service.getNearSignal()) {
                log.warn("释放物品到托盘区失败");
                throw new SerialPortException(SERIAL_PORT_EXCEPTION_NOT_EXISTS_GOODS);
            }

            log.info("开始回到零点");
            this.returnZeroPosi();
        }

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
        if (PUT_ONE.equals(synchronousQueue.take())) {
            log.info("执行完毕，返回");
            Thread.sleep(MAX_SLEEP * 5);
        }
    }

    /**
     * 移动物品从托盘区放到仓库区,再回到零点
     */
    @Override
    public void returnMoveGoods(int x, int y, int z, String goodsId) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        //移动到托盘区域
        this.moveToCart();
        log.info("开始移动到托盘区域");

        //判断托盘区的孔中是否有物体
        if (!this.serialPort2Service.getNearSignal()) {
            log.info("判断托盘区的孔中是否有物体");
            throw new SerialPortException(SERIAL_PORT_EXCEPTION_NOT_EXISTS_GOODS_RETURN);
        }

        //伸Z
        if (PUT_ONE.equals(synchronousQueue.take())) {
            log.info("开始伸Z");
            this.sendData(config.CART_X, config.CART_Y, config.CART_Z_2, false);
        }

        //吸取物品
        if (PUT_ONE.equals(synchronousQueue.take())) {

            log.info("开始条形码扫描");
            //扫描条形码核对物体时候一致
            String readGoodsId = this.readScan();
            if (!goodsId.equals(readGoodsId)) {
                log.warn("扫描条形码核对物体时候发现不一致:{}:{}", goodsId, readGoodsId);
                throw new SerialPortException(SERIAL_PORT_EXCEPTION_NOT_SAME);
            }

            log.info("开始吸取物品");
            this.execMagNet(config.CART_X, config.CART_Y, config.CART_Z_2);
        }

        //缩Z
        if (PUT_ONE.equals(synchronousQueue.take())) {
            log.info("开始缩Z");
            this.sendData(config.CART_X, config.CART_Y, 0, true);
        }

        //移动到指定地点
        if (PUT_ONE.equals(synchronousQueue.take())) {

            //判断是否吊起成功
            if (!this.readLight(x, y, 0)) {
                log.info("判断是否吊起成功");
                throw new SerialPortException(SERIAL_PORT_EXCEPTION_PULL_GOODS);
            }

            log.info("开始移动到指定地点");
            this.sendData(x, y, 0, true);
        }

        //释放物品
        if (PUT_ONE.equals(synchronousQueue.take())) {
            log.info("开始释放物品");
            this.execMagoff(x, y, 0);
        }

        //回到零点
        if (PUT_ONE.equals(synchronousQueue.take())) {
            log.info("开始回到零点");
            this.returnZeroPosi();
        }

        //执行完毕，返回
        if (PUT_ONE.equals(synchronousQueue.take())) {
            log.info("执行完毕，返回");
            Thread.sleep(MAX_SLEEP * 5);
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
        serialPort = null;
    }

    /**
     * 移动货物（前期已经地毯式扫描过货物，取得了货物坐标）
     *
     * @param hmGoods
     */
    @Override
    public Map<String, Integer> moveGoodsByFinder(Map<String, String> hmGoods) throws IOException, PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        Map<String, Integer> result = new HashMap<>(hmGoods.size());
        if (hmGoods.size() == 0) {
            return result;
        }

        String json = null;

        json = getAllGoodsFromJsonFile(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        List<GoodsBean> lstGoodsBean = new Gson().fromJson(json, new TypeToken<List<GoodsBean>>() {
        }.getType());
        for (GoodsBean goodsBean : lstGoodsBean) {
            String value = hmGoods.get(goodsBean.getGoodsId());
            if (StringUtils.isNotEmpty(value)) {
                //匹配到需要搬动的货物
                log.debug("匹配到需要搬动的货物,value={}", goodsBean);
                String[] split = value.split("_");
                assert split.length == 2;
                boolean commonResult = serialPort2Service.outOfStock(split[0], split[1], "0");
                if (commonResult) {
                    this.moveGoods(goodsBean.getX(), goodsBean.getY(), goodsBean.getZ(), goodsBean.getGoodsId());
                    commonResult = serialPort2Service.initStep();
                    if (commonResult) {
                        result.put(goodsBean.getGoodsId(), 1);
                    } else {
                        throw new IOException("serialPort2Service.initStep return result is error");
                    }
                } else {
                    throw new IOException("serialPort2Service.outOfStock return result is error");
                }
            } else {
                //从退货区再查询

                List<ReturnMoveBean> lstBean;
                File file = new File(config.SAVE_FINDER_PATH + FILE_RETURN_MOVE);
                if (file.exists()) {
                    String strReturnMove = FileUtils.readFileToString(file);
                    lstBean =
                            new Gson().fromJson(strReturnMove, new TypeToken<List<ReturnMoveBean>>() {
                            }.getType());
                    for (ReturnMoveBean item : lstBean) {
                        if (goodsBean.getGoodsId().equals(item.getGoodsId())) {
                            //找到货物
                            String[] split = value.split("-");
                            assert split.length == 2;
                            boolean commonResult = serialPort2Service.outOfStock(split[0], split[1], "0");
                            if (commonResult) {
                                int x = config.RETURN_VALUE_X;
                                int y = config.RETURN_VALUE_Y * config.RETURN_STEP_VALUE * (item.getIndex() + 1);
                                this.moveGoods(x, y, config.GOODS_MOVE_VALUE_Z, item.getGoodsId());
                                //清空此位置
                                item.setGoodsId(null);
                                item.setHave(false);
                                result.put(goodsBean.getGoodsId(), 1);
                                break;
                            } else {
                                log.error("serialPort2Service.outOfStock return result is error");
                            }
                        }
                    }
                    //保存json到文件
                    saveReturnMoveFile(lstBean, file);
                }
            }
        }


        return result;
    }

    /**
     * 出库商品再入库
     */
    @Override
    public int returnMoveGoodsByFinder(String turnTable, String box, String goodsId) {

        try {

            File file = new File(config.SAVE_FINDER_PATH + FILE_RETURN_MOVE);
            String strReturnMove;
            List<ReturnMoveBean> lstBean;
            if (file.exists()) {
                strReturnMove = FileUtils.readFileToString(file);
                lstBean =
                        new Gson().fromJson(strReturnMove, new TypeToken<List<ReturnMoveBean>>() {
                        }.getType());
            } else {
                //初次
                lstBean = new ArrayList<>();
                ReturnMoveBean item;
                for (int i = 0; i < RETURN_MOVE_SIZE; i++) {
                    item = new ReturnMoveBean();
                    item.setIndex(i);
                    lstBean.add(item);
                }
            }

            //查找空位,找到后移动物体
            for (ReturnMoveBean item : lstBean) {
                if (!item.isHave()) {
                    //找到空位
                    int x = config.RETURN_VALUE_X;
                    int y = config.RETURN_VALUE_Y * config.RETURN_STEP_VALUE * (item.getIndex() + 1);
                    if (serialPort2Service.outOfStock(turnTable, box, "0")) {
                        //移动货物
                        this.returnMoveGoods(x, y, config.GOODS_MOVE_VALUE_Z, item.getGoodsId());
                        item.setHave(true);
                        item.setGoodsId(goodsId);
                        break;
                    } else {
                        log.error("serialPort2Service.outOfStock return result is error");
                        return -2;
                    }
                }
            }

            //保存json到文件
            saveReturnMoveFile(lstBean, file);
        }catch (SerialPortException spe){
            throw spe;
        } catch (Exception e) {
            log.error("moveGoodsByFinder", e);
            return -1;
        }

        return 0;
    }

    /**
     * 读取指定日期的json文件（定时任务生成）
     *
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
     *
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
     *
     * @return
     * @throws PortInUseException
     * @throws NoSuchPortException
     * @throws InterruptedException
     * @throws UnsupportedCommOperationException
     */
    @Override
    public String readScan() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        this.sendData(DO_SCAN);
        //sample: 6970194002330#SCAN#000
        String result = synchronousScanQueue.poll(10, TimeUnit.SECONDS);
        if ("000#SCAN#404".equals(result)) {
            //读取失败,重读一次
            this.sendData(DO_SCAN);
            result = synchronousScanQueue.poll(10, TimeUnit.SECONDS);
        }
        log.info("条形码扫描结果:{}", result);
        if(StringUtils.isEmpty(result)){
            throw new SerialPortException(SERIAL_PORT_EXCEPTION_SCAN);
        }
        String[] split = result.split("#");
        assert split.length == 3;

        return "000".equals(split[0]) ? StringUtils.EMPTY : split[0];

    }

    /**
     * 地毯式扫描货物(定时任务执行），进行入库操作准备
     */
    @Override
    public List<GoodsBean> findAllGoodsByGrid() {

        long start = System.currentTimeMillis();
        int stepGap = config.STEP_VALUE;
        int count = 0;
        List<GoodsBean> lstFinderGoods = new ArrayList<>();
        for (int x = 1; x * stepGap <= config.MAX_VALUE_X; x++) {
            for (int y = 1; y * stepGap <= config.MAX_VALUE_X; y++) {
                log.debug("x:[{}],y:[{}]", x * stepGap, y * stepGap);
                ++count;
                try {

                    if (!this.readLight(x * stepGap, y * stepGap, config.MAX_VALUE_Z)) {
                        continue;
                    }

                    String strGoodsId = this.readScan();
                    if (StringUtils.isNotEmpty(strGoodsId)) {
                        log.info("find goods (x,y):[{},{}]", x * stepGap, y * stepGap);
                        lstFinderGoods.add(
                                GoodsBean.builder().x(x * stepGap).y(y * stepGap).goodsId(strGoodsId).build());
                    }
                } catch (Exception e) {
                    log.error("findAllGoodsByGrid", e);
                }
            }
        }
        log.info("move count:[{}],spend time:[{}]s", count, (System.currentTimeMillis() - start) / 1000);

        //回到零位
        try {
            this.setZeroPosi();
        } catch (Exception e) {
            log.error("setZeroPosi", e);
        }

        return lstFinderGoods;
    }

    /**
     * build send string to serial port
     *
     * @param x
     * @param y
     * @param z
     * @param type
     * @param isMagi
     */
    private String buildSendString(int x, int y, int z, ActionTypeEnum type, boolean isMagi) {

        if (x < 0 || y < 0 || z < 0) {
            throw new IllegalArgumentException("input xyz is not right.");
        }

        if (x > config.MAX_VALUE_X || y > config.MAX_VALUE_Y || z > config.MAX_VALUE_Z) {
            throw new IllegalArgumentException("input xyz is not right.");
        }

        //sample: #000000#000000#000000#MAGOFF#000
        StringBuilder sb = new StringBuilder();
        sb.append('#').append(StringUtils.leftPad(String.valueOf(x), 6, '0'));
        sb.append('#').append(StringUtils.leftPad(String.valueOf(y), 6, '0'));
        sb.append('#').append(StringUtils.leftPad(String.valueOf(z), 6, '0'));

        switch (type) {
            case MAGI:
                if (isMagi) {
                    sb.append("#MAGNET");
                } else {
                    sb.append("#MAGOFF");
                }
                break;
            case LIGHT:
                sb.append("#LIGHT");
                break;
            default:
                throw new IllegalArgumentException("type is invalid");
        }
        sb.append("#000");

        return sb.toString();

    }

    /**
     * 打开串口并监听
     *
     * @throws NoSuchPortException
     * @throws PortInUseException
     * @throws UnsupportedCommOperationException
     * @throws InterruptedException
     */
    private void openSerial() throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, InterruptedException {
        if (serialPort == null) {
            log.debug("begin open serial : [{}]", config.SERIAL_PORT);
            serialPort = SerialTool.openSerialPort(config.SERIAL_PORT);
            Thread.sleep(MAX_SLEEP);
            try {
                serialPort.notifyOnDataAvailable(true);
                SerialTool.setListenerToSerialPort(serialPort, serialPortEvent -> {
                            if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                                // we get here if data has been received
                                final StringBuilder sb = new StringBuilder();
                                final byte[] readBuffer = new byte[20];
                                try (InputStream inputStream = serialPort.getInputStream()) {
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

                                    log.info("received data:[{}]", sb.toString());
                                    try {
                                        if (sb.toString().contains("success,finish")) {
                                            log.info("put finish queue");
                                            synchronousQueue.put(PUT_ONE);
                                        } else if (sb.toString().contains("LIGHT")) {
                                            //光电操作
                                            log.info("put light queue");
                                            synchronousLightQueue.put(sb.toString());
                                        } else if (sb.toString().contains("SCAN")) {
                                            //条形码扫描
                                            log.info("put scan queue");
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
     * save json to return_move.txt file
     *
     * @param lstBean
     * @param file
     * @throws IOException
     */
    private void saveReturnMoveFile(List<ReturnMoveBean> lstBean, File file) throws IOException {
        String saveJson = new Gson().toJson(lstBean);
        FileUtils.writeStringToFile(file, saveJson);
    }


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

}
