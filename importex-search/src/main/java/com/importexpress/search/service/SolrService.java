package com.importexpress.search.service;

import com.importexpress.search.pojo.GoodsPriceRange;
import com.importexpress.search.pojo.SearchParam;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;

/**
 * @author jack.luo
 * @date 2019/11/22
 */
public interface SolrService {
    /**获取类别统计
     * @date 2017年2月13日
     * @author abc
     * @return
     * @throws Exception
     */
    QueryResponse groupCategory(SearchParam param);

    /**请求solr获取搜索
     * @param param
     * @return
     */
    QueryResponse serach(SearchParam param);


    /**请求solr获取店铺搜索
     * @param param
     * @return
     */
    QueryResponse shopSerach(SearchParam param);

    /**
     * 新版购物车该产品没有购买过则根据名称查询推荐商品
     * @param param
     * @return
     */
    QueryResponse bought(SearchParam param);
    /**
     * 新购物车页面展示该商品类别下的产品
     * @param param
     * @return
     */
    QueryResponse catidForGoods(SearchParam param);

    /**404页面推荐商品查询
     * @param param
     * @return
     */
    QueryResponse errorRecommend(SearchParam param);
    /**
     * 当搜索词没有搜索结果且也没有搜索词热卖商品时，取搜索词频次高的搜索词结果当做热销商品展示
     * @param param
     * @return
     * 王宏杰 2018-07-24
     */
    QueryResponse hotProduct(SearchParam param);

    /**
     * 搜索页类别搜索时查询热销商品
     * @param param
     * @return
     */
    QueryResponse hotProductForCatid(SearchParam param);
    /**
     * 价格区间统计分组
     * @param param
     * @return
     */
    GoodsPriceRange searPriceRangeByKeyWord(SearchParam param);
    /**
     * 提示词
     * @param keyWord
     * @return
     */
    SpellCheckResponse searchAutocomplete(String keyWord,int site);


    String getPriceField(int site);

}
