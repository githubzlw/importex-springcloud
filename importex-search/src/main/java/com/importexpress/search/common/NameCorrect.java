package com.importexpress.search.common;

import com.importexpress.comm.util.StrUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 产品名称处理
 */
@Component
public class NameCorrect {
    /**产品名称首字母大写
     * @param name
     * @return
     */
    public static String upperCaseProductName(String name){
        if(StringUtils.isBlank(name)){
            return name;
        }
        String[] names = name.split("(\\s+)");
        StringBuilder sb = new StringBuilder();
        for(String str : names){
            //小写字母开头的单词转为首字母大写
            if(StrUtils.isMatch(str, "([a-z]+.*)")){
                char[] charArray = str.toCharArray();
                charArray[0] -= 32;
                sb.append(String.valueOf(charArray)).append(" ");
            }else{
                sb.append(str).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
