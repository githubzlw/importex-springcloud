package com.importexpress.shoppingcart.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author luohao
 * @date 2019/12/16
 */
@Data
public class Shop {
    /**
     * 店铺id
     */
    private String shoipId;
    /**
     * 店铺中文名字
     */
    private String shopNameCn;
    /**
     * 店铺英文文名字
     */
    private String shopNameEn;
    /**
     * 当前店铺的总金额
     */
    private BigDecimal sumProductPrice;
    /**
     * 产品list
     */
    private List<ShoppingCartItem> item;
    /**
     * 是否需要店铺费
     * 1: 需要 2: 本来需要 但是满足15了 就不用了,要划掉
     */
    private int isProcessingFee;
}
