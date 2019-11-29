package com.importexpress.shopify.mapper;

import com.importexpress.shopify.pojo.orders.Line_items;
import com.importexpress.shopify.pojo.orders.Orders;
import com.importexpress.shopify.pojo.orders.Shipping_address;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.mapper
 * @date:2019/11/28
 */
@Component
@Mapper
public interface ShopifyOrderMapper {

    @Select(SqlProvider.SHOPIFY_ORDERINFO_QUERY)
    List<Orders> queryListByShopifyName(String shopifyName);

    @Insert(SqlProvider.SINGLE_ORDERINFO_INSERT)
    // @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int insertOrderInfoSingle(Orders orderInfo);


    @Select(SqlProvider.SINGLE_ORDER_DETAILS_QUERY)
    List<Line_items> queryOrderDetailsByOrderId(long orderNo);

    @Insert(SqlProvider.SINGLE_ORDER_DETAILS_INSERT)
    int insertOrderDetails(Line_items item);

    @Select(SqlProvider.SINGLE_ORDER_ADDRESS_QUERY)
    Shipping_address queryOrderAddressByOrderId(long orderNo);

    @Insert(SqlProvider.SINGLE_ORDER_ADDRESS_INSERT)
    int insertIntoOrderAddress(Shipping_address address);


    class SqlProvider {

        /*查询SQL*/
        private static final String SHOPIFY_ORDERINFO_QUERY = "select num_id,id,shopify_name,email,closed_at,created_at,updated_at,number,note,token,gateway,test,total_price,subtotal_price,total_weight,total_tax,taxes_included,currency,financial_status,confirmed,total_discounts,total_line_items_price,cart_token,buyer_accepts_marketing,name,referring_site,landing_site,cancelled_at,cancel_reason,total_price_usd,checkout_token,reference,user_id,location_id,source_identifier,source_url,processed_at,device_id,phone,customer_locale,app_id,browser_ip,landing_site_ref,order_number,processing_method,checkout_id,source_name,fulfillment_status,tags,contact_email,order_status_url,presentment_currency,admin_graphql_api_id,create_time,update_time from shopify_orderinfo where num_id in(select max(num_id) from shopify_orderinfo where shopify_name = #{shopifyName} group by id)";

        /*单个插入订单表*/
        private static final String SINGLE_ORDERINFO_INSERT = "insert into shopify_orderinfo(id,shopify_name,email,closed_at,created_at,updated_at,number,note,token,gateway,test,total_price,subtotal_price,total_weight,total_tax,taxes_included,currency,financial_status,confirmed,total_discounts,total_line_items_price,cart_token,buyer_accepts_marketing,name,referring_site,landing_site,cancelled_at,cancel_reason,total_price_usd,checkout_token,reference,user_id,location_id,source_identifier,source_url,processed_at,device_id,phone,customer_locale,app_id,browser_ip,landing_site_ref,order_number,processing_method,checkout_id,source_name,fulfillment_status,tags,contact_email,order_status_url,presentment_currency,admin_graphql_api_id) value (#{id},#{shopify_name},#{email},#{closed_at},#{created_at},#{updated_at},#{number},#{note},#{token},#{gateway},#{test},#{total_price},#{subtotal_price},#{total_weight},#{total_tax},#{taxes_included},#{currency},#{financial_status},#{confirmed},#{total_discounts},#{total_line_items_price},#{cart_token},#{buyer_accepts_marketing},#{name},#{referring_site},#{landing_site},#{cancelled_at},#{cancel_reason},#{total_price_usd},#{checkout_token},#{reference},#{user_id},#{location_id},#{source_identifier},#{source_url},#{processed_at},#{device_id},#{phone},#{customer_locale},#{app_id},#{browser_ip},#{landing_site_ref},#{order_number},#{processing_method},#{checkout_id},#{source_name},#{fulfillment_status},#{tags},#{contact_email},#{order_status_url},#{presentment_currency},#{admin_graphql_api_id})";

        /*查询订单详情表*/
        private static final String SINGLE_ORDER_DETAILS_QUERY = "select id,order_no,variant_id,title,quantity,sku,variant_title,vendor,fulfillment_service,product_id,requires_shipping,taxable,gift_card,name,variant_inventory_management,product_exists,fulfillable_quantity,grams,price,total_discount,fulfillment_status,admin_graphql_api_id,create_time from shopify_order_details where order_no = #{orderNo}";

        /*单个插入订单详情表*/
        private static final String SINGLE_ORDER_DETAILS_INSERT = "insert into shopify_order_details(order_no,variant_id,title,quantity,sku,variant_title,vendor,fulfillment_service,product_id,requires_shipping,taxable,gift_card,name,variant_inventory_management,product_exists,fulfillable_quantity,grams,price,total_discount,fulfillment_status,admin_graphql_api_id) values(#{order_no},#{variant_id},#{title},#{quantity},#{sku},#{variant_title},#{vendor},#{fulfillment_service},#{product_id},#{requires_shipping},#{taxable},#{gift_card},#{name},#{variant_inventory_management},#{product_exists},#{fulfillable_quantity},#{grams},#{price},#{total_discount},#{fulfillment_status},#{admin_graphql_api_id})";

        /*查询订单地址表*/
        private static final String SINGLE_ORDER_ADDRESS_QUERY = "select id,order_no,first_name,address1,phone,city,zip,province,country,last_name,address2,company,latitude,longitude,name,country_code,province_code,create_time from shopify_order_address where order_no = #{orderNo}";

        /*单个插入订单地址表*/
        private static final String SINGLE_ORDER_ADDRESS_INSERT = "insert into shopify_order_address(order_no,first_name,address1,phone,city,zip,province,country,last_name,address2,company,latitude,longitude,name,country_code,province_code) values(#{order_no},#{first_name},#{address1},#{phone},#{city},#{zip},#{province},#{country},#{last_name},#{address2},#{company},#{latitude},#{longitude},#{name},#{country_code},#{province_code})";

    }
}
