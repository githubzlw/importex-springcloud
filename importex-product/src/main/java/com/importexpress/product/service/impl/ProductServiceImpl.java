package com.importexpress.product.service.impl;

import com.importexpress.product.mongo.CatidGroup;
import com.importexpress.product.mongo.MongoProduct;
import com.importexpress.product.pojo.SearchParam;
import com.importexpress.product.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
     *
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
     *
     * @param shopId
     * @return
     */
    @Override
    public List<MongoProduct> findProductByShopId(String shopId) {
        Query query = new Query(Criteria.where("shop_id").is(shopId));
        return mongoTemplate.find(query, MongoProduct.class);
    }

    /**
     * 更新产品
     *
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
     *
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
     *
     * @param param
     * @return
     */
    @Override
    public List<MongoProduct> findProductByCatid(SearchParam param) {
        String preMonth = getPreMonth();
        List<String> catidList = new ArrayList<>();
        if (param.getCatidList() != null
                && !param.getCatidList().isEmpty()) {
            catidList = param.getCatidList();
        } else {
            if ("1037012".equals(param.getCatid())) {
                catidList.add("1037012");
                catidList.add("1037011");
                catidList.add("1037648");
                catidList.add("1042840");
                catidList.add("1042841");
                catidList.add("1037010");
                catidList.add("1037009");
                catidList.add("1037011");
                catidList.add("127430004");
            } else if ("1037192".equals(param.getCatid())) {
                catidList.add("1037192");
                catidList.add("1042754");
                catidList.add("1042754");
            } else if ("1037004".equals(param.getCatid())) {
                catidList.add("1037004");
                catidList.add("919987");
                catidList.add("127430003");
            } else {
                catidList.add(param.getCatid());
            }
        }

        List<Criteria> criteriaList = new ArrayList<Criteria>();
        for (String catid : catidList) {
            Criteria c1 = null;
            c1 = c1.where("path_catid").regex("^.*" + catid + ".*$");
            criteriaList.add(c1);
        }

        Criteria[] arr = new Criteria[criteriaList.size()];

        criteriaList.toArray(arr);
        Criteria criteria = new Criteria().orOperator(arr);

        Query query = null;
        //if (param.getFreeShipping() == 0) {
        if ("1".equals(param.getNewarrival())) {
            if (StringUtils.isNotBlank(param.getCatid())) {

                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()).and("createtime").gte(preMonth));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice()).and("createtime").gte(preMonth));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                            .and("$where").is("this.price_import < " + param.getMaxPrice()).and("createtime").gte(preMonth));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria).and("createtime").gte(preMonth));
                }

            } else {
                query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("createtime").gte(preMonth));

            }
        } else {
            if (StringUtils.isNotBlank(param.getCatid())) {

                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                            .and("$where").is("this.price_import < " + param.getMaxPrice()));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria));
                }

            } else {
                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                .and("$where").is("this.price_import > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                            .and("$where").is("this.price_import < " + param.getMaxPrice()));
                } else {
                    if (StringUtils.isNotBlank(param.getMinPrice())) {
                        if (StringUtils.isNotBlank(param.getMaxPrice())) {
                            query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                    .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                        } else {
                            query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                    .and("$where").is("this.price_import > " + param.getMinPrice()));
                        }
                    } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                .and("$where").is("this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1"));
                    }
                }

            }
        }

     /*   } else {
            if (StringUtils.isNotBlank(param.getCatid())) {
                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria)
                            .and("$where").is("&& this.price_import < " + param.getMaxPrice()));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria));
                }

            } else {
                query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1"));

            }
        }
*/
        if (!CollectionUtils.isEmpty(param.getCatidList())) {
            query.addCriteria(Criteria.where("catid1").in(param.getCatidList()));

        }
        if (param.getBackRows() == 0) {
            query.skip((param.getPage() - 1) * param.getPageSize());
            query.limit(param.getPageSize());
            if (param.getSort().contains("bbPrice")) {
                Document document = Collation.of("zh").toDocument();
                document.put("numericOrdering", true);
                query.collation(Collation.from(document));

                if ("bbPrice-desc".equals(param.getSort())) {
                    query.with(new Sort(Sort.Direction.DESC, "price_import"));
                } else {
                    query.with(new Sort(Sort.Direction.ASC, "price_import"));
                }

            } else if ("order-desc".equals(param.getSort())) {
                Document document = Collation.of("zh").toDocument();
                document.put("numericOrdering", true);
                query.collation(Collation.from(document));
                //销量排序
                query.with(new Sort(Sort.Direction.DESC, "sold"));
            }
        }

        return mongoTemplate.find(query, MongoProduct.class);
    }


    /**
     * 产品数量搜索
     *
     * @param param
     * @return
     */
    @Override
    public Long findProductByCatidCount(SearchParam param) {
        String preMonth = getPreMonth();
        List<String> catidList = new ArrayList<>();
        if (param.getCatidList() != null
                && !param.getCatidList().isEmpty()) {
            catidList = param.getCatidList();
        } else {
            if ("1037012".equals(param.getCatid())) {
                catidList.add("1037012");
                catidList.add("1037011");
                catidList.add("1037648");
                catidList.add("1042840");
                catidList.add("1042841");
                catidList.add("1037010");
                catidList.add("1037009");
                catidList.add("1037011");
                catidList.add("127430004");
            } else if ("1037192".equals(param.getCatid())) {
                catidList.add("1037192");
                catidList.add("1042754");
                catidList.add("1042754");
            } else if ("1037004".equals(param.getCatid())) {
                catidList.add("1037004");
                catidList.add("919987");
                catidList.add("127430003");
            } else {
                catidList.add(param.getCatid());
            }
        }

        List<Criteria> criteriaList = new ArrayList<Criteria>();
        for (String catid : catidList) {
            Criteria c1 = null;
            c1 = c1.where("path_catid").regex("^.*" + catid + ".*$");
            criteriaList.add(c1);
        }

        Criteria[] arr = new Criteria[criteriaList.size()];

        criteriaList.toArray(arr);
        Criteria criteria = new Criteria().orOperator(arr);


        Query query = null;
        //if (param.getFreeShipping() == 0) {
        if ("1".equals(param.getNewarrival())) {
            if (StringUtils.isNotBlank(param.getCatid())) {

                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()).and("createtime").gte(preMonth));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice()).and("createtime").gte(preMonth));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                            .and("$where").is("this.price_import < " + param.getMaxPrice()).and("createtime").gte(preMonth));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria).and("createtime").gte(preMonth));
                }

            } else {
                query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("createtime").gte(preMonth));

            }
        } else {
            if (StringUtils.isNotBlank(param.getCatid())) {

                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                            .and("$where").is("this.price_import < " + param.getMaxPrice()));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria));
                }

            } else {
                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                .and("$where").is("this.price_import > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                            .and("$where").is("this.price_import < " + param.getMaxPrice()));
                } else {
                    if (StringUtils.isNotBlank(param.getMinPrice())) {
                        if (StringUtils.isNotBlank(param.getMaxPrice())) {
                            query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                    .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                        } else {
                            query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                    .and("$where").is("this.price_import > " + param.getMinPrice()));
                        }
                    } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                .and("$where").is("this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1"));
                    }
                }

            }
        }
      /*  } else {
            if (StringUtils.isNotBlank(param.getCatid())) {
                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria)
                            .and("$where").is("&& this.price_import < " + param.getMaxPrice()));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria));
                }

            } else {
                query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1"));

            }
        }*/
        if (!CollectionUtils.isEmpty(param.getCatidList())) {
            query.addCriteria(Criteria.where("catid1").in(param.getCatidList()));
        }
        return mongoTemplate.count(query, MongoProduct.class);
    }


    /**
     * 产品catid组
     * @param catidList
     * @return
     */
