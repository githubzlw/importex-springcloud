package com.importexpress.serialport.service;


import com.importexpress.serialport.bean.GoodsBean;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author jack.luo
 * @date 2020/05/18
 */
public interface SerialPortService {

    /**
     * 直接发送指令
     * @param msg
     * @throws PortInUseException
     * @throws NoSuchPortException
     * @throws InterruptedException
     * @throws UnsupportedCommOperationException
     */
    void sendData(String msg) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

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
    void sendData(int x, int y, int z, boolean isMagi) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

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
    boolean readLight(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    /**
     * 普通的回到零点指令
     */
    void returnZeroPosi() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    /**
     * 当前位置设为零位
     */
    void setZeroPosi() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    /**
     * 释放物品（消磁）
     */
    void execMagoff(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    /**
     * 托盘区释放物品（消磁）
     */
    void execMagoff(String msg) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    /**
     * 吸取物品（吸磁）
     */
    void execMagNet(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    /**
     * 移动到托盘区
     */
    void moveToCart() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    /**
     * 移动物品到托盘区并且释放,再回到零点
     */
    void moveGoods(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    /**
     * 关闭串口（长时间不用需要关闭）
     */
    void closeSerial();

    /**
     * 移动货物（前期已经地毯式扫描过货物，取得了货物坐标）
     * @param hmGoods
     */
    Map<String,Integer> moveGoodsByFinder(Map<String, String> hmGoods) ;

    /**
     * 读取指定日期的json文件（定时任务生成）
     * @param yyyyMMdd
     * @return
     * @throws IOException
     */
    String getAllGoodsFromJsonFile(String yyyyMMdd) throws IOException;

    /**
     * 获取json文件名称
     * @param yyyyMMdd
     * @return
     */
    String getJsonFileName(String yyyyMMdd);

    /**
     * 条形码读取
     * @return
     */
    String readGoodsId();

    /**
     * 地毯式扫描货物(定时任务执行），进行入库操作准备
     */
    List<GoodsBean> findAllGoodsByGrid();

//    void callCMD(String msg);
}
