package com.importexpress.fx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.util.UrlUtil;
import com.importexpress.fx.service.ExchangeRateService;
import com.importexpress.fx.util.CurrencyEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class FX_K780Impl implements ExchangeRateService {

    /**
     * {
     * "success": "1",
     * "result": {
     * "status": "ALREADY",
     * "scur": "USD",
     * "tcur": "EUR",
     * "ratenm": "美元/欧元",
     * "rate": "0.9037",
     * "update": "2019-11-18 13:55:00"
     * }
     * }
     */

    private static final String URL = "http://api.k780.com/?app=finance.rate&scur=usd&tcur=%s&appkey=46712&sign=9563c8f536a285c92c8abc940d6c816c&format=jso";

    public static void main(String[] args) throws Exception {
        System.out.println(new FX_K780Impl().getExchangeRate());
    }

    @Override
    public Map<String, BigDecimal> getExchangeRate() throws IOException {
        Map<String, BigDecimal> mapResult = new HashMap<>();

        JSONObject jsonObject;
        for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
            jsonObject = UrlUtil.getInstance().callUrlByGet(String.format(URL, currencyEnum.toString()));
            Assert.isTrue("1".equals(jsonObject.getString("success")), "query is error");
            JSONObject result = jsonObject.getJSONObject("result");
            mapResult.put("USD" + currencyEnum.toString(), result.getBigDecimal("rate"));
        }
        Assert.isTrue(mapResult.size() == 5, "get rate result is not 5");
        return mapResult;

    }


}
