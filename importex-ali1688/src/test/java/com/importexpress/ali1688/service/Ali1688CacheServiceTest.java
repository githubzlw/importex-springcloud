package com.importexpress.ali1688.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Ali1688CacheServiceTest {


    @Autowired
    private Ali1688CacheService ali1688CacheService;

    @Test
    public void checkDescInAllPids() {

        System.out.println(ali1688CacheService.checkDescInAllPids());
    }
}

