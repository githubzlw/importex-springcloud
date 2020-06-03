package com.importexpress.utils.rest;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.utils.service.AiImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author jack.luo
 * @create 2020/5/6 13:50
 * Description
 */
@RestController
@Slf4j
@Api(tags = "AI图片处理接口")
public class AiImageController {

    private static final String REDIS_KEY_YINGSHI_TOKEN = "utils:aiimage:yingshi:token";

    private static final String REDIS_KEY_BAIDU_TOKEN = "utils:aiimage:baidu:token";

    private final StringRedisTemplate redisTemplate;

    private final AiImageService aiImageService;

    public AiImageController(StringRedisTemplate redisTemplate, AiImageService aiImageService) {
        this.redisTemplate = redisTemplate;
        this.aiImageService = aiImageService;
    }

    @GetMapping("/image/capture")
    @ApiOperation("抓取图片")
    public CommonResult captureImage() {
        try {
            String token = redisTemplate.opsForValue().get(REDIS_KEY_YINGSHI_TOKEN);
            if(StringUtils.isEmpty(token)){
                Pair<String, Long> pair = aiImageService.getYingShiToken();
                token = pair.getLeft();
                redisTemplate.opsForValue().set(REDIS_KEY_YINGSHI_TOKEN,pair.getLeft(),pair.getRight()-System.currentTimeMillis()-10000, TimeUnit.MILLISECONDS);
            }
            return CommonResult.success(aiImageService.captureImage(token));
        } catch (Exception e) {
            log.error("captureImage",e);
            return CommonResult.failed(e.getMessage());
        }

    }

    @GetMapping("/image/objectDetect")
    @ApiOperation("图片识别")
    public CommonResult objectDetect() {
        try {
            CommonResult commonResult = captureImage();
            if(commonResult.getCode()==CommonResult.SUCCESS){
                String url = (String) commonResult.getData();

                String token = redisTemplate.opsForValue().get(REDIS_KEY_BAIDU_TOKEN);
                if(StringUtils.isEmpty(token)){
                    Pair<String, Long> pair = aiImageService.getBaiduToken();
                    token = pair.getLeft();
                    redisTemplate.opsForValue().set(REDIS_KEY_BAIDU_TOKEN,pair.getLeft(),pair.getRight()-10000, TimeUnit.MILLISECONDS);
                }
                String result = aiImageService.objectDetect(token, url);
                return CommonResult.success(result);
            }else{
                return commonResult;
            }

        } catch (Exception e) {
            log.error("captureImage",e);
            return CommonResult.failed(e.getMessage());
        }

    }

//    @GetMapping(value = "/image/show",produces = MediaType.IMAGE_JPEG_VALUE)
//    @ResponseBody
//    @ApiOperation("图片识别后显示")
//    public byte[] show() throws Exception {
//
//        CommonResult commonResult = captureImage();
//        String url = (String) commonResult.getData();
//
//        String token = redisTemplate.opsForValueAiImageServiceImpl().get(REDIS_KEY_BAIDU_TOKEN);
//        if(StringUtils.isEmpty(token)){
//            Pair<String, Long> pair = aiImageService.getBaiduToken();
//            token = pair.getLeft();
//            redisTemplate.opsForValue().set(REDIS_KEY_BAIDU_TOKEN,pair.getLeft(),pair.getRight()-10000, TimeUnit.MILLISECONDS);
//        }
//        String json = aiImageService.objectDetect(token, url);
//        //{\"log_id\": 1093228794698255209, \"result\": {\"width\": 705, \"top\": 163, \"left\": 364, \"height\": 518}}
//        JSONObject jsonObject = JSONObject.parseObject(json);
//        JSONObject jsonResult = jsonObject.getJSONObject("result");
//
//        Rectangle rect = new Rectangle(
//                jsonResult.getIntValue("left"),jsonResult.getIntValue("top"),
//                jsonResult.getIntValue("width"),jsonResult.getIntValue("height"));
//        return aiImageService.drawRect(url, rect);
//
//    }

    @GetMapping(value = "/image/show",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    @ApiOperation("图片识别后显示")
    public byte[] show()  {

        CommonResult commonResult = captureImage();
        String url = (String) commonResult.getData();


        try {
            List<String> lstRect = aiImageService.callCMD(url);
            if(lstRect !=null && lstRect.size() >0){
        //            266,413,139,617,235,672,352,469
        //            122,130,124,301,367,297,376,139
                String str = lstRect.get(0);
                String[] split = str.split(",");
                Assert.isTrue(split.length ==8);
                int[] x = new int[4];
                x[0] = Integer.parseInt(split[0]);
                x[1] = Integer.parseInt(split[2]);
                x[2] = Integer.parseInt(split[4]);
                x[3] = Integer.parseInt(split[6]);
                int[] y = new int[4];
                y[0] = Integer.parseInt(split[1]);
                y[1] = Integer.parseInt(split[3]);
                y[2] = Integer.parseInt(split[5]);
                y[3] = Integer.parseInt(split[7]);

                return aiImageService.drawPolygon(url, x, y);
            }else{
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

}