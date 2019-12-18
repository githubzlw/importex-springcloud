package com.importexpress.search.service;

import com.importexpress.search.pojo.PageWrap;
import com.importexpress.search.pojo.SearchParam;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest
public class PageServiceTest {
    @Autowired
    private PageService pageService;

    @Test
    public void paging() {
        SearchParam param = new SearchParam();
        param.setUriRequest("/goodslist");
        param.setCatid("311");
        param.setKeyword("dress");
        param.setSite(2);
        param.setUserType(1);
        param.setFreeShipping(2);
        param.setPage(1);
        PageWrap paging = pageService.paging(param, 432);
        Assert.assertEquals(432,paging.getRecordCount());
        System.out.println(paging.getPaging());
    }
}