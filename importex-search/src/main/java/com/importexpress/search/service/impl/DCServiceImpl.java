package com.importexpress.search.service.impl;

import com.google.common.collect.Maps;
import com.importexpress.search.pojo.Category;
import com.importexpress.search.pojo.SearchParam;
import com.importexpress.search.service.DCService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.ServletContext;
import java.util.Map;

@Service
public class DCServiceImpl implements DCService {
    @Autowired
    private ServletContext applicationCtx;
    @Override
    public Map<String,String> getSearchNavigation(SearchParam param) {
        Object appCtx1688Cts = applicationCtx.getAttribute("categorys");
        Map<String,Category> catidList1688 = (Map<String,Category>) appCtx1688Cts;
        String catid = param.getCatid();
        Category catidPath = catidList1688.get(catid);
        if(catidPath == null || org.apache.commons.lang3.StringUtils.isBlank(catidPath.getPath()) ){
            return Maps.newHashMap();
        }
        Map<String,String> maps = Maps.newHashMap();
        StringBuilder naResult=new StringBuilder();
        String[] paths = catidPath.getPath().split(",");
        if(paths.length>0){
            naResult.append("{\"@context\": \"http://schema.org\",\"@type\": \"BreadcrumbList\",\"itemListElement\":[{\"@type\": \"ListItem\",\"position\": 1,\"item\":{\"@id\": \"/\",\"name\": \"Home\" }},");
        }
        String keyWord = param.getKeyword();
        StringBuilder result=new StringBuilder();
        boolean isOnlyCatid = StringUtils.isBlank(keyWord) || "*".equals(keyWord);
        for (int j=0,length = paths.length;j<length;j++) {
            String cid = paths[j];
            Category categoryBean = catidList1688.get(cid);
            if(categoryBean == null){
                continue;
            }
            String category = categoryBean.getName();
            result.append("<a class='cate_item' href='/goodslist?&amp;srt=default&amp;catid="+cid+"'>");
            maps.put("try_category", category);
            if(j==length-1){
                maps.put("k_word", category);
            }
            if(isOnlyCatid && j==length-1) {
                result.append("<h1>").append(category).append("</h1>").append("</a>");
            }else {
                result.append("<span>").append(category).append("</span>").append("</a>");
            }
            if(j!=length-1){
                result.append("<span class='cate_gang'>:</span>");
            }
            naResult.append("{\"@type\": \"ListItem\",\"position\": "+(j+2)+",\"item\":{\"@id\": \"/goodslist?&srt=default&catid="+cid+"\",\"name\": \""+category+"\"}}");
            if(j != length-1){
                naResult.append(",");
            }
        }
        naResult.append("]}");
        maps.put("naResult",result.append("$").append(naResult.toString()).toString());
        return maps;
    }
}
