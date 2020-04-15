package com.importexpress.email.service.impl;

import com.importexpress.comm.pojo.*;
import com.importexpress.email.service.TemplateMailProcess;
import com.importexpress.email.service.TemplateMailService;
import com.importexpress.email.service.impl.process.WelcomeMailImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.spring5.SpringTemplateEngine;

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
     * @param mailTemplateBean
     */
    @Override
    public MailBean processTemplate(MailTemplateBean mailTemplateBean) {

        Assert.notNull(mailTemplateBean);
        Assert.notNull(mailTemplateBean.getMailBean());

        if(mailTemplateBean.getMailBean().getTemplateType() == null){
            //not template mail
            return mailTemplateBean.getMailBean();
        }
        MailBean mailBean;
        TemplateMailProcess templateMailProcess ;
        switch (mailTemplateBean.getMailBean().getTemplateType()){
            case WELCOME:
                templateMailProcess = new WelcomeMailImpl();
                mailBean = templateMailProcess.process(mailTemplateBean,thymeleafEngine);
                break;
            default:
                throw new IllegalArgumentException("mailTemplateBean.getTemplateType() is not support! "+mailTemplateBean.getMailBean().getTemplateType());

        }
        return mailBean;
    }






//    /**
//     * RECEIVED 模板获取数据并且发送邮件
//     *
//     * @param orderNo
//     * @param userId
//     * @param siteEnum
//     */
//    public void genReceivedBodyAndSend(String orderNo, int userId, SiteEnum siteEnum) {
//
//        MailBean mailBean = MailBean.builder().type(1).templateType(TemplateType.RECEIVED).siteEnum(siteEnum).build();
//        String[] orderNos;
//        if (orderNo.contains(",")) {
//            orderNos = orderNo.split(",");
//        } else {
//            orderNos = new String[]{orderNo};
//        }
//        //获取订单信息，地址，交期--用IN
//        int length = orderNos.length;
//        StringBuffer orderNostr = new StringBuffer();
//        for (int i = 0; i < length; i++) {
//            if (i == (length - 1)) {
//                orderNostr.append("" + orderNos[i] + "");
//            } else {
//                orderNostr.append("" + orderNos[i] + ",");
//            }
//        }
//        //获取订单详情信息，产品名称，总价--用IN
//        List<OrderEmailBean> orderEmailBeans = null;
//        String delivery = null;
//        try {
//            delivery = orderEmailBeans.get(0).getDelivery_time();
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("getOrderDetails error!,orderDetails is null!,orderno:" + orderNostr);
//        }
//        int delivery_time = 0;
//        if (StringUtils.isNotBlank(delivery) && StrUtils.isNum(delivery)) {
//            int delivery1 = Integer.parseInt(delivery);
//            if (delivery_time < delivery1) {
//                delivery_time = delivery1;
//            }
//        }
//        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.DAY_OF_MONTH, +7);
//        c.add(Calendar.DAY_OF_MONTH, delivery_time);
//        String format = sf.format(c.getTime());
//        OrderAddressEmailBean orderAddressEmailInfo = null;
//        if (null == orderAddressEmailInfo) {
//            log.error("genReceivedBodyAndSend sendEmail error!,OrderAddressEmailBean is null!,orderno:" + orderNostr);
//            return;
//        }
//        String mode_transport = orderAddressEmailInfo.getMode_transport();
//        String transport = "";
//        String time1 = "";
//        //订单运输方式
//        String shippingMethod = "";
//        if (StringUtils.isNotBlank(mode_transport)) {
//            if (mode_transport.contains("@")) {
//                String[] mode_transports = mode_transport.split("@");
//                transport = mode_transports[1];
//                shippingMethod = mode_transports[0];
//                time1 = mode_transports[1];
//                if (time1.contains("-")) {
//                    time1 = time1.split("-")[1];
//                }
//            }
//        }
//        c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(time1));
//
//        //用户信息
//        UserBean ub = null;
//        mailBean.setTo(ub.getEmail());
//        Map<String, Object> model = new HashMap<>();
//        // 发送邮件
//        if (StringUtils.isNotBlank(ub.getEmail())) {
//            String valueFromResourceFile = siteEnum.getUrl();
//            String here = valueFromResourceFile + "individual/getCenter";
//            model.put("name", StringUtils.isNotBlank(ub.getName()) ? ub.getName() : "Valued Customer");
//            model.put("orderAddressEmailInfo", orderAddressEmailInfo);
//            model.put("orderEmailBeans", orderEmailBeans);
//            model.put("here", here);
//            model.put("transport", transport);
//            model.put("shippingMethod", shippingMethod);
//            model.put("estimatedShipOutDate", format);
//            int site = MultiSiteUtil.site;
//            model.put("logoUrl", site);
//            model.put("showUrl", valueFromResourceFile);
//            model.put("imgLogo", siteEnum.getUrl());
//            String title = "Your order is received!";
//            mailBean.setSubject(title);
//            mailBean.setModel(model);
//            sendMailFactory.sendMail(mailBean);
//        }
//
//    }
//
//
//    /**
//     * NEW_PASSWORD 模板获取数据并且发送邮件
//     *
//     * @param email
//     * @param passWord
//     * @param businessName
//     * @param businessIntroduction
//     * @param siteEnum
//     */
//    public void genNewPasswordBodyAndSend(String email, String passWord, String businessName, String businessIntroduction, SiteEnum siteEnum) {
//        MailBean mailBean = MailBean.builder().to(email).type(1).templateType(TemplateType.NEW_PASSWORD).siteEnum(siteEnum).build();
//        try {
//            String here = siteEnum.getUrl() + "/individual/getCenter";
//            Map<String, Object> model = new HashMap<>();
//            model.put("businessIntroduction", businessIntroduction);
//            model.put("businessName", businessName);
//            model.put("name", email);
//            model.put("email", email);
//            model.put("pass", passWord);
//            model.put("here", here);
//            model.put("logoUrl", String.valueOf(siteEnum.getCode()));
//            String title = "You've successfully complete your info!";
//            mailBean.setSubject(title);
//            mailBean.setModel(model);
//            sendMailFactory.sendMail(mailBean);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("genNewPasswordBodyAndSend email[{}],businessName[{}]", email, businessName, e);
//        }
//    }
//
//
//    /**
//     * ACTIVATION 模板获取数据并且发送邮件
//     *
//     * @param email
//     * @param name
//     * @param pass
//     * @param fromWhere
//     * @param siteEnum
//     */
//    public void genActivationBodyAndSend(String email, String name, String pass, String fromWhere, SiteEnum siteEnum) {
//        try {
//            MailBean mailBean = MailBean.builder().to(email).type(1).templateType(TemplateType.ACTIVATION).siteEnum(siteEnum).build();
//            String activationCode = MD5Util.encoder(mailBean.getTo() + UUID.randomUUID().toString().replaceAll("-", ""));
//            String activeLink = siteEnum.getUrl() + "/userController/upUserState?code=" + activationCode + "&email=" + email + "&from=" + fromWhere;
//            String here = siteEnum.getUrl() + "/individual/getCenter";
//            Map<String, Object> model = new HashMap<>();
//            model.put("logoUrl", String.valueOf(siteEnum.getCode()));
//            model.put("name", name);
//            model.put("email", email);
//            model.put("pass", pass);
//            model.put("activeLink", activeLink);
//            model.put("here", here);
//            String title = "Reset Your Password " + mailBean.getSiteEnum().getName() + " account.";
//            mailBean.setSubject(title);
//            mailBean.setModel(model);
//            sendMailFactory.sendMail(mailBean);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("genActivationBodyAndSend email[{}],fromWhere[{}]", email, fromWhere, e);
//        }
//
//    }
//
//    /**
//     * ACCOUNT_UPDATE 模板获取数据并且发送邮件
//     *
//     * @param email
//     * @param siteEnum
//     */
//    public void genAccountUpdateBodyAndSend(String email, SiteEnum siteEnum) {
//
//
//        String title = "Reset Your Password At " + siteEnum.getName();
//        try {
//            MailBean mailBean = MailBean.builder().to(email).type(1).templateType(TemplateType.ACCOUNT_UPDATE).siteEnum(siteEnum).build();
//            UserBean userBean = null;
//            String activationPassCode = MD5Util.encoder(mailBean.getTo() + System.currentTimeMillis());
//            if (userBean != null) {
//                String activeLink = siteEnum.getUrl() + "/forgotPassword/passActivate?email=" + mailBean.getTo()
//                        + "&validateCode=" + activationPassCode;
//                String here = siteEnum.getUrl() + "/individual/getCenter";
//                Map<String, Object> model = new HashMap<>();
//                model.put("name", mailBean.getTo());
//                model.put("email", mailBean.getTo());
//                model.put("activeLink", activeLink);
//                model.put("here", here);
//                model.put("logoUrl", String.valueOf(siteEnum.getCode()));
//                mailBean.setSubject(title);
//                mailBean.setModel(model);
//                sendMailFactory.sendMail(mailBean);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("genAccountUpdateBodyAndSend email:[{}],title:[{}]", email, title, e);
//        }
//
//    }
//
//    public void justSend(String email, String content, String title, SiteEnum siteEnum) {
//        MailBean mailBean = MailBean.builder().to(email).type(1).body(content).subject(title).siteEnum(siteEnum).build();
//        sendMailFactory.sendMail(mailBean);
//    }
}
