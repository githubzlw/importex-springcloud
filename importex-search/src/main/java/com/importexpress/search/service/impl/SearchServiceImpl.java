package com.importexpress.search.service.impl;


import com.google.common.collect.Lists;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.common.*;
import com.importexpress.search.pojo.*;
import com.importexpress.search.service.*;
import com.importexpress.search.util.ExhaustUtils;
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
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {
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
    private LandingPageTriggerKeyService landService;
    @Autowired
    private SolrService solrService;
    @Autowired
    private ServletContext application;
//    @Autowired
//    private CalculatePrice calculatePrice;
    @Autowired
    private ExhaustUtils exhaustUtils;
//    DecimalFormat df  = new DecimalFormat("#0.00");  //保留两位小数

    @Override
    public SearchResultWrap advertisement(String key, int site, String adgroupid) {
        LimitKey triggerKey = landService.getTriggerKey(key,adgroupid);
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
        param.setCurrency(new Currency());
        SearchResultWrap wrap = productSerach(param);
        return wrap;
    }

    @Override
    public List<SearchWordWrap> searchWord(String keyword, int site) {
        List<SearchWordWrap> cList = (List<SearchWordWrap>) application.getAttribute("recommendedWords");
        List<SearchWordWrap> list = Lists.newArrayList();
        try {
            FSearchTool tool = new FSearchTool(cList, "keyWord", "path");
            List<Object> listWord = tool.searchTasks(keyword);
            if (listWord == null || listWord.isEmpty()) {
                return list;
            }
            SearchParam param = new SearchParam();
            param.setSite(site);
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
            /*list = list.stream().filter(e -> StrUtils.isMatch(e.getPrice(),"(\\d+(\\.\\d+){0,1})"))
                    .collect(Collectors.toList());*/
        }
        return list;
    }

    @Override
    public List<Product> catidForGoods(SearchParam param) {
        List<Product> list = Lists.newArrayList();
        QueryResponse response = solrService.catidForGoods(param);
        if (response != null) {
            list = docToProduct(response.getResults(), param);
            /*list = list.stream().filter(e -> StrUtils.isMatch(e.getPrice(),"(\\d+(\\.\\d+){0,1})"))
                    .collect(Collectors.toList());*/
        }
        return list;

    }

    @Override
    public List<Product> errorRecommend(SearchParam param) {
        List<Product> list = Lists.newArrayList();
        QueryResponse response = solrService.errorRecommend(param);
        if (response != null) {
            list = docToProduct(response.getResults(), param);
           /* list = list.stream().filter(e -> StrUtils.isMatch(e.getPrice(),"(\\d+(\\.\\d+){0,1})"))
                    .collect(Collectors.toList());*/
        }
        return list;
    }

    @Override
    public List<Product> hotProductForCatid(SearchParam param) {
        List<Product> list = Lists.newArrayList();
        QueryResponse response = solrService.hotProductForCatid(param);
        if (response != null) {
            list = docToProduct(response.getResults(), param);
            list = list.stream()
                    .filter(e -> StrUtils.isMatch(e.getPrice(),"(\\d+(\\.\\d+){0,1})"))
                    .collect(Collectors.toList());
            list = Utility.getRandomNumList(list, 12);
        }
        return list;
    }

    @Override
    public List<Product> hotProduct(SearchParam param) {
        List<Product> list = Lists.newArrayList();
        QueryResponse response = solrService.hotProduct(param);
        if (response != null) {
            list = docToProduct(response.getResults(), param);
        }
        return list;
    }
    @Override
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
        suggestKey = suggestKey && recordCount < 40
                && param.getKeyword().split("(\\s+)").length > 1;
        if(suggestKey){
            List<AssociateWrap> associate = associate(param.getKeyword(), param);
            wrapTemp.setAssociates(associate);
        }
        wrapTemp.setSuggest(suggestKey ? 1 : 0);
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
        param.setCurrency(new Currency());
        param.setMobile(false);
        param.setFactCategory(false);
        param.setFactPvid(false);
        param.setOrder(false);
        param.setPage(1);
        param.setPageSize(1);
        param.setFreeShipping(2);
        param.setUserType(1);
        param.setSynonym(StringUtils.isBlank(param.getCatid()));
        QueryResponse response = solrService.serach(param);
        SolrResult solrResult = searchItem(param,response);
        return solrResult.getRecordCount();
    }

    @Override
    public GoodsPriceRange searPriceRangeByKeyWord(SearchParam param) {
        GoodsPriceRange range = new GoodsPriceRange();
        range.setCatid(param.getCatid());
        param.setSynonym(StringUtils.isBlank(param.getCatid()));
        GoodsPriceRange response = solrService.searPriceRangeByKeyWord(param);
        if(response == null){
            return range;
        }
        response.setKeyword(null);
        response.setOtherkeyword(null);
        response.setCatid(param.getCatid());
        response.setState(0);
        //切换货币
        if(!"USD".equals(param.getCurrency())){
            ChangeCurrency.chang(response,param.getCurrency());
        }
        return response;
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
    public List<AssociateWrap> associate(String keyWord, SearchParam param) {
        String[] exhaust = exhaustUtils.combination(keyWord);
        if(exhaust == null) {
            return Lists.newArrayList();
        }
        List<AssociateWrap> result = Lists.newArrayList();
        AssociateWrap wrap = null;
        for(int i=0,length=exhaust.length;i<length&&result.size() <4;i++) {
            param.setKeyword(KeywordCorrect.getKeyWord(exhaust[i]));
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

            ///import提高moq start
            /*calculatePrice.raiseMoqSearchGoods(solrDocument,param.getSite());
            if((param.getSite() == 1 || param.getSite() == 2 )
                    && StringUtils.isNotBlank(StrUtils.object2Str(solrDocument.get("custom_range_price")))){
                calculatePrice.searchRangePrice(solrDocument);
            }*/
            if(param.getSite() == 1 || param.getSite() == 2 ){
                if(StringUtils.isNotBlank(
                        StrUtils.object2Str(solrDocument.get("custom_range_price")))){
                    solrDocument.setField(
                            "custom_range_price_free",
                            solrDocument.get("custom_range_price_free_new"));
                }else{
                    solrDocument.setField(
                            "custom_feeprice",
                            solrDocument.get("custom_free_price_new"));
                }
            }

            Product product = new Product();
            product.setId(itemId);

            //店铺id
            product.setShopId((String) solrDocument.get("custom_shop_id"));

            //zlw 2018/05/25 update 对标商品销量 Max(速卖通，1688销量) str
            String custom_ali_sold = StrUtils.object2NumStr(solrDocument.get("custom_ali_sold"));
            String custom_sold = StrUtils.object2NumStr(solrDocument.get("custom_sold"));
            String soldObject = String.valueOf(
                    Integer.parseInt(custom_ali_sold) + Integer.parseInt(custom_sold));
            product.setSold(soldObject);
            //zlw 2018/05/25 update 对标商品销量 Max(速卖通，1688销量) end

            String catid = StrUtils.object2Str(solrDocument.get("custom_path_catid"));
            String infoReviseFlag = StrUtils.object2Str(solrDocument.get("custom_infoReviseFlag"));
            //后台人为确定过得标题翻译，直接使用，不在使用自动翻译的标题
            String title = StrUtils.object2Str(solrDocument.get("custom_finalName"));
            if (StringUtils.isBlank(infoReviseFlag) || "0".equals(infoReviseFlag)
                    || StringUtils.isBlank(title)) {
                title = StrUtils.object2Str(solrDocument.get("custom_enname"));
                //拼接类别名称
                /*1688标题短，就用 速卖通标题 这个 逻辑 去掉改成 1688 标题短
                就 在标题里面 加上 这个产品的 类别名绝不能 直接用
                速卖通 产品名--2018-01-05*/
                title = splicingSyntax.categoryNameToTitle(title, catid);
            }
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
            String goods_url = UriCompose.pseudoStaticUrl(
                    itemId, product.getName(), catid1, catid2, 1);
            product.setUrl(goods_url.replaceAll("\\%", ""));

            //价格
            if(!productPrice(isFree, solrDocument, product,param.getSite())){
               continue;
            }
            //单位
            String unit = StrUtils.object2Str(solrDocument.get("custom_sellunit"));
            unit = StringUtils.isBlank(unit) ? "piece" : unit;
            String setSellUnits_ = StrUtils.matchStr(unit, "(\\(.*\\))");
            unit = StringUtils.isNotBlank(setSellUnits_) ?
                    unit.replace(setSellUnits_, "").trim() : unit;
            product.setPriceUnit(unit);
            product.setMoqUnit(unit);
            String goods_minOrder = StrUtils.object2Str(solrDocument.get("custom_morder"));
            goods_minOrder = StrUtils.isNum(goods_minOrder) ? goods_minOrder : "1";
            product.setMinOrder(goods_minOrder);
            if (StringUtils.isNotBlank(goods_minOrder) &&
                    !"1".equals(goods_minOrder) && !"pcs".equals(unit)) {
                product.setMoqUnit(unit + "s" + setSellUnits_);
            } else {
                product.setMoqUnit(unit + setSellUnits_);
            }

            //添加视频链接判断
            String custom_video_url = StrUtils.object2Str(solrDocument.get("custom_video_url"));
            if (StringUtils.isNotBlank(custom_video_url)) {
                product.setIsVideo(1);
            }
            //商品库存标识  0没有库存  1有库存  hot
            String stock = StrUtils.object2NumStr(solrDocument.get("custom_is_stock_flag"));
            product.setIsStock(Integer.parseInt(stock));

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
    private boolean productPrice(boolean isFree, SolrDocument solrDocument, Product searchGoods,int site) {
        String rangePrice = StrUtils.object2Str(solrDocument.get("custom_range_price"));
        if(StringUtils.isNotBlank(rangePrice)){
            if (isFree) {
                String rangePriceFree = StrUtils.object2Str(
                        solrDocument.get("custom_range_price_free"));
                if (StringUtils.isNotBlank(rangePriceFree)) {
                    rangePrice = rangePriceFree;
                } else {
                    String finalWeightTem = StrUtils.object2Str(solrDocument.get("custom_final_weight"));
                    rangePrice = modefilePrice.getRangePrice(rangePrice, 0, finalWeightTem, rangePrice);
                }
            }
            searchGoods.setPrice(rangePrice);
            return true;
        }
        String price = StrUtils.object2Str(solrDocument.get(solrService.getPriceField(site)));
        searchGoods.setPrice(price);

        //批量价格显示
        String wprice = StrUtils.object2Str(solrDocument.get("custom_wprice"));
        List<Price> modefideWholesalePrice = modefilePrice.modefideWholesalePrice(wprice);
        //非免邮第二个价格
        if(modefideWholesalePrice.size() > 1){
            searchGoods.setWholesaleMiddlePrice(modefideWholesalePrice.get(1).getPrice());
        }
        if(isFree){
            String custom_feeprice = StrUtils.object2Str(solrDocument.get("custom_feeprice"));
            modefideWholesalePrice = modefilePrice.modefideWholesalePrice(custom_feeprice) ;
        }
        if(modefideWholesalePrice.isEmpty()){
            return false;
        }
        if(modefideWholesalePrice.size() == 1){
            searchGoods.setWholesaleMiddlePrice(null);
        }else{
            searchGoods.setWholesalePrice(modefideWholesalePrice);
        }
        price = modefideWholesalePrice.get(modefideWholesalePrice.size() - 1).getPrice();
        if(modefideWholesalePrice.size() > 1){
            price = price + "-" + modefideWholesalePrice.get(0).getPrice();
        }
        searchGoods.setPrice(price);
        return true;
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
            //See more products in category
            String productsCate = categoryService.productsCate(categorys);
            wrap.setProductsCate(productsCate);

        }

        //属性
        if (param.isFactPvid()) {
            List<AttributeWrap> attributes = attributeService.attributes(param, solrResult.getAttrFacet());
            wrap.setAttributes(attributes);
            AttributeWrap selectedAttr = attributeService.selectedAttributes(param);
            wrap.setSelectedAttr(selectedAttr);
        }

        //分页
        long recordCount = solrResult.getRecordCount();
        PageWrap paging = pageService.paging(param, recordCount);
        wrap.setPage(paging);

        return wrap;
    }

    private boolean isDefault(SearchParam param){
        boolean isDefault = "default".equals(param.getSort()) && StringUtils.isBlank(param.getAttrId());
        isDefault = isDefault && (StringUtils.isBlank(param.getCatid())) && param.getPage() < 2;
        isDefault = isDefault && StringUtils.isBlank(param.getMinPrice()) && StringUtils.isBlank(param.getMaxPrice());
        return isDefault;
    }

}