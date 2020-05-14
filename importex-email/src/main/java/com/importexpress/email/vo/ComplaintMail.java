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
 * @date:2020/4/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintMail extends MailTemplateBean {

    private String chatText;
    private String name;
    private String email;
    private Integer websiteType;
}
