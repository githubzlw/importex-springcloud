package com.importexpress.serialport.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.util.UrlUtil;
import com.importexpress.serialport.service.AiImageService;
import com.importexpress.serialport.util.Config;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
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

    private static String YINGSHI_TOKEN;

    public AiImageServiceImpl(Config config) {
        this.config = config;
    }

    /**
     * get yingshi token
     *
     * @return Pair.of(accessToken, expireTime)
     * @throws IOException
     */
    @Override
    public Pair<String, Long> getYingShiToken() throws IOException {

        Map<String, String> maps = new HashMap<>();
        maps.put("appKey", config.YINGSHI_API_APPKEY);
        maps.put("appSecret", config.YINGSHI_API_APPSECRET);
        JSONObject jsonObject = UrlUtil.getInstance().callUrlByPost(config.YINGSHI_API_URL_TOKEN, maps);
        if (jsonObject != null && "200".equals(jsonObject.getString("code"))) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                String accessToken = data.getString("accessToken");
                Long expireTime = data.getLong("expireTime");
                return Pair.of(accessToken, expireTime);
            }
        }
        return null;
    }

//    /**
//     * get baidu token
//     *
//     * @return Pair.of(accessToken, expireTime)
//     * @throws IOException
//     */
//    @Override
//    public Pair<String, Long> getBaiduToken() throws IOException {
//
//        Map<String, String> maps = new HashMap<>();
//        maps.put("grant_type", "client_credentials");
//        maps.put("client_id", config.BAIDU_API_CLIENT_ID);
//        maps.put("client_secret", config.BAIDU_API_CLIENT_SECRET);
//        JSONObject jsonObject = UrlUtil.getInstance().callUrlByPost(config.BAIDU_API_URL_TOKEN, maps);
//        if (jsonObject != null && StringUtils.isEmpty(jsonObject.getString("error_code"))) {
//            String accessToken = jsonObject.getString("access_token");
//            Long expireTime = jsonObject.getLong("expires_in");
//            return Pair.of(accessToken, expireTime);
//        } else {
//            log.error("call objectDetect url error,result:[{}]", jsonObject.toJSONString());
//        }
//
//        return null;
//    }

    /**
     * capture image
     *
     * @return
     * @throws IOException
     */
    @Override
    public String captureImage() throws IOException {

        if(StringUtils.isEmpty(YINGSHI_TOKEN)){
            Pair<String, Long> pair = this.getYingShiToken();
            YINGSHI_TOKEN = pair.getLeft();
        }

        Map<String, String> maps = new HashMap<>();
        maps.put("accessToken", YINGSHI_TOKEN);
        maps.put("deviceSerial", config.YINGSHI_API_DEVICESERIAL);
        maps.put("channelNo", "1");
        JSONObject jsonObject = UrlUtil.getInstance().callUrlByPost(config.YINGSHI_API_URL_CAPTURE, maps);
        if (jsonObject != null && "200".equals(jsonObject.getString("code"))) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                return data.getString("picUrl");
            }
        }
        return null;
    }

