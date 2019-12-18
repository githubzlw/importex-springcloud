package com.importexpress.cart.pojo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author jack.luo
 * @date 2019/12/16
 */
@Data
@Slf4j
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

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

    /**重量 */
    private float wei;

    /**状态 上架:1 下架:0 */
    private int st =1;

    /**价格 */
    private BigDecimal pri;

    /**区间价格 */
    private String wpri;

    /**规格 */
    private String tn;

    /**数量 */
    private long num;

    /**是否勾选 1：勾选 0：未勾选 -1：无效 */
    private int chk;

    /**名称 */
    private String name;

    /**图片路径 */
    private String img;

    /**create timestamp */
    private long ct;

    /**update timestamp */
    private long ut;

    public String getItemId(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.pid).append(":");
        sb.append(this.sid1);
        if(this.sid2 > 0){
            sb.append(":").append(this.sid2);
        }
        return sb.toString();
    }

}
