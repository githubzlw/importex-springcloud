package com.importexpress.shopify.service;

public interface UserService {
    /**
     * 更新客户shopify标识
     * @param userId : 客户ID
     * @param shopifyName : shopify店铺名称
     * @return
     */
    int updateUserShopifyFlag(int userId, String shopifyName);
}
