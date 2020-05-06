package com.importexpress.utils.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.util.UrlUtil;
import com.importexpress.utils.service.AiImageService;
import com.importexpress.utils.util.Config;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jack.luo
 * @create 2020/5/6 11:56
 * Description
 */
@Service
public class AiImageServiceImpl implements AiImageService {

    private final Config config;

    public AiImageServiceImpl(Config config) {
        this.config = config;
    }

    /**
     * get token
     * @return Pair.of(accessToken, expireTime)
     * @throws IOException
     */
    @Override
    public Pair<String, Long> getToken() throws IOException {

        Map<String, String> maps = new HashMap<>();
        maps.put("appKey", config.YINGSHI_API_APPKEY);
        maps.put("appSecret", config.YINGSHI_API_APPSECRET);
        JSONObject jsonObject = UrlUtil.getInstance().callUrlByPost(config.YINGSHI_API_URL_TOKEN, maps);
        if(jsonObject !=null && "200".equals(jsonObject.getString("code"))){
            JSONObject data = jsonObject.getJSONObject("data");
            if(data!=null){
                String accessToken = data.getString("accessToken");
                Long expireTime = data.getLong("expireTime");
                return Pair.of(accessToken, expireTime);
            }
        }
        return null;
    }

    /**
     * capture image
     * @param accessToken
     * @return
     * @throws IOException
     */
    @Override
    public String captureImage(String accessToken) throws IOException {

        Map<String, String> maps = new HashMap<>();
        maps.put("accessToken", accessToken);
        maps.put("deviceSerial", config.YINGSHI_API_DEVICESERIAL);
        maps.put("channelNo", "1");
        JSONObject jsonObject = UrlUtil.getInstance().callUrlByPost(config.YINGSHI_API_URL_CAPTURE, maps);
        if(jsonObject !=null && "200".equals(jsonObject.getString("code"))){
            JSONObject data = jsonObject.getJSONObject("data");
            if(data!=null){
                return data.getString("picUrl");
            }
        }
        return null;
    }
}
