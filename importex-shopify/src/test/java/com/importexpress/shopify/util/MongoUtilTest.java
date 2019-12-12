package com.importexpress.shopify.util;

import com.google.gson.Gson;
import com.importexpress.shopify.mapper.OverSeaProductMapper;
import com.importexpress.shopify.pojo.MongoProduct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.util
 * @date:2019/12/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoUtilTest {

    @Autowired
    private MongoUtil mongoUtil;

    @Autowired
    private OverSeaProductMapper overSeaProductMapper;


    @Test
    public void testQuery() {

        Long pid = 556860707964L;
        MongoProduct product = mongoUtil.querySingleProductByPid(pid);
        System.err.println(product);
    }


    @Test
    public void testQueryList(){

        List<Long> pidList = overSeaProductMapper.queryOverSeaProductList();
        List<MongoProduct> tempList = mongoUtil.queryProductList(pidList, 1);
        System.err.println(tempList.size());
    }
    @Test
    public void test(){
        String str = "[1003015602/493664063_1751210702.60x60.jpg]";
        List<String> list = new Gson().fromJson(str, List.class);
        System.out.println(list.toString());
    }
}
