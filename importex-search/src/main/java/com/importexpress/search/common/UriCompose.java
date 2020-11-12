package com.importexpress.search.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 产品静态链接处理
 */
@Component
@Slf4j
public class UriCompose {

    /**产品静态链接
     * @param itemid
     * @param name
     * @param catid1
     * @param catid2
     * @param dataType
     * @return
     */
    public static String pseudoStaticUrl(String itemid,String name,String catid1,String catid2,int dataType){
        name = name.toLowerCase().replaceAll("[^a-zA-Z0-9]"," ").trim();
        //去除静态页名字中一些不需要的词
        name = SensitiveWordUtil.removeGoodsNameWords(name);
        String[] nameArr = name.split("\\s+");
        StringBuffer goodnameNew = new StringBuffer();
        if(nameArr.length>10){
            for (int i = 0; i < 10; i++) {
                if(StringUtils.isNotBlank(nameArr[i])){
                    goodnameNew.append(nameArr[i]+"-");
                }
            }
        }else{
            goodnameNew.append(name.replaceAll("(\\s+)", "-"));
        }
        String url  = "/goodsinfo/";
        if(StringUtils.isNotBlank(goodnameNew.toString())){
            url = url + goodnameNew.deleteCharAt(goodnameNew.length()-1);
        } else {
            url = url+"name";
        }
        if(StringUtils.isNotBlank(catid1) && StringUtils.isNotBlank(catid2)) {
            url = url +"-"+catid1+"-"+catid2;
        }
        url = url +"-"+dataType+ itemid + ".html";
        return url;
    }

    /**产品静态链接
     * @param itemid
     * @param name
     * @param catid1
     * @param catid2
     * @param dataType
     * @return
     */
    public static String pseudoStaticUrlB2C(String itemid,String name,String catid1,String catid2,int dataType){
        name = name.toLowerCase().replaceAll("[^a-zA-Z0-9]"," ").trim();
        //去除静态页名字中一些不需要的词
        name = SensitiveWordUtil.removeGoodsNameWords(name);
        String[] nameArr = name.split("\\s+");
        StringBuffer goodnameNew = new StringBuffer();
        if(nameArr.length>10){
            for (int i = 0; i < 10; i++) {
                if(StringUtils.isNotBlank(nameArr[i])){
                    goodnameNew.append(nameArr[i]+"-");
                }
            }
        }else{
            goodnameNew.append(name.replaceAll("(\\s+)", "-"));
        }
        String url  = "/productbc/";
        if(StringUtils.isNotBlank(goodnameNew.toString())){
            url = url + goodnameNew.deleteCharAt(goodnameNew.length()-1);
        } else {
            url = url+"name";
        }
        if(StringUtils.isNotBlank(catid1) && StringUtils.isNotBlank(catid2)) {
            url = url +"-"+catid1+"-"+catid2;
        }
        url = url +"-"+dataType+ itemid + ".html";
        return url;
    }
}
