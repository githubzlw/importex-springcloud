package com.importexpress.product.service;

import com.importexpress.product.mongo.MongoProduct;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
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
public class ProductServiceTest {

    @Autowired
    private ProductService productService;


    @Test
    public void testQuery() {

        Long pid = 556860707964L;
        MongoProduct product = productService.findProduct(pid);
        System.err.println(product);
        Assert.assertNotNull("获取异常", product);
    }


    @Test
    public void testQueryList() {

        long[] pidList = new long[]{556860707964L, 544049586548L};
        List<MongoProduct> tempList = productService.findProducts(pidList, 1);
        Assert.assertTrue("获取异常", CollectionUtils.isNotEmpty(tempList));
        System.err.println(tempList.size());
    }

    @Test
    public void updateProduct() {
        Long pid = 530333452003L;
        Assert.assertEquals(1, productService.updateProduct(pid, 0));
    }
}
