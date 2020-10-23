package com.importexpress.search.mongo;

import com.google.gson.Gson;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.product.mongo.MongoProduct;
import com.importexpress.search.pojo.SearchParam;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

/**
 * @author jinjie
 * @date 2020/10/15
 */
@Service
@Slf4j
public class
MongoHelp {

    private static final String COLLECTION_PRODUCT = "product";
    private static final String PID = "pid";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 30;

    private MongoTemplate mongoTemplate;
    private MongoCollection<Document> collection;

    @Autowired
    public MongoHelp(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.collection = this.mongoTemplate.getDb().getCollection(COLLECTION_PRODUCT);
    }



    public List<Product> findProductByCatid(SearchParam param, int page, int pageSize){

        Query query = null;
        if(param.getFreeShipping() == 0){
            if(StringUtils.isNotBlank(param.getCatid())){
                if(StringUtils.isNotBlank(param.getMinPrice()) ){
                    if(StringUtils.isNotBlank(param.getMaxPrice())){
                        query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8")
                                .and("$where").is("this.price > "+ param.getMinPrice() +" && this.price < "+ param.getMaxPrice() +" && this.final_weight > 0.5"));
                    }
                    else{
                        query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8")
                                .and("$where").is("this.price > "+param.getMinPrice() + " && this.final_weight > 0.5"));
                    }
                }
                else if(StringUtils.isNotBlank(param.getMaxPrice())){
                    query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8")
                            .and("$where").is("this.price < " + param.getMaxPrice()+" && this.final_weight > 0.5"));
                }
                else{
                    query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8").and("$where").is("this.final_weight > 0.5"));
                }

            } else {
                query = new Query(Criteria.where("matchSource").is("8").and("$where").is("this.final_weight > 0.5"));

            }
        }
        else{
            if(StringUtils.isNotBlank(param.getCatid())){
                if(StringUtils.isNotBlank(param.getMinPrice()) ){
                    if(StringUtils.isNotBlank(param.getMaxPrice())){
                        query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8")
                                .and("$where").is("this.price > "+ param.getMinPrice() +" && this.price < "+ param.getMaxPrice() +" && this.final_weight < 0.5"));
                    }
                    else{
                        query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8")
                                .and("$where").is("this.price > "+ param.getMinPrice() +" && this.final_weight < 0.5"));
                    }
                }
                else if(StringUtils.isNotBlank(param.getMaxPrice())){
                    query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8")
                            .and("$where").is("&& this.price < "+ param.getMaxPrice() + " && this.final_weight < 0.5"));
                }
                else{
                    query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8").and("$where").is("this.final_weight < 0.5"));
                }

            } else {
                query = new Query(Criteria.where("matchSource").is("8").and("$where").is("this.final_weight < 0.5"));

            }
        }


       if(param.getSort().indexOf("bbPrice") > -1) {
            if("bbPrice-desc".equals(param.getSort())){
                query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "price")));
            }
            else{
                query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "price")));
            }

        }else if("order-desc".equals(param.getSort())){
            //销量排序
            query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "sold")));
        }


        query.skip((page-1)*pageSize);
        query.limit(pageSize);

        return mongoTemplate.find(query, Product.class);
    }


    public Long findProductByCatidCount(SearchParam param){

        Query query = null;
        if(param.getFreeShipping() == 0){
            if(StringUtils.isNotBlank(param.getCatid())){
                if(StringUtils.isNotBlank(param.getMinPrice()) ){
                    if(StringUtils.isNotBlank(param.getMaxPrice())){
                        query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8")
                                .and("$where").is("this.price > "+ param.getMinPrice() +" && this.price < "+ param.getMaxPrice() +" && this.final_weight > 0.5"));
                    }
                    else{
                        query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8")
                                .and("$where").is("this.price > "+param.getMinPrice() + " && this.final_weight > 0.5"));
                    }
                }
                else if(StringUtils.isNotBlank(param.getMaxPrice())){
                    query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8")
                            .and("$where").is("this.price < " + param.getMaxPrice()+" && this.final_weight > 0.5"));
                }
                else{
                    query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8").and("$where").is("this.final_weight > 0.5"));
                }

            } else {
                query = new Query(Criteria.where("matchSource").is("8").and("$where").is("this.final_weight > 0.5"));

            }
        }
        else{
            if(StringUtils.isNotBlank(param.getCatid())){
                if(StringUtils.isNotBlank(param.getMinPrice()) ){
                    if(StringUtils.isNotBlank(param.getMaxPrice())){
                        query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8")
                                .and("$where").is("this.price > "+ param.getMinPrice() +" && this.price < "+ param.getMaxPrice() +" && this.final_weight < 0.5"));
                    }
                    else{
                        query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8")
                                .and("$where").is("this.price > "+ param.getMinPrice() +" && this.final_weight < 0.5"));
                    }
                }
                else if(StringUtils.isNotBlank(param.getMaxPrice())){
                    query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8")
                            .and("$where").is("&& this.price < "+ param.getMaxPrice() + " && this.final_weight < 0.5"));
                }
                else{
                    query = new Query(Criteria.where("catid1").is(param.getCatid()).and("matchSource").is("8").and("$where").is("this.final_weight < 0.5"));
                }

            } else {
                query = new Query(Criteria.where("matchSource").is("8").and("$where").is("this.final_weight < 0.5"));

            }
        }
        return mongoTemplate.count(query, Product.class);
    }

/*

    public List findCatidGroup(List<String> catidList){
        Aggregation customerAgg = Aggregation.newAggregation(
                Aggregation.project("catid1","category_name"),
                Aggregation.match(Criteria.where("catid1").in(catidList)),
                Aggregation.unwind("orders"),
                Aggregation.group("catid1").first("catid1").as("catid").count().as("num")

        );
        List<CatidGroup> catidGroupList = mongoTemplate.aggregate(customerAgg,"catidList",CatidGroup.class);
        return
    }
*/


}
