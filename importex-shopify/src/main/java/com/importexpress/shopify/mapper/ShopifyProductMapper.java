package com.importexpress.shopify.mapper;

import com.importexpress.shopify.pojo.product.ShopifyBean;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface ShopifyProductMapper {
    /**
     * 绑定shopify铺货的ID与我司网站的PID关联
     *
     * @param  shopifyBean
     * @return
     */
    @Insert("insert into shopify_pid_info_log(shopify_name,shopify_pid,pid,shopify_info,create_time,publish )" +
            "values( #{shopifyName},#{shopifyPid},#{pid},#{shopifyInfo},now(),#{publish})")
    int insertShopifyIdLog(ShopifyBean shopifyBean);
    /**
     * 绑定shopify铺货的ID与我司网站的PID关联
     *
     * @param  shopifyBean
     * @return
     */
    @Insert("insert into shopify_pid_info(shopify_name,shopify_pid,pid,create_time,publish,shopify_info )" +
            "values( #{shopifyName},#{shopifyPid},#{pid},now(),#{publish},#{shopifyInfo})")
    int insertShopifyIdWithPid(ShopifyBean shopifyBean);
    /**
     * 绑定shopify铺货的ID与我司网站的PID关联
     *
     * @param  shopifyBean
     * @return
     */
    @Update("update shopify_pid_info set shopify_pid=#{shopifyPid},publish=#{publish},shopify_info=#{shopifyInfo} " +
            "where  shopify_name=#{shopifyName} and pid=#{pid}")
    int updateShopifyIdWithPid(ShopifyBean shopifyBean);
    /**
     * 下架shopify商品
     *
     * @param  shopifyPid
     * @param  shopName
     * @return
     */
    @Update("update shopify_pid_info set publish=-1 " +
            "where  shopify_name=#{shopifyName} and shopify_pid=#{shopifyPid}")
    int deleteShopifyIdWithPid(String shopName,String shopifyPid);
    /**
     * 绑定shopify铺货的ID与我司网站的PID关联
     *
     * @param  shopifyBean
     * @return
     */
    @Select("select id,shopify_name,shopify_pid,pid,publish,shopify_info from shopify_pid_info " +
            "where shopify_name = #{shopifyName} and pid=#{pid} limit 1")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "shopify_name", property = "shopifyName"),
            @Result(column = "shopify_pid", property = "shopifyPid"),
            @Result(column = "publish", property = "publish"),
            @Result(column = "pid", property = "pid")
    })
    ShopifyBean selectShopifyId(ShopifyBean shopifyBean);

}