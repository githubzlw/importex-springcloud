package com.importexpress.search.common;

import com.google.common.collect.Maps;
import com.importexpress.search.pojo.SearchParam;
import com.importexpress.search.service.DCService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ProductSearch {
    @Autowired
    private DCService dcService;
    /**搜索页增加面包屑导航
     * @param param
     * @return
     */
    public Map<String,String> searchNavigation(SearchParam param){
        Map<String,String> resultMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String  searchStr = "", naStr = "";
        Map<String,String> result = Maps.newHashMap();
        boolean isDefaultCatid = StringUtils.isBlank(param.getCatid()) || "0".equals(param.getCatid());
        boolean isBlankKeyword = StringUtils.isBlank(param.getKeyword()) || "*".equals(param.getKeyword());
        if (isDefaultCatid && !isBlankKeyword) {
            sb.append("<span class='cate_gang' style='font-size: 14px;cursor: auto;'>Results for</span>");
        } else if (!isDefaultCatid) {
            result = dcService.getSearchNavigation(param);
            if(!result.isEmpty()){
                String naResult = result.get("naResult");
                if (StringUtils.isNotBlank(naResult) && naResult.indexOf("$") > -1) {
                    String[] str = naResult.split("\\$");
                    naStr = str[1];
                    searchStr = str[0];
                }
                sb.append(searchStr);
            }
        }
        String try_category = String.valueOf(result.get("try_category"));
        if(!isDefaultCatid) {
            resultMap.put("select_category", try_category);
            if(StringUtils.isNotBlank(param.getKeyword()) && !"*".equals(param.getKeyword()) ) {
                resultMap.put("try_category", try_category);
            }
        }
        resultMap.put("naStr", naStr);
        resultMap.put("searchNavigation", sb.toString());
        return resultMap;

    }

}
