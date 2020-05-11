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
public class CouponMail extends MailTemplateBean {

    private String firstName;
    private String description;
    private String codeId;
    private String validityPeriod;
    private String codeValue;
    private String email;
    private String title;
    private int websiteType;

}
