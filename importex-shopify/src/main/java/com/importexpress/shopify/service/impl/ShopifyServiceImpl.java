package com.importexpress.shopify.service.impl;


import com.google.gson.Gson;
import com.importexpress.shopify.exception.ShopifyException;
import com.importexpress.shopify.util.ShopifyUtil;
import com.importexpress.shopify.mapper.ShopifyAuthMapper;
import com.importexpress.shopify.pojo.ShopifyAuth;
import com.importexpress.shopify.pojo.ShopifyAuthExample;
import com.importexpress.shopify.pojo.orders.OrdersWraper;
import com.importexpress.shopify.pojo.product.ProductWraper;
import com.importexpress.shopify.pojo.product.ProductsWraper;
import com.importexpress.shopify.pojo.product.ShopifyBean;
import com.importexpress.shopify.service.ShopifyService;
import com.importexpress.shopify.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author luohao
 */
@Slf4j
@Service
public class ShopifyServiceImpl implements ShopifyService {

    @Autowired
    private  ShopifyAuthMapper shopifyAuthMapper;

    private final Config config;

    private final ShopifyUtil shopifyUtil;

    public ShopifyServiceImpl( Config config, ShopifyUtil shopifyUtil) {
        this.config = config;
        this.shopifyUtil = shopifyUtil;
    }


    /**
     * 取得店铺授权的token
     *
     * @param shopName
     * @param code
     * @return
     * @throws IOException
     */
    @Override
    public HashMap<String, String> getAccessToken(String shopName, String code) throws IOException {

        log.info("shopName:[{}]",shopName);

        HashMap<String, String> result = shopifyUtil.postForEntity(shopName, code);

        return result;
    }

    /**
     * 保存shopify的店铺信息到db
     * @param shopName
     * @param token
     * @param scope
     * @return
     */
    @Override
    public int saveShopifyAuth(String shopName, String token,String scope) {

        ShopifyAuthExample shopifyAuthExample = new ShopifyAuthExample();
        shopifyAuthExample.or().andShopNameEqualTo(shopName);
        List<ShopifyAuth> shopifyAuths = null;//shopifyAuthMapper.selectByExample(shopifyAuthExample);
        if(shopifyAuths!=null && shopifyAuths.size()>0){
            if(shopifyAuths.size()>1){
                throw new ShopifyException("1004", "exist many recorders in table(shopifyAuth)");
            }

            log.info("update token info by shop name:[{}]",shopName);
            ShopifyAuth shopifyAuth = shopifyAuths.get(0);
            shopifyAuth.setAccessToken(token);
            shopifyAuth.setScope(scope);
            shopifyAuth.setUpdateTime(new Date());
            return 0;//shopifyAuthMapper.updateByPrimaryKey(shopifyAuth);
        }else{
            ShopifyAuth shopifyAuth = new ShopifyAuth();
            shopifyAuth.setShopName(shopName);
            shopifyAuth.setAccessToken(token);
            shopifyAuth.setScope(scope);
            Date now = new Date();
            shopifyAuth.setCreateTime(now);
            shopifyAuth.setUpdateTime(now);
            return 0;//shopifyAuthMapper.insert(shopifyAuth);
        }


    }

    /**
     * get token by shopName
     * @param shopName
     * @return
     */
    private String getShopifyToken(String shopName) {

        List<ShopifyAuth> shopifyAuths = shopifyAuthMapper.selectByShopName(shopName);
        Assert.isTrue(shopifyAuths!=null,"select from table 's data is null");
        Assert.isTrue(shopifyAuths.size()==1,"select from table 's data > 1");
        return shopifyAuths.get(0).getAccessToken();
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
        log.info("shopName:[{}] productWraper:[{}]",shopName,productWraper);

        Gson gson = new Gson();
        String json = gson.toJson(productWraper);
        String returnJson = shopifyUtil.postForObject(String.format(config.SHOPIFY_URI_PRODUCTS, shopName), getShopifyToken(shopName), json);
        log.info("returnJson:[{}]",returnJson);
        ProductWraper result = gson.fromJson(returnJson, ProductWraper.class);
        return result;
    }


    /**
     * 获得所有产品
     *
     * @param shopName
     * @return
     */
    @Override
    public ProductsWraper getProduct(String shopName) {

        String json = shopifyUtil.exchange(String.format(config.SHOPIFY_URI_PRODUCTS, shopName), getShopifyToken(shopName));
        ProductsWraper result = new Gson().fromJson(json, ProductsWraper.class);
        return result;
    }

    /**
     * 获取所有订单
     * @param shopName
     */
    @Override
    public OrdersWraper getOrders(String shopName) {

        String url = String.format(config.SHOPIFY_URI_ORDERS, shopName);
        String json = shopifyUtil.exchange(url, getShopifyToken(shopName));
        OrdersWraper result = new Gson().fromJson(json, OrdersWraper.class);
        return result;
    }

    /**
     * insertShopifyIdWithPid
     * @param  shopifyBean
     * @return
     */
    @Override
    public int insertShopifyIdWithPid(ShopifyBean shopifyBean) {
        return 0;//shopifyAuthMapper.insertShopifyIdWithPid(shopifyBean);
    }

    /**
     * queryPidbyShopifyName
     * @param shopifyName : shopify店铺名
     * @return
     */
    @Override
    public List<ShopifyBean> queryPidbyShopifyName(String shopifyName) {
        return null;//shopifyAuthMapper.queryPidbyShopifyName(shopifyName);
    }


}
