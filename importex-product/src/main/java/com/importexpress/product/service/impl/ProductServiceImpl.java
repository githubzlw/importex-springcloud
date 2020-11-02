package com.importexpress.product.service.impl;

import com.importexpress.product.mongo.CatidGroup;
import com.importexpress.product.mongo.MongoProduct;
import com.importexpress.product.pojo.SearchParam;
import com.importexpress.product.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
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

    /**
     * 产品搜索
     * @param pid
     * @return
     */
    @Override
    public MongoProduct findProduct(Long pid) {
        Query query = new Query(Criteria.where(PID).is(pid));
        return mongoTemplate.findOne(query, MongoProduct.class);
    }

    /**
     * 通过店铺id搜索
     * @param shopId
     * @return
     */
    @Override
    public List<MongoProduct> findProductByShopId(String shopId){
        Query query = new Query(Criteria.where("shop_id").is(shopId));
        return mongoTemplate.find(query, MongoProduct.class);
    }

    /**
     * 更新产品
     * @param pid
     * @param valid
     * @return
     */
    @Override
    public int updateProduct(Long pid, int valid) {
        Query query = new Query();
        query.addCriteria(Criteria.where(PID).is(pid));
        Update update = new Update();
        update.set("valid", valid);

        return mongoTemplate.findAndModify(query, update, MongoProduct.class) != null ? 1 : 0;
    }

    /**
     * 多个产品搜索
     * @param pids
     * @param valid
     * @return
     */
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

    /**
     * 多个产品搜索
     * @param param
     * @return
     */
    @Override
    public List<MongoProduct> findProductByCatid(SearchParam param) {

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
                        query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1").and("img_check").is("1")
                                .and("$where").is("this.price > " + param.getMinPrice() + " && this.price < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1").and("img_check").is("1")
                                .and("$where").is("this.price > " + param.getMinPrice() ));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1").and("img_check").is("1")
                            .and("$where").is("&& this.price < " + param.getMaxPrice() ));
                } else {
                    query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1").and("img_check").is("1"));
                }

            } else {
                query = new Query(Criteria.where("matchSource").is("8").and("valid").is("1").and("img_check").is("1"));

            }
        }

        return mongoTemplate.find(query, MongoProduct.class);
    }


    /**
     * 产品数量搜索
     * @param param
     * @return
     */
    @Override
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
                        query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1").and("img_check").is("1")
                                .and("$where").is("this.price > " + param.getMinPrice() + " && this.price < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1").and("img_check").is("1")
                                .and("$where").is("this.price > " + param.getMinPrice() ));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1").and("img_check").is("1")
                            .and("$where").is("&& this.price < " + param.getMaxPrice() ));
                } else {
                    query = new Query(Criteria.where("path_catid").regex("^.*" + param.getCatid() + ".*$").and("matchSource").is("8").and("valid").is("1").and("img_check").is("1"));
                }

            } else {
                query = new Query(Criteria.where("matchSource").is("8").and("valid").is("1").and("img_check").is("1"));

            }
        }
        return mongoTemplate.count(query, MongoProduct.class);
    }


    /**
     * 产品catid组
     * @param catidList
     * @return
     */
    @Override
    public List<CatidGroup> findCatidGroup(List<String> catidList) {

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
