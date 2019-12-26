package com.importexpress.search.service.impl;


import com.google.common.collect.Lists;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.common.*;
import com.importexpress.search.pojo.*;
import com.importexpress.search.service.*;
import com.importexpress.search.util.DoubleUtil;
import com.importexpress.search.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jack.luo
 * @date 2019/11/22
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {
//    private String chineseChar = "([\\一-\\龥]+)";
    @Autowired
    private ModefilePrice modefilePrice;
    @Autowired
    private SplicingSyntax splicingSyntax;
    @Autowired
    private PageService pageService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private LandingPageTriggerKeyService land;
    @Autowired
    private SolrService solrService;
    @Autowired
    private ServletContext application;
    @Autowired
    private CalculatePrice calculatePrice;

    @Override
    public SearchResultWrap advertisement(String key, int site, String adgroupid) {
        LimitKey triggerKey = land.getTriggerKey(key,adgroupid);
        if(triggerKey == null){
            return new SearchResultWrap();
        }
        SearchParam param = new SearchParam();
        param.setKeyword(triggerKey.getTriggerKey());
        param.setCatid(triggerKey.getTriggerCatid());
        param.setPageSize(16);
        param.setFactCategory(false);
        param.setFactPvid(false);
        param.setSynonym(false);
        param.setMinPrice("2.00");
        param.setSite(site);
        param.setUserType(1);
        SearchResultWrap wrap = productSerach(param);
        return wrap;
    }

    @Override
    public List<SearchWordWrap> searchWord(String keyword, int site) {
        Object wordList = application.getAttribute("recommendedWords");
        List<SearchWordWrap> list = Lists.newArrayList();
        if (wordList == null || StringUtils.isBlank(keyword)) {
            return list;
        }
        List<SearchWordWrap> cList = (List<SearchWordWrap>) wordList;
        try {
            FSearchTool tool = new FSearchTool(cList, "key_word", "path");
            List<Object> listWord = tool.searchTasks(keyword);
            if (listWord == null || listWord.isEmpty()) {
                return list;
            }
            SearchParam param = new SearchParam();
            param.setPage(1);
            param.setFreeShipping(1);
            param.setMobile(false);
            param.setFactPvid(false);
            param.setFactPvid(false);
            param.setPageSize(1);
            param.setFreeShipping(1);
            SearchWordWrap s = (SearchWordWrap) listWord.get(0);
            List<Object> listq = tool.searchTasks(s.getPath());
            for (Object o : listq) {
                SearchWordWrap sw = (SearchWordWrap) o;
                if (!sw.getKeyWord().equals(keyword)) {
                    param.setKeyword(KeywordCorrect.getKeyWord(sw.getKeyWord()));
                    if(serachCount(param) > 0){
                        list.add(sw);
                    }
                }
            }
        }catch (Exception e){
            log.error("FSearchTool",e);
        }
        return list;
    }

    @Override
    public List<Product> similarProduct(SearchParam param) {
        return Lists.newArrayList();
    }

    @Override
    public List<Product> guessYouLike(SearchParam param) {
        List<Product> list = Lists.newArrayList();
        if (StringUtils.isBlank(param.getKeyword())) {
            return list;
        }
        param.setSynonym(false);
        QueryResponse response = solrService.serach(param);
        if (response != null) {
            list = docToProduct(response.getResults(), param);
        }
        return list;
    }

    @Override
    public List<Product> boughtAndBought(SearchParam param) {
        List<Product> list = Lists.newArrayList();
        QueryResponse response = solrService.bought(param);
        if (response != null) {
            list = docToProduct(response.getResults(), param);
            list = list.stream().filter(e -> StrUtils.isMatch(e.getPrice(),"(\\d+(\\.\\d+){0,1})"))
                    .collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public List<Product> catidForGoods(SearchParam param) {
        List<Product> list = Lists.newArrayList();
        QueryResponse response = solrService.catidForGoods(param);
        if (response != null) {
            list = docToProduct(response.getResults(), param);
            list = list.stream().filter(e -> StrUtils.isMatch(e.getPrice(),"(\\d+(\\.\\d+){0,1})"))
                    .collect(Collectors.toList());
        }
        return list;

    }

    @Override
    public List<Product> errorRecommend(SearchParam param) {
        List<Product> list = Lists.newArrayList();
        QueryResponse response = solrService.errorRecommend(param);
        if (response != null) {
            list = docToProduct(response.getResults(), param);
            list = list.stream().filter(e -> !StrUtils.isMatch(e.getPrice(),"(\\d+(\\.\\d+){0,1})"))
                    .collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public List<Product> hotProductForCatid(SearchParam param) {
        List<Product> list = Lists.newArrayList();
        QueryResponse response = solrService.hotProductForCatid(param);
        if (response != null) {
            list = docToProduct(response.getResults(), param);
            list = list.stream().filter(e -> StrUtils.isMatch(e.getPrice(),"(\\d+(\\.\\d+){0,1})"))
                    .collect(Collectors.toList());
            list = Utility.getRandomNumList(list, 12);
        }
        return list;
    }

    @Override
    public List<Product> hotProduct(SearchParam param) {
        List<Product> list = Lists.newArrayList();
        if (StringUtils.isBlank(param.getKeyword())) {
            // @author: cjc @date：2019/8/19 11:36:18   Description : 沈静说无需处理,修改日志级别即可
            log.warn("param.getKeyword() is null ");
            return list;
        }
        QueryResponse response = solrService.hotProduct(param);
        if (response != null) {
            list = docToProduct(response.getResults(), param);
        }
        return list;
    }

    public List<FacetField> groupCategory(SearchParam param) {
        QueryResponse response = solrService.groupCategory(param);
        //取分组统计列表
        return response == null ? Lists.newArrayList() : response.getFacetFields();
    }


    @Override
    public SearchResultWrap productSerach(SearchParam param) {
        SearchResultWrap wrap = new SearchResultWrap();
        wrap.setParam(param);
        String queryString = param.getKeyword();
        if (StringUtils.isBlank(queryString)) {
            return wrap;
        }
        //solr结果
        param.setSynonym(StringUtils.isBlank(param.getCatid()));
        SearchResultWrap wrapTemp = productsFromSolr(param);

        PageWrap page1 = wrapTemp.getPage();
        //是否需要推荐联想词
        long recordCount = page1 == null ? 0 : page1.getRecordCount();
        boolean suggestKey = isDefault(param);
        suggestKey = suggestKey && recordCount < 40 && param.getKeyword().split("(\\s+)").length > 2;
        if(suggestKey){
            List<AssociateWrap> associate = associate(param.getKeyword(), param.getSite());
            wrap.setAssociates(associate);
        }
        wrapTemp.setSuggest(suggestKey);
        return wrapTemp;

    }

    @Override
    public SearchResultWrap shopSerach(SearchParam param) {
        QueryResponse response = solrService.shopSerach(param);
        SolrResult solrResult = searchItem(param,response);
        SearchResultWrap wrap = compose(solrResult, param);
        return wrap;
    }

    @Override
    public long serachCount(SearchParam param) {
        param.setFactCategory(false);
        param.setFactPvid(false);
        param.setOrder(false);
        param.setPage(1);
        param.setPageSize(1);
        param.setMobile(false);
        param.setSynonym(true);
        QueryResponse response = solrService.serach(param);
        SolrResult solrResult = searchItem(param,response);
        return solrResult.getRecordCount();
    }

    @Override
    public GoodsPriceRange searPriceRangeByKeyWord(SearchParam param) {
        GoodsPriceRange range = new GoodsPriceRange();
        range.setCatid(param.getCatid());
        Map<String,Object> response = solrService.searPriceRangeByKeyWord(param);
        if(response == null){
            return range;
        }
        double midPrice = (Double) response.get("midPrice");
        Map<String, Integer> solrMap = (Map<String, Integer>)response.get("solrMap");
        boolean isChangeCurrency = !"USD".equals(param.getCurrency());
        DecimalFormat df  = new DecimalFormat("0.00");  //保留两位小数
        range.setKeyword(null);
        range.setOtherkeyword(null);
        range.setSectionOnePrice(Double.valueOf(df.format(midPrice/2)));
        range.setSectionOneCount(solrMap.get("custom_max_price:[* TO "+Double.parseDouble(df.format(midPrice/2))+"]"));
        range.setSectionTwoPrice(Double.parseDouble(df.format(midPrice)));
        range.setSectionTwoCount(solrMap.get("custom_max_price:["+(Double.parseDouble(df.format(midPrice/2)+1))+" TO "+midPrice+"]"));
        range.setSectionThreePrice(2*Double.parseDouble(df.format(midPrice)));
        range.setSectionThreeCount(solrMap.get("custom_max_price:["+(midPrice+0.001)+" TO "+2*midPrice+"]"));
        range.setSectionFourCount(solrMap.get("custom_max_price:["+(Double.parseDouble((2*midPrice+""+1)))+" TO *]"));
        range.setState(0);
        //存放到数据库中
        if(isChangeCurrency){
            ChangeCurrency.chang(range,param.getCurrency());
        }
        return range;
    }

    @Override
    public List<String> searchAutocomplete(String keyWord,int site) {
        SpellCheckResponse response = solrService.searchAutocomplete(keyWord,site);
        if(response == null){
            return Lists.newArrayList();
        }
        List<String> suggest = Lists.newArrayList();
        List<SpellCheckResponse.Suggestion> suggestionList = response.getSuggestions();
        for (int i=0,length=suggestionList.size();i<length;i++) {
            List<String> suggestedWordList = suggestionList.get(i).getAlternatives();
            for (int j=0,size=suggestedWordList.size();j<size && suggest.size()<11;j++) {
                String word = suggestedWordList.get(j);
                word = SwitchDomainUtil.correctAutoResult(word,site);
                if(!suggest.contains(word)){
                    suggest.add(word);
                }
            }
        }
        suggest.stream().sorted();
        if (suggest.size() > 0) {
            String suggestCatid = splicingSyntax.suggestCatid(suggest.get(0));
            if(StringUtils.isNotBlank(suggestCatid)){
                suggest.add(0,suggestCatid);
            }
        }
        return suggest;
    }

    @Override
    public List<AssociateWrap> associate(String keyWord, int site) {
        String[] exhaust = Utility.combination(keyWord);
        if(exhaust == null) {
            return Lists.newArrayList();
        }
        List<AssociateWrap> result = Lists.newArrayList();
        AssociateWrap wrap = null;
        SearchParam param = new SearchParam();
        param.setPageSize(1);
        param.setPage(1);
        param.setFreeShipping(1);
        param.setMobile(false);
        param.setFactPvid(false);
        param.setFactPvid(false);
        param.setSite(site);
        param.setCurrency(new Currency());
        param.setUserType(1);
        for(int i=0,length=exhaust.length;i<length&&result.size() <4;i++) {
            param.setKeyword(KeywordCorrect.getKeyWord(exhaust[i]));
            param.setFreeShipping(1);
            long countResult = serachCount(param);
            if(countResult > 4 && result.size() < 5) {
                wrap = new AssociateWrap();
                wrap.setCount(countResult);
                wrap.setKey(exhaust[i]);
                result.add(wrap);
            }
        }
        return result;
    }

    /**
     * 请求执行结果
     *
     * @param response
     * @param param
     * @return
     */
    private SolrResult searchItem(SearchParam param,QueryResponse response) {
        SolrResult solrResult = new SolrResult();
        if (response == null) {
            return solrResult;
        }
        SolrDocumentList documentList = response.getResults();
        //取分组统计列表
        solrResult.setAttrFacet(response.getFacetFields());
        //取查询结果总数量
        solrResult.setRecordCount(documentList.getNumFound());

        List<Product> itemList = docToProduct(documentList, param);
        solrResult.setItemList(itemList);
        return solrResult;
    }

    /**
     * 解析出产品
     *
     * @param documentList
     * @param param
     * @return
     */
    private List<Product> docToProduct(SolrDocumentList documentList, SearchParam param) {
        List<Product> products = Lists.newArrayList();
        boolean isFree = 2 - param.getFreeShipping() == 0;
        boolean changeCurrency = !"USD".equals(param.getCurrency());
        for (SolrDocument solrDocument : documentList) {
            //产品id
            String itemId = StrUtils.object2Str(solrDocument.get("custom_pid"));
            if (StringUtils.isBlank(itemId)) {
                continue;
            }
            calculatePrice.raiseMoqSearchGoods(solrDocument,param.getSite());
            Product product = new Product();
            product.setId(itemId);

            //店铺id
            product.setShopId((String) solrDocument.get("custom_shop_id"));

            //zlw 2018/05/25 update 对标商品销量 Max(速卖通，1688销量) str
            String custom_ali_sold = StrUtils.object2NumStr(solrDocument.get("custom_ali_sold"));
            String custom_sold = StrUtils.object2NumStr(solrDocument.get("custom_sold"));
            String soldObject = String.valueOf(Integer.parseInt(custom_ali_sold) + Integer.parseInt(custom_sold));
            product.setSolder(soldObject);
            //zlw 2018/05/25 update 对标商品销量 Max(速卖通，1688销量) end

            String catid = StrUtils.object2Str(solrDocument.get("custom_path_catid"));
            String infoReviseFlag = StrUtils.object2Str(solrDocument.get("custom_infoReviseFlag"));
            //后台人为确定过得标题翻译，直接使用，不在使用自动翻译的标题
            String title = StrUtils.object2Str(solrDocument.get("custom_finalName"));
            if (StringUtils.isBlank(infoReviseFlag) || "0".equals(infoReviseFlag)
                    || StringUtils.isBlank(title)) {
                title = StrUtils.object2Str(solrDocument.get("custom_enname"));
            }
            //拼接类别名称
        /*1688标题短，就用 速卖通标题 这个 逻辑 去掉改成 1688 标题短 就 在标题里面 加上 这个产品的 类别名绝不能 直接用
        速卖通 产品名--2018-01-05*/
            title = splicingSyntax.categoryNameToTitle(title, catid);
            product.setName(title);


            String catid1 = "0";
            String catid2 = "0";
            if (StringUtils.isNotBlank(catid) && catid.indexOf(" ") > -1) {
                String[] catids = catid.split(" ");
                catid1 = catids[0];
                catid2 = catids[1];
            }

            //图片切换域名N
            String custom_img = StrUtils.object2Str(solrDocument.get("custom_main_image"));
            String path = custom_img;
            if (!custom_img.contains("import-express.com")) {
                String remotpath = StrUtils.object2Str(solrDocument.get("custom_remotpath"));
                if (StringUtils.isNotBlank(remotpath)) {
                    path = remotpath + custom_img;
                }
            }
            path = SwitchDomainUtil.checkIsNullAndReplace(path, param.getSite());
            product.setImage(path.replace("220x220", "285x285"));

            /**
             * 添加伪静态化链接
             */
            String goods_url = UriCompose.pseudoStaticUrl(itemId, product.getName(), catid1, catid2, 1);
            product.setUrl(goods_url.replaceAll("\\%", ""));

            //价格
            productPrice(isFree, solrDocument, product);

            String unit = StrUtils.object2Str(solrDocument.get("custom_sellunit"));
            unit = StringUtils.isBlank(unit) ? "piece" : unit;
            String setSellUnits_ = StrUtils.matchStr(unit, "(\\(.*\\))");
            unit = StringUtils.isNotBlank(setSellUnits_) ? unit.replace(setSellUnits_, "").trim() : unit;
            product.setPriceUnit(unit);
            product.setMoqUnit(unit);
            String goods_minOrder = StrUtils.object2Str(solrDocument.get("custom_morder"));
            goods_minOrder = StrUtils.isNum(goods_minOrder) ? goods_minOrder : "1";
            //单位
            product.setMinOrder(goods_minOrder);
            if (StringUtils.isNotBlank(goods_minOrder) && !"1".equals(goods_minOrder) && !"pcs".equals(unit)) {
                product.setMoqUnit(unit + "s" + setSellUnits_);
            } else {
                product.setMoqUnit(unit + setSellUnits_);
            }

            //添加视频链接判断
//            String custom_video_url = StrUtils.object2Str(solrDocument.get("custom_video_url"));
//            if (StringUtils.isNotBlank(custom_video_url)) {
//                product.setIsVideo(1);
//            }

            //其他数据----不是搜索页面必须数据

            //货币切换
            if (changeCurrency) {
                ChangeCurrency.chang(product, param.getCurrency());
            }
            products.add(product);
        }
        return products;

    }

    /**
     * 价格
     *
     * @param isFree
     * @param solrDocument
     * @param searchGoods
     */
    private void productPrice(boolean isFree, SolrDocument solrDocument, Product searchGoods) {
        String custom_is_sold_flag = StrUtils.object2NumStr(solrDocument.get("custom_is_sold_flag"));
        String finalWeightTem = StrUtils.object2Str(solrDocument.get("custom_final_weight"));
        String price = StrUtils.object2Str(solrDocument.get("custom_price"));
        String rangePrice = StrUtils.object2Str(solrDocument.get("custom_range_price"));
        String rangePriceFree = StrUtils.object2Str(solrDocument.get("custom_range_price_free"));
        String custom_feeprice = StrUtils.object2Str(solrDocument.get("custom_feeprice"));
        if (StringUtils.isBlank(rangePrice)) {
            price = isFree ? custom_feeprice : price;
            searchGoods.setPrice(price);
            //批量价格显示
            String wprice = StrUtils.object2Str(solrDocument.get("custom_wprice"));
            if (!"0".equals(custom_is_sold_flag) && StringUtils.isNotBlank(custom_feeprice)) {
                wprice = custom_feeprice;
            }
            List<Price> modefideWholesalePrice = modefilePrice.modefideWholesalePrice(wprice);
            if (modefideWholesalePrice != null && modefideWholesalePrice.size() == 1) {
                if (isFree) {
                    getFreePrice(finalWeightTem, modefideWholesalePrice);
                }
                searchGoods.setPrice(modefideWholesalePrice.get(0).getPrice());
                searchGoods.setMinOrder(searchGoods.getMinOrder());
            } else if (modefideWholesalePrice != null && modefideWholesalePrice.size() > 1) {
                searchGoods.setWholesaleMiddlePrice(modefideWholesalePrice.size() > 1 ? modefideWholesalePrice.get(1).getPrice() : null);
                if (isFree) {
                    getFreePrice(finalWeightTem, modefideWholesalePrice);
                }
                searchGoods.setWholesalePrice(modefideWholesalePrice);
                price = modefideWholesalePrice.get(modefideWholesalePrice.size() - 1).getPrice();
                price = price + "-" + modefideWholesalePrice.get(0).getPrice();
                searchGoods.setPrice(price);
                searchGoods.setMinOrder(searchGoods.getMinOrder());
            } else {
                searchGoods.setMinOrder(searchGoods.getMinOrder());
            }
        } else {
            if (isFree) {
                if (StringUtils.isNotBlank(rangePriceFree)) {
                    rangePrice = rangePriceFree;
                } else {
                    rangePrice = modefilePrice.getRangePrice(rangePrice, 0, finalWeightTem, rangePrice);
                }
            }
            searchGoods.setPrice(rangePrice);
            searchGoods.setMinOrder(searchGoods.getMinOrder());
        }
    }

    public void getFreePrice(String finalWeightTem, List<Price> modefideWholesalePrice) {
        modefideWholesalePrice.stream().forEach(priceBean -> {
            try {
                String price1 = priceBean.getPrice();
                price1 = getShippingCostByWeight(finalWeightTem, price1);
                // @author: cjc @date：2019/8/27 9:39:44   Description :	 取精度
                price1 = new BigDecimal(price1).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                priceBean.setPrice(price1);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                log.error("e", e);
            }

        });
    }

    @NotNull
    public static String getShippingCostByWeight(String finalWeightTem, String price1) {
        try {
            String s3 = finalWeightTem.replaceAll("[^(\\d+\\.\\d+)]", "");
            Double divide = DoubleUtil.divide(DoubleUtil.mul(Double.parseDouble(s3) * 1000, Utility.PERGRAMUSA), Utility.EXCHANGE_RATE, 2);
            price1 = String.valueOf(Double.parseDouble(price1) + divide);
            price1 = new BigDecimal(price1).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.error("error", e);
        }
        return price1;
    }

    /**请求solr解析产品列表
     * @param param
     * @return
     */
    private  SearchResultWrap productsFromSolr(SearchParam param){
        SearchResultWrap wrap = new SearchResultWrap();
        //请求solr获取产品列表
        QueryResponse response = solrService.serach(param);
        //拼接参数
        if (response == null) {
            return wrap;
        }
        //执行查询
        SolrResult solrResult = searchItem(param,response);

        //分组数据,第二次查询取得类别分组统计数据--类别统计
        if (param.isFactCategory() && solrResult.getRecordCount() > 0) {
            List<FacetField> searchGroup = groupCategory(param);
            solrResult.setCategoryFacet(searchGroup);
        }
        //结果解析
        return compose(solrResult, param);
    }

    /**
     * 结果集重新分组
     *
     * @param solrResult
     * @param param
     * @return
     */
    private SearchResultWrap compose(SolrResult solrResult, SearchParam param) {
        SearchResultWrap wrap = new SearchResultWrap();

        //产品
        List<Product> itemList = solrResult.getItemList();
        wrap.setProducts(itemList);

        //类别
        if (param.isFactCategory()) {
            List<CategoryWrap> categorys = categoryService.categorys(param, solrResult.getCategoryFacet());
            wrap.setCategorys(categorys);
        }

        //属性
        if (param.isFactPvid()) {
            List<AttributeWrap> attributes = attributeService.attributes(param, solrResult.getAttrFacet());
            wrap.setAttributes(attributes);
            List<Attribute> selectedAttr = attributeService.selectedAttributes(param);
            wrap.setSelectedAttr(selectedAttr);
        }

        //分页
        long recordCount = solrResult.getRecordCount();
        PageWrap paging = pageService.paging(param, recordCount);
        wrap.setPage(paging);
        return wrap;
    }
    private boolean isDefault(SearchParam param){
        boolean isDefault =  "default".equals(param.getSort());
        isDefault = isDefault && (StringUtils.isBlank(param.getCatid()));
        isDefault = isDefault && param.getPage() < 2;
        isDefault = isDefault && StringUtils.isBlank(param.getMinPrice()) && StringUtils.isBlank(param.getMaxPrice());
        isDefault = isDefault && StringUtils.isBlank(param.getAttrId());
        return isDefault;
    }

}