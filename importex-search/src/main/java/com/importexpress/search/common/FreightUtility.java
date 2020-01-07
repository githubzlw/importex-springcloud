package com.importexpress.search.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class FreightUtility {
    /**
     * @Title: getShippingFormulaOne
     * @Author: cjc
     * @Despricetion: Description : 根据 首重 续重 计算运费 正常运费
     * @Date: 2018/12/8 14:17:16
     * @Param: [basePrice：首重运费, ratioPrice：续重运费, baseWeight：续重重量, weight：需要计算的重量]
     * @Return: java.math.BigDecimal
     */
    public static BigDecimal getShippingFormula(BigDecimal basePrice, BigDecimal ratioPrice, BigDecimal ratioWeight, BigDecimal weight) {
        BigDecimal shippingCost = new BigDecimal("0");
        BigDecimal zero = new BigDecimal("0");
        BigDecimal ratiopriceT = new BigDecimal("500");
        if (weight.compareTo(zero) == 1) {
            if (ratioWeight.compareTo(ratiopriceT) == 0) {
                shippingCost = basePrice.add((weight.divide(ratioWeight).setScale(0, BigDecimal.ROUND_UP).subtract(new BigDecimal(1d))).multiply(ratioPrice));
            } else {
                shippingCost = basePrice.add(weight.subtract(ratioWeight).divide(ratioWeight).multiply(ratioPrice));
            }
        }
        if (shippingCost.compareTo(zero) == -1) {
            shippingCost = zero;
        }
        return shippingCost;
    }
}
