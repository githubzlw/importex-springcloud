package com.importexpress.serialport.service;


import java.io.IOException;

/**
 * @author jack.luo
 * @date 2020/05/18
 */
public interface SerialPortService {

    void comWriter(String msg);

    void callCMD(String msg);
}
