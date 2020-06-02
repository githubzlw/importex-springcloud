package com.importexpress.login.rest;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.login.service.ThirdPartyLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;

/**
 * @author jack.luo
 */
@RestController
@Slf4j
@Api(tags = "第三方登录验证")
public class ThirdPartyLoginController {

    private final ThirdPartyLoginService service;

    public ThirdPartyLoginController(ThirdPartyLoginService service) {
        this.service = service;
    }

    @GetMapping("/getGoogleClientId")
    @ApiOperation("get GoogleClientId")
    public CommonResult getGoogleClientId(@RequestParam SiteEnum site) {

        try{
            return CommonResult.success(service.getGoogleClientId(site));

        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }

    }

    @GetMapping("/googleAuth")
    @ApiOperation("google login auth")
    public CommonResult googleAuth(@RequestParam SiteEnum site,@RequestParam  String idTokenString) {

        try{
            return CommonResult.success(service.googleAuth(site,idTokenString));

        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }

    }

    @GetMapping("/getFacebookURL")
    @ApiOperation("get FacebookURL")
    public CommonResult getFacebookUrl(@RequestParam SiteEnum site) {

        try{
            return CommonResult.success(URLEncoder.encode(service.getFacebookUrl(site),"utf-8"));

        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }

    }

    @GetMapping("/facebookAuth")
    @ApiOperation("facebook login auth")
    public CommonResult facebookAuth(@RequestParam SiteEnum site,@RequestParam  String code) {

        try{
            return CommonResult.success(service.facebookAuth(site,code));

        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }

    }

}
