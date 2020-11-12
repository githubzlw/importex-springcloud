package com.importexpress.product.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 货币
 */
@Data
public class Currency implements Serializable {
    private static final long serialVersionUID = 986818736352041314L;

    /**
     * 币种,默认USD
     */
    @ApiModelProperty(value = "币种,默认USD")
    private String currency = "USD";

    /**
     * 汇率USD,默认1.0
     */
    @ApiModelProperty(value = "汇率USD,默认1.0")
    private double exchangeRate = 1.0;

    /**
     * 符号,默认$
     */
    @ApiModelProperty(value = "符号,默认$")
    private String symbol = "$";

}
