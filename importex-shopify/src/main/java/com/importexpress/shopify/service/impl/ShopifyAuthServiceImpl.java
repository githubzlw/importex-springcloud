package com.importexpress.shopify.service.impl;


import com.importexpress.shopify.exception.ShopifyException;
import com.importexpress.shopify.mapper.ShopifyAuthMapper;
import com.importexpress.shopify.pojo.ShopifyAuth;
import com.importexpress.shopify.service.ShopifyAuthService;
import com.importexpress.shopify.util.Config;
import com.importexpress.shopify.util.ShopifyUtil;
import lombok.extern.slf4j.Slf4j;
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
public class ShopifyAuthServiceImpl implements ShopifyAuthService {
    private  ShopifyAuthMapper shopifyAuthMapper;

    private final Config config;

    private final ShopifyUtil shopifyUtil;

    public ShopifyAuthServiceImpl(ShopifyAuthMapper shopifyAuthMapper, Config config, ShopifyUtil shopifyUtil) {
        this.shopifyAuthMapper = shopifyAuthMapper;
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

        List<ShopifyAuth> shopifyAuths = shopifyAuthMapper.selectByShopName(shopName);
        if(shopifyAuths!=null && shopifyAuths.size()>0){
            if(shopifyAuths.size()>1){
                throw new ShopifyException("1004", "exist many recorders in table(shopifyAuth)");
            }

            log.info("update token info by shop name:[{}]",shopName);
            ShopifyAuth shopifyAuth = shopifyAuths.get(0);
            shopifyAuth.setAccessToken(token);
            shopifyAuth.setScope(scope);
            shopifyAuth.setUpdateTime(new Date());
            shopifyAuth.setShopName(shopName);
            return shopifyAuthMapper.updateByPrimaryKey(shopifyAuth);
        }else{
            ShopifyAuth shopifyAuth = new ShopifyAuth();
            shopifyAuth.setShopName(shopName);
            shopifyAuth.setAccessToken(token);
            shopifyAuth.setScope(scope);
            Date now = new Date();
            shopifyAuth.setCreateTime(now);
            shopifyAuth.setUpdateTime(now);
            return shopifyAuthMapper.insert(shopifyAuth);
        }
    }

    /**
     * get token by shopName
     * @param shopName
     * @return
     */
    @Override
    public String getShopifyToken(String shopName) {

        List<ShopifyAuth> shopifyAuths = shopifyAuthMapper.selectByShopName(shopName);
        Assert.isTrue(shopifyAuths!=null,"select from table 's data is null");
        Assert.isTrue(shopifyAuths.size()==1,"select from table 's data > 1");
        return shopifyAuths.get(0).getAccessToken();
    }

}
