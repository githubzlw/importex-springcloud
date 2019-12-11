package com.importexpress.shopify.util;

import com.importexpress.shopify.pojo.MongoProduct;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.util
 * @date:2019/12/10
 */
@Component
public class MongoUtil {
    private final MongoTemplate mongoTemplate;

    public MongoUtil(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MongoProduct querySingleProductByPid(Long pid) {
        Query query = new Query(Criteria.where("pid").is(pid));
        MongoProduct product = mongoTemplate.findOne(query, MongoProduct.class);
        return product;
    }

    public List<MongoProduct> queryProductList(List<Long> pidList, int isValid) {
        Query query;
        if (isValid > -1) {
            query = new Query(Criteria.where("pid").in(pidList).and("valid").is(String.valueOf(isValid)));
        } else {
            query = new Query(Criteria.where("pid").in(pidList));
        }
        List<MongoProduct> productList = mongoTemplate.find(query, MongoProduct.class);
        return productList;
    }

}
