package com.importexpress.ali1688.control;

import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.importexpress.ali1688.service.Ali1688Service;
import com.importexpress.ali1688.util.Config;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author jack.luo
 */
@RestController
@Slf4j
@Api("图片搜索接口")
@RequestMapping("/searchimg")
public class ImageSearchController {

    private Ali1688Service ali1688Service;

    private Config config;

    @Autowired
    public ImageSearchController(Ali1688Service ali1688Service, Config config) {

        this.ali1688Service = ali1688Service;
        this.config = config;
    }


    @PostMapping("/upload")
    @ApiOperation("图片搜索")
    public CommonResult imageSearch(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return CommonResult.failed("上传失败，请选择文件");
        }

        String fileName = UUID.randomUUID()+ "_" + file.getOriginalFilename();
        File dest = new File(config.fileUploadPath + fileName);
        try {
            file.transferTo(dest);
            log.info("upload file({}) successful",dest.getAbsolutePath());

            String md5;
            try {
                md5 = DigestUtils.md5Hex(new FileInputStream(dest.getAbsolutePath()));
            } catch (IOException e) {
                log.error(e.getMessage(),e);
                return CommonResult.failed("md5获取失败");
            }
            //缓存中判断
            JSONObject imageSearchFromCatch = ali1688Service.getImageSearchFromCatch(md5);
            if (imageSearchFromCatch != null) {
                return CommonResult.success(imageSearchFromCatch);
            }

            String url = ali1688Service.uploadImgToTaobao(dest.getAbsolutePath());
            if (StringUtils.isEmpty(url)) {
                return CommonResult.failed("upload image failed");
            }

            Callable<JSONObject> callable = () -> ali1688Service.searchImgFromTaobao(url);

            JSONObject jsonObject = null;
            //增加重试次数
            Retryer<JSONObject> retryer = RetryerBuilder.<JSONObject>newBuilder()
                    .retryIfResult(Predicates.isNull())
                    .retryIfExceptionOfType(IllegalStateException.class)
                    .retryIfExceptionOfType(BizException.class)
                    .withWaitStrategy(WaitStrategies.randomWait(1000, TimeUnit.MILLISECONDS))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                    .build();
            try {
                jsonObject = retryer.call(callable);
            } catch (ExecutionException | RetryException e) {
                log.error("Retry", e);
                return CommonResult.failed(e.toString());
            }

            //缓存中保存
            ali1688Service.saveImageSearchFromCatch(md5, jsonObject);

            return CommonResult.success(jsonObject);
        } catch (IOException e) {
            log.error(e.toString(), e);
            return CommonResult.failed(e.toString());
        }finally {
            boolean isOk = dest.delete();
            if(isOk){
                log.info("file:[{}] is deleted",dest.getAbsolutePath());
            }
        }
    }

    @GetMapping("/details/{pid}")
    @ResponseBody
    public CommonResult getDetails(@PathVariable(name = "pid") String pid) {
        try {

            Callable<CommonResult> callable = () -> ali1688Service.getDetails(pid);

            //增加重试次数
            Retryer<CommonResult> retryer = RetryerBuilder.<CommonResult>newBuilder()
                    .retryIfResult(Predicates.isNull())
                    .retryIfExceptionOfType(IllegalStateException.class)
                    .retryIfExceptionOfType(BizException.class)
                    .withWaitStrategy(WaitStrategies.randomWait(1000, TimeUnit.MILLISECONDS))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                    .build();
            return retryer.call(callable);

        } catch (Exception e) {
            log.error(e.toString(), e);
            return CommonResult.failed(e.toString());
        }
    }

}
