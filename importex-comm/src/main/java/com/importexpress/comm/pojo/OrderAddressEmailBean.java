package com.importexpress.comm.pojo;

import lombok.Data;

@Data
public class OrderAddressEmailBean {
    private int id;
    private int AddressID;
    private String orderno;
    private String Country;
    private String statename;
    private String address;
    private String address2;
    private String phoneNumber;
    private String zipcode;
    private String Adstatus;
    private String Updatetimr;
    private String admUserID;
    private String street;
    private String recipients;
    private String product_cost;
    private String mode_transport;
    private String currency;
    private String pay_price_tow;
    private String pay_price;
    private String create_time;

}
