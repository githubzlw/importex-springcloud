package com.importexpress.utils.service;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.List;

/**
 * @Author jack.luo
 * @create 2020/5/6 11:51
 * Description
 */
public interface AiImageService {

    /**
     * get yingshi token
     * @return
     * @throws IOException
     */

    Pair<String, Long> getYingShiToken() throws IOException;


    /**
     * get baidu token
     * @return Pair.of(accessToken, expireTime)
     * @throws IOException
     */
    Pair<String, Long> getBaiduToken() throws IOException;

    /**
     * capture image
     * @param accessToken
     * @return
     * @throws IOException
     */
    String captureImage(String accessToken) throws IOException;

    /**
     * object Detect
     * @param accessToken
     * @param imgUrl
     * @return
     * @throws IOException
     */
    String objectDetect(String accessToken, String imgUrl) throws Exception;
}
