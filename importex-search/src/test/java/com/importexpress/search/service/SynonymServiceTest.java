package com.importexpress.search.service;

import com.importexpress.search.pojo.SynonymsCategoryWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;
@RunWith(SpringRunner.class)
@SpringBootTest
public class SynonymServiceTest {
    @Autowired
    private SynonymService synonymService;

    @Test
    public void getSynonymKeyword() {
        Map<String, Set<String>> synonymKeyword =
                synonymService.getSynonymKeyword();
        Assert.assertEquals(695,synonymKeyword.size());
    }
    @Test
    public void getSynonymsCategory() {
        List<SynonymsCategoryWrap> synonymsCategory = synonymService.getSynonymsCategory();
        Assert.assertEquals(1669,synonymsCategory.size());
    }
}