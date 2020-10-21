package com.importexpress.cart.pojo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jack.luo
 * @date 2019/12/16
 */
@Data
@Slf4j
public class CartItem {

    private long pid;

    private long sid1;

    private long sid2;

    /**
     * 店铺id
     */
    private String si;

    /**
     * 店铺英文名字
     */
    private String sn;

    /**
     * 重量
     */
    private float wei;

    /**
     * 状态 上架:1 下架:0
     */
    private int st = 1;

    /**
     * 价格
     */
    private long pri;

    /**
     * 区间价格
     */
    private String wpri;

    /**
     * 规格（多规格情况下用@分割）
     */
    private String tn;

    /**
     * sku
     */
    private String sku;


    /**
     * 数量
     */
    private long num;

    /**
     * 是否勾选 1：勾选 0：未勾选 -1：无效
     */
    private int chk;

    /**
     * 名称
     */
    private String name;

    /**
     * 图片路径
     */
    private String img;


    /**
     * sellunit
     */
    private String su;

    /**
     * remotpath
     */
    private String rp;

    /**
     * morder
     */
    private String mo;

    /**
     * range_price
     */
    private String rpe;

    /**
     * feeprice
     */
    private String fp;

    /**
     * 美加限制区分(1可搜索，0不可搜索)
     */
    private String sl;

    /**
     * 备注
     */
    private String memo;

    /**
     * 备用1
     */
    private String bk1;

    /**
     * 备用2
     */
    private String bk2;

    /**
     * 备用3
     */
    private String bk3;

    /**
     * 备用4
     */
    private String bk4;

    /**
     * 备用5
     */
    private String bk5;

    /**
     * create timestamp
     */
    private long ct;

    /**
     * update timestamp
     */
    private long ut;

    /**
     * match source
     */
    private  int ms = 0;
    /**
     * 得到唯一ID标识购物车商品
     * @return
     */
    public String getItemId() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.pid).append(":");
        sb.append(this.sid1);
        if (this.sid2 > 0) {
            sb.append(":").append(this.sid2);
        }
        return sb.toString();
    }

}
