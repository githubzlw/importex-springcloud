package com.importexpress.search.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * @author luohao
 * @date 2018/7/26
 */
public class ExchangeRateUtil {

    private static final Logger logger = LogManager.getLogger(ExchangeRateUtil.class);

    private static ExchangeRateUtil _instance = null;

    private Map<String, Double> exchangeRateMap = null;

    private ExchangeRateUtil() {
    }

    public static ExchangeRateUtil getInstance() {
        if (_instance == null) {
            _instance = new ExchangeRateUtil();
        }
        return _instance;
    }

    public Map<String, Double> getExchangeRateMap() {
        return this.exchangeRateMap;
    }

    public void setExchangeRateMap(Map<String, Double> exchangeRateMap) {
        this.exchangeRateMap = exchangeRateMap;
    }

    public String getLastExchangeRateOfUSDRMB() {
        if (this.exchangeRateMap != null) {
            Double rmb = exchangeRateMap.get("RMB");
            return rmb != null ? rmb.toString() : StringUtils.EMPTY;
        } else {
            logger.error("获取不到最近汇率！！！");
            return StringUtils.EMPTY;
        }
    }


    /**
     * 根据货币名称获取汇率枚举，不存在或者无匹配返回USD
     * @param currencyName : AUD,CAD,EUR,GBP
     * @return
     */
    public CurrencyEnum getCurrencyValueByName(String currencyName) {
        CurrencyEnum rsEnum = CurrencyEnum.USD;
        if (StringUtils.isNotBlank(currencyName) && !CurrencyEnum.USD.getName().equalsIgnoreCase(currencyName)) {
            if (this.exchangeRateMap != null && this.exchangeRateMap.containsKey(currencyName)) {
                for (CurrencyEnum tempEn : CurrencyEnum.values()) {
                    if (tempEn.getName().toLowerCase().equals(currencyName.toLowerCase())) {
                        tempEn.setValue(this.exchangeRateMap.get(currencyName));
                        //tempEn.setValue(1000d);
                        rsEnum = tempEn;
                        break;
                    }
                }
            }
        }
        if("USD".equals(rsEnum.getName())){
            rsEnum.setValue(1);
        }
        //rsEnum.setValue(1000);
        return rsEnum;
    }


}
