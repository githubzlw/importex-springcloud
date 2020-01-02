package com.importexpress.email.mapper;

import com.importexpress.comm.pojo.OrderAddressEmailBean;
import com.importexpress.comm.pojo.OrderEmailBean;
import com.importexpress.comm.pojo.UserBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.email.mapper
 * @date:2019/12/31
 */
@Mapper
@Component
public interface QueryMapper {


    @Select(SqlProvider.GET_USER_BY_EMAIL)
    UserBean getUserByEmail(@Param("email") String email, @Param("site") int site);

    @Update(SqlProvider.UP_USER_ACTIVATION_CODE_STATE2)
    int upUserActivationCodeState2(@Param("email") String email, @Param("activationCode") String activationCode);

    @Update(SqlProvider.UP_USER_ACTIVATION_CODE_STATE1)
    int upUserActivationCodeState1(@Param("email") String email, @Param("activationCode") String activationCode);

    @Select(SqlProvider.GET_ORDER_DETAILS)
    List<OrderEmailBean> getOrderDetails(String orderNo);


    @Select(SqlProvider.GET_ORDER_ADDRESS_EMAIL_INFO)
    OrderAddressEmailBean getOrderAddressEmailInfo(String orderNo);

    @Select(SqlProvider.GET_USER_BY_ID)
    UserBean getUserById(int id);

    class SqlProvider {

        static final String GET_USER_BY_EMAIL = "select user.id,name,activationState,activationCode,createtime,email,currency,countryId,user_category as userCategory,area,country ,signkey ,user.businessName,user.activationTime from user left join zone on zone.id=user.countryId  where (email = #{email} OR  name = #{email}) and (site & #{site}) limit 1";


        static final String UP_USER_ACTIVATION_CODE_STATE2 = "update user set activationPassTime=now(),  activationPassCode=#{activationCode} where email = #{email}";

        static final String UP_USER_ACTIVATION_CODE_STATE1 = "update user set activationTime=now(),activationCode=#{activationCode} where email = #{email}";

        static final String GET_ORDER_DETAILS = "select id,orderid,yourorder,goodsprice,goodsname,oa.delivery_time,car_img,yourorder,car_type,(select img from goods_typeimg where oa.goodsid = goods_id limit 1) as goods_typeimg from order_details oa where  orderid in (#{orderNo})";

        static final String GET_ORDER_ADDRESS_EMAIL_INFO = "select  id,order_no as orderno,product_cost,address,address2,phoneNumber,zipcode,Country,statename,street,recipients,create_time,pay_price,pay_price_tow,currency,mode_transport from order_address oa ,orderinfo o where oa.orderno=o.order_no and  oa.orderNo in (#{orderNo})";

        static final String GET_USER_BY_ID = "select a.*,ifnull((select flag from user_checkout where id = #{id} and flag =1 limit 1),0) as authorizationFlag from user a where a.id = #{id}";
    }
}
