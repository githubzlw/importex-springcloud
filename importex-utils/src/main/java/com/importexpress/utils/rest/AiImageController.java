package com.importexpress.utils.rest;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.utils.service.AiImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
