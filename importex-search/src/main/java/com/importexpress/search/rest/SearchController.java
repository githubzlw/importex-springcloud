package com.importexpress.search.rest;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.common.VerifySearchParameter;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
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

    /**产品搜索
     * @param request
     * @param param
     * @return
     */
    @PostMapping("/products")
    @ApiOperation("搜索")
    public CommonResult getSearch(
            @ApiParam(name="searchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
                                  HttpServletRequest request) {
        if(param == null){
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param  = verifySearchParameter.verification(request,param);
            //请求solr获取结果
            SearchResultWrap wrap = service.productSerach(param);
            if(wrap == null){
                return CommonResult.failed(" SOMETHING WRONG HAPPENED WHEN GET SOLR RESULT!");
            }else{
                return CommonResult.success("GET SOLR RESULT SUCCESSED!",JSONObject.toJSONString(wrap));
            }
        }catch (Exception e){
            log.error("WRONG HAPPENED",e);
            return  CommonResult.failed(e.getMessage());
        }
    }

    /**店铺搜索
     * @param request
     * @param param
     * @return
     */
    @PostMapping("/shop")
    @ApiOperation("店铺")
    public CommonResult getShop(
            @ApiParam(name="searchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
                                HttpServletRequest request) {
        if(param == null){
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
           param  = verifySearchParameter.shopParam(request,param);
            //请求solr获取结果
            SearchResultWrap wrap = service.shopSerach(param);
            if(wrap == null){
                return CommonResult.failed(" WRONG HAPPENED WHEN GET SHOP RESULT!");
            }else{
                return CommonResult.success("GET SHOP PRODUCT SUCCESSED!",JSONObject.toJSONString(wrap));
            }
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    /**数量
     * @param request
     * @param param
     * @return
     */
    @PostMapping("/count")
    @ApiOperation("统计产品数量")
    public CommonResult count(
            @ApiParam(name="searchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
                              HttpServletRequest request) {
        if(param == null){
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param  = verifySearchParameter.verification(request,param);
            verifySearchParameter.setFalse(param);
            param.setPageSize(1);
            //请求solr获取结果
            long count = service.serachCount(param);
            return CommonResult.success("GET COUNT SUCCESSED!",count);
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }
    /**类别统计
     * @param param
     * @param request
     * @return
     */
    @ApiOperation("统计类别列表")
    @PostMapping("/category")
    public CommonResult categoryStatistics(
            @ApiParam(name="searchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
                                           HttpServletRequest request) {
        if(param == null){
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            param  = verifySearchParameter.verification(request,param);
            param.setKeyword("*");
            param.setPage(1);
            param.setPageSize(1);
            verifySearchParameter.setFalse(param);

            List<FacetField> facetFields = service.groupCategory(param);

            List<CategoryWrap> categorys = categoryService.categorys(param, facetFields);

            categorys = categorys.stream()
                                 .sorted(Comparator.comparing(CategoryWrap::getName))
                                 .collect(Collectors.toList());
            return CommonResult.success("CATEGORY STATISTICS SUCCESSED",JSONObject.toJSONString(categorys));
        } catch (Exception e) {
            log.error("错误--exception:",e);
            return CommonResult.failed(e.getMessage());
        }
    }
    /**相似搜索
     * @param request
     * @param param
     * @return
     */
    @PostMapping("/similar")
    @ApiOperation("相似搜索")
    public CommonResult similarProduct(
            @ApiParam(name="searchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
                                       HttpServletRequest request) {
        if(param == null){
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param  = verifySearchParameter.verification(request,param);
            //请求solr获取结果
            List<Product> products = service.similarProduct(param);
            return CommonResult.success("GET SIMILAR PRODUCT SUCCESSED!",JSONObject.toJSONString(products));
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }
    /** guess you like
     * @param request
     * @param param
     * @return
     */
    @PostMapping("/like")
    @ApiOperation("猜测客户喜欢")
    public CommonResult guessYouLike(
            @ApiParam(name="searchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
                                     HttpServletRequest request) {
        if(param == null){
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param  = verifySearchParameter.verification(request,param);
            //请求solr获取结果
            List<Product> products = service.guessYouLike(param);
            return CommonResult.success("GET GUESS YOU LIKE PRODUCT SUCCESSED!",JSONObject.toJSONString(products));
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }
    /**新版购物车该产品没有购买过则根据名称查询推荐商品
     * @param request
     * @param param
     * @return
     */
    @PostMapping("/bought")
    @ApiOperation("根据已买过的产品推荐产品")
    public CommonResult boughtAndBought(
            @ApiParam(name="searchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
                                        HttpServletRequest request) {
        if(param == null){
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param  = verifySearchParameter.verification(request,param);
            //请求solr获取结果
            List<Product> products = service.boughtAndBought(param);
            return CommonResult.success("GET PRODUCT SUCCESSED!",JSONObject.toJSONString(products));
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    /**展示该商品类别下的产品
     * @param param
     * @param request
     * @return
     */
    @PostMapping("/bycatid")
    @ApiOperation("展示该产品类别下的其他产品")
    public CommonResult productsByCatid(
            @ApiParam(name="searchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
                                      HttpServletRequest request) {
        if(param == null){
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param  = verifySearchParameter.verification(request,param);
            //请求solr获取结果
            List<Product> products = service.catidForGoods(param);
            return CommonResult.success("GET PRODUCT SUCCESSED!",JSONObject.toJSONString(products));
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    /**发生错误时推荐产品
     * @param request
     * @param param
     * @return
     */
    @PostMapping("/recommend")
    @ApiOperation("发生错误时候推荐给用户产品")
    public CommonResult errorRecommend(
            @ApiParam(name="searchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
                                       HttpServletRequest request) {
        if(param == null){
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param  = verifySearchParameter.verification(request,param);
            //请求solr获取结果
            List<Product> products = service.errorRecommend(param);
            return CommonResult.success("GET PRODUCT SUCCESSED!",JSONObject.toJSONString(products));
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }
    @PostMapping("/hot")
    @ApiOperation("推荐热销产品")
    public CommonResult hotProduct(
            @ApiParam(name="searchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
                                   HttpServletRequest request) {
        if(param == null){
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param  = verifySearchParameter.verification(request,param);
            //请求solr获取结果
            List<Product> products = service.hotProduct(param);
            return CommonResult.success("GET PRODUCT SUCCESSED!",JSONObject.toJSONString(products));
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }
    @PostMapping("/hotbycatid")
    @ApiOperation("根据类别推荐热销产品")
    public CommonResult hotProductForCatid(
            @ApiParam(name="searchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
                                           HttpServletRequest request) {
        if(param == null){
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param  = verifySearchParameter.verification(request,param);
            //请求solr获取结果
            List<Product> products = service.hotProductForCatid(param);
            return CommonResult.success("GET PRODUCT SUCCESSED!",JSONObject.toJSONString(products));
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }
    @PostMapping("/auto")
    @ApiOperation("获取搜索词提示词列表")
    public CommonResult searchAutocomplete(
            @ApiParam(name="keyWord",value="搜索词",required=true) String keyWord,
            @ApiParam(name="site",value="网站",required=true) String site,
                                           HttpServletRequest request) {
        if(StringUtils.isBlank(keyWord)){
            return CommonResult.failed(" Keyword IS NULL!");
        }
        site = StrUtils.isNum(site) ? site : "1";
        int _site = Integer.parseInt(site);
        if((_site & -_site) != _site){
            return CommonResult.failed(" SITE IS WRONG!");
        }
        try {
            List<String> lstAuto = service.searchAutocomplete(keyWord, _site);
            return CommonResult.success("GET AUTOCOMPLETE SUCCESSED!",JSONObject.toJSONString(lstAuto));
        }catch (Exception e){
            log.error("搜索提示词错误",e);
            return CommonResult.failed(e.getMessage());
        }
    }
    @PostMapping("/range")
    @ApiOperation("统计价格区间分布")
    public CommonResult loadRangePrice(
            @ApiParam(name="searchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
            HttpServletRequest request) {
        try {
            GoodsPriceRangeWrap wrap = new GoodsPriceRangeWrap();
            param = verifySearchParameter.verification(request,param);
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
            return CommonResult.success("GET AUTOCOMPLETE SUCCESSED!",JSONObject.toJSONString(wrap));
        }catch (Exception e){
            log.error("搜索页异步加载区间价错误",e);
            return CommonResult.failed(e.getMessage());
        }
    }
    @PostMapping("/associate")
    @ApiOperation("获取搜索词其他组合推荐")
    public CommonResult associateKey(
            @ApiParam(name="keyWord",value="搜索词",required=true) String keyWord,
            @ApiParam(name="site",value="网站",required=true) String site,
            HttpServletRequest request) {
        if(StringUtils.isBlank(keyWord)){
            return CommonResult.failed(" Keyword IS NULL!");
        }
        site = StrUtils.isNum(site) ? site : "1";
        int _site = Integer.parseInt(site);
        if((_site & -_site) != _site){
            return CommonResult.failed(" SITE IS WRONG!");
        }
        try {
            verifySearchParameter.initApplication(request);
            List<AssociateWrap> associate = service.associate(keyWord, _site);
            return CommonResult.success("GET ASSOCIATE KEY SUCCESSED!",JSONObject.toJSONString(associate));
        }catch (Exception e){
            log.error("获取搜索词其他组合推荐错误",e);
            return CommonResult.failed(e.getMessage());
        }
    }
}
