package com.importexpress.email.service;

import com.importexpress.common.pojo.mail.MailBean;
import com.importexpress.common.pojo.mail.TemplateType;
import com.importexpress.email.service.impl.SendMailByAmazon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
 * @author luohao
 * @date 2018/10/23
 */
@Service
@Slf4j
public class SendMailFactory {

    private static final String SAVE_HTMLS = "saveHtmls";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYMMdd_HHmmss_SSS");

    private final SpringTemplateEngine thymeleafEngine;

    public SendMailFactory(SpringTemplateEngine thymeleafEngine) {
        this.thymeleafEngine = thymeleafEngine;
    }


    public void sendMail(MailBean mailBean) {

        if(StringUtils.isBlank(mailBean.getBody())){
            mailBean.setBody(getHtmlContent(mailBean.getModel(), mailBean.getTemplateType()));
        }

        if(!mailBean.isTest()){
            SendMail mail = new SendMailByAmazon();
            mail.sendMail(mailBean);
        }
    }


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
            String strTime = LocalDateTime.now().format(formatter);
            String fileName = preFileName.replace("/", "_").replace("emailTemplate_", "") + "_" + strTime + ".html";

            String dir = System.getProperty("user.dir");
            Path path = Paths.get(dir, SAVE_HTMLS,fileName);
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
