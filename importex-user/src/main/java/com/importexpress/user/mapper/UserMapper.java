package com.importexpress.user.mapper;

import com.importexpress.user.pojo.UserBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {


    /**
     * 更新客户shopify标识
     *
     * @param userId      : 客户ID
     * @param shopifyName : shopify店铺名称
     * @return
     */
    int updateUserShopifyFlag(@Param("userId") int userId, @Param("shopifyName") String shopifyName);


    /**
     * 根据shopify店铺名称获取用户信息
     *
     * @param shopifyName
     * @return
     */
    UserBean getUserByShopifyName(@Param("shopifyName") String shopifyName);
}
