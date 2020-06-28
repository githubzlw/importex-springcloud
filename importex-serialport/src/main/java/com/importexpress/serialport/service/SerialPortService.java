package com.importexpress.serialport.service;


import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jack.luo
 * @date 2020/05/18
 */
public interface SerialPortService {

    void sendData(String msg) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void sendData(int x, int y, int z, boolean isMagi) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void returnZeroPosi() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void setZeroPosi() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void execMagoff(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void execMagoff(String msg) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void execMagNet(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void moveToCart() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void moveGoods(int x, int y, int z) throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException;

    void closeSerial();

    Map<String,Integer> findGoodsByGrid(Map<String, String> hmGoods) ;

    String readGoodsId() throws IOException;

//    void callCMD(String msg);
}
