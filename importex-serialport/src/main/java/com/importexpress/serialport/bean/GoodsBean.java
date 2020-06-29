package com.importexpress.serialport.bean;

import lombok.Builder;
import lombok.Data;

/**
 * @Author jack.luo
 * @create 2020/6/29 16:31
 * Description
 */
@Data
@Builder
public final class GoodsBean {

    private int x;

    private int y;

    private int z;

    private String goodsId;
}
