package com.importexpress.email.pojo;

import lombok.Data;

@Data
public class OrderEmailBean {
    private int id;
    private String orderid;
    private int yourorder;
    private String goodsprice;
    private String goodsname;
    private String delivery_time;
    private String car_img;
    private String car_type;
    private String goods_typeimg;
}
