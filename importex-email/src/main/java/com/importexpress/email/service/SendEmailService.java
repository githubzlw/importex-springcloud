package com.importexpress.email.service;

import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.comm.pojo.TemplateType;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: importexpress.email.service
 * @date:2019/12/20
 */
public interface SendEmailService {


    List<SiteEnum> getSiteEnumByParam(int siteEnumNum);

    List<TemplateType> getTemplateTypeByParam(String templateTypeName);
}
