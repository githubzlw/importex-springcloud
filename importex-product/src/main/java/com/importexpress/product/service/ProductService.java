package com.importexpress.product.service;


import com.importexpress.product.mongo.CatidGroup;
import com.importexpress.product.mongo.MongoProduct;
import com.importexpress.product.pojo.SearchParam;

import java.util.List;

public interface ProductService {

    /**
     * 产品搜索
     * @param pid
     * @return
     */
    MongoProduct findProduct(Long pid);

    /**
     * 通过店铺id搜索
     * @param shopId
     * @return
     */
    List<MongoProduct> findProductByShopId(String shopId);

    /**
     * 更新产品
     * @param pid
     * @param valid
     * @return
     */
    int updateProduct(Long pid, int valid);

    /**
     * 多个产品搜索
     * @param pids
     * @param valid
     * @return
     */
    List<MongoProduct> findProducts(long[] pids, int valid);

    public List<MongoProduct> findProductByCatid(SearchParam param);

    public Long findProductByCatidCount(SearchParam param);

    public List<CatidGroup> findCatidGroup(List<String> catidList);


}
