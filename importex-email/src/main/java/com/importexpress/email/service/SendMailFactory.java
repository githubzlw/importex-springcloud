package com.importexpress.email.service;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.MailTemplateBean;
import com.importexpress.email.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author jack.luo
 * @date 2018/10/23
 */
@Service
@Slf4j
public class SendMailFactory {

    private final static DateTimeFormatter FORMATTER
            = DateTimeFormatter.ofPattern("YYMMdd_HHmmss_SSS");
    private final Config config;
    private final SendMail mail;

    public SendMailFactory(Config config, @Qualifier("SendMailAmzImpl") SendMail mail) {
        this.config = config;
        this.mail = mail;
    }


    /**
     * sendMail
     *
     * @param mailBean
     */
    public void sendMail(MailBean mailBean) {

        checkMailBean(mailBean);

        saveHtml(mailBean.getTemplateType().toString(), mailBean.getBody());

        if (!mailBean.isTest()) {
            //是否实际发送邮件
            mail.sendMail(mailBean);
        }
    }

    /**
     * checkMailBean
     *
     * @param mailBean
     */
    private void checkMailBean(MailBean mailBean) {
        Objects.requireNonNull(mailBean);
        Assert.isTrue(StringUtils.isNotEmpty(mailBean.getTo()), "to is invalid");
        Assert.isTrue(StringUtils.isNotEmpty(mailBean.getSubject()), "subject is invalid");
        if (mailBean.getType() != 1 && mailBean.getType() != 2) {
            throw new IllegalArgumentException("type is invalid");
        }
    }


    /**
     * 保存邮件内容到文件
     *
     * @param preFileName
     * @param content
     */
    private void saveHtml(String preFileName, String content) {

        try {
            String strTime = LocalDateTime.now().format(FORMATTER);
            String fileName = preFileName.replace("/", "_").replace("emailTemplate_", "") + "_" + strTime + ".html";

            Path path = Paths.get(config.saveHtmlPath, fileName);
            if (!path.toFile().exists()) {
                path.toFile().getParentFile().mkdir();
            }
            Path write = Files.write(path, content.getBytes());
            log.info("save to html,path:[{}]", write);
        } catch (IOException e) {
            log.error("save html err:[{}]", content, e);
        }
    }

}
