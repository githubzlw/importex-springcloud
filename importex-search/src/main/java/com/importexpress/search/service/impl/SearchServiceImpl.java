package com.importexpress.search.service.impl;


import com.google.common.collect.Lists;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.common.*;
import com.importexpress.search.pojo.*;
import com.importexpress.search.service.*;
import com.importexpress.search.service.base.SolrBase;
import com.importexpress.search.util.Config;
import com.importexpress.search.util.DoubleUtil;
import com.importexpress.search.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author luohao
 * @date 2019/11/22
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {
//    private String chineseChar = "([\\一-\\龥]+)";
    @Autowired
    private ModefilePrice modefilePrice;
    @Autowired
    private SolrOperationUtils solrOperationUtils;
    @Autowired
    private PageService pageService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private SolrService solrService;


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
    public List<Product> catIdForGoods(SearchParam param) {
        List<Product> list = Lists.newArrayList();
        QueryResponse response = solrService.catIdForGoods(param);
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
            list = list.stream().filter(e -> StrUtils.isMatch(e.getPrice(),"(\\d+(\\.\\d+){0,1})"))
                    .collect(Collectors.toList());
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

    public List<FacetField> groupCategory(SearchParam param) {
        QueryResponse response = solrService.groupCategory(param);
        //取分组统计列表
        return response == null ? Lists.newArrayList() : response.getFacetFields();
    }


    @Override
    public SearchResultWrap productSerach(SearchParam param) {
        SearchResultWrap wrap = new SearchResultWrap();
        Page page = new Page();
        page.setRecordCount(0L);
        wrap.setPage(page);
        String queryString = param.getKeyword();
        if (StringUtils.isBlank(queryString)) {
            return wrap;
        }
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
        wrap = compose(solrResult, param);

        return wrap;
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
        QueryResponse response = solrService.serach(param);
        SolrResult solrResult = searchItem(param,response);
        return solrResult.getRecordCount();
    }

    @Override
    public GoodsPriceRange searPriceRangeByKeyWord(SearchParam param) {
        QueryResponse response = solrService.searPriceRangeByKeyWord(param);
        if(response != null){

        }
        return null;
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
        return suggest;
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
            Product product = new Product();
            product.setId(itemId);

            //店铺id
            product.setShopId((String) solrDocument.get("custom_shop_id"));

            //zlw 2018/05/25 update 对标商品销量 Max(速卖通，1688销量) str
            String custom_ali_sold = StrUtils.object2NumStr(solrDocument.get("custom_ali_sold"));
            String custom_sold = StrUtils.object2NumStr(solrDocument.get("custom_sold"));
            String soldObject = String.valueOf(Integer.parseInt(custom_ali_sold) + Integer.parseInt(custom_sold));
            //zlw 2018/05/25 update 对标商品销量 Max(速卖通，1688销量) end
            product.setSolder(StrUtils.isNum(soldObject) ? soldObject : "0");

            String unit = StrUtils.object2Str(solrDocument.get("custom_sellunit"));
            unit = StringUtils.isBlank(unit) ? "piece" : unit;
            String setSellUnits_ = StrUtils.matchStr(unit, "(\\(.*\\))");
            unit = StringUtils.isNotBlank(setSellUnits_) ? unit.replace(setSellUnits_, "").trim() : unit;
            product.setPriceUnit(unit);
            product.setMoqUnit(unit);
            String goods_minOrder = StrUtils.object2Str(solrDocument.get("custom_morder"));
            product.setMinOrder(StrUtils.isNum(goods_minOrder) ? goods_minOrder : "1");

            String title = StrUtils.object2Str(solrDocument.get("custom_enname"));
                   // .replaceAll(chineseChar, "").replace("?", " ");
            product.setName(title);
            //如果翻译标题太短直接使用ali标题
        /*1688标题短，就用 速卖通标题 这个 逻辑 去掉改成 1688 标题短 就 在标题里面 加上 这个产品的 类别名绝不能 直接用
        速卖通 产品名--2018-01-05*/
            String catid = StrUtils.object2Str(solrDocument.get("custom_path_catid"));
            String catid1 = "0";
            String catid2 = "0";
            if (StringUtils.isNotBlank(catid) && catid.indexOf(" ") > -1) {
                String[] catids = catid.split(" ");
                catid1 = catids[0];
                catid2 = catids[1];
            }

            //拼接类别名称
            title = solrOperationUtils.categoryNameToTitle(title, catid);
            product.setName(title);

            //后台人为确定过得标题翻译，直接使用，不在使用自动翻译的标题
            String infoReviseFlag = StrUtils.object2Str(solrDocument.get("custom_infoReviseFlag"));
            if (StringUtils.isNotBlank(infoReviseFlag) && !"0".equals(infoReviseFlag)
                    && StringUtils.isNotBlank(StrUtils.object2Str(solrDocument.get("custom_finalName")))) {
                product.setName(StrUtils.object2Str(solrDocument.get("custom_finalName")));
            }
            //产品名称首字母大写
            product.setName(NameCorrect.upperCaseProductName(product.getName()));

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
            path = path.replace("http://", "https://");
            product.setImage(path.replace("220x220", "285x285"));

            /**
             * 添加伪静态化链接
             */
            String goods_url = UriCompose.pseudoStaticUrl(itemId, product.getName(), catid1, catid2, 1);
            product.setUrl(goods_url.replaceAll("\\%", ""));

            String s = "1";
            //价格
            productPrice(isFree, solrDocument, product);

            s = isFree ? "2" : "0";
            //单位
            goods_minOrder = product.getMinOrder();
            if (StringUtils.isNotBlank(goods_minOrder) && !s.equals(goods_minOrder) && !"pcs".equals(unit)) {
                product.setMoqUnit(unit + "s" + setSellUnits_);
            } else {
                product.setMoqUnit(unit + setSellUnits_);
            }

            //添加视频链接判断
            String custom_video_url = StrUtils.object2Str(solrDocument.get("custom_video_url"));
            if (StringUtils.isNotBlank(custom_video_url)) {
                product.setIsVideo(1);
            }

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
            wrap.setAttributeWraps(attributes);
            List<Attribute> selectedAttr = attributeService.selectedAttributes(param);
            wrap.setSelectedAttr(selectedAttr);
        }

        //分页
        long recordCount = solrResult.getRecordCount();
        Page paging = pageService.paging(param, recordCount);
        wrap.setPage(paging);
        return wrap;
    }

}