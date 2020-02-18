package com.importexpress.shopify.exception;

import lombok.ToString;

/**
 * @author jack.luo
 */
@ToString
public class ShopifyException extends RuntimeException {

    private String retCd;
    private String msgDes;


    public ShopifyException(String message) {
        this.msgDes = message;
    }

    public ShopifyException(String retCd, String msgDes) {
        this.retCd = retCd;
        this.msgDes = msgDes;
    }

    public String getRetCd() {
        return retCd;
    }

    public String getMsgDes() {
        return msgDes;
    }

}