//    /**
//     * object Detect
//     *
//     * @param accessToken
//     * @param imgUrl
//     * @return
//     * @throws IOException
//     */
//    @Override
//    public String objectDetect(String accessToken, String imgUrl) throws Exception {
//
////        List<Long> lstResult = new ArrayList<>(4);
////        String filePath = "C:\\Users\\luohao\\Downloads\\goods1-1.jpg";
//        //byte[] imgData = FileUtil.readFileByBytes(filePath);
//        byte[] imgData = downloadUrl(imgUrl);
//        Objects.requireNonNull(imgData);
////        FileOutputStream fos = new FileOutputStream("d:/tmp.jpg");
////        fos.write(imgData);
////        fos.close();
//        //byte[] imgData = FileUtil.readFileByBytes("d:\\goods1-1.jpg");
//
//        String imgParam = URLEncoder.encode(Base64Util.encode(imgData), "UTF-8");
//
////        Map<String, String> maps = new HashMap<>();
////        maps.put("access_token", accessToken);
////        maps.put("image", imgParam);
////        maps.put("with_face", "0");
//
//        String param = "image=" + imgParam + "&with_face=" + 0;
//        return HttpUtil.post(config.BAIDU_API_URL_OBJECT_DETECT, accessToken, param);
//
////        JSONObject jsonObject = UrlUtil.getInstance().callUrlByPost(config.BAIDU_API_URL_OBJECT_DETECT, maps);
////        if(jsonObject !=null && StringUtils.isEmpty(jsonObject.getString("error_code")) ){
////            JSONObject result = jsonObject.getJSONObject("result");
////            if(result!=null){
////                lstResult.add(result.getLong("width"));
////                lstResult.add(result.getLong("top"));
////                lstResult.add(result.getLong("left"));
////                lstResult.add(result.getLong("height"));
////            }
////        }else{
////            log.error("call objectDetect url error,result:[{}]",jsonObject.toJSONString());
////        }
//    }

    private byte[] downloadUrl(String downloadUrl) throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to download file: " + response);
        }
        return response.body() != null ? response.body().bytes() : null;
    }

    /**
     * 图片标识出红框
     *
     * @param downloadUrl
     * @param lstPolygon
     * @return
     * @throws IOException
     */
    @Override
    public byte[] drawPolygons(String downloadUrl, List<Polygon> lstPolygon) throws IOException {

        byte[] bytes = downloadUrl(downloadUrl);
        return drawPolygons(bytes, lstPolygon);
    }

    /**
     * 图片标识出红框
     * @param bytes
     * @param lstPolygon
     * @return
     * @throws IOException
     */
    @Override
    public byte[] drawPolygons(byte[] bytes, List<Polygon> lstPolygon) throws IOException {

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));

        Graphics g = image.getGraphics();
        for(Polygon polygon:lstPolygon){
            drawPolygon(polygon.xpoints, polygon.ypoints, g);
        }
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", out);
        return out.toByteArray();
    }

    /**
     * 画出矩形
     * @param x
     * @param y
     * @param g
     */
    private void drawPolygon(int[] x, int[] y, Graphics g) {
        Polygon po = new Polygon();
        for (int i = 0; i < x.length; i++) {
            po.addPoint(x[i], y[i]);
        }
        g.setColor(Color.RED);
        g.drawPolygon(po);
    }

    /**
     * call command
     *
     * @param imgUrl
     * @return
     * @throws IOException
     */
    @Override
    public List<String> callCMD(String imgUrl) throws IOException {

        byte[] imgData = downloadUrl(imgUrl);
        Objects.requireNonNull(imgData);
        UUID uuid = UUID.randomUUID();
        String inputFile = config.SHELL_PATH + uuid + ".jpg";
        Files.write(Paths.get(inputFile), imgData);
        //String cmd = config.SHELL_PATH + "squares " + uuid + ".jpg";
        //Process proc =Runtime.getRuntime().exec("./exefile");
        Process exec = Runtime.getRuntime().exec("cmd /c opencv.exe "+ uuid + ".jpg", null,new File(config.SHELL_PATH));
        int status = 0;
        try {
            status = exec.waitFor();
        } catch (InterruptedException e) {
            log.error("InterruptedException",e);
        }
        if (status != 0) {
            log.error("Failed to call shell's command and the return status's is: " + status);
        }
        String outputFile = config.SHELL_PATH + uuid + ".jpg.csv";
        return Files.readAllLines(Paths.get(outputFile));

    }

    /**
     *比较2个数组，to数组是否比from数组少1个，并且to数组的坐标是否都在from数组中（允许5%坐标误差）
     * @param lstFrom
     * @param lstTo
     * @return
     */
    @Override
    public boolean compareTwoList(List<String> lstFrom,List<String> lstTo){

        if(lstFrom.size()-1==lstTo.size()){
            //少了一个矩形物体，推测成功移动出去

            //from数组合计
            List<Integer> lstFromSum = new ArrayList<>();
            lstFrom.forEach( item -> lstFromSum.add(
                    Arrays.stream(item.split( "," )).mapToInt(Integer::parseInt).sum()));

            //to数组合计
            List<Integer> lstToSum = new ArrayList<>();
            lstTo.forEach( item -> lstToSum.add(
                    Arrays.stream(item.split( "," )).mapToInt(Integer::parseInt).sum()));

            //计算数组包含
            int countFind=0;
            for (int intToSum : lstToSum) {
                for (int intFromSum : lstFromSum) {
                    if (intToSum <= intFromSum * 1.05 && intToSum >= intFromSum * 0.95) {
                        //找到原图中物体(考虑误差增加5%)
                        ++countFind;
                        break;
                    }
                }
            }

            return countFind == lstToSum.size();
        }
        return false;
    }

}
