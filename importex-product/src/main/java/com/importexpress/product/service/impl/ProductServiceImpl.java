package com.importexpress.product.service.impl;

import com.importexpress.product.mongo.MongoProduct;
import com.importexpress.product.service.ProductService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

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
    public int updateProduct(Long pid, int valid) {
        Query query = new Query();
        query.addCriteria(Criteria.where(PID).is(pid));
        Update update = new Update();
        update.set("valid", valid);

        return mongoTemplate.findAndModify(query, update, MongoProduct.class) != null ? 1 : 0;
    }

    @Override
    public List<MongoProduct> findProducts(long[] pids, int valid) {

        List<Long> lstPid = new ArrayList<>();
        LongStream.of(pids).forEach(lstPid::add);
        Query query;
        if (valid > -1) {
            query = new Query(Criteria.where(PID).in(lstPid).and("valid").is(String.valueOf(valid)));
        } else {
            query = new Query(Criteria.where(PID).in(lstPid));
        }
        return mongoTemplate.find(query, MongoProduct.class);
    }

}
