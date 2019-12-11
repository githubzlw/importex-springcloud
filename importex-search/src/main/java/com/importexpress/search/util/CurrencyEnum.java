package com.importexpress.search.util;
/**
 * @author JiangXw
 * @date 2019/10/10
 */
public enum CurrencyEnum {
    /**
     * 货币枚举
     */
    USD("USD",1,"US$"),AUD("AUD",1,"AU$"),CAD("CAD",1,"C$"),EUR("EUR",1,"€"),GBP("GBP",1,"£");

    CurrencyEnum(String name, double value, String symbol) {
        this.name = name;
        this.value = value;
        this.symbol = symbol;
    }

    /**
     * 汇率名称
     */
    private String name;
    /**
     * 汇率值
     */
    private double value;

    /**
     * 货币符号
     */
    private String symbol;


    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getSymbol() {
        return symbol;
    }
}
