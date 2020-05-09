package com.importexpress.utils.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.util.UrlUtil;
import com.importexpress.utils.service.AiImageService;
import com.importexpress.utils.util.Base64Util;
import com.importexpress.utils.util.Config;
import com.importexpress.utils.util.HttpUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @Author jack.luo
 * @create 2020/5/6 11:56
 * Description
 */
@Service
@Slf4j
public class AiImageServiceImpl implements AiImageService {

    private final Config config;

    public AiImageServiceImpl(Config config) {
        this.config = config;
    }

    /**
     * get yingshi token
     * @return Pair.of(accessToken, expireTime)
     * @throws IOException
     */
    @Override
    public Pair<String, Long> getYingShiToken() throws IOException {

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
     * get baidu token
     * @return Pair.of(accessToken, expireTime)
     * @throws IOException
     */
    @Override
    public Pair<String, Long> getBaiduToken() throws IOException {

        Map<String, String> maps = new HashMap<>();
        maps.put("grant_type", "client_credentials");
        maps.put("client_id", config.BAIDU_API_CLIENT_ID);
        maps.put("client_secret", config.BAIDU_API_CLIENT_SECRET);
        JSONObject jsonObject = UrlUtil.getInstance().callUrlByPost(config.BAIDU_API_URL_TOKEN, maps);
        if(jsonObject !=null  && StringUtils.isEmpty(jsonObject.getString("error_code")) ){
            String accessToken = jsonObject.getString("access_token");
            Long expireTime = jsonObject.getLong("expires_in");
            return Pair.of(accessToken, expireTime);
        }else{
            log.error("call objectDetect url error,result:[{}]",jsonObject.toJSONString());
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

    /**
     * object Detect
     * @param accessToken
     * @param imgUrl
     * @return
     * @throws IOException
     */
    @Override
    public String objectDetect(String accessToken, String imgUrl) throws Exception {

//        List<Long> lstResult = new ArrayList<>(4);
//        String filePath = "C:\\Users\\luohao\\Downloads\\goods1-1.jpg";
        //byte[] imgData = FileUtil.readFileByBytes(filePath);
        byte[] imgData = downloadUrl(imgUrl);
        Objects.requireNonNull(imgData);
//        FileOutputStream fos = new FileOutputStream("d:/tmp.jpg");
//        fos.write(imgData);
//        fos.close();
        //byte[] imgData = FileUtil.readFileByBytes("d:\\goods1-1.jpg");

        String imgParam = URLEncoder.encode(Base64Util.encode(imgData), "UTF-8");

//        Map<String, String> maps = new HashMap<>();
//        maps.put("access_token", accessToken);
//        maps.put("image", imgParam);
//        maps.put("with_face", "0");

        String param = "image=" + imgParam + "&with_face=" + 0;
        return HttpUtil.post(config.BAIDU_API_URL_OBJECT_DETECT, accessToken, param);

//        JSONObject jsonObject = UrlUtil.getInstance().callUrlByPost(config.BAIDU_API_URL_OBJECT_DETECT, maps);
//        if(jsonObject !=null && StringUtils.isEmpty(jsonObject.getString("error_code")) ){
//            JSONObject result = jsonObject.getJSONObject("result");
//            if(result!=null){
//                lstResult.add(result.getLong("width"));
//                lstResult.add(result.getLong("top"));
//                lstResult.add(result.getLong("left"));
//                lstResult.add(result.getLong("height"));
//            }
//        }else{
//            log.error("call objectDetect url error,result:[{}]",jsonObject.toJSONString());
//        }
    }

    private byte[] downloadUrl(String downloadUrl) throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to download file: " + response);
        }
        return response.body()!=null ?  response.body().bytes() : null;
    }

    /**
     * 图片标识出红框
     * @param downloadUrl
     * @param rect
     * @return
     * @throws IOException
     */
    @Override
    public byte[] drawRect(String downloadUrl,Rectangle rect) throws IOException {

        byte[] bytes = downloadUrl(downloadUrl);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        Graphics g = image.getGraphics();
        g.setColor(Color.RED);
        g.drawRect(rect.x,rect.y,rect.width,rect.height);
        g.dispose();
        ByteOutputStream out = new ByteOutputStream();
        ImageIO.write(image, "jpeg", out);
        return out.getBytes();
    }
}
