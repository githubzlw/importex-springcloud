package com.importexpress.serialport.bean;

import lombok.Data;

/**
 * @Author jack.luo
 * @create 2020/7/9 13:27
 * Description
 */
@Data
public class ReturnMoveBean {

    private int index;

    private boolean isHave = false;

    private String goodsId;
}
