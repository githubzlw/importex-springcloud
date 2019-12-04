package com.importexpress.utils.service;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExchangeRateServiceFactoryTest {

    @Autowired
    private ExchangeRateServiceFactory exchangeRateServiceFactory;

    @Test
    public void test() throws IOException {
        Map<String, BigDecimal> exchangeRate = exchangeRateServiceFactory.getExchangeRate();
        Assert.assertEquals(5, exchangeRate.size());
    }


}
