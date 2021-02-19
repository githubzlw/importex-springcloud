package com.importexpress.search.common;

import com.google.common.collect.Lists;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.pojo.Category;
import com.importexpress.search.pojo.KeyToCategoryWrap;
import com.importexpress.search.pojo.SearchParam;
import com.importexpress.search.pojo.SynonymsCategoryWrap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;

/**
 *搜索特殊拼接语法
 */
@Slf4j
@Component
public class SplicingSyntax {
    @Autowired
    private SynonymProcess synonymProcess;
    @Autowired
    private ServletContext application;

    /**判断是否有反关键词查询
     * @param autiKey
     * @param quertString
     * @return
     */
    public String reverseKeywords(String autiKey,String quertString){
        if(StringUtils.isBlank(autiKey)){
            Object autiKeyList = application.getAttribute("autiKeyList");
            if(autiKeyList != null){
                Map<String, String> auti_map=(HashMap<String,String>)autiKeyList;
                autiKey = auti_map.get(quertString);
            }
            autiKey = StringUtils.isNotBlank(autiKey) ? autiKey.toLowerCase() : autiKey;
        }else{
            autiKey = autiKey.toLowerCase();
        }
        return autiKey;
    }

    /**类别设置最小价格
     * @param keyWord
     * @return
     */
    public String categoryPrice(String keyWord){
        if(StringUtils.isBlank(keyWord) || "*".equals(keyWord)){
            return "";
        }
        Object categoryPrice = application.getAttribute("categoryPriceList");
        Map<String, String> keywordPrice_map = (HashMap<String,String>)categoryPrice;
        String prices = keywordPrice_map != null ?keywordPrice_map.get(keyWord) : null;
        String minPrices = StringUtils.isBlank(prices) ? "" : prices.split("@")[0];
        return StrUtils.isMatch(minPrices, "(\\d+(\\.\\d+){0,1})") && !"0".equals(minPrices) ? minPrices : "";
    }
    /**优先类别排序
     * @param keyword
     * @param sorts
     */
    public String priorityCategorySort(String keyword) {
        //优先类别数据
        Object priorityCategoryList = application.getAttribute("priorityCategoryList");
        String priorityCategory = getPriorityCategory(priorityCategoryList,keyword);
        //
        if(StringUtils.isBlank(priorityCategory)){
            return "";
        }
        String category [] = priorityCategory.trim().split(",");
        StringBuilder termCatid = new StringBuilder();
        boolean multCatID= false;
        for(int i=0;i<category.length;i++){
            if(StringUtils.isBlank(category[i])) {
                continue;
            }
            if(termCatid.length() > 0) {
                termCatid.append(",");
                multCatID = true;
            }
            termCatid.append("map(termfreq(custom_path_catid,\"" + category[i] + "\"),1,10,0.01,1)");
        }
        if(multCatID) {
//            sorts.append("sum(").append(termCatid).append(") ");
            return "sum("+termCatid.toString()+")";
        }else {
//            sorts.append(termCatid);
            return termCatid.toString();
        }
    }
    /**获取优先类别
     * @param priorityCategoryList
     * @param keyWord
     * @return
     */
    public static String getPriorityCategory(Object priorityCategoryList,String keyWord){
        if(StringUtils.isBlank(keyWord) || priorityCategoryList == null){
            return "";
        }
        Map<String, String> priorityCategory_map = (HashMap<String,String>)priorityCategoryList;

        String priorityCategory = priorityCategory_map.get(keyWord);
        //通过整词没有获取到相应的优先别类
        if(org.apache.commons.lang.StringUtils.isNotBlank(priorityCategory) || !keyWord.contains(" ")){
            return priorityCategory;
        }
        /*(?=.*?第1个词)(?=.*?第2个词)*/
        String nkeyWord = "(?=.*?\\b"+keyWord.replaceAll("(\\s+)", "\\\\b)(?=.*?\\\\b")+"\\b)";
        Iterator<String> iterator = priorityCategory_map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if(StrUtils.isFind(key, nkeyWord)) {
                priorityCategory = priorityCategory_map.get(key);
                break;
            }
        }
        return priorityCategory;
    }
    /**特殊类别限定
     * @return
     */
    public String specialCatidSearch(SearchParam param) {
        if(param.getUserType() == 0 || param.getSite()== 1){
            return "";
        }
        Object special = application.getAttribute("specialCatidList");
        if(special == null){
            return "";
        }
        Map<Integer,List<String>> specialCatid = (Map<Integer,List<String>>)special;
        StringBuilder fq_condition = new StringBuilder();
        List<String> specialCatidList = specialCatid.get(param.getSite());
        if(specialCatidList != null){
            fq_condition.append("(");
            for (int i = 0, size = specialCatidList.size(); i < size; i++) {
                fq_condition.append("custom_path_catid:\"").append(specialCatidList.get(i)).append("\" ");
                if (i < size - 1) {
                    fq_condition.append(" OR ");
                }
            }
            fq_condition.append(") AND ");
            return fq_condition.toString();
        }
        return "";
    }
    /**
     * 多个单词组成的搜索词设置搜索主查询
     * @return
     */
    public String queryKey(String queryString) {
        if(StringUtils.isBlank(queryString) || "*".equals(queryString.trim())) {
            return queryString;
        }
        //多个搜索词------热卖搜索
        String synonymKey = synonymProcess.moreKeys(queryString);

        //单个搜索词
        synonymKey = StringUtils.isBlank(synonymKey) ? synonymProcess.synonymKey(queryString) : synonymKey;
        return synonymKey;
    }

    /**产品名称过短就增加类别名称
     * @param title
     * @param catid
     * @return
     */
    public String categoryNameToTitle(String title,String catid){
        Object catidList1688 = application.getAttribute("categorys");
        if(catidList1688 == null){
            return "";
        }
        Map<String, Category> catidMap1688 = (Map<String, Category>)catidList1688;
        if(title.split("(\\s+)").length < 6 && StringUtils.isNotBlank(catid)){
            catid = catid.startsWith(",") ? catid.substring(1) : catid;
            catid = catid.endsWith(",") && catid.length() > 1 ? catid.substring(0, catid.length()-1) : catid;
            String[] catids = catid.split("(\\s+)");
            String category = "";
            for(String catidTemp : catids){
                Category categoryTemp = catidMap1688.get(catidTemp);
                category = categoryTemp!=null && StringUtils.isNotBlank(categoryTemp.getName()) ?
                        category+" "+ categoryTemp.getName() : category;
            }
            title = category+" "+title;
            return title.trim();
        }
        return title;
    }

    /**搜索词过长截断
     * @param queryString
     * @return
     */
    public String queryString(String queryString){
        if(StringUtils.isBlank(queryString)){
            queryString = "*";
        }
        boolean isValidQueryString = StringUtils.equals(queryString, "*");
        if(!isValidQueryString){
            queryString = queryString.replaceAll("(%[0-9A-Fa-f]{2})|(\\s*\\+\\s*)", " ");
            if(queryString.length() > 50){
                queryString = queryString.lastIndexOf(" ")>1 ?
                        queryString.substring(0, queryString.lastIndexOf(" ")).trim() : queryString;
            }
        }
        return queryString;
    }

    /**
     * 首先：我们建一个 大类 小类的 表格
     * 第一步：
     * 搜索词 如果 完全match 这些 类别名字，就直接跳转相关类别 的 搜索结果   search?keyword=shoes  直接变成  search?cadid=12345
     * 如果 只是 match 大类别，很好
     * 如果 只是 match 小类别，很好
     * 如果 同时 match 大类别 和 小类别，就是很精确的定位，且只显示这个 小类别里面的产品 （这时需要一个逻辑 判断这个类别是不是小类别）
     * 如果 同时 match 多个类别名，暂时不考虑这种特殊情况
     * 如果不match 任何类别 就 直接不显示
     * 第二步：
     * 搜索词 如果 有部分  match 这些名字，就 把部分match 的部分转换为 类别ID,  剩下的 搜索  标题 和 规格
     * search?keyword=red shoes  直接变成  search?cadid=12345&keyword=red
     * 注意：
     * 每个类别ID，可以维护一个同义词表
     * 目标：一旦Match 类别名，就提供丰富搜索结果 （在产品数量少时很重要）
     * 我们并不需要 满足奇怪的搜索，但是 要满足
     * A.  细分品类的搜索    （其实目录树里面有，但是 人不愿意去找了）  我们只要确保 每个细分品类里面有  一般 60个 产品 （需要有人定义所有的细分品类）
     * B. 规格 + 细分品类的搜索：  （这种情况下，我们的搜索逻辑应该是   “如果搜索词里面有 的品类名”
     * @param queryString
     * @return
     */
   public KeyToCategoryWrap queryStrToCategory(String  queryString){
       long start = System.currentTimeMillis();
       int queryLength = queryString.length();
       Object lstCategory = application.getAttribute("synonymsCategory");
       List<SynonymsCategoryWrap> categorys = (List<SynonymsCategoryWrap>)lstCategory;

       //过滤
       categorys = filterCategory(categorys,queryLength);

       List<SynonymsCategoryWrap> setCatid = Lists.newArrayList();
       long start1 = System.currentTimeMillis();
       String spaceStr = " ";
       String queryMatch = spaceStr + queryString + spaceStr;
       String surplusStr = queryMatch;

       for(SynonymsCategoryWrap w : categorys){
           String key = w.getCategory();
           if(queryString.equals(key)){
               surplusStr = "";
               setCatid.add(w);
               break;
           }
           String reg = spaceStr + key + spaceStr;
           String queryTemp= queryMatch.replaceAll(reg," ").replaceAll("(\\s+)"," ");
           surplusStr = surplusStr.replace(reg," ");
           if(!queryMatch.trim().equals(queryTemp.trim())){
               setCatid.add(w);
           }
       }
       log.info("find catid:"+(System.currentTimeMillis() - start1));
       List<String> lstCatid = checkCatid(setCatid);
       KeyToCategoryWrap wrap = KeyToCategoryWrap.builder()
                                               .keyword(surplusStr.replaceAll("(\\s+)"," ").trim())
                                               .lstCatid(lstCatid).build();
       log.info("all cost:"+(System.currentTimeMillis() - start));
       return wrap;
   }

   private List<SynonymsCategoryWrap> filterCategory(List<SynonymsCategoryWrap> lstCategory, int length){
       List<SynonymsCategoryWrap>  result = lstCategory.stream()
               .filter(x -> x.getLength() < length + 1)
               .sorted(Comparator.comparing(SynonymsCategoryWrap::getLength).reversed())
               .collect(Collectors.toList());
        return result;

   }

    /**检查多个类别之间关系
     * @param lstCatid
     * @return
     */
   private List<String> checkCatid(List<SynonymsCategoryWrap> lstCatid){
        if(lstCatid == null || lstCatid.isEmpty()){
            return null;
        }
        if(lstCatid.size() < 2){
            return lstCatid.stream().map(SynonymsCategoryWrap::getCatid)
                    .collect(Collectors.toList());
        }

       //先选择出匹配单词最多的
       lstCatid = lstCatid.stream()
               .sorted(Comparator.comparing(SynonymsCategoryWrap::getNum).reversed())
               .collect(Collectors.toList());
       int bestMatch = lstCatid.get(0).getNum();
       lstCatid = lstCatid.stream().filter(l->l.getNum()==bestMatch).collect(Collectors.toList());

       //找出最小类别
       List<String> result = surplusMinCatid(lstCatid);
       return result;
   }

    /**获取最小类别
     * @param lstCatid
     * @return
     */
   private List<String> surplusMinCatid(List<SynonymsCategoryWrap> lstCatid){
       List<String> setCatid = Lists.newArrayList();
       if(lstCatid.size() < 2){
           return lstCatid.size() == 1 ? Lists.newArrayList(lstCatid.get(0).getCatid()) : setCatid;
       }
       Object catidList = application.getAttribute("1688CatidList");
       Map<String, Category> secatidList = (Map<String, Category>)catidList;
       List<SynonymsCategoryWrap> reCatid = Lists.newArrayList();
       for(SynonymsCategoryWrap c : lstCatid){
           Category categoryBean = secatidList.get(c.getCatid());
           if(categoryBean == null){
               continue;
           }
           String categoryPath = ","+categoryBean.getPath()+",";
           for(SynonymsCategoryWrap l : lstCatid){
               if(l.getCatid().equals(c.getCatid()) || setCatid.contains(c.getCatid())){
                   continue;
               }
               if(categoryPath.indexOf(","+l.getCatid()+",") > -1 ){
                   setCatid.add(c.getCatid());
                   reCatid.add(c);
               }
           }
       }
       if(reCatid.size() > 1){
           setCatid = surplusMinCatid(reCatid);
       }
       return setCatid;
   }

    /**提示词是否匹配到类别
     * @param auto
     * @return
     */
    public String suggestCatid(String auto){
        Object priorityCategoryList = application.getAttribute("priorityCategoryList");
        String cid = getPriorityCategory(priorityCategoryList, auto);
        Map<String, Category> cMap = (Map<String, Category>) application.getAttribute("categorys");
        if (StringUtils.isBlank(cid) || cMap.get(cid) == null) {
            return "";
        }
        String category = cMap.get(cid).getName();
        if (StringUtils.isBlank(category)) {
            return "";
        }
        StringBuilder str = new StringBuilder(auto).append("@")
                .append(cid).append("@").append(category);
        return str.toString();
    }

}
