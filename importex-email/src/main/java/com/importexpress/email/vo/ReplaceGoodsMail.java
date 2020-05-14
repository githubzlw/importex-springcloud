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
public class ReplaceGoodsMail extends MailTemplateBean {

    private String clickHereForDetails;
    private String title;
    private String email;
    private String emailInfo;
    private String copyEmail;
    private String userId;
    private String orderNo;
    private String reason3;
    private int websiteType;

}
