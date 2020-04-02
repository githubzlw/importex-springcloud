package com.importexpress.ali1688.control;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.ali1688.service.Ali1688Service;
import com.importexpress.ali1688.util.Config;
import com.importexpress.comm.domain.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

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
            String url = ali1688Service.uploadImgToTaobao(dest.getAbsolutePath());
            if(StringUtils.isEmpty(url)){
                return CommonResult.failed("upload image failed");
            }
            JSONObject jsonObject = ali1688Service.searchImgFromTaobao(url);
            return CommonResult.success(jsonObject);
        } catch (IOException e) {
            log.error(e.toString(), e);
            return CommonResult.failed(e.toString());
        }finally {
//            boolean isOk = dest.delete();
//            if(isOk){
//                log.info("file:[{}] is deleted",dest.getAbsolutePath());
//            }
        }

    }

}
