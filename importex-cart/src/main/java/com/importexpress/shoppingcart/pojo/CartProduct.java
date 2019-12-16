package com.importexpress.shoppingcart.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author luohao
 * @date 2019/12/16
 */
@Data
public class CartProduct implements Serializable {

    private Long productId;

    private BigDecimal salePrice;

    private Long productNum;

    private Long limitNum;

    private String checked;

    private String productName;

    private String productImg;

}
