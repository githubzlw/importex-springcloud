package com.importexpress.search.common;

import com.importexpress.comm.util.StrUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 关键词特殊处理
 */
@Component
public class KeywordCorrect {
    //特殊字符
    private static String specialCharactersReg =
            "([/\\\\\\$=\\|%`~<>_,\\:\\.\\?\\{\\}\\[\\]\";\\+\\*\\^#@!￥\\(\\)《》，。？、“”；：‘’（）｛｝【】*！~])";

    private static String delGegex = "(\\b(where|to|is|are|for|if|of|by|or|in|at|on|and|now|before|after|when|who" +
            "|but|how|designer|cute|cheap|wholesale|buy)\\b)";

    /**
     *   对字符串做过滤特殊字符处理
     *
     * @param keyword
     * @return String
     * @Title getKeyWord
     * @Description TODO
     */
    public static String getKeyWord(String keyword) {
        if(StringUtils.isBlank(keyword) || "*".equals(keyword)) {
            return "";
        }
        keyword = keyword.replaceAll(delGegex, "");

        if(StrUtils.isMatch(keyword,"([a-zA-Z0-9]+(\\-[0-9a-zA-Z]+)+)")){
            keyword = keyword.replaceAll("\\-{2}", "-");
        }else{
            keyword = keyword.replaceAll("\\-+", " ");
        }
        keyword = keyword.replaceAll(specialCharactersReg, "")
                .replaceAll("(\\s*[\\+\\s&]{1}\\s*)", " ");
        if(keyword.indexOf("'s") == -1 && keyword.indexOf("'") > -1) {
            keyword = keyword.replace("'", "");
        }
        keyword = keyword.toLowerCase().trim();
        return keyword;
    }


}
