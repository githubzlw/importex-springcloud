package com.importexpress.serialport.rest;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.serialport.service.AiImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author jack.luo
 * @create 2020/6/4 13:50
 * Description
 */
@RestController
@Slf4j
@Api(tags = "AI图片处理接口")
public class AiImageController {



    private final AiImageService aiImageService;

    public AiImageController(AiImageService aiImageService) {
        this.aiImageService = aiImageService;
    }

    @GetMapping("/image/capture")
    @ApiOperation("抓取图片")
    public CommonResult captureImage() {
        try {
            return CommonResult.success(aiImageService.captureImage());
        } catch (Exception e) {
            log.error("captureImage",e);
            return CommonResult.failed(e.getMessage());
        }

    }


    @GetMapping(value = "/image/show",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    @ApiOperation("图片识别后显示")
    public byte[] show()  {

        CommonResult commonResult = captureImage();
        String url = (String) commonResult.getData();

        try {
            List<String> lstRect = aiImageService.callCMD(url);
            //sample:            266,413,139,617,235,672,352,469
            //                   122,130,124,301,367,297,376,139
            List<Polygon> lstPolygon = new ArrayList<>(lstRect.size());
            for(String str : lstRect){
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
                lstPolygon.add(new Polygon(x, y, 4));
            }

            return aiImageService.drawPolygons(url, lstPolygon);

        } catch (IOException e) {
            log.error("show",e);
            return null;
        }
    }

    @GetMapping(value = "/image/showXYZ",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    @ApiOperation("图片识别后显示矩形物体坐标")
    public List<String> showXYZ()  {

        try {
            CommonResult commonResult = captureImage();
            String url = (String) commonResult.getData();
            return aiImageService.callCMD(url);
        } catch (IOException e) {
            log.error("show",e);
            return new ArrayList<>();
        }
    }

}
