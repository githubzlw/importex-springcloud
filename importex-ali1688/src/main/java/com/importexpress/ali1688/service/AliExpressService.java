package com.importexpress.ali1688.service;

import com.importexpress.comm.domain.CommonResult;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.aliexpress.service
 * @date:2020/3/16
 */
public interface AliExpressService {


    CommonResult getItemByKeyWord(Integer page, String keyword, boolean isCache);
}
