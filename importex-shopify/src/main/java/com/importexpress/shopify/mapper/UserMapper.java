package com.importexpress.shopify.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {
    /**
     * 更新客户shopify标识
     * @param userId : 客户ID
     * @param shopifyName : shopify店铺名称
     * @return
     */
    @Update("update user set shopify_flag = 1,shopify_name = #{shopifyName} where id = #{userId}")
    int updateUserShopifyFlag(@Param("userId") int userId, @Param("shopifyName") String shopifyName);
}
