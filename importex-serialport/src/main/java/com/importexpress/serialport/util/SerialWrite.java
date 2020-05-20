package com.importexpress.serialport.util;

import gnu.io.*;
import org.apache.commons.codec.binary.Hex;

import java.util.TooManyListenersException;

public class SerialWrite {
    public static void main(String[] args)
            throws Exception {

        String str = "#001000#000900#000200#MAGNET#360\n";
        byte[] abc = str.getBytes();
        //开启端口COM3，波特率9600
        final SerialPort serialPort = SerialTool.openSerialPort("COM3");
        //byte[] bytes = new byte[]{'#','0','0','0','0','0','0','#','0','0','0','0','0','0','#','0','0','0','0','0','0','#','M','A','G','O','F','F','#','3','6','0'};

        //SerialTool.sendData(serialPort, Hex.encodeHexString(bytes).getBytes());
        SerialTool.sendData(serialPort, str.getBytes());
        System.out.println(SerialTool.readData(serialPort));
        SerialTool.closeSerialPort(serialPort);
    }

    public static byte[] hexString2Bytes(String strSource) {
        if (strSource == null || "".equals(strSource.trim())) {
            System.out.println("hexString2Bytes 参数为空，放弃转换.");
            return null;
        }
        strSource = strSource.replace(" ", "");
        int l = strSource.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = Integer.valueOf(strSource.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }
}
