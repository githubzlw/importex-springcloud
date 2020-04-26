package com.importexpress.email.service.impl.process;

import com.alibaba.fastjson.JSONArray;
import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.service.TemplateMailProcess;
import com.importexpress.email.vo.ShopCarMarketing;
import com.importexpress.email.vo.ShopMarketingCarListMail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.importexpress.email.util.Util.getHtmlContent;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.email.service.impl.process
 * @date:2020/4/20
 */
public class ShopMarketingCarListMailImpl implements TemplateMailProcess {
    @Override
    public MailBean process(MailTemplateBean mailTemplateBean, SpringTemplateEngine thymeleafEngine) {

        MailBean mailBean = new MailBean();
        BeanUtils.copyProperties(mailTemplateBean.getMailBean(), mailBean);

        ShopMarketingCarListMail carListMail = (ShopMarketingCarListMail) mailTemplateBean;

        Map<String, Object> modelM = new HashMap<String, Object>();

        modelM.put("emailFollowUrl", carListMail.getEmailFollowUrl());
        modelM.put("carUrl", carListMail.getCarUrl());
        modelM.put("followCode", carListMail.getFollowCode());
        modelM.put("userId", carListMail.getUserId());

        modelM.put("userEmail", carListMail.getUserEmail());
        modelM.put("adminNameFirst", carListMail.getAdminNameFirst());
        modelM.put("adminName", carListMail.getAdminName());
        modelM.put("adminEmail", carListMail.getAdminEmail());
        modelM.put("whatsApp", carListMail.getWhatsApp());
        modelM.put("websiteType", mailBean.getSiteEnum().getCode());

        if ("1".equals(carListMail.getType()) || "2".equals(carListMail.getType())) {
            modelM.put("productCost", carListMail.getProductCost());
            modelM.put("actualCost", carListMail.getActualCost());
            modelM.put("totalProductCost", carListMail.getTotalProductCost());
            modelM.put("totalActualCost", carListMail.getTotalActualCost());
            modelM.put("offRate", carListMail.getOffRate());
            modelM.put("offCost", carListMail.getOffCost());
            if (StringUtils.isNotBlank(carListMail.getUpdateList())) {
                List<ShopCarMarketing> updateList = JSONArray.parseArray(carListMail.getUpdateList(), ShopCarMarketing.class);
                modelM.put("updateList", updateList);
            } else {
                modelM.put("updateList", "[]");
            }
            if (StringUtils.isNotBlank(carListMail.getSourceList())) {
                List<ShopCarMarketing> sourceList = JSONArray.parseArray(carListMail.getSourceList(), ShopCarMarketing.class);
                modelM.put("sourceList", sourceList);
            } else {
                modelM.put("sourceList", "[]");
            }
            modelM.put("couponValue", carListMail.getCouponValue());
        } else if ("4".equals(carListMail.getType())) {
            modelM.put("oldMethod", carListMail.getOldMethod());
            modelM.put("oldTransport", carListMail.getOldTransport());
            modelM.put("oldPrice", carListMail.getOldPrice());
            modelM.put("newMethod", carListMail.getNewMethod());
            modelM.put("newTransport", carListMail.getNewTransport());
            modelM.put("newPrice", carListMail.getNewPrice());
            modelM.put("savePrice", carListMail.getSavePrice());
        }

        mailBean.setBody(getHtmlContent(modelM, mailBean.getTemplateType(), thymeleafEngine));
        mailBean.setTest(mailBean.isTest());
        return mailBean;
    }
}
