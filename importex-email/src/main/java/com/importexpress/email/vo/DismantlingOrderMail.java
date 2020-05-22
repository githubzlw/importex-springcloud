package com.importexpress.email.vo;

import com.importexpress.comm.pojo.MailTemplateBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
public class DismantlingOrderMail extends MailTemplateBean {

    private String remark;
    private String orderno;
    private String ordernoNew;
    private String time;
    private String time_;
    private int state;
    private String autoUrl;
    private String email;
    private List<Object[]> details;
    private List<Object[]> details_;
    private String expect_arrive_time_;

    private String expect_arrive_time;
    private OrderBean orderbean;
    private OrderBean orderbean_;
    private String title;
    private String message;
    private String totalDisCount;
    private String totalExtraFree;
    private String currency;

    private String here;
    private int websiteType;
}
