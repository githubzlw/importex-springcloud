package com.importexpress.email.vo;

import com.importexpress.comm.pojo.MailTemplateBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importExpress.mail
 * @date:2020/5/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseMail extends MailTemplateBean {

    private String name;
    private String orderid;
    private String recipients;
    private String street;
    private String street1;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String phone;
    private int websiteType;
    private String toHref;

}
