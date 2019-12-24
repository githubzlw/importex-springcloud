package com.importexpress.email;

import com.importexpress.email.pojo.OrderAddressEmailBean;
import com.importexpress.email.pojo.OrderEmailBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.email
 * @date:2019/12/20
 */
public class TestBeanUtils {

    public static OrderAddressEmailBean getOrderAddressEmailBean() {
        // id,order_no,product_cost,address,address2,phoneNumber,zipcode,Country,statename,street,recipients,create_time,pay_price,pay_price_tow,currency,mode_transport
        OrderAddressEmailBean bean = new OrderAddressEmailBean();

        bean.setId(1);
        bean.setOrderno("1");
        bean.setProduct_cost("100");
        bean.setAddress("address");
        bean.setAddress2("address2");
        bean.setPhoneNumber("phoneNumber");
        bean.setZipcode("zipcode");
        bean.setCountry("Country");
        bean.setStatename("statename");
        bean.setStreet("street");
        bean.setRecipients("recipients");
        bean.setCreate_time("create_time");
        bean.setPay_price("101");
        bean.setPay_price_tow("");
        bean.setCurrency("USD");
        bean.setMode_transport("mode_transport");
        return bean;
    }

    public static List<OrderEmailBean> getOrderEmailBeans() {
        // id,orderid,yourorder,goodsprice,goodsname,oa.delivery_time,car_img,yourorder,car_type,goods_typeimg
        List<OrderEmailBean> orderEmailBeans = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            OrderEmailBean bean = new OrderEmailBean();
            bean.setId(i);
            bean.setOrderid("1");
            bean.setYourorder(i);
            bean.setGoodsprice(String.valueOf(i));
            bean.setGoodsname(UUID.randomUUID().toString());
            bean.setDelivery_time(LocalDateTime.now().toString());
            bean.setCar_img("https://img.kidsproductwholesale.com/importcsvimg/webpic/img/wh89/pet/pet_logo.png?v=1");
            bean.setCar_type("car_type");
            bean.setGoods_typeimg(bean.getCar_img());
            orderEmailBeans.add(bean);
        }
        return orderEmailBeans;
    }
}
