package com.importexpress.search.common;

import com.importexpress.search.pojo.SearchParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.*;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importExpress.util
 * @date:2019/12/6
 */
@Component
@Slf4j
public class ImportCategoryUtil {
    /*@Autowired
    private NewCategoryService newCategoryService;
    @Autowired
    private SolrSearchService solrSearchService;

    *//**
     * 全部1688Category的数据导入
     *
     * @param application
     *//*
    public void category(ServletContext application) {
        long startTime = System.currentTimeMillis();
        List<AliCategory> allCategory = newCategoryService.getAllCategory();
        if (MultiSiteUtil.site == 1) {
            // 对Import的做单独类别数据处理
            allCategory = getImportAllCategory(allCategory);
        }

        application.setAttribute("allCategorys", allCategory);
        Map<String, CategoryBean> catidListResult = new HashMap<String, CategoryBean>();
        for (AliCategory categoryBean : allCategory) {
            CategoryBean bean = new CategoryBean();
            bean.setCategoryId(categoryBean.getCid());
            bean.setCategoryLevel(categoryBean.getLv());
            bean.setCategoryName(categoryBean.getCategory());
            bean.setCategoryPath(categoryBean.getPath());
            bean.setNewArrivalsFlag(categoryBean.getNewArrivalsFlag());
            bean.setNewArrivalDate(categoryBean.getNewArrivalDate());
            String[] categoryParent = categoryBean.getPath().split(",");
            bean.setCategoryParent(categoryParent.length > 1 ? categoryParent[categoryParent.length - 2] : "0");
            catidListResult.put(categoryBean.getCid(), bean);
        }
        application.setAttribute("1688CatidList", catidListResult);
        log.info("全部1688Category的数据导入 Time:" + (System.currentTimeMillis() - startTime));
    }


    *//**
     * 获取Import的类别数据
     *
     * @param allCategory
     * @return
     * @autor JXW-2019-12-06
     *//*
    private List<AliCategory> getImportAllCategory(List<AliCategory> allCategory) {

        List<AliCategory> resultList = new ArrayList<>(allCategory.size());
        // 1.处理没有日期的数据，查询LV是1的数据，循环判断path含有catid的数据
        Map<String, List<AliCategory>> catidLvMap = dealCatidData(allCategory);
        allCategory.clear();
        if (catidLvMap.size() > 0) {
            for (String lvCatid : catidLvMap.keySet()) {
                if (CollectionUtils.isNotEmpty(catidLvMap.get(lvCatid))) {
                    // 1.1 判断类别和子类别是否存在搜索数据,如果有加入到集合中
                    checkIsImportSearchByCatid(lvCatid, catidLvMap.get(lvCatid), resultList);
                }
            }
            catidLvMap.clear();
        }

        // 2.过滤含有到达日期的类别数据
        Map<String, List<AliCategory>> arrivalDateMap = new HashMap<>(resultList.size());

        List<AliCategory> noArrivalDateList = new ArrayList<>(resultList.size());

        String[] tempDateList;
        for (AliCategory categoryBean : resultList) {
            // 2.1判断是否有新到达日期
            if (StringUtils.isNotBlank(categoryBean.getNewArrivalDate()) && categoryBean.getNewArrivalDate().length() > 2) {
                tempDateList = categoryBean.getNewArrivalDate().split(",");
                for (String tempDate : tempDateList) {
                    // 生成日期map对象数据
                    if (arrivalDateMap.containsKey(tempDate)) {
                        arrivalDateMap.get(tempDate).add(categoryBean);
                    } else {
                        List<AliCategory> dateAliCategoryList = new ArrayList<>();
                        dateAliCategoryList.add(categoryBean);
                        arrivalDateMap.put(tempDate, dateAliCategoryList);
                    }
                }
            } else {
                noArrivalDateList.add(categoryBean);
            }
        }

        resultList.clear();
        // 2.2 如果有日期数据，查询solr日期数据，判断类别是否存在
        if (arrivalDateMap.size() > 0) {
            Map<String, AliCategory> checkDataCategoryMap = new HashMap<>(allCategory.size());
            // 开始循环检查数据
            for (String tempDate : arrivalDateMap.keySet()) {
                if (CollectionUtils.isNotEmpty(arrivalDateMap.get(tempDate))) {
                    checkIsImportSearchByDate(tempDate, arrivalDateMap.get(tempDate));
                    // 循环，获取最新的类别Bean数据
                    arrivalDateMap.get(tempDate).forEach(e -> {
                        if (StringUtils.isBlank(e.getNewArrivalDate())) {
                            e.setNewArrivalsFlag(1);
                        }
                        checkDataCategoryMap.put(e.getCid(), e);
                    });
                }
            }
            // 2.2将结果加入到集合中
            noArrivalDateList.addAll(checkDataCategoryMap.values());
        }
        return noArrivalDateList;
    }


    *//**
     * 获取LV等级是1的类别和子类别数据
     *
     * @param noArrivalDateList
     * @return
     *//*
    private Map<String, List<AliCategory>> dealCatidData(List<AliCategory> noArrivalDateList) {

        if (CollectionUtils.isNotEmpty(noArrivalDateList)) {
            Map<String, List<AliCategory>> catidLvMap = new HashMap<>(noArrivalDateList.size());
            // 首先获取LV是1的数据
            noArrivalDateList.forEach(e -> {
                if (e.getLv() == 1) {
                    if (catidLvMap.containsKey(e.getCid())) {
                        catidLvMap.get(e.getCid()).add(e);
                    } else {
                        List<AliCategory> tempList = new ArrayList<>();
                        tempList.add(e);
                        catidLvMap.put(e.getCid(), tempList);
                    }
                }
            });
            // 再获取LV大于1的数据
            noArrivalDateList.forEach(e -> {
                if (e.getLv() > 1) {
                    for (Map.Entry<String, List<AliCategory>> entity : catidLvMap.entrySet()) {
                        if (("," + e.getPath() + ",").contains("," + entity.getKey() + ",")) {
                            entity.getValue().add(e);
                        }
                    }
                }
            });
            return catidLvMap;
        }
        return new HashMap<>();
    }


    *//**
     * 根据日期查询solr,检查类别是否按照Import逻辑有搜索结果
     *
     * @param newArrivalDate
     * @param categoryList
     * @return
     * @autor JXW-2019-12-06
     *//*
    private void checkIsImportSearchByDate(String newArrivalDate, List<AliCategory> categoryList) {
        if (MultiSiteUtil.site == 1 && CollectionUtils.isNotEmpty(categoryList)) {
            String tampDate = "," + newArrivalDate;
            ParamBean param = getInitParamBean();
            param.setNewArrivalDate(newArrivalDate);

            List<FacetField> searchGroup = solrSearchService.searchGroupCatid(param.getKeyword(), param);
            // 转换Set数据
            Set<String> catidSet = checkResultCatid(searchGroup);
            searchGroup.clear();
            categoryList.forEach(category -> {
                // 如果没有查询结果，替换日期信息
                if (!catidSet.contains(category.getCid())) {
                    System.err.println("catid:" + category.getCid() + ",newArrivalDate["
                            + category.getNewArrivalDate() + "],tampDate:" + tampDate);
                    if (StringUtils.isNotBlank(category.getNewArrivalDate())) {
                        String rsArrivalDate = ("," + category.getNewArrivalDate()).replace(tampDate, "");
                        if (StringUtils.isNotBlank(rsArrivalDate) && rsArrivalDate.indexOf(",") == 0) {
                            category.setNewArrivalDate(rsArrivalDate.substring(1));
                        } else {
                            category.setNewArrivalDate(rsArrivalDate);
                        }
                    }
                }
            });
            catidSet.clear();
        }
    }


    *//**
     * 根据类别查询solr,检查类别是否按照Import逻辑有搜索结果
     *
     * @param catid
     * @param categoryList
     * @return
     * @autor JXW-2019-12-06
     *//*
    private void checkIsImportSearchByCatid(String catid, List<AliCategory> categoryList, List<AliCategory> resultList) {
        if (MultiSiteUtil.site == 1 && CollectionUtils.isNotEmpty(categoryList)) {
            ParamBean param = getInitParamBean();
            param.setCatid(catid);
            param.setCollection(1);

            List<FacetField> searchGroup = solrSearchService.searchGroupCatid(param.getKeyword(), param);
            // 转换Set数据
            Set<String> catidSet = checkResultCatid(searchGroup);
            searchGroup.clear();
            categoryList.forEach(category -> {
                // 如果没有查询结果，替换日期信息
                if (catidSet.contains(category.getCid())) {
                    resultList.add(category);
                }
                if (!catidSet.contains(category.getCid())) {
                    System.err.println("catid:" + category.getCid());
                }
            });
            catidSet.clear();
        }
    }

    *//**
     * 获取Import初始化查询solr bean
     *
     * @return
     * @autor JXW-2019-12-06
     *//*
    private SearchParam getInitParamBean() {
        SearchParam param = new SearchParam();
        param.setKeyword("*");
        param.setSort("order-desc");
        param.setMinPrice("");
        param.setMaxPrice("");
        param.setCatid("0");
        param.setAttrId("");
        param.setUnkey("");
        param.setFKey("");
        param.setStoried("");
        param.setMobile(false);
        param.setCollection(8);
        param.setFreeShipping(2);
        param.setUserType(1);
        param.setOnlyImport(true);
        return param;
    }

    *//**
     * 返回solr结果的类别数据
     *
     * @param facetList
     * @return
     * @autor JXW-2019-12-06
     *//*
    private Set<String> checkResultCatid(List<FacetField> facetList) {

        if (facetList != null && !facetList.isEmpty()) {
            Set<String> catidSet = new HashSet<>(facetList.size());
            for (FacetField facet : facetList) {
                List<FacetField.Count> values = facet.getValues();
                for (FacetField.Count value : values) {
                    String catid = value.getName();
                    if ("0".equals(catid)) {
                        continue;
                    }
                    catidSet.add(catid);
                }
            }
            return catidSet;
        }
        return new HashSet<>();
    }*/

}
