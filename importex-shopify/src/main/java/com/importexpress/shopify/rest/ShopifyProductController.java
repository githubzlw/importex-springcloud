package com.importexpress.shopify.rest;


import com.google.gson.Gson;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.shopify.pojo.ShopifyData;
import com.importexpress.shopify.pojo.ShopifyRequestWrap;
import com.importexpress.shopify.pojo.product.ProductWraper;
import com.importexpress.shopify.service.ShopifyProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/shopify")
@Api(tags = "shopify铺货调用接口")
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
    @ApiOperation("铺货")
    public CommonResult addProduct(@ApiParam(name="shopifyRequestWrap",value="铺货参数",required=true) @RequestBody ShopifyRequestWrap wrap) {
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