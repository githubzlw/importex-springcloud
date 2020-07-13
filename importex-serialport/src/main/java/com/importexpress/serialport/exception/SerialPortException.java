package com.importexpress.serialport.exception;

/**
 * @Author jack.luo
 * @create 2020/7/11 13:30
 * Description
 */
public class SerialPortException extends RuntimeException{

    /** 扫描的条形码和实际要搬动的物体不一致*/
    public final static int SERIAL_PORT_EXCEPTION_NOT_SAME = 1000;

    /** 出库操作中，待放入的托盘的货架有物体*/
    public final static int SERIAL_PORT_EXCEPTION_HAVE_GOODS = 1001;



    private int code;
    private String msg;


    public SerialPortException(int code){
        this.code = code;
        switch (code){
            case SERIAL_PORT_EXCEPTION_NOT_SAME:
                this.msg = "扫描的条形码和实际要搬动的物体不一致";
                break;
            case SERIAL_PORT_EXCEPTION_HAVE_GOODS:
                this.msg = "出库操作中，待放入的托盘的货架有物体";
                break;
            default:
                this.msg = "";
        }
    }

    public SerialPortException(String code,String msg){
        this.msg = msg;
    }

    @Override
    public String toString(){
        return "code:" + code + " msg:" + msg;
    }
}
