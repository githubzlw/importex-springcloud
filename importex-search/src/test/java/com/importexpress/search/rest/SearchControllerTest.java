package com.importexpress.search.rest;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.search.common.InitApplicationParameter;
import com.importexpress.search.mongo.CatidGroup;
import com.importexpress.search.pojo.Currency;
import com.importexpress.search.pojo.Product;
import com.importexpress.search.pojo.SearchParam;
import com.importexpress.search.pojo.SearchResultWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Autowired
    private InitApplicationParameter init;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        init.init(wac.getServletContext());
    }

    @Test
    public void getSearch() throws Exception {
        SearchParam param = new SearchParam();
        param.setKeyword("coat");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/products")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andReturn().getResponse().getContentAsString();

        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("搜索结果:" + result.getData().toString());
    }

    @Test
    public void getSearchByMobile() throws Exception {
        Gson gson = new Gson();
        SearchParam param = new SearchParam();
        param.setMobile(true);
        param.setKeyword("coat");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/product")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson)).andReturn().getResponse().getContentAsString();
        CommonResult result = gson.fromJson(contentAsString, CommonResult.class);

        Assert.assertEquals(200, result.getCode());
        SearchResultWrap wrap = gson.fromJson(result.getData().toString(), SearchResultWrap.class);
        List<Product> products = wrap.getProducts();
        products.stream().forEach(p -> System.out.println(p.getId() + "--" + p.getName()));
        System.out.println("搜索结果:" + wrap.getPage().getPageSize());
    }

    @Test
    public void getShop() throws Exception {
        SearchParam param = new SearchParam();
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);
        param.setStoried("jcyurong");
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/shop")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andReturn().getResponse().getContentAsString();
        Gson gson = new Gson();
        CommonResult result = gson.fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        SearchResultWrap wrap = gson.fromJson(result.getData().toString(), SearchResultWrap.class);
        Assert.assertEquals(42, wrap.getPage().getRecordCount());
        System.out.println("店铺产品数量:" + wrap.getPage().getRecordCount());
    }

    @Test
    public void count() throws Exception {
        SearchParam param = new SearchParam();
        param.setKeyword("dress");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/count")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andReturn().getResponse().getContentAsString();
        Gson gson = new Gson();
        CommonResult result = gson.fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("返回数量为:" + result.getData());
    }

    @Test
    public void categoryStatistics() throws Exception {
        SearchParam param = new SearchParam();
        param.setKeyword("*");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/category")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andReturn().getResponse().getContentAsString();
        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("类别列表;" + result.getData().toString());
    }

    @Test
    public void guessYouLike() throws Exception {
        SearchParam param = new SearchParam();
        param.setKeyword("light cotton vest");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/like")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andReturn().getResponse().getContentAsString();
        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("guess you like:" + result.getData().toString());
    }

    @Test
    public void boughtAndBought() throws Exception {
        SearchParam param = new SearchParam();
        param.setKeyword("dress summer");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/bought")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andReturn().getResponse().getContentAsString();
        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("bought:" + result.getData().toString());
    }

    @Test
    public void productsByCatid() throws Exception {
        SearchParam param = new SearchParam();
        param.setPid("528638604453");
        param.setCatid("1042841");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/bycatid")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andReturn().getResponse().getContentAsString();
        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("productsByCatid:" + result.getData().toString());
    }

    @Test
    public void errorRecommend() throws Exception {
        SearchParam param = new SearchParam();
        param.setKeyword("*");
        param.setCatid("1042841");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/recommend")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andReturn().getResponse().getContentAsString();
        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("recommend:" + result.getData().toString());
    }

    @Test
    public void hotProduct() throws Exception {
        SearchParam param = new SearchParam();
        param.setKeyword("shoes");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/hot")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andReturn().getResponse().getContentAsString();
        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("hot:" + result.getData().toString());
    }

    @Test
    public void hotProductForCatid() throws Exception {
        SearchParam param = new SearchParam();
        param.setKeyword("*");
        param.setCatid("1038378");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/hotbycatid")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andReturn().getResponse().getContentAsString();
        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("hotProductForCatid:" + result.getData().toString());
    }

    @Test
    public void loadRangePrice() throws Exception {
        SearchParam param = new SearchParam();
        param.setKeyword("*");
        param.setCatid("1038378");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);
        param.setSelectedInterval(0);
        param.setRange(false);
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/range")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson)
        ).andReturn().getResponse().getContentAsString();
        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("loadRangePrice:" + result.getData().toString());
    }

    @Test
    public void searchAutocomplete() throws Exception {
        String contentAsString = mockMvc.perform(post("/search/auto")
                .param("keyWord", "dress")
                .param("site", "2")
        ).andReturn().getResponse().getContentAsString();
        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("searchAutocomplete:" + result.getData().toString());
    }

    @Test
    public void associateKey() throws Exception {
        String contentAsString = mockMvc.perform(post("/search/associate")
                .param("keyWord", "kid wash towel")
                .param("site", "2")
        ).andReturn().getResponse().getContentAsString();
        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("associateKey:" + result.getData().toString());
    }


    @Test
    public void getSearchMongo() throws Exception {
        SearchParam param = new SearchParam();
        param.setCatid("311");
       /* param.setSort("order-desc");
        Currency currency = new Currency();
        currency.setCurrency("EUR");
        currency.setExchangeRate(0.903);
        currency.setSymbol("€");
        param.setCurrency(currency);*/
        //param.setFreeShipping(2);
        //param.setMaxPrice("1.0");
        //param.setMinPrice("0.01");
      /*  param.setKeyword("coat");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);*/
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/productsB2C")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andReturn().getResponse().getContentAsString();

        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("搜索结果:" + result.getData().toString());
    }

    @Test
    public void getSearchMongoCatidGroup() throws Exception {
        SearchParam param = new SearchParam();
        param.setSite(2);
      /*  param.setCatid("123648007");
        param.setFreeShipping(2);
        param.setMaxPrice("1.0");
        param.setMinPrice("0.01");*/
      /*  param.setKeyword("coat");
        param.setSite(2);
        param.setPage(1);
        param.setUserType(1);*/
        String requestJson = JSONObject.toJSONString(param);
        String contentAsString = mockMvc.perform(post("/search/catidGroup")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andReturn().getResponse().getContentAsString();

        CommonResult result = new Gson().fromJson(contentAsString, CommonResult.class);
        Assert.assertEquals(200, result.getCode());
        System.out.println("搜索结果:" + result.getData().toString());
    }



   /* public  List<CatidGroup> findCatidGroup(List<String> catidList){
        List<CatidGroup> list = new ArrayList<>();
        for(String catid : catidList){
            List<CatidGroup> catidGroupList = new ArrayList<>();
            Aggregation customerAgg = Aggregation.newAggregation(
                    Aggregation.project("catid1","category_name","num","matchSource","valid"),
                    Aggregation.match(Criteria.where("matchSource").is("8").and("path_catid").regex("^.*" +catid+ ".*$").and("valid").is("1")),
                    Aggregation.group("catid1").first("catid1").as("catid").first("category_name").as("category_name")
                            .first("matchSource").as("matchSource").first("valid").as("valid").count().as("num")
            );
            AggregationResults<CatidGroup> outputTypeCount1 =
                    mongoTemplate.aggregate(customerAgg, "product", CatidGroup.class);

            for (Iterator<CatidGroup> iterator = outputTypeCount1.iterator(); iterator.hasNext(); ) {
                CatidGroup obj = iterator.next();
                catidGroupList.add(obj);
            }
            list.addAll(catidGroupList);
        }

        return list;
    }*/


}