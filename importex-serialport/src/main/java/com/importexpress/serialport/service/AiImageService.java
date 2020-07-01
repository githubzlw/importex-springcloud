package com.importexpress.serialport.service;

import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
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


//    /**
//     * get baidu token
//     * @return Pair.of(accessToken, expireTime)
//     * @throws IOException
//     */
//    Pair<String, Long> getBaiduToken() throws IOException;

    /**
     * capture image
     * @return
     * @throws IOException
     */
    String captureImage() throws IOException;

//    /**
//     * object Detect
//     * @param accessToken
//     * @param imgUrl
//     * @return
//     * @throws IOException
//     */
//    String objectDetect(String accessToken, String imgUrl) throws Exception;


    /**
     * 图片标识出红框
     * @param downloadUrl
     * @return
     * @throws IOException
     */
    byte[] drawPolygons(String downloadUrl, List<Polygon> lstPolygon) throws IOException;

    /**
     * call command
     * @param imgUrl
     * @return
     * @throws IOException
     */
    List<String> callCMD(String imgUrl) throws IOException;

    boolean compareTwoList(List<String> lstFrom,List<String> lstTo);
}
