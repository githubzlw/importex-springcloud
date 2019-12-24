package com.importexpress.comm.pojo;

/**
 * @author jack.luo
 * @date 2018/10/23
 */
public enum TemplateType {
    ACCOUNT_UPDATE("ACCOUNT-UPDATE"),
    ACTIVATION("ACTIVATION"),
    NEW_PASSWORD("NEW-PASSWORD"),
    RECEIVED("RECEIVED"),
    WELCOME("WELCOME"),
    SEND_ERROR("SEND-ERROR");

    private String name;

    private TemplateType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name.toLowerCase();
    }
}
