package com.importexpress.ali1688.service;

import com.importexpress.comm.domain.CommonResult;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.ali1688.service
 * @date:2020/5/6
 */
public interface AmazonService {

    CommonResult getDetails(String pid);
}
