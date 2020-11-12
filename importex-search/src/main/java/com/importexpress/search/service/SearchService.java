package com.importexpress.search.service;

import com.importexpress.search.mongo.CatidGroup;
import com.importexpress.search.pojo.*;
import org.apache.solr.client.solrj.response.FacetField;

import java.util.List;


/**
 *
 */
public interface SearchService {
    /**
     * 获取类别统计
     *
     * @return
     * @date 2017年2月13日
     * @author abc
     */
    List<FacetField> groupCategory(SearchParam param);

    /**
     * 请求solr获取搜索
     *
     * @param param 搜索参数
     * @return
     */
    SearchResultWrap productSerach(SearchParam param);


    /**
     * 请求solr获取店铺搜索
     *
     * @param param 搜索参数
     * @return
     */
    SearchResultWrap shopSerach(SearchParam param);

    /**
     * 请求solr获取搜索产品数
     *
     * @param param 搜索参数
     * @return
     */
    long serachCount(SearchParam param);


    /**
     * 产品单页侧边展示相似商品
     *
     * @param param 搜索参数
     * @return
     */
    List<Product> similarProduct(SearchParam param);

    /**
     * 首页guess you like查询热销商品从solr
     *
     * @param param
     * @return
     */
    List<Product> guessYouLike(SearchParam param);

    /**
     * 新版购物车该产品没有购买过则根据名称查询推荐商品
     *
     * @param param 搜索参数
     * @return
     */
    List<Product> boughtAndBought(SearchParam param);

    /**
     * 新购物车页面展示该商品类别下的产品
     *
     * @param param
     * @return
     */
    List<Product> catidForGoods(SearchParam param);

    /**
     * 404页面推荐商品查询
     *
     * @param param 搜索参数
     * @return
     */
    List<Product> errorRecommend(SearchParam param);

    /**
     * 当搜索词没有搜索结果且也没有搜索词热卖商品时，取搜索词频次高的搜索词结果当做热销商品展示
     *
     * @param param 搜索参数
     * @return
     * @author 王宏杰
     */
    List<Product> hotProduct(SearchParam param);

    /**
     * 搜索页类别搜索时查询热销商品
     *
     * @param param
     * @return
     */
    List<Product> hotProductForCatid(SearchParam param);

    /**
     * 对搜索数据进行分区间统计 -- yyl
     *
     * @throws Exception
     */
    GoodsPriceRange searPriceRangeByKeyWord(SearchParam param);

    /**
     * 自动补全搜索词
     *
     * @param keyWord 搜索词
     * @return List<String>
     * 王宏杰  2018-04-26
     * @Title searchAutocomplete
     * @Description TODO
     */
    List<String> searchAutocomplete(String keyWord, int site);


    /**
     * 搜索词联想推荐
     * 若搜索商品数量不足10个，推荐AB//ABCD及以上 减少一个单词-》 ABC//搜索ABC三个词没有结果，
     * 就要推荐AB或者AC或者BC(直接显示或者推荐都行)
     *
     * @param keyWord
     * @param param
     * @return
     */
    List<AssociateWrap> associate(String keyWord, SearchParam param);

    /**
     * 异步加载搜索页类别推荐搜索词
     *
     * @param keyword
     * @param site
     * @return
     */
    List<SearchWordWrap> searchWord(String keyword, int site);

    /**
     * 广告落地页
     *
     * @param keyword
     * @param site
     * @param adgroupid
     * @return
     */
    SearchResultWrap advertisement(String keyword, int site, String adgroupid);

    /**
     * 请求Mongo获取搜索
     *
     * @param param 搜索参数
     * @return
     */
    SearchResultWrap productSerachMongo(SearchParam param);

    /**
     * 请求Mongo获取catidGroup
     *
     * @return
     */
    List<CatidGroup> getCatidGroup(int site);

    /**
     * 请求Mongo获取搜索
     *
     * @param param 搜索参数
     * @return
     */
    SearchResultWrap productSerachMongoImport(SearchParam param);

    /**
     * 请求Mongo获取catidGroup
     *
     * @return
     */
    List<CatidGroup> getCatidGroupImport(int site);


}
