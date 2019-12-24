package com.importexpress.email.service.impl;

import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.comm.pojo.TemplateType;
import com.importexpress.email.service.SendEmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: importexpress.email.service.impl
 * @date:2019/12/20
 */
@Slf4j
@Service
public class SendEmailServiceImpl implements SendEmailService {


    /**
     * 根据网站类型ID获取枚举网站数据
     *
     * @param siteEnumNum
     * @return
     */
    @Override
    public List<SiteEnum> getSiteEnumByParam(int siteEnumNum) {
        return Arrays.stream(SiteEnum.values()).filter(e -> e.getCode() == siteEnumNum).collect(Collectors.toList());
    }

    /**
     * 根据模板名称获取枚举模板数据
     *
     * @param templateTypeName
     * @return
     */
    @Override
    public List<TemplateType> getTemplateTypeByParam(String templateTypeName) {
        if (StringUtils.isNotBlank(templateTypeName)) {
            return Arrays.stream(TemplateType.values())
                    .filter(e -> e.name().equalsIgnoreCase(templateTypeName)).collect(Collectors.toList());
        } else {
            return null;
        }
    }

}
