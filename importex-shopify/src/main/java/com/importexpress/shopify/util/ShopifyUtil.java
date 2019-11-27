package com.importexpress.shopify.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importexpress.shopify.exception.ShopifyException;
import com.importexpress.shopify.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luohao
 * @date 2019/3/1
 */
@Slf4j
@Service
public class ShopifyUtil {


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Config config;


    /**
     * postForEntity
     * @param shopname
     * @param code
     * @return
     */
    public HashMap<String, String> postForEntity(String shopname, String code) {

        Map<String, String> params = new HashMap<>();
        params.put("client_id", config.SHOPIFY_CLIENT_ID);
        params.put("client_secret", config.SHOPIFY_CLIENT_SECRET);
        params.put("code", code);

        ResponseEntity<String> response =
                restTemplate.postForEntity(String.format(config.SHOPIFY_URI_OAUTH, shopname), params, String.class);


        try {
            HashMap<String, String> result = new ObjectMapper().readValue(response.getBody(), HashMap.class);
            return result;
        } catch (IOException e) {
            log.error("postForEntity",e);
            throw new ShopifyException("1001", "postForEntity error");
        }

    }

    /**
     * postForObject
     * @param uri
     * @param token
     * @param json
     * @return
     */
    public String postForObject(String uri, String token, String json) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Shopify-Access-Token", token);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        try {
            return restTemplate.postForObject(uri, requestEntity, String.class);
        } catch (Exception e) {
            log.error("postForObject",e);
            throw new ShopifyException("1002", "postForObject error");
        }

    }

    /**
     * exchange
     * @param uri
     * @param token
     * @return
     */
    public String exchange(String uri, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", token);
        HttpEntity entity = new HttpEntity(headers);
        String params = null;

        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class, params);
            return response.getBody();
        } catch (Exception e) {
            log.error("exchange",e);
            throw new ShopifyException("1003", "exchange error");
        }
    }


    /**
     * getForObjectByBAI
     * @param uri
     * @return
     */
    public String getForObjectByBAI(String uri) {

        BasicAuthorizationInterceptor basicAuthorizationInterceptor =
                new BasicAuthorizationInterceptor("ef75308bcd9586383870056f3d9823e6", "d1b1e87b24bc76bc0e485ee5a04aeede");

        restTemplate.getInterceptors().add(basicAuthorizationInterceptor);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String result = restTemplate.getForObject(uri, String.class);
        log.info("result:[{}]", result);
        return result;

    }

    /**
     * postForObjectByBAI
     * @param uri
     * @param json
     * @return
     */
    public String postForObjectByBAI(String uri, String json) {

        BasicAuthorizationInterceptor basicAuthorizationInterceptor =
                new BasicAuthorizationInterceptor("ef75308bcd9586383870056f3d9823e6", "d1b1e87b24bc76bc0e485ee5a04aeede");

        restTemplate.getInterceptors().add(basicAuthorizationInterceptor);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        String result = restTemplate.postForObject(uri, requestEntity, String.class);
        log.info("result:[{}]", result);
        return result;

    }

    /**
     * getShopName
     * @param shop
     * @return
     */
    public String getShopName(String shop){
        Assert.notNull(shop,"shop must not be null");
        return shop.substring(0, shop.indexOf(".")-1);
    }
}