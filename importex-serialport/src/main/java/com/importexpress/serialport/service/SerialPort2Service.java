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
public interface SerialPort2Service {

    void sendData(String msg) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void sendData(int x, int y, int z, boolean isMagi) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    String readLight(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void returnZeroPosi() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void setZeroPosi() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void execMagoff(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void execMagoff(String msg) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void execMagNet(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void moveToCart() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void moveGoods(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void closeSerial();

    Map<String,Integer> moveGoodsByFinder(Map<String, String> hmGoods) ;

    String getAllGoodsFromJsonFile(String yyyyMMdd) throws IOException;

    StringBuilder getJsonFileName(String yyyyMMdd);

    String readGoodsId(Map<Integer, Integer> mapTmp);

    List<GoodsBean> findAllGoodsByGrid();

    boolean outOfStock(String turnTable, String box, String number);

    boolean getNearSignal();

    boolean getLightSignal();

    boolean initStep();

    boolean moveTurnTable(String steps, String box);

    boolean warningLight(boolean onOrOff);
}
