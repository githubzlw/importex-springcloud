package com.importexpress.email.vo;

import com.importexpress.comm.pojo.MailTemplateBean;
import lombok.Builder;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.email.vo
 * @date:2020/4/20
 */
@Data
@Builder
public class ShopMarketingCarListMail extends MailTemplateBean {


    private String type;
    /**
     * type 1/2
     */
    private String productCost;
    private String actualCost;
    private String totalProductCost;
    private String totalActualCost;
    private String offRate;
    private String offCost;
    private String updateList;
    private String sourceList;
    private String couponValue;


    /**
     *type 4
     */
    private String oldMethod;
    private String oldTransport;
    private String oldPrice;
    private String newMethod;
    private String newTransport;
    private String newPrice;
    private String savePrice;

    /**
     * common
     */
    private String emailFollowUrl;
    private String carUrl;
    private String followCode;
    private String userId;

    private String userEmail;
    private String adminNameFirst;
    private String adminName;
    private String adminEmail;
    private String whatsApp;

}
