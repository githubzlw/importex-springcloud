package com.importexpress.shopify.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
import com.google.gson.Gson;
import com.importexpress.comm.pojo.Product;
import com.importexpress.shopify.component.MongoProductUtil;
import com.importexpress.shopify.component.ShopifyProduct;
import com.importexpress.shopify.exception.ShopifyException;
import com.importexpress.shopify.feign.ProductServiceFeign;
import com.importexpress.shopify.mapper.ShopifyProductMapper;
import com.importexpress.shopify.pojo.ProductRequestWrap;
import com.importexpress.shopify.pojo.ShopifyData;
import com.importexpress.shopify.pojo.product.ProductWraper;
import com.importexpress.shopify.pojo.product.ShopifyBean;
import com.importexpress.shopify.service.ShopifyAuthService;
import com.importexpress.shopify.service.ShopifyProductService;
import com.importexpress.shopify.util.Config;
import com.importexpress.shopify.util.ShopifyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author jack.luo
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
        ProductWraper result = new ProductWraper();
        try {
            Gson gson = new Gson();
            String json = gson.toJson(productWraper);
            String returnJson = shopifyUtil.postForObject(String.format(config.SHOPIFY_URI_PRODUCTS, shopName), shopifyAuthService.getShopifyToken(shopName), json);
            log.info("returnJson:[{}]", returnJson);
            result = gson.fromJson(returnJson, ProductWraper.class);

        }catch (Exception e){
            log.error("postForObject",e);
            throw e;
        }
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
//        product.setTitle("AAAAAAAA"+product.getTitle());
        ShopifyBean  shopifyBean = new ShopifyBean();
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
            shopifyBean.setPublish(product.isPublished() ? 1 : 0);
            insertShopifyIdWithPid(shopifyBean);
        }
        return productWraper;
    }
    @Override
    public ProductWraper pushProduct(ProductRequestWrap wrap) throws ShopifyException {
        //验证是否已经铺货过
        ProductWraper productWraper = checkPush(wrap.getShopname(), wrap.getPid());
        if(productWraper != null){
            return productWraper;
        }
        Product mongoProducts = productServiceFeign.findProduct(Long.parseLong(wrap.getPid()));
        ShopifyData goods = MongoProductUtil.composeShopifyData(mongoProducts, wrap.getSite());
        goods.setSkus(wrap.getSkus());
        goods.setPublished(wrap.isPublished());
        return onlineProduct(wrap.getShopname(),goods);
    }
    @Override
    public ShopifyBean checkProduct(String shopname, String itemId) throws ShopifyException {
        ShopifyBean  shopifyBean = new ShopifyBean();
        shopifyBean.setShopifyName(shopname);
        shopifyBean.setPid(itemId);
        return selectShopifyId(shopifyBean);
    }

    @Override
    public List<ProductWraper> onlineProducts(String shopname, String[] ids, int site,boolean published) throws ShopifyException {
        List<ProductWraper> wraps = Lists.newArrayList();
        List<Long> pids = Lists.newArrayList();
        for (String id : ids) {
            ProductWraper productWraper = checkPush(shopname,id);
            if(productWraper != null){
                wraps.add(productWraper);
                continue;
            }
            pids.add(Long.parseLong(id));
        }
        List<Product> mongoProducts = productServiceFeign.findProducts(Longs.toArray(pids), 1);
        for (Product product : mongoProducts) {
            ShopifyData goods = MongoProductUtil.composeShopifyData(product, site);
            goods.setPublished(published);
            ProductWraper wraper = onlineProduct(shopname, goods);
            if (wraper != null) {
                wraps.add(wraper);
            }
        }
        return wraps;
    }
    private ProductWraper checkPush(String shopName,String pid){
        ShopifyBean shopifyBean = checkProduct(shopName, pid);
        if(shopifyBean != null && StringUtils.isNotBlank(shopifyBean.getShopifyPid())){
            ProductWraper wraper = new ProductWraper();
            if(StringUtils.isNotBlank(shopifyBean.getShopifyInfo())){
                wraper = JSON.parseObject(shopifyBean.getShopifyInfo(),ProductWraper.class);
            }
            wraper.setPush(true);
            return wraper;
        }
        return null;
    }
    @Override
    public int delete(String shopname, String id){
        ShopifyBean shopifyBean = new ShopifyBean();
        shopifyBean.setShopifyName(shopname);
        shopifyBean.setPid(id);
        shopifyBean = shopifyProductMapper.selectShopifyId(shopifyBean);
        Assert.notNull(id, "id is null");
        Assert.notNull(shopname, "shopname is null");
        log.info("shopName:[{}] productId:[{}]", shopname, id);
        int result = 0;
        try {
            result = shopifyUtil.deleteForObject(String.format(config.SHOPIFY_URI_DELETE,
                    shopname,shopifyBean.getShopifyPid()),
                    shopifyAuthService.getShopifyToken(shopname));
            if(result > 0){
                shopifyBean.setPublish(-1);
                shopifyProductMapper.deleteShopifyIdWithPid(shopname,shopifyBean.getShopifyPid());
                shopifyProductMapper.insertShopifyIdLog(shopifyBean);
            }
        }catch (Exception e){
            log.error("postForObject",e);
            throw e;
        }
        return result;

    }
}
