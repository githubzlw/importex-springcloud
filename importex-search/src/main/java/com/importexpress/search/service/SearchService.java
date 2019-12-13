package com.importexpress.search.service;

import com.importexpress.search.pojo.GoodsPriceRange;
import com.importexpress.search.pojo.Product;
import com.importexpress.search.pojo.SearchParam;
import com.importexpress.search.pojo.SearchResultWrap;
import org.apache.solr.client.solrj.response.FacetField;

import java.util.List;

/**
 * @author luohao
 * @date 2019/11/22
 */
public interface SearchService {
    /**获取类别统计
     * @date 2017年2月13日
     * @author abc
     * @return
     * @throws Exception
     */
    List<FacetField> groupCategory(SearchParam param);

    /**请求solr获取搜索
     * @param param
     * @return
     */
    SearchResultWrap productSerach(SearchParam param);


    /**请求solr获取店铺搜索
     * @param param
     * @return
     */
    SearchResultWrap shopSerach(SearchParam param);
    /**请求solr获取搜索产品数
     * @param param
     * @return
     */
    long serachCount(SearchParam param);


    /**
     *产品单页侧边展示相似商品
     * @param param
     * @return
     */
    List<Product> similarProduct(SearchParam param);
    /**
     * 首页guess you like查询热销商品从solr
     * @param param
     * @return
     */
    List<Product> guessYouLike(SearchParam param);
    /**
     * 新版购物车该产品没有购买过则根据名称查询推荐商品
     * @param param
     * @return
     */
    List<Product> boughtAndBought(SearchParam param);
    /**
     * 新购物车页面展示该商品类别下的产品
     * @param param
     * @return
     */
    List<Product> catidForGoods(SearchParam param);

    List<Product> errorRecommend(SearchParam param);
    /**
     * 当搜索词没有搜索结果且也没有搜索词热卖商品时，取搜索词频次高的搜索词结果当做热销商品展示
     * @param param
     * @return
     * 王宏杰 2018-07-24
     */
    List<Product> hotProduct(SearchParam param);

    /**
     * 搜索页类别搜索时查询热销商品
     * @param param
     * @return
     */
    List<Product> hotProductForCatid(SearchParam param);
    /**
     * 对搜索数据进行分区间统计 -- yyl
     * @throws Exception
     */
    GoodsPriceRange searPriceRangeByKeyWord(SearchParam param);
    /**
     * 自动补全搜索词
     * @Title searchAutocomplete
     * @Description TODO
     * @param keyWord 搜索词
     * @return  10个补全的搜索词
     * @return List<String>
     * 王宏杰  2018-04-26
     */
    List<String> searchAutocomplete(String keyWord,int site);




}
