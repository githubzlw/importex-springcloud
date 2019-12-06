package com.importexpress.pay.service;

import java.io.IOException;

/**
 * @author luohao
 * @date 2019/12/6
 */
public interface PaypalService {
    boolean refund(String captureId, Double amount) throws IOException;
}
