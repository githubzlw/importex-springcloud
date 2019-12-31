package com.importexpress.comm.pojo;

/**
 * @author jack.luo
 * @date 2018/10/23
 */
public enum TemplateType {

    // ----前台的
    ACCOUNT_UPDATE("ACCOUNT-UPDATE"),
    ACTIVATION("ACTIVATION"),
    NEW_PASSWORD("NEW-PASSWORD"),
    RECEIVED("RECEIVED"),
    WELCOME("WELCOME"),
    SEND_ERROR("SEND-ERROR"),

    // ----后台的
    CANCEL_ORDER("CANCEL-ORDER"),
    DISMANTLING_ORDER("DISMANTLING-ORDER"),
    SHOPPING_CART_NO_CHANGE("SHOPPING-CART-NO-CHANGE");



    private String name;

    private TemplateType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name.toLowerCase();
    }
}
