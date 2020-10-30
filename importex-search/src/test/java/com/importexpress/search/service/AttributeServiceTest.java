package com.importexpress.search.service;

import com.importexpress.search.pojo.Attribute;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
@RunWith(SpringRunner.class)
@SpringBootTest
public class AttributeServiceTest {
    @Autowired
    private AttributeService attributeService;

    @Test
    public void getAttributes() {
        Map<String, Attribute> attributes = attributeService.getAttributes();
        Assert.assertEquals(1604,attributes.size());
    }
}