package com.importexpress.search.rest;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.common.InitApplicationParameter;
import com.importexpress.search.common.VerifySearchParameter;
import com.importexpress.search.pojo.CategoryWrap;
import com.importexpress.search.pojo.Product;
import com.importexpress.search.pojo.SearchParam;
import com.importexpress.search.pojo.SearchResultWrap;
import com.importexpress.search.service.CategoryService;
import com.importexpress.search.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.response.FacetField;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luohao
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
    @PostMapping("/product")
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
            @ApiParam(name="SearchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
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
            @ApiParam(name="SearchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
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
            @ApiParam(name="SearchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
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
            @ApiParam(name="SearchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
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
            @ApiParam(name="SearchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
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
            @ApiParam(name="SearchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
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
    @PostMapping("/catIdForGoods")
    @ApiOperation("")
    public CommonResult catIdForGoods(
            @ApiParam(name="SearchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
                                      HttpServletRequest request) {
        if(param == null){
            return CommonResult.failed(" SearchParam IS NULL!");
        }
        try {
            //参数处理
            param  = verifySearchParameter.verification(request,param);
            //请求solr获取结果
            List<Product> products = service.catIdForGoods(param);
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
            @ApiParam(name="SearchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
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
            @ApiParam(name="SearchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
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
            @ApiParam(name="SearchParam",value="搜索参数",required=true) @RequestBody SearchParam param,
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
}
