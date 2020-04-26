package com.importexpress.email.service.impl;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.service.TemplateMailService;
import com.importexpress.email.service.impl.process.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Objects;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: importexpress.email.service.impl
 * @date:2019/12/20
 */
@Slf4j
@Service
public class TemplateMailServiceImpl implements TemplateMailService {


    private final SpringTemplateEngine thymeleafEngine;

    public TemplateMailServiceImpl(SpringTemplateEngine thymeleafEngine) {
        this.thymeleafEngine = thymeleafEngine;
    }


    /**
     * 模板获取数据
     *
     * @param mailTemplateBean
     */
    @Override
    public MailBean processTemplate(MailTemplateBean mailTemplateBean) {

        Objects.requireNonNull(mailTemplateBean);
        Objects.requireNonNull(mailTemplateBean.getMailBean());

        if (mailTemplateBean.getMailBean().getTemplateType() == null) {
            //not template mail
            return mailTemplateBean.getMailBean();
        }
        MailBean mailBean;
        switch (mailTemplateBean.getMailBean().getTemplateType()) {
            case WELCOME:
                mailBean = new WelcomeMailImpl().process(mailTemplateBean, thymeleafEngine);
                break;
            case NEW_PASSWORD:
                mailBean = new NewPasswordMailImpl().process(mailTemplateBean, thymeleafEngine);
                break;
            case ACTIVATION:
                mailBean = new ActivationMailImpl().process(mailTemplateBean, thymeleafEngine);
                break;
            case ACCOUNT_UPDATE:
                mailBean = new AccountUpdateMailImpl().process(mailTemplateBean, thymeleafEngine);
                break;
            case RECEIVED:
                mailBean = new ReceivedMailImpl().process(mailTemplateBean, thymeleafEngine);
                break;
            case SHOPPING_CART_NO_CHANGE:
            case SHOPPING_CART_UPDATE_PRICE:
            case SHOPPING_CART_FREIGHT_COUPON:
            case SHOPPING_CART_BEST_TRANSPORT:
                mailBean = new ShopMarketingCarListMailImpl().process(mailTemplateBean, thymeleafEngine);
                break;
            case CANCEL_ORDER:
                mailBean = new OrderCancelMailImpl().process(mailTemplateBean, thymeleafEngine);
                break;
            case CHECK:
                mailBean = new CheckMailImpl().process(mailTemplateBean, thymeleafEngine);
                break;
            default:
                throw new IllegalArgumentException("mailTemplateBean.getTemplateType() is not support! " + mailTemplateBean.getMailBean().getTemplateType());

        }
        return mailBean;
    }


}