/*
    @Override
    public List<CatidGroup> findCatidGroup(List<String> catidList) {

        List<CatidGroup> list = new ArrayList<>();
        for (String catid : catidList) {
            List<CatidGroup> catidGroupList = new ArrayList<>();
            int catidNumm = 0;
            Aggregation customerAgg = Aggregation.newAggregation(
                    //Aggregation.project("catid1","category_name","num","matchSource","valid"),
                    Aggregation.match(Criteria.where("matchSource").ne("8").and("path_catid").regex("^.*" + catid + ".*$").and("valid").is("1")),
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
*/


    /**
     * 产品catid组
     *
     * @param catidList
     * @return
     */
    @Override
    public List<CatidGroup> findCatidGroup(List<String> catidList) {
        String preMonth = getPreMonth();
        List<Criteria> criteriaList = new ArrayList<Criteria>();
        for (String catid : catidList) {
            Criteria c1 = null;
            c1 = c1.where("path_catid").regex("^.*" + catid + ".*$");
            criteriaList.add(c1);
        }

        Criteria[] arr = new Criteria[criteriaList.size()];

        criteriaList.toArray(arr);
        Criteria criteria = new Criteria().orOperator(arr);
        List<CatidGroup> list = new ArrayList<>();
        List<CatidGroup> catidGroupList = new ArrayList<>();
        Aggregation customerAgg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)),
                Aggregation.group("path_catid").first("path_catid").as("catid")
                        .count().as("num")
        );

        AggregationResults<CatidGroup> outputTypeCount1 =
                mongoTemplate.aggregate(customerAgg, "product", CatidGroup.class);

        for (Iterator<CatidGroup> iterator = outputTypeCount1.iterator(); iterator.hasNext(); ) {
            CatidGroup obj = iterator.next();
            catidGroupList.add(obj);
        }

        for (String catid : catidList) {
            int count = 0;
            for (CatidGroup catidGroup : catidGroupList) {
                if (catidGroup.getCatid().contains(catid)) {
                    count += Integer.parseInt(catidGroup.getNum());
                }
                if ("1037012".equals(catid)) {
                    if (catidGroup.getCatid().contains("1037011")
                            || catidGroup.getCatid().contains("1037648")
                            || catidGroup.getCatid().contains("1042840")
                            || catidGroup.getCatid().contains("1042841")
                            || catidGroup.getCatid().contains("1037010")
                            || catidGroup.getCatid().contains("1037009")
                            || catidGroup.getCatid().contains("1037011")) {
                        count += Integer.parseInt(catidGroup.getNum());
                    }
                } else if ("1037192".equals(catid)) {
                    if (catidGroup.getCatid().contains("1042754")) {
                        count += Integer.parseInt(catidGroup.getNum());
                    }
                } else if ("1037004".equals(catid)) {
                    if (catidGroup.getCatid().contains("919987")) {
                        count += Integer.parseInt(catidGroup.getNum());
                    }
                }
            }
            CatidGroup catidGroup1 = new CatidGroup();
            catidGroup1.setCatid(catid);
            catidGroup1.setNum(String.valueOf(count));
            list.add(catidGroup1);
        }

        return list;
    }


    /**
     * 产品catid组
     *
     * @param catidList
     * @return
     */
    @Override
    public List<CatidGroup> findCatidGroupImport(List<String> catidList) {
        String preMonth = getPreMonth();
        List<CatidGroup> list = new ArrayList<>();
        List<CatidGroup> catidGroupList = new ArrayList<>();
        for (String catid : catidList) {
            CatidGroup catidGroup = new CatidGroup();
            catidGroup.setCatid(catid);
            catidGroup.setNum("0");
            catidGroupList.add(catidGroup);
        }
        CatidGroup catidGroupTemp = new CatidGroup();
        catidGroupTemp.setCatid("1813-1");
        catidGroupTemp.setNum("0");
        catidGroupList.add(catidGroupTemp);

        List<Criteria> criteriaList = new ArrayList<Criteria>();
        for (String catid : catidList) {
            Criteria c1 = null;
            c1 = c1.where("path_catid").regex("^" + catid + ",.*$");
            criteriaList.add(c1);
            c1 = c1.where("path_catid").regex("^.*," + catid + ",.*$");
            criteriaList.add(c1);
            c1 = c1.where("path_catid").regex("^.*," + catid + "$");
            criteriaList.add(c1);
        }

        Criteria[] arr = new Criteria[criteriaList.size()];

        criteriaList.toArray(arr);
        Criteria criteria = new Criteria().orOperator(arr);


        Aggregation customerAgg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)),
                Aggregation.group("path_catid").first("path_catid").as("catid")
                        .count().as("num")
        );

        AggregationResults<CatidGroup> outputTypeCount1 =
                mongoTemplate.aggregate(customerAgg, "product", CatidGroup.class);

        System.out.println(outputTypeCount1.getRawResults());

        for (Iterator<CatidGroup> iterator = outputTypeCount1.iterator(); iterator.hasNext(); ) {
            CatidGroup obj = iterator.next();
            //catidGroupList.add(obj);
            String[] catidArry = obj.getCatid().split(",");
            Integer num = Integer.parseInt(obj.getNum());
            for (String catid : catidArry) {

                for (CatidGroup catidGroup : catidGroupList) {
                    if (catid.equals(catidGroup.getCatid())) {
                        num += Integer.parseInt(catidGroup.getNum());
                        catidGroup.setNum(String.valueOf(num));
                    }
                }
            }
        }

        for (CatidGroup catidGroup1 : catidGroupList) {
            if ("1813-1".equals(catidGroup1.getCatid())) {
                for (CatidGroup catidGroup2 : catidGroupList) {
                    if ("311".equals(catidGroup2.getCatid())
                            || "1501".equals(catidGroup2.getCatid())
                            || "125386001".equals(catidGroup2.getCatid())
                            || "201161703".equals(catidGroup2.getCatid())
                            || "125372003".equals(catidGroup2.getCatid())
                            || "1813".equals(catidGroup2.getCatid())) {
                        int numTemp = Integer.parseInt(catidGroup1.getNum());
                        numTemp += Integer.parseInt(catidGroup2.getNum());
                        catidGroup1.setNum(String.valueOf(numTemp));
                    }
                }
            } else if ("10165".equals(catidGroup1.getCatid())) {
                for (CatidGroup catidGroup2 : catidGroupList) {
                    if ("10166".equals(catidGroup2.getCatid())
                            || "54".equals(catidGroup2.getCatid())
                            || "312".equals(catidGroup2.getCatid())) {
                        int numTemp = Integer.parseInt(catidGroup1.getNum());
                        numTemp += Integer.parseInt(catidGroup2.getNum());
                        catidGroup1.setNum(String.valueOf(numTemp));
                    }
                }
            } else if ("97".equals(catidGroup1.getCatid())) {
                for (CatidGroup catidGroup2 : catidGroupList) {
                    if ("130822220".equals(catidGroup2.getCatid())
                            || "3007".equals(catidGroup2.getCatid())) {
                        int numTemp = Integer.parseInt(catidGroup1.getNum());
                        numTemp += Integer.parseInt(catidGroup2.getNum());
                        catidGroup1.setNum(String.valueOf(numTemp));
                    }
                }
            } else if ("5".equals(catidGroup1.getCatid())) {
                for (CatidGroup catidGroup2 : catidGroupList) {
                    if ("13".equals(catidGroup2.getCatid())
                            || "6".equals(catidGroup2.getCatid())
                            || "15".equals(catidGroup2.getCatid())
                            || "96".equals(catidGroup2.getCatid())
                            || "65".equals(catidGroup2.getCatid())
                            || "68".equals(catidGroup2.getCatid())
                            || "19999".equals(catidGroup2.getCatid())) {
                        int numTemp = Integer.parseInt(catidGroup1.getNum());
                        numTemp += Integer.parseInt(catidGroup2.getNum());
                        catidGroup1.setNum(String.valueOf(numTemp));
                    }
                }
            } else if ("7".equals(catidGroup1.getCatid())) {
                for (CatidGroup catidGroup2 : catidGroupList) {
                    if ("72".equals(catidGroup2.getCatid())
                            || "67".equals(catidGroup2.getCatid())
                            || "70".equals(catidGroup2.getCatid())) {
                        int numTemp = Integer.parseInt(catidGroup1.getNum());
                        numTemp += Integer.parseInt(catidGroup2.getNum());
                        catidGroup1.setNum(String.valueOf(numTemp));
                    }
                }
            } else if ("58".equals(catidGroup1.getCatid())) {
                for (CatidGroup catidGroup2 : catidGroupList) {
                    if ("55".equals(catidGroup2.getCatid())
                            || "59".equals(catidGroup2.getCatid())
                            || "4".equals(catidGroup2.getCatid())) {
                        int numTemp = Integer.parseInt(catidGroup1.getNum());
                        numTemp += Integer.parseInt(catidGroup2.getNum());
                        catidGroup1.setNum(String.valueOf(numTemp));
                    }
                }
            }

            list.add(catidGroup1);

        }

        return list;
    }


    /**
     * 多个产品搜索
     *
     * @param param
     * @return
     */
    @Override
    public List<MongoProduct> findProductImport(SearchParam param) {
        String preMonth = getPreMonth();
        List<String> catidList = new ArrayList<>();
        if (param.getCatidList() != null
                && !param.getCatidList().isEmpty()) {
            catidList = param.getCatidList();
        } else {
            catidList.add(param.getCatid());
            if ("1813".equals(param.getCatid())) {
                catidList.add("311");
                catidList.add("1501");
                catidList.add("125386001");
                catidList.add("201161703");
                catidList.add("125372003");
            } else if ("10165".equals(param.getCatid())) {
                catidList.add("10166");
                catidList.add("54");
                catidList.add("312");
            } else if ("97".equals(param.getCatid())) {
                catidList.add("130822220");
                catidList.add("3007");
            } else if ("5".equals(param.getCatid())) {
                catidList.add("6");
                catidList.add("13");
                catidList.add("15");
                catidList.add("96");
                catidList.add("68");
                catidList.add("19999");
                catidList.add("65");
            } else if ("7".equals(param.getCatid())) {
                catidList.add("72");
                catidList.add("67");
                catidList.add("70");
            } else if ("58".equals(param.getCatid())) {
                catidList.add("59");
                catidList.add("55");
                catidList.add("4");
            }
        }


        List<Criteria> criteriaList = new ArrayList<Criteria>();
        for (String catid : catidList) {
            Criteria c1 = null;
            c1 = c1.where("path_catid").regex("^" + catid + ",.*$");
            criteriaList.add(c1);
            c1 = c1.where("path_catid").regex("^.*," + catid + ",.*$");
            criteriaList.add(c1);
            c1 = c1.where("path_catid").regex("^.*," + catid + "$");
            criteriaList.add(c1);
        }

        Criteria[] arr = new Criteria[criteriaList.size()];

        criteriaList.toArray(arr);
        Criteria criteria = new Criteria().orOperator(arr);

        Query query = null;
        //if (param.getFreeShipping() == 0) {
        if ("1".equals(param.getNewarrival())) {
            if (StringUtils.isNotBlank(param.getCatid())) {

                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()).and("createtime").gte(preMonth));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice()).and("createtime").gte(preMonth));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                            .and("$where").is("this.price_import < " + param.getMaxPrice()).and("createtime").gte(preMonth));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria).and("createtime").gte(preMonth));
                }

            } else {
                query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("createtime").gte(preMonth));

            }
        } else {
            if (StringUtils.isNotBlank(param.getCatid())) {

                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                            .and("$where").is("this.price_import < " + param.getMaxPrice()));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria));
                }

            } else {
                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                .and("$where").is("this.price_import > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                            .and("$where").is("this.price_import < " + param.getMaxPrice()));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1"));
                }

            }
        }
     /*   } else {
            if (StringUtils.isNotBlank(param.getCatid())) {
                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria)
                            .and("$where").is("&& this.price_import < " + param.getMaxPrice()));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria));
                }

            } else {
                query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1"));

            }
        }*/
        if (!CollectionUtils.isEmpty(param.getCatidList())) {
            query.addCriteria(Criteria.where("catid1").in(param.getCatidList()));
        }

        if (param.getBackRows() == 0) {
            query.skip((param.getPage() - 1) * param.getPageSize());
            query.limit(param.getPageSize());
            if (param.getSort().contains("bbPrice")) {
                Document document = Collation.of("zh").toDocument();
                document.put("numericOrdering", true);
                query.collation(Collation.from(document));

                if ("bbPrice-desc".equals(param.getSort())) {
                    query.with(new Sort(Sort.Direction.DESC, "price_import"));
                } else {
                    query.with(new Sort(Sort.Direction.ASC, "price_import"));
                }

            } else if ("order-desc".equals(param.getSort())) {
                Document document = Collation.of("zh").toDocument();
                document.put("numericOrdering", true);
                query.collation(Collation.from(document));
                //销量排序
                query.with(new Sort(Sort.Direction.DESC, "sold"));
            }
        }
        return mongoTemplate.find(query, MongoProduct.class);
    }


    /**
     * 产品数量搜索
     *
     * @param param
     * @return
     */
    @Override
    public Long findProductCountImport(SearchParam param) {
        String preMonth = getPreMonth();
        List<String> catidList = new ArrayList<>();
        if (param.getCatidList() != null
                && !param.getCatidList().isEmpty()) {
            catidList = param.getCatidList();
        } else {
            catidList.add(param.getCatid());
            if ("1813".equals(param.getCatid())) {
                catidList.add("311");
                catidList.add("1501");
                catidList.add("125386001");
                catidList.add("201161703");
                catidList.add("125372003");
            } else if ("10165".equals(param.getCatid())) {
                catidList.add("10166");
                catidList.add("54");
                catidList.add("312");
            } else if ("97".equals(param.getCatid())) {
                catidList.add("130822220");
                catidList.add("3007");
            } else if ("5".equals(param.getCatid())) {
                catidList.add("6");
                catidList.add("13");
                catidList.add("15");
                catidList.add("96");
                catidList.add("68");
                catidList.add("19999");
                catidList.add("65");
            } else if ("7".equals(param.getCatid())) {
                catidList.add("72");
                catidList.add("67");
                catidList.add("70");
            } else if ("58".equals(param.getCatid())) {
                catidList.add("59");
                catidList.add("55");
                catidList.add("4");
            }
        }

        List<Criteria> criteriaList = new ArrayList<Criteria>();
        for (String catid : catidList) {
            Criteria c1 = null;
            c1 = c1.where("path_catid").regex("^" + catid + ",.*$");
            criteriaList.add(c1);
            c1 = c1.where("path_catid").regex("^.*," + catid + ",.*$");
            criteriaList.add(c1);
            c1 = c1.where("path_catid").regex("^.*," + catid + "$");
            criteriaList.add(c1);
        }

        Criteria[] arr = new Criteria[criteriaList.size()];

        criteriaList.toArray(arr);
        Criteria criteria = new Criteria().orOperator(arr);


        Query query = null;
        //if (param.getFreeShipping() == 0) {
        if ("1".equals(param.getNewarrival())) {
            if (StringUtils.isNotBlank(param.getCatid())) {

                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()).and("createtime").gte(preMonth));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice()).and("createtime").gte(preMonth));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                            .and("$where").is("this.price_import < " + param.getMaxPrice()).and("createtime").gte(preMonth));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria).and("createtime").gte(preMonth));
                }

            } else {
                query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("createtime").gte(preMonth));

            }
        } else {
            if (StringUtils.isNotBlank(param.getCatid())) {

                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria)
                            .and("$where").is("this.price_import < " + param.getMaxPrice()));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").andOperator(criteria));
                }

            } else {
                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                                .and("$where").is("this.price_import > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1")
                            .and("$where").is("this.price_import < " + param.getMaxPrice()));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1"));
                }

            }
        }
  /*      } else {
            if (StringUtils.isNotBlank(param.getCatid())) {
                if (StringUtils.isNotBlank(param.getMinPrice())) {
                    if (StringUtils.isNotBlank(param.getMaxPrice())) {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice() + " && this.price_import < " + param.getMaxPrice()));
                    } else {
                        query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria)
                                .and("$where").is("this.price_import > " + param.getMinPrice()));
                    }
                } else if (StringUtils.isNotBlank(param.getMaxPrice())) {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria)
                            .and("$where").is("&& this.price_import < " + param.getMaxPrice()));
                } else {
                    query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1").andOperator(criteria));
                }

            } else {
                query = new Query(Criteria.where("matchSource").ne("8").and("valid").is("1").and("img_check").is("1"));

            }
        }*/
        if (!CollectionUtils.isEmpty(param.getCatidList())) {
            query.addCriteria(Criteria.where("catid1").in(param.getCatidList()));
        }
        return mongoTemplate.count(query, MongoProduct.class);
    }

    private String getPreMonth() {
        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.add(Calendar.MONTH, -1); //月份减1
        Date lastMonth = ca.getTime(); //结果
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(lastMonth);
    }


}
