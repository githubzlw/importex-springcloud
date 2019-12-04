package com.importexpress.utils.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.util.UrlUtil;
import com.importexpress.utils.service.ExchangeRateService;
import com.importexpress.utils.util.CurrencyEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class FX_OERImpl implements ExchangeRateService {

    /**
     * {
     * "disclaimer": "Usage subject to terms: https://openexchangerates.org/terms",
     * "license": "https://openexchangerates.org/license",
     * "timestamp": 1574053200,
     * "base": "USD",
     * "rates": {
     * "AUD": 1.468192,
     * "CAD": 1.321732,
     * "CNY": 7.0116,
     * "EUR": 0.904064,
     * "GBP": 0.7737
     * }
     * }
     */
    private static final String URL = "https://openexchangerates.org/api/latest.json?app_id=b00a5a433ef04225af7aa44fa7537bf3&base=USD&symbols=EUR,CAD,GBP,AUD,CNY";

    public static void main(String[] args) throws Exception {
        System.out.println(new FX_OERImpl().getExchangeRate());
    }

    @Override
    public Map<String, BigDecimal> getExchangeRate() throws IOException {
        Map<String, BigDecimal> mapResult = new HashMap<>();


        JSONObject jsonObject = UrlUtil.getInstance().callUrlByGet(URL);
        Assert.isTrue("USD".equals(jsonObject.getString("base")), "query is error");

        JSONObject rates = jsonObject.getJSONObject("rates");
        mapResult.put("USD" + CurrencyEnum.AUD.toString(), rates.getBigDecimal(CurrencyEnum.AUD.toString()));
        mapResult.put("USD" + CurrencyEnum.CAD.toString(), rates.getBigDecimal(CurrencyEnum.CAD.toString()));
        mapResult.put("USD" + CurrencyEnum.EUR.toString(), rates.getBigDecimal(CurrencyEnum.EUR.toString()));
        mapResult.put("USD" + CurrencyEnum.GBP.toString(), rates.getBigDecimal(CurrencyEnum.GBP.toString()));
        mapResult.put("USD" + CurrencyEnum.CNY.toString(), rates.getBigDecimal(CurrencyEnum.CNY.toString()));

        Assert.isTrue(mapResult.size() == 5, "get rate result is not 5");

        return mapResult;

    }

}
