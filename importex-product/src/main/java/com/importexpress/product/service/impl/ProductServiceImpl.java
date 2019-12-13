package com.importexpress.product.service.impl;

import com.importexpress.comm.pojo.MongoProduct;
import com.importexpress.product.service.ProductService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description:
 * @date:2019/12/10
 */
@Service
public class ProductServiceImpl implements ProductService {

    public static final String PID = "pid";

    private final MongoTemplate mongoTemplate;

    public ProductServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public MongoProduct findProduct(Long pid) {
        Query query = new Query(Criteria.where(PID).is(pid));
        return mongoTemplate.findOne(query, MongoProduct.class);
    }

    @Override
    public List<MongoProduct> findProducts(long[] pids, int valid) {
        Query query;
        if (valid > -1) {
            query = new Query(Criteria.where(PID).in(pids).and("valid").is(String.valueOf(valid)));
        } else {
            query = new Query(Criteria.where(PID).in(pids));
        }
        return mongoTemplate.find(query, MongoProduct.class);
    }

}
