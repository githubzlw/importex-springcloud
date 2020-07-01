package com.importexpress.serialport.service.impl;

import com.importexpress.serialport.service.AiImageService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author jack.luo
 * @create 2020/5/6 13:13
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AiImageServiceImplTest {

    @Autowired
    private AiImageService aiImageService;

    @Test
    public void getToken() throws IOException {
        Pair<String, Long> token = aiImageService.getYingShiToken();
        Assert.notNull(token);
        System.out.println(token);
    }


    @Test
    public void captureImage() throws IOException {
        String url = aiImageService.captureImage();
        Assert.notNull(url);
        System.out.println(url);
    }

    @Test
    public void compareTwoList1() {

        List<String> lstFrom = new ArrayList<>();
        lstFrom.add("266,413,139,617,235,672,352,469");
        lstFrom.add("122,130,124,301,367,297,376,139");

        List<String> lstTo = new ArrayList<>();
        lstTo.add("122,130,124,301,367,297,376,139");

        Assert.isTrue(aiImageService.compareTwoList(lstFrom, lstTo));

    }

    @Test
    public void compareTwoList2() {

        List<String> lstFrom = new ArrayList<>();
        lstFrom.add("266,413,139,617,235,672,352,469");
        lstFrom.add("122,130,124,301,367,297,376,139");

        List<String> lstTo = new ArrayList<>();
        lstTo.add("266,413,139,617,235,672,352,469");
        lstTo.add("122,130,124,301,367,297,376,139");

        Assert.isTrue(!aiImageService.compareTwoList(lstFrom, lstTo));
    }

    @Test
    public void compareTwoList3() {

        List<String> lstFrom = new ArrayList<>();
        lstFrom.add("266,413,139,617,235,672,352,469");
        lstFrom.add("122,130,124,301,367,297,376,139");

        List<String> lstTo = new ArrayList<>();
        lstTo.add("276,413,139,617,235,672,352,479");

        Assert.isTrue(aiImageService.compareTwoList(lstFrom, lstTo));
    }

    @Test
    public void compareTwoList4() {

        List<String> lstFrom = new ArrayList<>();
        lstFrom.add("266,413,139,617,235,672,352,469");
        lstFrom.add("122,130,124,301,367,297,376,139");

        List<String> lstTo = new ArrayList<>();
        lstTo.add("111,121,131,141,151,111,352,479");

        Assert.isTrue(!aiImageService.compareTwoList(lstFrom, lstTo));
    }


}