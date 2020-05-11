package com.importexpress.email.service.impl.process;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.service.TemplateMailProcess;
import com.importexpress.email.vo.ReplaceGoodsMail;
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
public class ReplaceGoodsImpl implements TemplateMailProcess {
    @Override
    public MailBean process(MailTemplateBean mailTemplateBean, SpringTemplateEngine thymeleafEngine) {

        MailBean mailBean = new MailBean();
        BeanUtils.copyProperties(mailTemplateBean.getMailBean(), mailBean);

        ReplaceGoodsMail replaceGoodsMail = (ReplaceGoodsMail) mailTemplateBean;

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("clickHereForDetails", replaceGoodsMail.getClickHereForDetails());
        //标题
        map.put("title", replaceGoodsMail.getTitle());
        //客户邮件
        map.put("email", replaceGoodsMail.getEmail());
        //邮件信息
        map.put("emailInfo", replaceGoodsMail.getEmailInfo());
        //抄送人
        map.put("copyEmail", replaceGoodsMail.getCopyEmail());
        //用户id
        map.put("userId", replaceGoodsMail.getUserId());
        //订单
        map.put("orderNo", replaceGoodsMail.getOrderNo());
        //备注
        map.put("reason3", replaceGoodsMail.getReason3());

        map.put("websiteType", replaceGoodsMail.getWebsiteType());

        mailBean.setBody(getHtmlContent(map, mailBean.getTemplateType(), thymeleafEngine));
        mailBean.setTest(mailBean.isTest());
        return mailBean;
    }
}
