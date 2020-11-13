package com.importexpress.search.rest;

import com.google.gson.Gson;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.common.ProductSearch;
import com.importexpress.search.common.VerifySearchParameter;
import com.importexpress.search.mongo.CatidGroup;
import com.importexpress.search.pojo.*;
import com.importexpress.search.service.CategoryService;
import com.importexpress.search.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jack.luo
 */
@RestController
@Slf4j
@RequestMapping("/search")
@Api(tags = "solr搜索调用接口")
public class SearchController {

    @Autowired
    private VerifySearchParameter verifySearchParameter;
    @Autowired
    private SearchService service;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductSearch productSearch;
    private Gson gson = new Gson();

    /**
     * 产品搜索
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/products")
    @ApiOperation("搜索")
    public CommonResult getSearch(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param = verifySearchParameter.verification(request, param);
            //请求solr获取结果
            SearchResultWrap wrap = service.productSerach(param);

            if (wrap == null) {
                return CommonResult.failed(" SOMETHING WRONG HAPPENED WHEN GET SOLR RESULT!");
            } else {
                Map<String, String> searchNavigation =
                        productSearch.searchNavigation(param);
                wrap.setSearchNavigation(searchNavigation);
                wrap.setParam(param);
                return CommonResult.success("GET SOLR RESULT SUCCESSED!", gson.toJson(wrap));
            }
        } catch (Exception e) {
            log.error("WRONG HAPPENED", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 店铺搜索
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/shop")
    @ApiOperation("店铺")
    public CommonResult getShop(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param = verifySearchParameter.shopParam(request, param);
            //请求solr获取结果
            SearchResultWrap wrap = service.shopSerach(param);
            if (wrap == null) {
                return CommonResult.failed(" WRONG HAPPENED WHEN GET SHOP RESULT!");
            } else {
                return CommonResult.success("GET SHOP PRODUCT SUCCESSED!", gson.toJson(wrap));
            }
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 数量
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/count")
    @ApiOperation("统计产品数量")
    public CommonResult count(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param = verifySearchParameter.verification(request, param);
            verifySearchParameter.setFalse(param);
            param.setPageSize(1);
            //请求solr获取结果
            long count = service.serachCount(param);
            return CommonResult.success("GET COUNT SUCCESSED!", count);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 类别统计
     *
     * @param param   搜索参数
     * @param request
     * @return
     */
    @ApiOperation("统计类别列表")
    @PostMapping("/category")
    public CommonResult categoryStatistics(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            param = verifySearchParameter.verification(request, param);
            param.setKeyword("*");
            param.setPage(1);
            param.setPageSize(1);
            verifySearchParameter.setFalse(param);

            List<FacetField> facetFields = service.groupCategory(param);

            List<CategoryWrap> categorys = categoryService.categorys(param, facetFields);

            categorys = categorys.stream()
                    .sorted(Comparator.comparing(CategoryWrap::getName))
                    .collect(Collectors.toList());
            return CommonResult.success("CATEGORY STATISTICS SUCCESSED", gson.toJson(categorys));
        } catch (Exception e) {
            log.error("错误--exception:", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 相似搜索
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/similar")
    @ApiOperation("相似搜索")
    public CommonResult similarProduct(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param = verifySearchParameter.verification(request, param);
            //请求solr获取结果
            List<Product> products = service.similarProduct(param);
            return CommonResult.success("GET SIMILAR PRODUCT SUCCESSED!", gson.toJson(products));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * guess you like
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/like")
    @ApiOperation("猜测客户喜欢")
    public CommonResult guessYouLike(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param = verifySearchParameter.verification(request, param);
            //请求solr获取结果
            List<Product> products = service.guessYouLike(param);
            return CommonResult.success("GET GUESS YOU LIKE PRODUCT SUCCESSED!", gson.toJson(products));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 根据已买过的产品推荐产品
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/bought")
    @ApiOperation("根据已买过的产品推荐产品")
    public CommonResult bought(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param = verifySearchParameter.verification(request, param);
            //请求solr获取结果
            List<Product> products = service.boughtAndBought(param);
            return CommonResult.success("GET PRODUCT SUCCESSED!", gson.toJson(products));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 新版购物车该产品没有购买过则根据名称查询推荐商品
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/boughtandbought")
    @ApiOperation("根据已买过的产品推荐产品")
    public CommonResult boughtAndBought(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param = verifySearchParameter.verification(request, param);
            //请求solr获取结果
            List<Product> products = service.boughtAndBought(param);

            if (products.size() < 4 && StringUtils.isNotBlank(param.getKeyword())) {
                String title = param.getKeyword();
                int length = title.split(" ").length;
                for (int i = 0; i < length && products.size() < 5; i++) {
                    String word = param.getKeyword();
                    if (!word.contains(" ")) {
                        break;
                    }
                    word = word.substring(0, word.lastIndexOf(" "));
                    param.setKeyword(word);
                    products = service.boughtAndBought(param);
                }
            }
            return CommonResult.success("GET PRODUCT SUCCESSED!", gson.toJson(products));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 展示该商品类别下的产品
     *
     * @param param   搜索参数
     * @param request
     * @return
     */
    @PostMapping("/bycatid")
    @ApiOperation("展示该产品类别下的其他产品")
    public CommonResult productsByCatid(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param = verifySearchParameter.verification(request, param);
            //请求solr获取结果
            List<Product> products = service.catidForGoods(param);
            return CommonResult.success("GET PRODUCT SUCCESSED!", gson.toJson(products));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 发生错误时推荐产品
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/recommend")
    @ApiOperation("发生错误时候推荐给用户产品")
    public CommonResult errorRecommend(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param = verifySearchParameter.verification(request, param);
            //请求solr获取结果
            List<Product> products = service.errorRecommend(param);
            return CommonResult.success("GET PRODUCT SUCCESSED!", gson.toJson(products));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 推荐热销产品
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/hot")
    @ApiOperation("推荐热销产品")
    public CommonResult hotProduct(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param = verifySearchParameter.verification(request, param);
            //请求solr获取结果
            List<Product> products = service.hotProduct(param);
            return CommonResult.success("GET PRODUCT SUCCESSED!", gson.toJson(products));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 根据类别推荐热销产品
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/hotbycatid")
    @ApiOperation("根据类别推荐热销产品")
    public CommonResult hotProductForCatid(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param = verifySearchParameter.verification(request, param);
            //请求solr获取结果
            List<Product> products = service.hotProductForCatid(param);
            return CommonResult.success("GET PRODUCT SUCCESSED!", gson.toJson(products));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 获取搜索词提示词列表
     *
     * @param request
     * @param keyWord 搜索词
     * @param site    网站
     * @return
     */
    @PostMapping("/auto")
    @ApiOperation("获取搜索词提示词列表")
    public CommonResult searchAutocomplete(
            @ApiParam(name = "keyWord", value = "搜索词", required = true) @RequestParam String keyWord,
            @ApiParam(name = "site", value = "网站", required = true) @RequestParam String site,
            HttpServletRequest request) {
        if (StringUtils.isBlank(keyWord)) {
            return CommonResult.failed(" Keyword IS NULL!");
        }
        site = StrUtils.isNum(site) ? site : "1";
        int _site = Integer.parseInt(site);
        if ((_site & -_site) != _site) {
            return CommonResult.failed(" SITE IS WRONG!");
        }
        try {
            List<String> lstAuto = service.searchAutocomplete(keyWord, _site);
            return CommonResult.success("GET AUTOCOMPLETE SUCCESSED!", gson.toJson(lstAuto));
        } catch (Exception e) {
            log.error("搜索提示词错误", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 统计价格区间分布
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/range")
    @ApiOperation("统计价格区间分布")
    public CommonResult loadRangePrice(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        try {
            GoodsPriceRangeWrap wrap = new GoodsPriceRangeWrap();
            param = verifySearchParameter.verification(request, param);
            String minprice = param.getMinPrice();
            String maxprice = param.getMaxPrice();
            wrap.setMinPrice(minprice);
            wrap.setMaxPrice(maxprice);
            if (param.isRange()) {
                wrap.setMinPrice("min".equals(minprice) || StringUtils.isBlank(minprice) ? "" : minprice);
                wrap.setMaxPrice("max".equals(maxprice) || StringUtils.isBlank(maxprice) ? "" : maxprice);
                wrap.setBackDiv(param.getSelectedInterval());
            }
            param.setMinPrice("");
            param.setMaxPrice("");
            if (StringUtils.isBlank(param.getKeyword()) ||
                    ("*".equals(param.getKeyword()) && !"0".equals(param.getCatid()))) {
                //当根据关键字来的,但是被屏蔽了
                param.setKeyword("*");
            }

            GoodsPriceRange goodsPriceRange = service.searPriceRangeByKeyWord(param);
            wrap.setParam(param);
            wrap.setRange(goodsPriceRange);
            if (goodsPriceRange != null) {
                goodsPriceRange.setKeyword(param.getKeyword());
                int totalNumber = goodsPriceRange.getSectionOneCount()
                        + goodsPriceRange.getSectionTwoCount()
                        + goodsPriceRange.getSectionThreeCount()
                        + goodsPriceRange.getSectionFourCount();
                wrap.setTotal(totalNumber);
            } else {
                wrap.setTotal(0);
            }
            return CommonResult.success("GET AUTOCOMPLETE SUCCESSED!", gson.toJson(wrap));
        } catch (Exception e) {
            log.error("搜索页异步加载区间价错误", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 获取搜索词其他组合推荐
     *
     * @param keyWord 搜索词
     * @param site    网站
     * @param request
     * @return
     */
    @PostMapping("/associate")
    @ApiOperation("获取搜索词其他组合推荐")
    public CommonResult associateKey(
            @ApiParam(name = "keyWord", value = "搜索词", required = true) @RequestParam String keyWord,
            @ApiParam(name = "site", value = "网站", required = true) @RequestParam String site,
            @ApiParam(name = "salable", value = "是否开启不可售限制", required = true) @RequestParam String salable,
            HttpServletRequest request) {
        if (StringUtils.isBlank(keyWord)) {
            return CommonResult.failed(" Keyword IS NULL!");
        }
        site = StrUtils.isNum(site) ? site : "1";
        int _site = Integer.parseInt(site);
        if ((_site & -_site) != _site) {
            return CommonResult.failed(" SITE IS WRONG!");
        }
        try {
            SearchParam param = new SearchParam();
            param.setSite(_site);
            param.setSalable("true".equalsIgnoreCase(salable));
            List<AssociateWrap> associate = service.associate(keyWord, param);
            return CommonResult.success("GET ASSOCIATE KEY SUCCESSED!", gson.toJson(associate));
        } catch (Exception e) {
            log.error("获取搜索词其他组合推荐错误", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 异步加载搜索页类别推荐搜索词
     *
     * @param keyWord 搜索词
     * @param site    网站
     * @param request
     * @return
     */
    @PostMapping("/catid/suggest")
    @ApiOperation("异步加载搜索页类别推荐搜索词")
    public CommonResult catidSuggest(
            @ApiParam(name = "keyWord", value = "搜索词", required = true) @RequestParam String keyWord,
            @ApiParam(name = "site", value = "网站", required = true) @RequestParam String site,
            HttpServletRequest request) {
        if (StringUtils.isBlank(keyWord)) {
            return CommonResult.failed(" Keyword IS NULL!");
        }
        site = StrUtils.isNum(site) ? site : "1";
        int _site = Integer.parseInt(site);
        if ((_site & -_site) != _site) {
            return CommonResult.failed(" SITE IS WRONG!");
        }
        try {
            List<SearchWordWrap> list = service.searchWord(keyWord, _site);
            return CommonResult.success("GET SUGGEST KEY SUCCESSED!", gson.toJson(list));
        } catch (Exception e) {
            log.error("异步加载搜索页类别推荐搜索词", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 广告落地页
     *
     * @param keyWord   搜索词
     * @param site      网站
     * @param adgroupid 广告词
     * @param request
     * @return
     */
    @PostMapping("advertisement")
    @ApiOperation("广告落地页")
    public CommonResult advertisement(
            @ApiParam(name = "keyWord", value = "搜索词", required = true) @RequestParam String keyWord,
            @ApiParam(name = "site", value = "网站", required = true) @RequestParam String site,
            @ApiParam(name = "adgroupid", value = "广告词", required = true) @RequestParam String adgroupid,
            HttpServletRequest request) {
        if (StringUtils.isBlank(keyWord)) {
            return CommonResult.failed(" Keyword IS NULL!");
        }
        site = StrUtils.isNum(site) ? site : "1";
        int _site = Integer.parseInt(site);
        if ((_site & -_site) != _site) {
            return CommonResult.failed(" SITE IS WRONG!");
        }
        try {
            SearchResultWrap advertisement = service.advertisement(keyWord, _site, adgroupid);
            return CommonResult.success("GET ADVERTISEMENT KEY SUCCESSED!", gson.toJson(advertisement));
        } catch (Exception e) {
            log.error("广告落地页", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 产品搜索
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/productsB2C")
    @ApiOperation("搜索")
    public CommonResult getSearchB2C(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            SearchResultWrap wrap = new SearchResultWrap();
            //参数处理
            param = verifySearchParameter.verification(request, param);
            if(1 == param.getSite()){
                wrap = service.productSerachMongoImport(param);
            }
            else{
                //请求mongo获取结果
                wrap = service.productSerachMongo(param);
            }


            if (wrap == null) {
                return CommonResult.failed(" SOMETHING WRONG HAPPENED WHEN GET SOLR RESULT!");
            } else {
                Map<String, String> searchNavigation =
                        productSearch.searchNavigation(param);
                wrap.setSearchNavigation(searchNavigation);
                wrap.setParam(param);
                return CommonResult.success("GET SOLR RESULT SUCCESSED!", gson.toJson(wrap));
            }
        } catch (Exception e) {
            log.error("WRONG HAPPENED", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 产品搜索
     *
     * @return
     */
    @PostMapping("/catidGroup")
    @ApiOperation("搜索catid分组商品数量")
    public CommonResult getCatidGroup(@ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
                                      HttpServletRequest request) {

        try {

            //参数处理
            param = verifySearchParameter.verification(request, param);
            List<CatidGroup> catidGroupList = new ArrayList<>();
            if(1 == param.getSite()){
                catidGroupList = service.getCatidGroupImport(param.getSite());
            }
            else{
                catidGroupList = service.getCatidGroup(param.getSite());
            }


            if (catidGroupList == null) {
                return CommonResult.failed(" SOMETHING WRONG HAPPENED WHEN GET SOLR RESULT!");
            } else {

                return CommonResult.success("GET SOLR RESULT SUCCESSED!", gson.toJson(catidGroupList));
            }
        } catch (Exception e) {
            log.error("WRONG HAPPENED", e);
            return CommonResult.failed(e.getMessage());
        }
    }


    /**
     * 产品搜索
     *
     * @param request
     * @param param   搜索参数
     * @return
     */
    @PostMapping("/productsB2CImport")
    @ApiOperation("搜索")
    public CommonResult productsB2CImport(
            @ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        if (param == null) {
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param = verifySearchParameter.verification(request, param);
            //请求mongo获取结果
            SearchResultWrap wrap = service.productSerachMongoImport(param);

            if (wrap == null) {
                return CommonResult.failed(" SOMETHING WRONG HAPPENED WHEN GET SOLR RESULT!");
            } else {
                Map<String, String> searchNavigation =
                        productSearch.searchNavigation(param);
                wrap.setSearchNavigation(searchNavigation);
                wrap.setParam(param);
                return CommonResult.success("GET SOLR RESULT SUCCESSED!", gson.toJson(wrap));
            }
        } catch (Exception e) {
            log.error("WRONG HAPPENED", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 产品搜索
     *
     * @return
     */
    @PostMapping("/catidGroupImport")
    @ApiOperation("搜索catid分组商品数量")
    public CommonResult getCatidGroupImport(@ApiParam(name = "searchParam", value = "搜索参数", required = true) @RequestBody SearchParam param,
                                      HttpServletRequest request) {

        try {

            //参数处理
            param = verifySearchParameter.verification(request, param);

            List<CatidGroup> catidGroupList = service.getCatidGroupImport(param.getSite());

            if (catidGroupList == null) {
                return CommonResult.failed(" SOMETHING WRONG HAPPENED WHEN GET SOLR RESULT!");
            } else {

                return CommonResult.success("GET SOLR RESULT SUCCESSED!", gson.toJson(catidGroupList));
            }
        } catch (Exception e) {
            log.error("WRONG HAPPENED", e);
            return CommonResult.failed(e.getMessage());
        }
    }



}
