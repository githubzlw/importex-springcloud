package com.importexpress.shopify.mapper;

import com.importexpress.shopify.pojo.ShopifyAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShopifyAuthMapper{
  /*  int countByExample(ShopifyAuthExample example);

    int deleteByExample(ShopifyAuthExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ShopifyAuth record);

    int insertSelective(ShopifyAuth record);

    List<ShopifyAuth> selectByExampleWithRowbounds(ShopifyAuthExample example, RowBounds rowBounds);

    List<ShopifyAuth> selectByExample(ShopifyAuthExample example);*/

    @Select("select id,shop_name as shopName,access_token as accessToken,scope,create_time as createTime,update_time as updateTime " +
            " from shopify_auth where shop_name=#{shopName}")
    List<ShopifyAuth> selectByShopName(String shopName);

   /* ShopifyAuth selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ShopifyAuth record, @Param("example") ShopifyAuthExample example);

    int updateByExample(@Param("record") ShopifyAuth record, @Param("example") ShopifyAuthExample example);

    int updateByPrimaryKeySelective(ShopifyAuth record);

    int updateByPrimaryKey(ShopifyAuth record);*/

    /**
     * 绑定shopify铺货的ID与我司网站的PID关联
     *
     * @param  shopifyBean
     * @return
     */
//    int insertShopifyIdWithPid(ShopifyBean shopifyBean);

    /**
     *  根据shopify店铺名称获取所有对应的PID
     *
     * @param shopifyName
     * @return
     */
//    List<ShopifyBean> queryPidbyShopifyName(@Param("shopifyName") String shopifyName);

}