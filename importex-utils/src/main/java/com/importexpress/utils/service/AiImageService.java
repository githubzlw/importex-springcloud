package com.importexpress.utils.service;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;

/**
 * @Author jack.luo
 * @create 2020/5/6 11:51
 * Description
 */
public interface AiImageService {

    /**
     * get token
     * @return
     * @throws IOException
     */

    Pair<String, Long> getToken() throws IOException;


    /**
     * capture image
     * @param accessToken
     * @return
     * @throws IOException
     */
    String captureImage(String accessToken) throws IOException;
}
