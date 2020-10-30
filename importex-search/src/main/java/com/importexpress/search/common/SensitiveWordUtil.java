package com.importexpress.search.common;

import org.springframework.stereotype.Component;

/**
 * 产品标题去敏感词
 * @author Administrator
 *
 */
@Component
public class SensitiveWordUtil {
    /**
     * 产品单页静态化文件中的名字，
     */
    private static final String[] goodsNameWords={"hotsale","aliexpress","free-shipping","new","shipping","HOT","fashion","Hot sale","Hot Worldwide"};

    /**
     * 移除产品单页名字中一些乱七八糟的词
     * @param goodsName
     * @return
     */
    public static String removeGoodsNameWords(String goodsName){

        if(goodsName == null || "".equals(goodsName)){
            return "goods-name";
        }
        for (String name : goodsNameWords) {
            //替换忽略大小写
            String newName = "(?i)"+name;
            goodsName = goodsName.replaceAll(newName, "");
        }
        return goodsName;
    }
}