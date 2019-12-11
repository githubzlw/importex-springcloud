package com.importexpress.shopify.service;

import com.importexpress.shopify.pojo.ImportProductBean;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.service
 * @date:2019/12/10
 */
public interface OverSeaProductService {

    List<ImportProductBean> queryOverSeaProductList();
}
