package com.importexpress.shopify.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import com.google.gson.Gson;
import com.importexpress.comm.pojo.Product;
import com.importexpress.shopify.component.MongoProductUtil;
import com.importexpress.shopify.component.ShopifyProduct;
import com.importexpress.shopify.exception.ShopifyException;
import com.importexpress.shopify.feign.ProductServiceFeign;
import com.importexpress.shopify.mapper.ShopifyProductMapper;
import com.importexpress.shopify.pojo.ShopifyData;
import com.importexpress.shopify.pojo.product.ProductWraper;
import com.importexpress.shopify.pojo.product.ShopifyBean;
import com.importexpress.shopify.service.ShopifyAuthService;
import com.importexpress.shopify.service.ShopifyProductService;
import com.importexpress.shopify.util.Config;
import com.importexpress.shopify.util.ShopifyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author luohao
 */
@Slf4j
@Service
public class ShopifyProductServiceImpl implements ShopifyProductService {

    private ShopifyProductMapper shopifyProductMapper;

    private final Config config;

    private final ShopifyUtil shopifyUtil;
    @Autowired
    private ShopifyProduct shopifyProduct;
    @Autowired
    private ShopifyAuthService shopifyAuthService;
    @Autowired
    private ProductServiceFeign productServiceFeign;


    public ShopifyProductServiceImpl(ShopifyProductMapper shopifyProductMapper, Config config, ShopifyUtil shopifyUtil) {
        this.shopifyProductMapper = shopifyProductMapper;
        this.config = config;
        this.shopifyUtil = shopifyUtil;
    }

    /**
     * 铺货到shopify
     *
     * @param shopName
     * @param productWraper
     * @return
     */
    @Override
    public ProductWraper addProduct(String shopName, ProductWraper productWraper) {

        Assert.notNull(productWraper, "product object is null");
        log.info("shopName:[{}] productWraper:[{}]", shopName, productWraper);

        Gson gson = new Gson();
        String json = gson.toJson(productWraper);
        String returnJson = shopifyUtil.postForObject(String.format(config.SHOPIFY_URI_PRODUCTS, shopName), shopifyAuthService.getShopifyToken(shopName), json);
        log.info("returnJson:[{}]", returnJson);
        ProductWraper result = gson.fromJson(returnJson, ProductWraper.class);
        return result;
    }

    /**
     * insertShopifyIdWithPid
     *
     * @param shopifyBean
     * @return
     */
    @Override
    public int insertShopifyIdWithPid(ShopifyBean shopifyBean) {
        ShopifyBean sopifyId = shopifyProductMapper.selectShopifyId(shopifyBean);
        int result = 0;
        if (sopifyId != null) {
            result = shopifyProductMapper.updateShopifyIdWithPid(shopifyBean);
        } else {
            result = shopifyProductMapper.insertShopifyIdWithPid(shopifyBean);
        }
        shopifyProductMapper.insertShopifyIdLog(shopifyBean);
        return result;
    }

    /**
     * selectShopifyId
     *
     * @param shopifyBean
     * @return
     */
    @Override
    public ShopifyBean selectShopifyId(ShopifyBean shopifyBean) {
        return shopifyProductMapper.selectShopifyId(shopifyBean);
    }

    @Override
    public ProductWraper onlineProduct(String shopname, ShopifyData goods) throws ShopifyException {
        com.importexpress.shopify.pojo.product.Product product = shopifyProduct.toProduct(goods);

        ShopifyBean shopifyBean = new ShopifyBean();
        shopifyBean.setShopifyName(shopname);
        shopifyBean.setPid(goods.getPid());
        ShopifyBean shopifyId = selectShopifyId(shopifyBean);
        if (shopifyId != null) {
            product.setId(Long.parseLong(shopifyId.getShopifyPid()));
        }
        ProductWraper productWraper = new ProductWraper();
        productWraper.setProduct(product);
        productWraper = addProduct(shopname, productWraper);

        if (productWraper != null && productWraper.getProduct() != null) {
            // 铺货完成后，绑定店铺数据信息，方便下单后对应ID获取我们产 品ID
            shopifyBean.setShopifyPid(String.valueOf(productWraper.getProduct().getId()));
            shopifyBean.setShopifyInfo(JSONObject.toJSONString(productWraper));
            insertShopifyIdWithPid(shopifyBean);
        }
        return productWraper;
    }

    @Override
    public List<ProductWraper> onlineProducts(String shopname, String[] ids, int site) throws ShopifyException {
        List<ProductWraper> wraps = Lists.newArrayList();
        List<Long> pids = Lists.newArrayList();
        for (String id : ids) {
            pids.add(Long.parseLong(id));
        }
        List<Product> mongoProducts = productServiceFeign.findProducts(Longs.toArray(pids), 1);
        for (Product product : mongoProducts) {
            ShopifyData goods = MongoProductUtil.composeShopifyData(product, site);
            ProductWraper wraper = onlineProduct(shopname, goods);
            if (wraper != null) {
                wraps.add(wraper);
            }
        }
        return wraps;
    }
}
