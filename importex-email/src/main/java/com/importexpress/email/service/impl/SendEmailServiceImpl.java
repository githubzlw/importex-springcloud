package com.importexpress.email.service.impl;

import com.importexpress.comm.pojo.*;
import com.importexpress.comm.util.MD5Util;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.email.service.SendEmailService;
import com.importexpress.email.service.SendMailFactory;
import com.importexpress.email.vo.WelcomeBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: importexpress.email.service.impl
 * @date:2019/12/20
 */
@Slf4j
@Service
public class SendEmailServiceImpl implements SendEmailService {

    @Autowired
    private SendMailFactory sendMailFactory;


    /**
     * WELCOME 模板获取数据并且发送邮件
     * @param welcomeBean
     */
    @Override
    public void genWelcomeBodyAndSend(WelcomeBean welcomeBean) {

        MailBean mailBean = MailBean.builder().to(welcomeBean.getToEmail()).type(1).templateType(TemplateType.WELCOME).siteEnum(welcomeBean.getSiteEnum()).build();
        String activeLink = welcomeBean.getSiteEnum().getUrl() + "/userController/upUserState?code="
                + welcomeBean.getActivationCode() + "&toEmail=" + welcomeBean.getToEmail() + "&from=" + welcomeBean.getFrom();
        String here = welcomeBean.getSiteEnum().getUrl() + "/individual/getCenter";
        int site = MultiSiteUtil.site;
        Map<String, Object> model = new HashMap<>(9);
        model.put("logoUrl", String.valueOf(site));
        model.put("name", welcomeBean.getName());
        model.put("email", welcomeBean.getToEmail());
        model.put("pass", welcomeBean.getPass());
        model.put("activeLink", activeLink);
        model.put("here", here);
        String title = "You've successfully created an " + welcomeBean.getSiteEnum().getName() + " account. We Welcome You!";
        mailBean.setSubject(title);
        mailBean.setModel(model);
        mailBean.setTest(welcomeBean.isTest());
        sendMailFactory.sendMail(mailBean);
    }


