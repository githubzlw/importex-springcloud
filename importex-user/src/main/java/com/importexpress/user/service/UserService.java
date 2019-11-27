package com.importexpress.user.service;


import com.importexpress.user.pojo.UserBean;

public interface UserService {

	 /**
     * 更新客户shopify标识
     *
     * @param userId      : 客户ID
     * @param shopifyName : shopify店铺名称
     * @return
     */
    int updateUserShopifyFlag(int userId, String shopifyName);
    /**
     * 根据shopify店铺名称获取用户信息
     *
     * @param shopifyName
     * @return
     */
    UserBean getUserByShopifyName(String shopifyName);


}
