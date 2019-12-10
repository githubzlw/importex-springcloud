package com.importexpress.shopify.rest;

import com.alibaba.fastjson.JSONObject;
import com.google.common.primitives.Ints;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.util.UrlUtil;
import com.importexpress.shopify.service.ShopifyAuthService;
import com.importexpress.shopify.service.UserService;
import com.importexpress.shopify.util.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author luohao
 */
@Slf4j
@RestController
@RequestMapping("/shopify")
@Api(tags = "shopify授权调用接口")
public class ShopifyAuthController {
    private static final String SHOPIFY_COM = ".myshopify.com";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final Config config;
    private final ShopifyAuthService shopifyAuthService;
    @Autowired
    private UserService userService;

    public ShopifyAuthController(Config config, ShopifyAuthService shopifyAuthService) {
        this.config = config;
        this.shopifyAuthService = shopifyAuthService;
    }

    @PostMapping(value = "/auth")
    @ApiOperation("授权回调")
    public CommonResult auth(
            @ApiParam(name="code",value="shopify返回的code",required=true) String code,
            @ApiParam(name="hmac",value="shopify返回的hmac",required=true) String hmac,
            @ApiParam(name="state",value="shopify返回的state",required=true) String state,
            @ApiParam(name="shop",value="shopify店铺名",required=true) String shop,
            @ApiParam(name="userId",value="用户ID",required=true) String userid,
                             HttpServletRequest request, HttpServletResponse response) {

        log.info("code:{},hmac:{},state:{},shop:{},userid:{}", code, hmac, state, shop,userid);

        Map<String, String[]> parameters = request.getParameterMap();
        String data = null;
        SortedSet<String> keys = new TreeSet<String>(parameters.keySet());
        for (String key : keys) {
            if (!key.equals("hmac") && !key.equals("signature")) {
                if (data == null) {
                    data = key + "=" + request.getParameter(key);
                } else {
                    data = data + "&" + key + "=" + request.getParameter(key);
                }
            }
        }
        SecretKeySpec keySpec = new SecretKeySpec(config.SHOPIFY_CLIENT_ID.getBytes(), HMAC_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            if (Hex.encodeHexString(rawHmac).equals(request.getParameter("hmac"))) {
                log.info("HMAC IS VERIFIED");
                HashMap<String, String> result = shopifyAuthService.getAccessToken(shop, code);
                String accessToken = result.get("access_token");
                String scope = result.get("scope");
                int auth = shopifyAuthService.saveShopifyAuth(shop, accessToken, scope);
                if(auth > 0){
                    userService.updateUserShopifyFlag(Ints.tryParse(userid), shop);
                    return CommonResult.success("SAVE SHOPIFY AUTH SUCCESSED");
                }
                return CommonResult.failed("SAVE SHOPIFY AUTH ERROR");
            } else {
                log.warn("HMAC IS NOT VERIFIED");
                return CommonResult.failed("HMAC IS NOT VERIFIED");
            }

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
                return CommonResult.success( authUri);
            }else{
                return CommonResult.failed("The shop name is invalid");
            }


        } catch (Exception e) {
            log.error("auth", e);
            return CommonResult.failed(e.getMessage());
        }
    }
}
