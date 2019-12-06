package com.importexpress.pay.service;

import com.importexpress.comm.domain.CommonResult;

import java.io.IOException;

/**
 * @author luohao
 * @date 2019/12/6
 */
public interface PaypalService {
    CommonResult refund(String captureId, Double amount) throws IOException;
}
