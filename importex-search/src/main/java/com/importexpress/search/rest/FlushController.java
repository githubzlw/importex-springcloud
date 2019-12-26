package com.importexpress.search.rest;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.search.common.InitApplicationParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/**
 * @author jack.luo
 */
@RestController
@Slf4j
@RequestMapping("/flush")
@Api(tags = "solr搜索调用接口")
public class FlushController {
    @Autowired
    private InitApplicationParameter init;

    /**全部1688Category的数据导入
     * @param request
     */
    @GetMapping("/category")
    @ApiOperation("全部1688Category的数据导入")
    public CommonResult category(HttpServletRequest request){
        init.category(request.getServletContext());
        return CommonResult.success("刷新成功");
    }

    /**获取规格属性表信息
     * @param request
     */
    @ApiOperation("获取规格属性表信息")
    @GetMapping("/atrrid")
    public CommonResult newPvid(HttpServletRequest request){
        init.newPvid(request.getServletContext());
        return CommonResult.success("刷新成功");

    }

    /**初始化同义词列表
     * @param request
     */
    @ApiOperation("初始化同义词列表")
    @GetMapping("/synonyms")
    public CommonResult synonyms(HttpServletRequest request){
        init.synonyms(request.getServletContext());
        return CommonResult.success("刷新成功");
    }
    /**初始化类别同义词列表
     * @param request
     */
    @ApiOperation("初始化类别同义词列表")
    @GetMapping("/synonyms/category")
    public CommonResult synonymsCategory(HttpServletRequest request){
        init.synonymsCategory(request.getServletContext());
        return CommonResult.success("刷新成功");
    }

    /**初始化搜索词对应的最低价和最高价
     * @param request
     */
    @ApiOperation("初始化搜索词对应的最低价和最高价")
    @GetMapping("/category/price")
    public CommonResult categoryPrice(HttpServletRequest request){
        init.categoryPrice(request.getServletContext());
        return CommonResult.success("刷新成功");
    }

    /**初始化盲搜类别列表
     * @param request
     */
    @ApiOperation("初始化盲搜类别列表")
    @GetMapping("/blind/category")
    public CommonResult blindSearchCategory(HttpServletRequest request){
        init.blindSearchCategory(request.getServletContext());
        return CommonResult.success("刷新成功");
    }

    /**初始化类别限制列表
     * @param request
     */
    @ApiOperation("初始化类别限制列表")
    @GetMapping("/special/category")
    public CommonResult specialCatid(HttpServletRequest request){
        init.specialCatid(request.getServletContext());
        return CommonResult.success("刷新成功");
    }

    /**初始化优先类别列表
     * @param request
     */
    @ApiOperation("初始化优先类别列表")
    @GetMapping("/priority/category")
    public CommonResult priorityCategory(HttpServletRequest request){
        init.priorityCategory(request.getServletContext());
        return CommonResult.success("刷新成功");
    }

    /**初始化反关键词集合
     * @param request
     */
    @ApiOperation("初始化反关键词集合")
    @GetMapping("/autikey")
    public CommonResult autiKey(HttpServletRequest request){
        init.autiKey(request.getServletContext());
        return CommonResult.success("刷新成功");
    }

    /**获取搜索页面底部推荐词 whj
     * @param request
     */
    @ApiOperation("搜索页面底部推荐词")
    @GetMapping("/recommen")
    public CommonResult recommendedWords(HttpServletRequest request){
       init.recommendedWords(request.getServletContext());
       return CommonResult.success("刷新成功");
    }

}
