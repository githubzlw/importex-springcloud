package com.importexpress.shopify.control;


import com.google.gson.Gson;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.shopify.pojo.ShopifyData;
import com.importexpress.shopify.pojo.ShopifyRequestWrap;
import com.importexpress.shopify.pojo.orders.OrdersWraper;
import com.importexpress.shopify.pojo.product.ProductWraper;
import com.importexpress.shopify.service.ShopifyService;
import com.importexpress.shopify.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

@Slf4j
@RestController
@RequestMapping("/shopify")
public class ShopifyController {

    @Autowired
    private ShopifyService shopifyService;

    @Autowired
    private Config config;

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @GetMapping(value = "/auth/callback")
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
        CommonResult commonResult = new CommonResult();
        SecretKeySpec keySpec = new SecretKeySpec(config.SHOPIFY_CLIENT_ID.getBytes(), HMAC_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            if (Hex.encodeHexString(rawHmac).equals(request.getParameter("hmac"))) {
                log.info("HMAC IS VERIFIED");
                HashMap<String, String> result = shopifyService.getAccessToken(shop, code);
                String accessToken = result.get("access_token");
                String scope = result.get("scope");
                shopifyService.saveShopifyAuth(shop, accessToken, scope);
                commonResult.success("HMAC IS VERIFIED,authorization successed");
            } else {
                log.warn("HMAC IS NOT VERIFIED");
                commonResult.failed("HMAC IS NOT VERIFIED");
            }

        } catch (Exception e) {
            log.error("auth", e);
            commonResult.failed(e.getMessage());
        }
        return commonResult;
    }
    /**
     * shopify铺货
     *
     * @param wrap
     */
    @PostMapping(value = "/add/product")
    public CommonResult addProduct(@RequestBody ShopifyRequestWrap wrap) {
        if (wrap == null) {
            return new CommonResult().failed("request parameter is null");
        }
        String shopname = wrap.getShopname();
        if (StringUtils.isBlank(shopname)) {
            return new CommonResult().failed("shopname is null");
        }
        ShopifyData goods = wrap.getData();
        if (goods == null || StringUtils.isBlank(goods.getPid())) {
            return new CommonResult().failed("product is null");
        }
        ProductWraper productWraper;
        try {
            productWraper = shopifyService.onlineProduct(shopname,goods);
            if(productWraper == null){
                return new CommonResult().failed("add shopify product failed");
            }
        } catch (Exception e) {
            log.error("add product", e);
            return new CommonResult().failed(e.getMessage());
        }
        return new CommonResult().success(new Gson().toJson(productWraper));
    }

}