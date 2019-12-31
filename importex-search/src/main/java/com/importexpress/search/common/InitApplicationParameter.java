package com.importexpress.search.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.pojo.*;
import com.importexpress.search.pojo.Currency;
import com.importexpress.search.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.*;

@Slf4j
@Component
public class InitApplicationParameter {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttributeService attributesService;
    @Autowired
    private SynonymService synonymService;
    @Autowired
    private AutiKeyService autiKeyService;
    @Autowired
    private KeywordRecordService keywordRecordService;
    @Autowired
    private SearchService search;

    public void init(ServletContext application){
        synonyms(application);
        synonymsCategory(application);
        autiKey(application);
        specialCatid(application);
        categoryPrice(application);
        priorityCategory(application);
        newPvid(application);
        recommendedWords(application);
        category(application);
    }

    /**全部1688Category的数据导入
     * @param application
     */
    public void category(ServletContext application){
        long startTime = System.currentTimeMillis();
        Map<Integer,List<String>> special = (Map<Integer,List<String>>)application.getAttribute("specialCatidList");
        List<String> lstKids= special.get(2);
        List<String> lstPets = special.get(4);
        Map<String,List<CategoryWrap>> importMap = Maps.newHashMap();
        Map<String,List<CategoryWrap>> kidsMap = Maps.newHashMap();
        Map<String,List<CategoryWrap>> petsMap = Maps.newHashMap();
        List<Category> allCategory = categoryService.getCategories();
        Map<String,Category> catidListResult = Maps.newHashMap();
        SearchParam param = new SearchParam();
        param.setCurrency(new Currency());
        for(Category categoryBean : allCategory){
            String[] categoryParent = categoryBean.getPath().split(",");
            categoryBean.setParentCategory(categoryParent.length > 1 ?
                    categoryParent[categoryParent.length-2] : "0");
            catidListResult.put(categoryBean.getCatid(), categoryBean);
            if(categoryBean.getLevel() != 1){
                continue;
            }
            param.setKeyword("*");
            param.setCatid(categoryBean.getCatid());
            String newArrivalDate = categoryBean.getNewArrivalDate();
            if(StringUtils.isBlank(newArrivalDate)){
                continue;
            }
            String[] newArrivalDates = newArrivalDate.split(",");
            for(String d : newArrivalDates){
                param.setNewArrivalDate(d);
                initDate(param,1,importMap,categoryBean.getCatid(),d);
                if(lstPets.contains(categoryBean.getCatid())){
                    initDate(param,4,petsMap,categoryBean.getCatid(),d);
                }
                if(lstKids.contains(categoryBean.getCatid())){
                    initDate(param,2,kidsMap,categoryBean.getCatid(),d);
                }
            }
        }
        application.setAttribute("categorys", catidListResult);
        application.setAttribute("importDate", importMap);
        application.setAttribute("kidsDate", kidsMap);
        application.setAttribute("petsDate", petsMap);
        log.info("全部1688Category的数据导入 Time:"+(System.currentTimeMillis()-startTime));
    }

    private void initDate(SearchParam param, int site, Map<String,List<CategoryWrap>> map, String catid, String d){
        param.setSite(site);
        param.setCollection(8);
        if(search.serachCount(param) > 0){
            List<CategoryWrap> lst = map.get(catid);
            lst = lst == null ? Lists.newArrayList() : lst;
            CategoryWrap category = new CategoryWrap();
            category.setId(catid);
            category.setLevel(3);
            category.setName(d);
            category.setUrl("collection=8&catid="+catid+"&newArrivalDate="+d);
            lst.add(category);
            map.put(catid,lst);
        }
    }



    /**获取规格属性表信息
     * @param application
     */
    public void newPvid(ServletContext application){
        long startTime = System.currentTimeMillis();
        Map<String, Attribute> newPvids = attributesService.getAttributes();
        application.setAttribute("newpvidList", newPvids);
        log.info("Attribute Time:"+(System.currentTimeMillis()-startTime));

    }

