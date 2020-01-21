package com.importexpress.shopify.rest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.util.UrlUtil;
import com.importexpress.shopify.service.ShopifyAuthService;
import com.importexpress.shopify.service.UserService;
import com.importexpress.shopify.util.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jack.luo
 */
@Slf4j
@RestController
@RequestMapping("/shopify")
@Api(tags = "shopify授权调用接口")
public class ShopifyAuthController {
    private final Config config;
    private final ShopifyAuthService shopifyAuthService;
    @Autowired
    private UserService userService;

    public ShopifyAuthController(Config config, ShopifyAuthService shopifyAuthService) {
        this.config = config;
        this.shopifyAuthService = shopifyAuthService;
    }

    @GetMapping("/shopifyName/{userId}")
    @ApiOperation("查询客户是否绑定店铺")
    public CommonResult shopifyName(
            @ApiParam(name = "userId", value = "用户ID", required = true) @PathVariable(name = "userId") Integer userId) {

        try {
            String result = shopifyAuthService.getShopifyName(userId);
            if (StringUtils.isNotBlank(result)) {
                return CommonResult.success("GET SHOPIFY SUCCESSED", result);
            } else {
                return CommonResult.success("GET SHOPIFY SUCCESSED IS EMPTY", "");
            }
        } catch (Exception e) {
            log.error("auth", e);
            return CommonResult.failed(e.getMessage());
        }
    }



    @PostMapping(value = "/auth")
    @ApiOperation("授权回调")
    public CommonResult auth(
            @ApiParam(name="code",value="shopify返回的code",required=true)String code,
            @ApiParam(name="shop",value="shopify店铺名",required=true)String shop,
            @ApiParam(name="userid",value="用户ID",required=true)String userid) {

        log.info("code:{},shop:{}", code, shop);
        try {
            HashMap<String, String> result = shopifyAuthService.getAccessToken(shop, code);
            String accessToken = result.get("access_token");
            String scope = result.get("scope");
            int auth = shopifyAuthService.saveShopifyAuth(shop, accessToken, scope);
            if(auth > 0){
                userService.updateUserShopifyFlag(Integer.parseInt(userid), shop);
                return CommonResult.success("SAVE SHOPIFY AUTH SUCCESSED",shop);
            }
            return CommonResult.failed("SAVE SHOPIFY AUTH ERROR");
        } catch (Exception e) {
            log.error("auth", e);
            return CommonResult.failed(e.getMessage());
        }
    }
    @GetMapping(value = "/authuri")
    @ApiOperation("请求授权接口")
    public CommonResult authUri(
            @ApiParam(name="shop",value="shopify店铺名称",required=true) String shop){
        try {
            //请求授权
            String shopUrl = shop;
            if(!StringUtils.startsWithIgnoreCase(shop,"https://")
                    && !StringUtils.startsWithIgnoreCase(shop,"http://")){
                shopUrl = "https://" + shop + ".myshopify.com";
            }
            if(UrlUtil.getInstance().isAccessURL(shopUrl)){
                String authUri = shopUrl + "/admin/oauth/authorize?client_id="
                        + config.SHOPIFY_CLIENT_ID + "&scope=" + config.SHOPIFY_SCOPE + "&redirect_uri="
                        + config.SHOPIFY_REDIRECT_URI;

                return CommonResult.success("AUTH URI",new Gson().toJson(
                        ImmutableMap.of("id", config.SHOPIFY_CLIENT_SECRET, "uri", authUri)));
            }else{
                return CommonResult.failed("The shop name is invalid");
            }


        } catch (Exception e) {
            log.error("auth", e);
            return CommonResult.failed(e.getMessage());
        }
    }
}
