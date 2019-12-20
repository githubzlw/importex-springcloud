package com.importexpress.email.service;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.TemplateType;
import com.importexpress.email.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author jack.luo
 * @date 2018/10/23
 */
@Service
@Slf4j
public class SendMailFactory {

    private final Config config;

    private final SendMail mail;

    private final static DateTimeFormatter FORMATTER
            = DateTimeFormatter.ofPattern("YYMMdd_HHmmss_SSS");

    private final SpringTemplateEngine thymeleafEngine;

    public SendMailFactory(SpringTemplateEngine thymeleafEngine, Config config, @Qualifier("SendMailByAmazon") SendMail mail) {
        this.thymeleafEngine = thymeleafEngine;
        this.config = config;
        this.mail = mail;
    }


    public void sendMail(MailBean mailBean) {

        if(StringUtils.isBlank(mailBean.getBody())){
            //邮件模板填充方式
            mailBean.setBody(getHtmlContent(mailBean.getModel(), mailBean.getTemplateType()));
        }

        if(!mailBean.isTest()){
            //是否实际发送邮件
            mail.sendMail(mailBean);
        }
    }


    /**
     * getHtmlContent
     * @param model
     * @param templateType
     * @return
     */
    private String getHtmlContent(Map<String, String> model, TemplateType templateType) {

        String result = "";
        if (model != null) {
            Context context = new Context();
            for (Map.Entry<String, String> param : model.entrySet()) {
                context.setVariable(param.getKey(), param.getValue());
            }
            result = thymeleafEngine.process(templateType.toString(), context);
            log.debug(result);
            saveHtml(templateType.toString(), result);
        }
        return result;
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

            Path path = Paths.get(config.saveHtmlPath,fileName);
            if(!path.toFile().exists()){
                path.toFile().getParentFile().mkdir();
            }
            Path write = Files.write(path, content.getBytes());
            log.info("save to html,path:[{}]", write);
        } catch (IOException e) {
            log.error("save html err:[{}]",content, e);
        }
    }

}