    /**初始化同义词列表
     * @param application
     */
    public void synonyms(ServletContext application){
        long startTime = System.currentTimeMillis();
        Map<String,Set<String>> synonymsListResult = synonymService.getSynonymKeyword();
        application.setAttribute("synonymsList", synonymsListResult);
        log.info("Synonyms Time:"+(System.currentTimeMillis()-startTime));
    }
    /**初始化类别同义词列表
     * @param application
     */
    public void synonymsCategory(ServletContext application){
        long startTime = System.currentTimeMillis();
        application.setAttribute("synonymsCategory", synonymService.getSynonymsCategory());
        log.info("Synonyms Category Time:"+(System.currentTimeMillis()-startTime));
    }

    /**初始化搜索词对应的最低价和最高价
     * @param application
     */
    public void categoryPrice(ServletContext application){
        long startTime = System.currentTimeMillis();
        Map<String,String> categoryPriceResult = new HashMap<String, String>();
        List<Map<String, String>> categoryPriceAll = keywordRecordService.getCategoryPriceList();
        for(Map<String,String> map : categoryPriceAll){
            categoryPriceResult.put(map.get("keyword").toLowerCase(), map.get("price"));
        }
        application.setAttribute("categoryPriceList", categoryPriceResult);
        log.info("Category Price Time:"+(System.currentTimeMillis()-startTime));
    }

    /**初始化盲搜类别列表
     * @param application
     */
    public void blindSearchCategory(ServletContext application){
        long startTime = System.currentTimeMillis();
        List<Map<String,String>> keywordCatidList = keywordRecordService.getKeywordCatidList();
        Map<String,String> blindSearchCategoriesList = new HashMap<String, String>();
        for(Map<String,String> map : keywordCatidList){
            String catid = blindSearchCategoriesList.get(map.get("keyword").toLowerCase());
            catid = StringUtils.isBlank(catid) ? "" : catid;
            String catid1 = map.get("catid1");
            catid = StringUtils.isBlank(catid1) || StrUtils.isFind(","+catid+",", "(,"+catid1+",)")? catid : catid+","+catid1;
            String catid2 = map.get("catid2");
            catid = StringUtils.isBlank(catid2) || StrUtils.isFind(","+catid+",", "(,"+catid2+",)")? catid : catid+","+catid2;
            String catid3 = map.get("catid3");
            catid = StringUtils.isBlank(catid3) || StrUtils.isFind(","+catid+",", "(,"+catid3+",)")? catid : catid+","+catid3;
            blindSearchCategoriesList.put(map.get("keyword").toLowerCase(), catid);
        }
        application.setAttribute("blindSearchCategoriesList", blindSearchCategoriesList);
        log.info("Blind Search Category Time:"+(System.currentTimeMillis()-startTime));
    }

    /**初始化类别限制列表
     * @param application
     */
    public void specialCatid(ServletContext application){
        long startTime = System.currentTimeMillis();
        application.setAttribute("specialCatidList", keywordRecordService.getSpecialCategory());
        log.info("Special Category Time:"+(System.currentTimeMillis()-startTime));
    }

    /**初始化优先类别列表
     * @param application
     */
    public void priorityCategory(ServletContext application){
        long startTime = System.currentTimeMillis();
        Map<String,String> priorityCategoryResult = new HashMap<String, String>();
        List<Map<String, String>> priorityCategoryAll = keywordRecordService.getPriorityCategoryList();
        for(Map<String,String> map : priorityCategoryAll){
            priorityCategoryResult.put(map.get("keyword").toLowerCase(), map.get("category"));
        }
        application.setAttribute("priorityCategoryList", priorityCategoryResult);
        log.info("Priority Category Time:"+(System.currentTimeMillis()-startTime));
    }

    /**初始化反关键词集合
     * @param application
     */
    public void autiKey(ServletContext application){
        long startTime = System.currentTimeMillis();
        Map<String,String> autiKeyResult = autiKeyService.getAutiKey();
        application.setAttribute("autiKeyList", autiKeyResult);
        log.info("Auti Key Time:"+(System.currentTimeMillis()-startTime));
    }

    /**获取搜索页面底部推荐词 whj
     * @param application
     */
    public void recommendedWords(ServletContext application){
        long startTime = System.currentTimeMillis();
        List<SearchWordWrap> cList = categoryService.getRecommendedWords();
        application.setAttribute("recommendedWords", cList);
        log.info("Recommended Words Time:"+(System.currentTimeMillis()-startTime));
    }

}
