package com.importexpress.shopify.control;


import com.google.gson.Gson;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.shopify.pojo.ShopifyData;
import com.importexpress.shopify.pojo.ShopifyRequestWrap;
import com.importexpress.shopify.pojo.product.ProductWraper;
import com.importexpress.shopify.service.ShopifyProductService;
import com.importexpress.shopify.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/shopifyProduct")
public class ShopifyProductController {

    private final ShopifyProductService shopifyProductService;

    public ShopifyProductController(ShopifyProductService shopifyProductService) {
        this.shopifyProductService = shopifyProductService;
    }

    /**
     * shopify铺货
     *
     * @param wrap
     */
    @PostMapping("/product")
    public CommonResult addProduct(@RequestBody ShopifyRequestWrap wrap) {
        if (wrap == null) {
            return CommonResult.failed("request parameter is null");
        }
        String shopname = wrap.getShopname();
        if (StringUtils.isBlank(shopname)) {
            return CommonResult.failed("shopname is null");
        }
        ShopifyData goods = wrap.getData();
        if (goods == null || StringUtils.isBlank(goods.getPid())) {
            return CommonResult.failed("product is null");
        }
        ProductWraper productWraper;
        try {
            productWraper = shopifyProductService.onlineProduct(shopname,goods);
            if(productWraper == null){
                return CommonResult.failed("add shopify product failed");
            }
        } catch (Exception e) {
            log.error("add product", e);
            return CommonResult.failed(e.getMessage());
        }
        return CommonResult.success(new Gson().toJson(productWraper));
    }

}