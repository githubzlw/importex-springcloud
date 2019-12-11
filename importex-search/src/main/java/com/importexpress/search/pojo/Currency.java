package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 货币
 */
@Data
public class Currency implements Serializable {
    @ApiModelProperty(value = "币种,默认")
    private String currency = "USD";
    @ApiModelProperty(value = "汇率USD,默认1.0")
    private double exchangeRate = 1.0;
    @ApiModelProperty(value = "符号,默认$")
    private String symbol = "$";

}
