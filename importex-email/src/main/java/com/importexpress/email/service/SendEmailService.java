package com.importexpress.email.service;

import com.importexpress.comm.pojo.SiteEnum;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: importexpress.email.service
 * @date:2019/12/20
 */
public interface SendEmailService {

    void genWelcomeBodyAndSend(String email, String name, String pass, String from, SiteEnum siteEnum);

    void genReceivedBodyAndSend(String orderNo, int userId, SiteEnum siteEnum);

    void genNewPasswordBodyAndSend(String email, String passWord, String businessName, String businessIntroduction, SiteEnum siteEnum);

    void genActivationBodyAndSend(String email, String name, String pass, String fromWhere, SiteEnum siteEnum);

    void genAccountUpdateBodyAndSend(String email, SiteEnum siteEnum);

    void justSend(String email, String content, String title, SiteEnum siteEnum);
}
