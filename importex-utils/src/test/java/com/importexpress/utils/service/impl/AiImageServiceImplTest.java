package com.importexpress.utils.service.impl;

import com.importexpress.utils.service.AiImageService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

/**
 * @Author jack.luo
 * @create 2020/5/6 13:13
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AiImageServiceImplTest{

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
        Pair<String, Long> token = aiImageService.getYingShiToken();
        String url = aiImageService.captureImage(token.getLeft());
        Assert.notNull(url);
        System.out.println(url);
    }

    @Test
    public void getBaiduToken() throws IOException {
        Pair<String, Long> token = aiImageService.getBaiduToken();
        Assert.notNull(token);
        System.out.println(token);
    }


    @Test
    public void objectDetect() throws Exception {
        Pair<String, Long> token = aiImageService.getYingShiToken();
        String url = aiImageService.captureImage(token.getLeft());

        Pair<String, Long> baiduToken = aiImageService.getBaiduToken();
        System.out.println(aiImageService.objectDetect(baiduToken.getLeft(), url));
    }
}