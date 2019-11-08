package com.importexpress.common.pojo;

/**
 * @author luohao
 * @date 2019/6/25
 */
public enum SiteEnum {

    IMPORTX(1),KIDS(2),PETS(4);

    private int code;

    SiteEnum(int code){
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }

}
