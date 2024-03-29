package com.importexpress.shopify.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.importexpress.shopify.exception.ShopifyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jack.luo
 * @date 2019/3/1
 */
@Slf4j
@Service
public class ShopifyUtil {


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Config config;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }

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

        log.info("uri:[{}] token:[{}]  json:[{}]",uri,token,json);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Shopify-Access-Token", token);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        try {
            return restTemplate.postForObject(uri, requestEntity, String.class);
        } catch (Exception e) {
            log.error("postForObject",e);
            throw e;
        }

    }
    /**
     * deleteForObject
     * @param uri
     * @return
     */
    public int deleteForObject(String uri) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            BasicAuthorizationInterceptor basicAuthorizationInterceptor =
                    new BasicAuthorizationInterceptor(config.SHOPIFY_API_KEY, config.SHOPIFY_API_KEY_SECRET);
            restTemplate.getInterceptors().add(basicAuthorizationInterceptor);
            restTemplate.delete(uri);
            return 1;
        } catch (Exception e) {
            log.error("postForObject",e);
            throw e;
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
            e.printStackTrace();
            log.error("exchange",e);
            throw new ShopifyException("1003", "exchange error");
        }
    }


    /**
     * getObject
     * @param uri
     * @return
     */
    public String getObject(String uri) {

        BasicAuthorizationInterceptor basicAuthorizationInterceptor =
                new BasicAuthorizationInterceptor(config.SHOPIFY_API_KEY, config.SHOPIFY_API_KEY_SECRET);

        restTemplate.getInterceptors().add(basicAuthorizationInterceptor);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String result = restTemplate.getForObject(uri, String.class);
        log.info("result:[{}]", result);
        return result;

    }

    /**
     * postObject
     * @param uri
     * @param json
     * @return
     */
    public String postObject(String uri, String json) {

        BasicAuthorizationInterceptor basicAuthorizationInterceptor =
                new BasicAuthorizationInterceptor(config.SHOPIFY_API_KEY, config.SHOPIFY_API_KEY_SECRET);

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
