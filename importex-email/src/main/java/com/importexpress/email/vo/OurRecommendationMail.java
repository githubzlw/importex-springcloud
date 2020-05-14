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
public class OurRecommendationMail extends MailTemplateBean {

    private String userId;
    private String createTime;
    private String buniessInfo;
    private String goodsNeed;
    private String goodsRequire;
    private String sendUrl;
    private String webSite;
    private String title;

}
