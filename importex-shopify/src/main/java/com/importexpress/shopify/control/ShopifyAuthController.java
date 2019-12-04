package com.importexpress.shopify.control;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.shopify.service.ShopifyAuthService;
import com.importexpress.shopify.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author luohao
 */
@Slf4j
@RestController
@RequestMapping("/api/shopify")
public class ShopifyAuthController {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final Config config;
    private final ShopifyAuthService shopifyAuthService;

    public ShopifyAuthController(Config config, ShopifyAuthService shopifyAuthService) {
        this.config = config;
        this.shopifyAuthService = shopifyAuthService;
    }

    @GetMapping(value = "/auth")
    public CommonResult auth(String code, String hmac, String state, String shop,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {

        log.info("code:{},hmac:{},state:{},shop:{}", code, hmac, state, shop);

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
                shopifyAuthService.saveShopifyAuth(shop, accessToken, scope);
                return CommonResult.success();
            } else {
                log.warn("HMAC IS NOT VERIFIED");
                return CommonResult.failed("HMAC IS NOT VERIFIED");
            }

        } catch (Exception e) {
            log.error("auth", e);
            return CommonResult.failed(e.getMessage());
        }
    }
}
