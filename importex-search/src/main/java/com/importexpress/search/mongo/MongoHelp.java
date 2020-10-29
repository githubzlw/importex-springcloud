package com.importexpress.search.mongo;

import com.alibaba.fastjson.JSON;
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
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
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


    public List<Product> findProductByCatid(SearchParam param, int page, int pageSize) {

        Query query = null;
        if (param.getFreeShipping() == 0) {
            if (StringUtils.isNotBlank(param.getCatid())) {
                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1")
                                .and("$where").is("this.price > " + param.getMinPrice() + " && this.price < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1")
                                .and("$where").is("this.price > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1")
                            .and("$where").is("this.price < " + param.getMaxPrice()));
                } else {
                    query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1"));
                }

            } else {
                query = new Query(Criteria.where("matchSource").is("8").and("valid").is("1"));

            }
        } else {
            if (StringUtils.isNotBlank(param.getCatid())) {
                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1")
                                .and("$where").is("this.price > " + param.getMinPrice() + " && this.price < " + param.getMaxPrice() + " && this.final_weight < 0.5 && this.volume_weight < 0.5"));
                    } else {
                        query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1")
                                .and("$where").is("this.price > " + param.getMinPrice() + " && this.final_weight < 0.5 && this.volume_weight < 0.5"));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1")
                            .and("$where").is("&& this.price < " + param.getMaxPrice() + " && this.final_weight < 0.5 && this.volume_weight < 0.5"));
                } else {
                    query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1").and("$where").is("this.final_weight < 0.5 && this.volume_weight < 0.5"));
                }

            } else {
                query = new Query(Criteria.where("matchSource").is("8").and("valid").is("1").and("$where").is("this.final_weight < 0.5 && this.volume_weight < 0.5"));

            }
        }


        //mongo属性都是string排序无效
       /* Document document = Collation.of("zh").toDocument();
        document.put("numericOrdering",true);
        query.collation(Collation.from(document));*/

       /* if(param.getSort().indexOf("bbPrice") > -1) {
            if("bbPrice-desc".equals(param.getSort())){
                query.with(new Sort(Sort.Direction.DESC, "price_import"));
            }
            else{
                query.with(new Sort(Sort.Direction.ASC, "price_import"));
            }

        }else if("order-desc".equals(param.getSort())){
            //销量排序
            query.with(new Sort(Sort.Direction.DESC, "sold"));
        }*/

       /* query.skip((page-1)*pageSize);
        query.limit(pageSize);*/


        return mongoTemplate.find(query, Product.class);
    }


    public Long findProductByCatidCount(SearchParam param) {

        Query query = null;
        if (param.getFreeShipping() == 0) {
            if (StringUtils.isNotBlank(param.getCatid())) {
                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1")
                                .and("$where").is("this.price > " + param.getMinPrice() + " && this.price < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1")
                                .and("$where").is("this.price > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1")
                            .and("$where").is("this.price < " + param.getMaxPrice()));
                } else {
                    query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1"));
                }

            } else {
                query = new Query(Criteria.where("matchSource").is("8").and("valid").is("1"));

            }
        } else {
            if (StringUtils.isNotBlank(param.getCatid())) {
                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1")
                                .and("$where").is("this.price > " + param.getMinPrice() + " && this.price < " + param.getMaxPrice() + " && this.final_weight < 0.5 && this.volume_weight < 0.5"));
                    } else {
                        query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1")
                                .and("$where").is("this.price > " + param.getMinPrice() + " && this.final_weight < 0.5 && this.volume_weight < 0.5"));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1")
                            .and("$where").is("&& this.price < " + param.getMaxPrice() + " && this.final_weight < 0.5 && this.volume_weight < 0.5"));
                } else {
                    query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1").and("$where").is("this.final_weight < 0.5 && this.volume_weight < 0.5"));
                }

            } else {
                query = new Query(Criteria.where("matchSource").is("8").and("valid").is("1").and("$where").is("this.final_weight < 0.5 && this.volume_weight < 0.5"));

            }
        }
        return mongoTemplate.count(query, Product.class);
    }


    public List<CatidGroup> findCatidGroup(List<String> catidList) {

     /*   List<CatidGroup> catidGroupList = new ArrayList<>();
        Aggregation customerAgg = Aggregation.newAggregation(
                Aggregation.project("catid1","category_name","num","matchSource","valid"),
                Aggregation.match(Criteria.where("matchSource").is("8").and("catid1").in(catidList).and("valid").is("1")),
                Aggregation.group("catid1").first("catid1").as("catid").first("category_name").as("category_name")
                        .first("matchSource").as("matchSource").first("valid").as("valid").count().as("num")
        );
        AggregationResults<CatidGroup> outputTypeCount1 =
                mongoTemplate.aggregate(customerAgg, "product", CatidGroup.class);

        for (Iterator<CatidGroup> iterator = outputTypeCount1.iterator(); iterator.hasNext(); ) {
            CatidGroup obj = iterator.next();
            catidGroupList.add(obj);
        }
        return catidGroupList;*/
        List<CatidGroup> list = new ArrayList<>();
        for (String catid : catidList) {
            List<CatidGroup> catidGroupList = new ArrayList<>();
            int catidNumm = 0;
            Aggregation customerAgg = Aggregation.newAggregation(
                    //Aggregation.project("catid1","category_name","num","matchSource","valid"),
                    Aggregation.match(Criteria.where("matchSource").is("8").and("path_catid").regex("^.*" + catid + ".*$").and("valid").is("1")),
                    Aggregation.group("catid1").first("catid1").as("catid")
                            .first("matchSource").as("matchSource").first("valid").as("valid").count().as("num")
            );
            AggregationResults<CatidGroup> outputTypeCount1 =
                    mongoTemplate.aggregate(customerAgg, "product", CatidGroup.class);

            for (Iterator<CatidGroup> iterator = outputTypeCount1.iterator(); iterator.hasNext(); ) {
                CatidGroup obj = iterator.next();
                catidGroupList.add(obj);
                catidNumm += Integer.parseInt(obj.getNum());
            }
            if (catidNumm > 0) {
                CatidGroup catidGroup = new CatidGroup();
                catidGroup.setCatid(catid);
                catidGroup.setCategory_name(catidGroupList.get(0).getCategory_name());
                catidGroup.setNum(String.valueOf(catidNumm));
                list.add(catidGroup);
            }

        }

        return list;
    }


}