    /**
     * RECEIVED 模板获取数据并且发送邮件
     *
     * @param orderNo
     * @param userId
     * @param siteEnum
     */
    @Override
    public void genReceivedBodyAndSend(String orderNo, int userId, SiteEnum siteEnum) {

        MailBean mailBean = MailBean.builder().type(1).templateType(TemplateType.RECEIVED).siteEnum(siteEnum).build();
        String[] orderNos;
        if (orderNo.contains(",")) {
            orderNos = orderNo.split(",");
        } else {
            orderNos = new String[]{orderNo};
        }
        //获取订单信息，地址，交期--用IN
        int length = orderNos.length;
        StringBuffer orderNostr = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == (length - 1)) {
                orderNostr.append("" + orderNos[i] + "");
            } else {
                orderNostr.append("" + orderNos[i] + ",");
            }
        }
        //获取订单详情信息，产品名称，总价--用IN
        List<OrderEmailBean> orderEmailBeans = null;
        String delivery = null;
        try {
            delivery = orderEmailBeans.get(0).getDelivery_time();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getOrderDetails error!,orderDetails is null!,orderno:" + orderNostr);
        }
        int delivery_time = 0;
        if (StringUtils.isNotBlank(delivery) && StrUtils.isNum(delivery)) {
            int delivery1 = Integer.parseInt(delivery);
            if (delivery_time < delivery1) {
                delivery_time = delivery1;
            }
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, +7);
        c.add(Calendar.DAY_OF_MONTH, delivery_time);
        String format = sf.format(c.getTime());
        OrderAddressEmailBean orderAddressEmailInfo = null;
        if (null == orderAddressEmailInfo) {
            log.error("genReceivedBodyAndSend sendEmail error!,OrderAddressEmailBean is null!,orderno:" + orderNostr);
            return;
        }
        String mode_transport = orderAddressEmailInfo.getMode_transport();
        String transport = "";
        String time1 = "";
        //订单运输方式
        String shippingMethod = "";
        if (StringUtils.isNotBlank(mode_transport)) {
            if (mode_transport.contains("@")) {
                String[] mode_transports = mode_transport.split("@");
                transport = mode_transports[1];
                shippingMethod = mode_transports[0];
                time1 = mode_transports[1];
                if (time1.contains("-")) {
                    time1 = time1.split("-")[1];
                }
            }
        }
        c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(time1));

        //用户信息
        UserBean ub = null;
        mailBean.setTo(ub.getEmail());
        Map<String, Object> model = new HashMap<>();
        // 发送邮件
        if (StringUtils.isNotBlank(ub.getEmail())) {
            String valueFromResourceFile = siteEnum.getUrl();
            String here = valueFromResourceFile + "individual/getCenter";
            model.put("name", StringUtils.isNotBlank(ub.getName()) ? ub.getName() : "Valued Customer");
            model.put("orderAddressEmailInfo", orderAddressEmailInfo);
            model.put("orderEmailBeans", orderEmailBeans);
            model.put("here", here);
            model.put("transport", transport);
            model.put("shippingMethod", shippingMethod);
            model.put("estimatedShipOutDate", format);
            int site = MultiSiteUtil.site;
            model.put("logoUrl", site);
            model.put("showUrl", valueFromResourceFile);
            model.put("imgLogo", siteEnum.getUrl());
            String title = "Your order is received!";
            mailBean.setSubject(title);
            mailBean.setModel(model);
            sendMailFactory.sendMail(mailBean);
        }

    }


    /**
     * NEW_PASSWORD 模板获取数据并且发送邮件
     *
     * @param email
     * @param passWord
     * @param businessName
     * @param businessIntroduction
     * @param siteEnum
     */
    @Override
    public void genNewPasswordBodyAndSend(String email, String passWord, String businessName, String businessIntroduction, SiteEnum siteEnum) {
        MailBean mailBean = MailBean.builder().to(email).type(1).templateType(TemplateType.NEW_PASSWORD).siteEnum(siteEnum).build();
        try {
            String here = siteEnum.getUrl() + "/individual/getCenter";
            Map<String, Object> model = new HashMap<>();
            model.put("businessIntroduction", businessIntroduction);
            model.put("businessName", businessName);
            model.put("name", email);
            model.put("email", email);
            model.put("pass", passWord);
            model.put("here", here);
            model.put("logoUrl", String.valueOf(siteEnum.getCode()));
            String title = "You've successfully complete your info!";
            mailBean.setSubject(title);
            mailBean.setModel(model);
            sendMailFactory.sendMail(mailBean);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("genNewPasswordBodyAndSend email[{}],businessName[{}]", email, businessName, e);
        }
    }


    /**
     * ACTIVATION 模板获取数据并且发送邮件
     *
     * @param email
     * @param name
     * @param pass
     * @param fromWhere
     * @param siteEnum
     */
    @Override
    public void genActivationBodyAndSend(String email, String name, String pass, String fromWhere, SiteEnum siteEnum) {
        try {
            MailBean mailBean = MailBean.builder().to(email).type(1).templateType(TemplateType.ACTIVATION).siteEnum(siteEnum).build();
            String activationCode = MD5Util.encoder(mailBean.getTo() + UUID.randomUUID().toString().replaceAll("-", ""));
            String activeLink = siteEnum.getUrl() + "/userController/upUserState?code=" + activationCode + "&email=" + email + "&from=" + fromWhere;
            String here = siteEnum.getUrl() + "/individual/getCenter";
            Map<String, Object> model = new HashMap<>();
            model.put("logoUrl", String.valueOf(siteEnum.getCode()));
            model.put("name", name);
            model.put("email", email);
            model.put("pass", pass);
            model.put("activeLink", activeLink);
            model.put("here", here);
            String title = "Reset Your Password " + mailBean.getSiteEnum().getName() + " account.";
            mailBean.setSubject(title);
            mailBean.setModel(model);
            sendMailFactory.sendMail(mailBean);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("genActivationBodyAndSend email[{}],fromWhere[{}]", email, fromWhere, e);
        }

    }

    /**
     * ACCOUNT_UPDATE 模板获取数据并且发送邮件
     *
     * @param email
     * @param siteEnum
     */
    @Override
    public void genAccountUpdateBodyAndSend(String email, SiteEnum siteEnum) {


        String title = "Reset Your Password At " + siteEnum.getName();
        try {
            MailBean mailBean = MailBean.builder().to(email).type(1).templateType(TemplateType.ACCOUNT_UPDATE).siteEnum(siteEnum).build();
            UserBean userBean = null;
            String activationPassCode = MD5Util.encoder(mailBean.getTo() + System.currentTimeMillis());
            if (userBean != null) {
                String activeLink = siteEnum.getUrl() + "/forgotPassword/passActivate?email=" + mailBean.getTo()
                        + "&validateCode=" + activationPassCode;
                String here = siteEnum.getUrl() + "/individual/getCenter";
                Map<String, Object> model = new HashMap<>();
                model.put("name", mailBean.getTo());
                model.put("email", mailBean.getTo());
                model.put("activeLink", activeLink);
                model.put("here", here);
                model.put("logoUrl", String.valueOf(siteEnum.getCode()));
                mailBean.setSubject(title);
                mailBean.setModel(model);
                sendMailFactory.sendMail(mailBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("genAccountUpdateBodyAndSend email:[{}],title:[{}]", email, title, e);
        }

    }

    @Override
    public void justSend(String email, String content, String title, SiteEnum siteEnum) {
        MailBean mailBean = MailBean.builder().to(email).type(1).body(content).subject(title).siteEnum(siteEnum).build();
        sendMailFactory.sendMail(mailBean);
    }
}
