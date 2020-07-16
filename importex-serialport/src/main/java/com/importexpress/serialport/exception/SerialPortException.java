package com.importexpress.serialport.exception;

import com.importexpress.serialport.service.SerialPort2Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author jack.luo
 * @create 2020/7/11 13:30
 * Description
 */
public class SerialPortException extends RuntimeException{

    private final int code;

    private final String msg;

    /** 扫描的条形码和实际要搬动的物体不一致*/
    public final static int SERIAL_PORT_EXCEPTION_NOT_SAME = 1000;

    /** 出库操作中，待放入的托盘的货架有物体*/
    public final static int SERIAL_PORT_EXCEPTION_HAVE_GOODS = 1001;

    /** 物体吊起失败*/
    public final static int SERIAL_PORT_EXCEPTION_PULL_GOODS = 1002;

    /** 出库操作中，托盘区的孔中已经有物体存在*/
    public final static int SERIAL_PORT_EXCEPTION_EXISTS_GOODS = 1003;

    /** 出库操作中，托盘区的孔中没有物体*/
    public final static int SERIAL_PORT_EXCEPTION_NOT_EXISTS_GOODS = 1004;

    /** 出库商品再入库操作中，托盘区的孔中没有物体*/
    public final static int SERIAL_PORT_EXCEPTION_NOT_EXISTS_GOODS_RETURN = 1005;

    /** 转盘操作失败*/
    public final static int SERIAL_PORT_EXCEPTION_TURN_TABLE_ERROR = 1006;

    /** 转盘操作失败*/
    public final static int SERIAL_PORT_EXCEPTION_INPUT_LIGHT = 1007;

    /** 转盘操作失败*/
    public final static int SERIAL_PORT_EXCEPTION_OUTPUT_LIGHT = 1008;

    /** 扫描的条形码失败*/
    public final static int SERIAL_PORT_EXCEPTION_SCAN = 1009;


    public SerialPortException(int code){
        super(String.valueOf(code));
        this.code = code;
        switch (code){
            case SERIAL_PORT_EXCEPTION_NOT_SAME:
                this.msg = "扫描的条形码和实际要搬动的物体不一致";
                break;
            case SERIAL_PORT_EXCEPTION_HAVE_GOODS:
                this.msg = "出库操作中，待放入的托盘的货架有物体";
                break;
            case SERIAL_PORT_EXCEPTION_PULL_GOODS:
                this.msg = "物体吊起失败";
                break;
            case SERIAL_PORT_EXCEPTION_EXISTS_GOODS:
                this.msg = "出库操作中，托盘区的孔中已经有物体存在";
                break;
            case SERIAL_PORT_EXCEPTION_NOT_EXISTS_GOODS:
                this.msg = "出库操作中，托盘区的孔中没有物体";
                break;
            case SERIAL_PORT_EXCEPTION_NOT_EXISTS_GOODS_RETURN:
                this.msg = "出库商品再入库操作中，托盘区的孔中没有物体";
                break;
            case SERIAL_PORT_EXCEPTION_TURN_TABLE_ERROR:
                this.msg = "转盘操作失败";
                break;
            case SERIAL_PORT_EXCEPTION_INPUT_LIGHT:
                this.msg = "获取装入信号失败";
                break;
            case SERIAL_PORT_EXCEPTION_OUTPUT_LIGHT:
                this.msg = "获取推出信号失败";
                break;
            case SERIAL_PORT_EXCEPTION_SCAN:
                this.msg = "扫描的条形码失败";
                break;
            default:
                this.msg = "";
        }
    }

    @Override
    public String toString(){
        return code + ":" + msg;
    }
}
