package com.importexpress.shopify.control;


import com.importexpress.comm.domain.CommonResult;
import com.importexpress.shopify.component.ShopifyProduct;
import com.importexpress.shopify.pojo.ShopifyData;
import com.importexpress.shopify.pojo.ShopifyRequestWrap;
import com.importexpress.shopify.pojo.orders.OrdersWraper;
import com.importexpress.shopify.pojo.product.Product;
import com.importexpress.shopify.pojo.product.ProductWraper;
import com.importexpress.shopify.pojo.product.ShopifyBean;
import com.importexpress.shopify.service.ShopifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/shopify")
public class ShopifyController {

    @Autowired
    private ShopifyService shopifyService;


    /*private String client_secret = PropertyUtils.getValueFromShopifyFile("CLIENT_SECRET");
    private String client_id = PropertyUtils.getValueFromShopifyFile("CLIENT_ID");

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String SHOPIFY_COM = ".myshopify.com";
    private static List<ZoneBean> zoneBeanList = null;

    @GetMapping(value = "/auth/callback")
    public String auth(String code, String hmac, String timestamp, String state, String shop, String itemId,
                       HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        log.info("code:{},hmac:{},timestamp:{},state:{},shop:{}", code, hmac, timestamp, state, shop);

        shop=shop.replace(SHOPIFY_COM, "");

        Map<String, String[]> parameters = request.getParameterMap();
        String data = null;
        SortedSet<String> keys = new TreeSet<String>(parameters.keySet());
        for (String key : keys) {
            if (!key.equals("hmac") && !key.equals("signature")) {
                if (data == null) {
                    data = key + "=" + request.getParameter(key);
                } else {
                    data = data + "&" + key + "=" + request.getParameter(key);
                }
            }
        }

        SecretKeySpec keySpec = new SecretKeySpec(client_secret.getBytes(), HMAC_ALGORITHM);
        Mac mac = null;
        String rtUrl = "apa/shopifyBindResult.html";
        model.addAttribute("isShopify", 0);
        try {

            mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            if (Hex.encodeHexString(rawHmac).equals(request.getParameter("hmac"))) {
                log.info("HMAC IS VERIFIED");
                HashMap<String, String> result = new HashMap<>();
                try {
                    result = shopifyService.getAccessToken(shop, code);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String accessToken = result.get("access_token");
                String scope = result.get("scope");
                shopifyService.saveShopifyAuth(shop, accessToken, scope);
                // jxw begin 更新user表字段
                UserBean userBean = userWebUtil.getUserInfo(request, response);
                String preShopName = shop;
                if (shop.contains(SHOPIFY_COM)) {
                    preShopName = shop.replace(SHOPIFY_COM, "");
                }
                userService.updateUserShopifyFlag(userBean.getId(), shop);
                userBean.setShopifyName(shop);
                userBean.setShopifyFlag(1);
                // 更新session
                LoginHelp.loginSucceed(request, response, userBean);
                // jxw end
                model.addAttribute("result", "ok");
                model.addAttribute("shop", shop);

                // 获取产品信息
                if (StringUtils.isNotBlank(itemId)) {
                    GoodsBean goods = customGoodsDriver.goodsDriver(itemId);
                    model.addAttribute("isShopify", 1);
                    rtUrl = "redirect:" + goods.getpUrl();
                } else {
                    model.addAttribute("isShopify", 0);
                }
            } else {
                log.warn("HMAC IS NOT VERIFIED");
                model.addAttribute("result", "error");
                model.addAttribute("shop", shop);
            }

        } catch (Exception e) {
            log.error("auth", e);
            e.printStackTrace();
            model.addAttribute("result", "error");
            model.addAttribute("shop", shop);
        } finally {
            return rtUrl;
        }
    }*/
    /**
     * shopify铺货
     *
     * @param wrap
     */
    @PostMapping(value = "/add/product")
    public CommonResult addProduct(@RequestBody ShopifyRequestWrap wrap) {
        if (wrap == null) {
            return new CommonResult().failed("request parameter is null");
        }
        String shopname = wrap.getShopname();
        if (StringUtils.isBlank(shopname)) {
            return new CommonResult().failed("shopname is null");
        }
        ShopifyData goods = wrap.getData();
        if (goods == null || StringUtils.isBlank(goods.getPid())) {
            return new CommonResult().failed("product is null");
        }
        ProductWraper productWraper;
        try {
            productWraper = shopifyService.onlineProduct(shopname,goods);
            if(productWraper == null){
                return new CommonResult().failed("add shopify product failed");
            }
        } catch (Exception e) {
            log.error("add product", e);
            return new CommonResult().failed(e.getMessage());
        }
        return new CommonResult().success(productWraper);
    }

    /**
     * shopify获取订单列表
     *
     * @param request
     * @param response
     */
    @PostMapping(value = "/get/order")
    @ResponseBody
    public Map<String, Object> getOrders(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", 200);

        //shopify店铺名称 例如："importxtest";//
        String shopName = request.getParameter("shopname");
        if (StringUtils.isBlank(shopName)) {
            resultMap.put("status", 500);
            resultMap.put("message","shopname is null");
            return resultMap;
        }
        try {
            OrdersWraper orders = shopifyService.getOrders(shopName);
            if (orders != null && orders.getOrders() != null) {
//                genShopifyOrderInfo(shopName, orders);
            }
            resultMap.put("orders", orders);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("get order", e);
            resultMap.put("status", 500);
            resultMap.put("message", e.getMessage().length() > 50 ? e.getMessage().substring(0,50) : e.getMessage());
        }
        return resultMap;
    }


}