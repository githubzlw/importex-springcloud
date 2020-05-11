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
public class GoodsChangeMail  extends MailTemplateBean {

    private String orderNo;
    private String name;
    private int websiteType;
    private String accountLink;

}
