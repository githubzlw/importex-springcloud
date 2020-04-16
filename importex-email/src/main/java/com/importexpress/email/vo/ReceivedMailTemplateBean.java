package com.importexpress.email.vo;

import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.comm.pojo.OrderAddressEmailBean;
import com.importexpress.comm.pojo.OrderEmailBean;
import lombok.Data;

import java.util.List;

/**
 * @Author jack.luo
 * @create 2020/4/15 11:22
 * Description
 */
@Data
public class ReceivedMailTemplateBean extends MailTemplateBean {


    private String name;
    private OrderAddressEmailBean orderAddressEmailInfo;
    private List<OrderEmailBean> orderEmailBeans;
    private String shippingMethod;
    private String format;
    private String transport;

}
