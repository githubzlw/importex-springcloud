package com.importexpress.shopify.rest;


import com.google.gson.Gson;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.shopify.pojo.ProductRequestWrap;
import com.importexpress.shopify.pojo.ShopifyData;
import com.importexpress.shopify.pojo.ShopifyRequestWrap;
import com.importexpress.shopify.pojo.product.ProductWraper;
import com.importexpress.shopify.pojo.product.ShopifyBean;
import com.importexpress.shopify.service.ShopifyProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * shopify铺货验证
     *
     */
    @GetMapping("/check")
    @ApiOperation("铺货")
    public CommonResult checking(@ApiParam(name="itemId",value="铺货产品id",required=true) String itemId,
                                 @ApiParam(name="shopName",value="店铺名称",required=true) String shopName) {
        if (StringUtils.isBlank(itemId) || StringUtils.isBlank(shopName)) {
            return CommonResult.failed("request parameter is null");
        }
        ShopifyBean shopifyBean;
        try {
            shopifyBean = shopifyProductService.checkProduct(shopName,itemId);
            if(shopifyBean == null){
                shopifyBean = new ShopifyBean();
                shopifyBean.setShopifyName(shopName);
                shopifyBean.setShopifyPid(itemId);
                return CommonResult.success(shopifyBean);
            }
        } catch (Exception e) {
            log.error("add product", e);
            return CommonResult.failed(e.getMessage());
        }
        return CommonResult.success(new Gson().toJson(shopifyBean));
    }

    /**
     * shopify铺货
     *
     * @param wrap
     */
    @PostMapping("/product")
    @ApiOperation("铺货")
    public CommonResult addProduct(
            @ApiParam(name="shopifyRequestWrap",value="铺货参数",required=true) @RequestBody ShopifyRequestWrap wrap) {
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
    /**
     * shopify铺货
     *
     * @param wrap
     */
    @PostMapping("/push/product")
    @ApiOperation("铺货")
    public CommonResult pushProduct(
            @ApiParam(name="productRequestWrap",value="铺货参数",required=true) @RequestBody ProductRequestWrap wrap) {
        if (wrap == null) {
            return CommonResult.failed("request parameter is null");
        }
        String shopname = wrap.getShopname();
        if (StringUtils.isBlank(shopname)) {
            return CommonResult.failed("shopname is null");
        }
        if (StringUtils.isBlank(wrap.getPid())) {
            return CommonResult.failed("product is null");
        }
        ProductWraper productWraper;
        try {
            productWraper = shopifyProductService.pushProduct(wrap);
            if(productWraper == null){
                return CommonResult.failed("add shopify product failed");
            }
        } catch (Exception e) {
            log.error("add product", e);
            return CommonResult.failed(e.getMessage());
        }
        return CommonResult.success(new Gson().toJson(productWraper));
    }
    /**
     * shopify铺货
     *
     * @param ids
     * @param site
     * @param shopName
     */
    @PostMapping("/products")
    @ApiOperation("铺货")
    public CommonResult addProductByIds(
            @ApiParam(name="ids",value="产品id数组",required=true) String ids,
            @ApiParam(name="site",value="网站",required=true) String site,
            @ApiParam(name="published",value="发布状态,1-发布 0-预发布",required=true) String published,
            @ApiParam(name="shopName",value="shopify店铺",required=true) String shopName) {
        if (ids == null) {
            return CommonResult.failed("ids is empty");
        }
        String[] idArray = ids.split(",");
        if (idArray.length < 1) {
            return CommonResult.failed("ids is empty");
        }
        if (StringUtils.isBlank(shopName)) {
            return CommonResult.failed("shopname is null");
        }
        int intSite = StrUtils.isNum(site) ? Integer.parseInt(site) : 1;
        try {
            List<ProductWraper> productWrapers =
                    shopifyProductService.onlineProducts(shopName,idArray,intSite,"1".equalsIgnoreCase(published));
            if(productWrapers == null || productWrapers.isEmpty()){
                return CommonResult.failed("add shopify product failed");
            }
            return CommonResult.success(new Gson().toJson(productWrapers));
        } catch (Exception e) {
            log.error("add product", e);
            return CommonResult.failed(e.getMessage());
        }
    }
    /**
     * 下架
     *
     * @param shopName
     * @param productId
     */
    @PostMapping("/delete")
    @ApiOperation("铺货")
    public CommonResult deleteProduct(
            @ApiParam(name="shopName",value="店铺名称",required=true)String shopName,
            @ApiParam(name="productId",value="产品id",required=true)String productId) {
        if (StringUtils.isBlank(shopName)) {
            return CommonResult.failed("shopname is null");
        }
        if (StringUtils.isBlank(productId)) {
            return CommonResult.failed("product is null");
        }
        try {
            int delete = shopifyProductService.delete(shopName, productId);
            if(delete < 1){
                return CommonResult.failed("delete shopify product failed");
            }
        } catch (Exception e) {
            log.error("delete product", e);
            return CommonResult.failed(e.getMessage());
        }
        return CommonResult.success(1);
    }

}