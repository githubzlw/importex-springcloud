package com.importexpress.email.util;

import com.importexpress.comm.pojo.TemplateType;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Map;

/**
 * @Author jack.luo
 * @create 2020/4/15 16:15
 * Description
 */
public class Util {

    /**
     * getHtmlContent
     *
     * @param model
     * @param templateType
     * @return
     */
    public static String getHtmlContent(Map<String, Object> model, TemplateType templateType, SpringTemplateEngine thymeleafEngine) {

        String result = "";
        if (model != null) {
            Context context = new Context();
            for (Map.Entry<String, Object> param : model.entrySet()) {
                context.setVariable(param.getKey(), param.getValue());
            }
            result = thymeleafEngine.process(templateType.toString(), context);
        }
        return result;
    }
}
