package com.importexpress.shopify.service;


import java.io.IOException;
import java.util.HashMap;

public interface ShopifyAuthService {

    String getShopifyName(int userId);

    HashMap<String, String> getAccessToken(String shopname, String code) throws IOException;

    int saveShopifyAuth(String shopName, String access_token, String scope);

    String getShopifyToken(String shopName);

}
