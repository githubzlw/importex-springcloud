package com.importexpress.email.vo;

import com.importexpress.comm.pojo.MailTemplateBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.email.vo
 * @date:2020/4/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCancelMail extends MailTemplateBean {


    private String email;
    private String name;
    private Integer websiteType;
    private String accountLink;
    private String cancelOrderLink;
    private String orderNo;



}
