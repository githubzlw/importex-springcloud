package com.importexpress.shopify.mapper;

import com.importexpress.shopify.pojo.ShopifyAuth;
import com.importexpress.shopify.pojo.ShopifyAuthExample;
import com.importexpress.shopify.pojo.product.ShopifyBean;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShopifyAuthMapper{
  /*  int countByExample(ShopifyAuthExample example);

    int deleteByExample(ShopifyAuthExample example);*/

    int deleteByPrimaryKey(Integer id);

    @Insert(" insert into shopify_auth (id, shop_name, access_token,scope, create_time, update_time )" +
            "values (#{id,jdbcType=INTEGER}, #{shopName,jdbcType=VARCHAR}, #{accessToken,jdbcType=VARCHAR}," +
            " #{scope,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})")
    int insert(ShopifyAuth record);

    int insertSelective(ShopifyAuth record);

    /* List<ShopifyAuth> selectByExampleWithRowbounds(ShopifyAuthExample example, RowBounds rowBounds);

    List<ShopifyAuth> selectByExample(ShopifyAuthExample example);*/


    @Select("select id,shop_name,access_token,scope,create_time,update_time " +
            "from shopify_auth where shop_name=#{shopName}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "shop_name", property = "shopName"),
            @Result(column = "access_token", property = "accessToken"),
            @Result(column = "scope", property = "scope"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime")
    })
    List<ShopifyAuth> selectByShopName(String shopName);

   /* ShopifyAuth selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ShopifyAuth record, @Param("example") ShopifyAuthExample example);

    int updateByExample(@Param("record") ShopifyAuth record, @Param("example") ShopifyAuthExample example);*/


    int updateByPrimaryKeySelective(ShopifyAuth record);
    @Update("update shopify_auth set  shop_name = #{shopName,jdbcType=VARCHAR}, " +
            "access_token = #{accessToken,jdbcType=VARCHAR}," +
            "scope = #{scope,jdbcType=VARCHAR}," +
            "update_time = #{updateTime,jdbcType=TIMESTAMP} " +
            "where id = #{id,jdbcType=INTEGER}")
    int updateByPrimaryKey(ShopifyAuth record);

    /**
     * 绑定shopify铺货的ID与我司网站的PID关联
     *
     * @param  shopifyBean
     * @return
     */
    @Insert("insert into shopify_pid_info(shopify_name,shopify_pid,pid,shopify_info,create_time )" +
            "values( #{shopifyName},#{shopifyPid},#{pid},#{shopifyInfo},now())")
    int insertShopifyIdWithPid(ShopifyBean shopifyBean);
    /**
     * 绑定shopify铺货的ID与我司网站的PID关联
     *
     * @param  shopifyBean
     * @return
     */
    @Update("update shopify_pid_info set shopify_pid=#{shopifyPid},shopify_info=#{shopifyInfo} " +
            "where  shopify_name=#{shopifyName} and pid=#{pid}")
    int updateShopifyIdWithPid(ShopifyBean shopifyBean);
    /**
     * 绑定shopify铺货的ID与我司网站的PID关联
     *
     * @param  shopifyBean
     * @return
     */
    @Select("select id,shopify_name,shopify_pid,pid from shopify_pid_info " +
            "where shopify_name = #{shopifyName} and pid=#{pid} limit 1")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "shopify_name", property = "shopifyName"),
            @Result(column = "shopify_pid", property = "shopifyPid"),
            @Result(column = "pid", property = "pid"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "shopify_info", property = "shopifyInfo")
    })
    ShopifyBean selectShopifyId(ShopifyBean shopifyBean);

    /**
     *  根据shopify店铺名称获取所有对应的PID
     *
     * @param shopifyName
     * @return
     */
    @Select("select id,shopify_name,shopify_pid,pid from shopify_pid_info where shopify_name = #{shopifyName}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "shopify_name", property = "shopifyName"),
            @Result(column = "shopify_pid", property = "shopifyPid"),
            @Result(column = "pid", property = "pid"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "shopify_info", property = "shopifyInfo")
    })
    List<ShopifyBean> queryPidbyShopifyName(@Param("shopifyName") String shopifyName);

}