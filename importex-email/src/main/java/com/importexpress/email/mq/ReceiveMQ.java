package com.importexpress.email.mq;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.comm.pojo.TemplateType;
import com.importexpress.email.config.Config;
import com.importexpress.email.service.SendMailFactory;
import com.importexpress.email.service.TemplateMailService;
import com.importexpress.email.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author lhao
 */
@Service
@Slf4j
public class ReceiveMQ {

    private static AtomicInteger count = new AtomicInteger(0);

    private final SendMailFactory sendMailFactory;

    private final TemplateMailService templateMailService;


    public ReceiveMQ(SendMailFactory sendMailFactory, TemplateMailService templateMailService) {
        this.sendMailFactory = sendMailFactory;
        this.templateMailService = templateMailService;
    }

    @RabbitListener(queues = Config.QUEUE_MAIL, containerFactory = "rabbitListenerContainerFactory")
    public void receiveMail(byte[] bytes) {

        try{
            log.info("received mq count:[{}]", count.incrementAndGet());
            String json = new String(bytes);
            log.info("received mq:[{}]", json);
            JSONObject jsonObject = JSONObject.parseObject(json);
            Objects.requireNonNull(jsonObject);
            JSONObject mailBean = jsonObject.getJSONObject("mailBean");
            Objects.requireNonNull(mailBean);
            String templateType = mailBean.getString("templateType");
            MailTemplateBean mailTemplateBean;
            switch (TemplateType.valueOf(templateType)){
                case WELCOME:
                    mailTemplateBean=JSONObject.parseObject(json, WelcomeMailTemplateBean.class);
                    break;
                case NEW_PASSWORD:
                    mailTemplateBean=JSONObject.parseObject(json, NewPasswordMailTemplateBean.class);
                    break;
                case ACTIVATION:
                    mailTemplateBean=JSONObject.parseObject(json, ActivationMailTemplateBean.class);
                    break;
                case ACCOUNT_UPDATE:
                    mailTemplateBean=JSONObject.parseObject(json, AccountUpdateMailTemplateBean.class);
                    break;
                case RECEIVED:
                    mailTemplateBean=JSONObject.parseObject(json, ReceivedMailTemplateBean.class);
                    break;
                case SHOPPING_CART_NO_CHANGE:
                case SHOPPING_CART_UPDATE_PRICE:
                case SHOPPING_CART_FREIGHT_COUPON:
                case SHOPPING_CART_BEST_TRANSPORT:
                    mailTemplateBean = JSONObject.parseObject(json, ShopMarketingCarListMail.class);
                    break;
                case CANCEL_ORDER:
                    mailTemplateBean = JSONObject.parseObject(json, OrderCancelMail.class);
                    break;
                case CHECK:
                    mailTemplateBean = JSONObject.parseObject(json, CheckMail.class);
                    break;
                default:
                    throw new IllegalArgumentException("mailTemplateBean.getTemplateType() is not support! "+templateType);

            }

            sendMailFactory.sendMail(templateMailService.processTemplate(mailTemplateBean));
        }catch(Exception e){
            log.error("receiveMail",e);
        }
    }

}