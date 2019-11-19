package com.importexpress.fx.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author luohao
 * @date 2019/11/18
 */
public interface ExchangeRateService {

    Map<String, BigDecimal> getExchangeRate() throws IOException;
}