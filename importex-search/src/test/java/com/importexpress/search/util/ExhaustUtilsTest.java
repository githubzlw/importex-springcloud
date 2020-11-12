package com.importexpress.search.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExhaustUtilsTest {
    @Autowired
    private ExhaustUtils exhaustUtils;
    @Test
    public void exhaust(){
        String str = "kid wash towel";
        List<String> exhaust = exhaustUtils.exhaust(str);

        Assert.assertEquals(true,exhaust!=null&&!exhaust.isEmpty());
        exhaust.stream().forEach(e->System.out.println(e));
    }
    @Test
    public void combination(){
        String str = "kid wash towel";
        String[] combination = exhaustUtils.combination(str);
        Assert.assertEquals(3,combination.length);
        for (String c : combination){
            System.out.println(c);
        }
    }

}