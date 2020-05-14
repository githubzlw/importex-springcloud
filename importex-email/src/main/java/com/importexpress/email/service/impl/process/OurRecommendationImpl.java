package com.importexpress.email.service.impl.process;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.service.TemplateMailProcess;
import com.importexpress.email.vo.OurRecommendationMail;
import org.springframework.beans.BeanUtils;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static com.importexpress.email.util.Util.getHtmlContent;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.email.service.impl.process
 * @date:2020/4/22
 */
public class OurRecommendationImpl implements TemplateMailProcess {
    @Override
    public MailBean process(MailTemplateBean mailTemplateBean, SpringTemplateEngine thymeleafEngine) {

        MailBean mailBean = new MailBean();
        BeanUtils.copyProperties(mailTemplateBean.getMailBean(), mailBean);

        OurRecommendationMail recommendationMail = (OurRecommendationMail) mailTemplateBean;

        Map<String, Object> sendMap = new HashMap<String, Object>();

        sendMap.put("userId", recommendationMail.getUserId());
        sendMap.put("createTime", recommendationMail.getCreateTime());
        sendMap.put("buniessInfo", recommendationMail.getBuniessInfo());
        sendMap.put("goodsNeed", recommendationMail.getGoodsNeed());
        sendMap.put("goodsRequire", recommendationMail.getGoodsRequire());
        sendMap.put("sendUrl", recommendationMail.getSendUrl());
        ;
        sendMap.put("title", recommendationMail.getTitle());

        mailBean.setBody(getHtmlContent(sendMap, mailBean.getTemplateType(), thymeleafEngine));
        mailBean.setTest(mailBean.isTest());
        return mailBean;
    }
}
