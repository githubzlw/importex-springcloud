package com.importexpress.search.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AutiKeyServiceTest {
    @Autowired
    private AutiKeyService autiKeyService;
    @Test
    public void getAutiKey(){
        Map<String, String> autiKey = autiKeyService.getAutiKey();
        Assert.assertEquals(5,autiKey.size());
    }

}