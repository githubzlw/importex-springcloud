package com.importexpress.email.service.impl.process;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.service.TemplateMailProcess;
import com.importexpress.email.vo.DismantlingOrderMail;
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
public class DismantlingOrderImpl implements TemplateMailProcess {
    @Override
    public MailBean process(MailTemplateBean mailTemplateBean, SpringTemplateEngine thymeleafEngine) {

        MailBean mailBean = new MailBean();
        BeanUtils.copyProperties(mailTemplateBean.getMailBean(), mailBean);

        DismantlingOrderMail dismantlingOrderMail = (DismantlingOrderMail) mailTemplateBean;

        Map<String, Object> modelM = new HashMap<String, Object>();

        if (dismantlingOrderMail.getState() == 1) {
            modelM.put("expect_arrive_time_", dismantlingOrderMail.getExpect_arrive_time_());
            modelM.put("expect_arrive_time", dismantlingOrderMail.getExpect_arrive_time());
        } else {
            modelM.put("totalDisCount", dismantlingOrderMail.getTotalDisCount());
            modelM.put("totalExtraFree", dismantlingOrderMail.getTotalExtraFree());
        }
        modelM.put("remark", dismantlingOrderMail.getRemark());
        modelM.put("orderno", dismantlingOrderMail.getOrderno());
        modelM.put("ordernoNew", dismantlingOrderMail.getOrdernoNew());
        modelM.put("time", dismantlingOrderMail.getTime());
        modelM.put("time_", dismantlingOrderMail.getTime_());
        modelM.put("state", dismantlingOrderMail.getState());
        modelM.put("autoUrl", dismantlingOrderMail.getAutoUrl());
        modelM.put("email", dismantlingOrderMail.getEmail());
        modelM.put("details", dismantlingOrderMail.getDetails());
        modelM.put("details_", dismantlingOrderMail.getDetails_());


        modelM.put("orderbean", dismantlingOrderMail.getOrderbean());
        modelM.put("orderbean_", dismantlingOrderMail.getOrderbean_());
        modelM.put("title", dismantlingOrderMail.getTitle());
        modelM.put("message", dismantlingOrderMail.getMessage());


        modelM.put("currency", dismantlingOrderMail.getCurrency());
        modelM.put("here", dismantlingOrderMail.getHere());
        modelM.put("websiteType", dismantlingOrderMail.getWebsiteType());

        mailBean.setBody(getHtmlContent(modelM, mailBean.getTemplateType(), thymeleafEngine));
        mailBean.setTest(mailBean.isTest());
        return mailBean;
    }
}
