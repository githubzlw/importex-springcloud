package com.importexpress.shopify.control;


import com.importexpress.shopify.pojo.orders.Line_items;
import com.importexpress.shopify.pojo.orders.Orders;
import com.importexpress.shopify.pojo.orders.OrdersWraper;
import com.importexpress.shopify.pojo.orders.Shipping_address;
import com.importexpress.shopify.pojo.product.*;
import com.importexpress.shopify.service.ShopifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/shopify")
public class ShopifyController {

    @Autowired
    private ShopifyService shopifyService;
    @Autowired
    private SkuJsonParse skuJsonParse;


    private String client_secret = PropertyUtils.getValueFromShopifyFile("CLIENT_SECRET");
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
    }

    /**
     * shopify铺货
     *
     * @param request
     * @param response
     */
    @PostMapping(value = "/add/product")
    @ResponseBody
    public Map<String, Object> addProduct(HttpServletRequest request, HttpServletResponse response) {
        //产品id号
        String itemId = request.getParameter("itemid");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", 200);
        //产品id号"importxtest";//
        UserBean  userBean = LoginHelp.getUserBean(request);
        if(userBean == null || StringUtils.isBlank(userBean.getShopifyName())){
            resultMap.put("status", 500);
            resultMap.put("message", "Not logged in");
            return resultMap;
        }
        String shopname = userBean.getShopifyName();
        if (StringUtils.isBlank(itemId) || StringUtils.isBlank(shopname)) {
            resultMap.put("status", 500);
            resultMap.put("message", "pid is null");
            return resultMap;
        }
        try {
            GoodsBean goods = customGoodsDriver.goodsDriver(itemId);
            Product product = new Product();
            product.setTitle(goods.getpName());
            String info_ori = goods.getInfo_ori();
            if (StringUtils.isNotBlank(info_ori)) {
                info_ori = info_ori.replace("src=\"https://img1.import-express.com/importcsvimg/webpic/newindex/img/dot.gif\"", "");
                info_ori = info_ori.replace("data-original", "src");
            } else {
                info_ori = "";
            }

            HashMap<String, String> getpDetail = goods.getpInfo();
            StringBuilder sb = new StringBuilder();
            if (getpDetail != null && !getpDetail.isEmpty()) {
                sb.append("<div>");
                Iterator<Entry<String, String>> iterator = getpDetail.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> next = iterator.next();
                    sb.append("<span>").append(next.getValue()).append("</span>");
                }
                sb.append("</div");
            }
            sb.append(info_ori);
            product.setBody_html(sb.toString());
            product.setVendor("www.import-express.com");
            String category = goods.getCategory();
            if (StringUtils.isNotBlank(category)) {
                String[] categorys = category.split("(\\^\\^)");
                category = categorys.length > 1 ? categorys[1] : categorys[0];
                categorys = category.split(">");
                product.setProduct_type(categorys[categorys.length - 1]);
            }

            List<Variants> lstVariants = skuJsonParse.sku2Variants(goods.getSkuProducts(), goods.getType(), goods.getPerWeight(), "kg");
            product.setVariants(lstVariants);

            List<Options> lstOptions = skuJsonParse.spec2Options(goods.getType());
            product.setOptions(lstOptions);
            Set<String> mageSet = new HashSet<>();
            List<Images> lstImages = new ArrayList<>();
            Images images;
            List<String> pImage = goods.getpImage();
            for (int i = 0, size = pImage.size(); i < size; i++) {
                String imgSrc = pImage.get(i).replace(".60x60", ".400x400");
                if (mageSet.contains(imgSrc)) {
                    continue;
                }
                mageSet.add(imgSrc);
                images = new Images();
                images.setSrc(imgSrc);
                lstImages.add(images);
            }
            List<TypeBean> type = goods.getType();
            if (type != null && !type.isEmpty()) {
                for (int i = 0, size = type.size(); i < size; i++) {
                    TypeBean typeBean = type.get(i);
                    if (StringUtils.isNotBlank(typeBean.getImg())) {
                        String imgSrc = typeBean.getImg().replace(".60x60", ".400x400");
                        if (mageSet.contains(imgSrc)) {
                            continue;
                        }
                        mageSet.add(imgSrc);
                        images = new Images();
                        images.setSrc(imgSrc);
                        lstImages.add(images);
                    }
                }
            }

            product.setImages(lstImages);

            ProductWraper productWraper = new ProductWraper();
            productWraper.setProduct(product);

            productWraper = shopifyService.addProduct(shopname, productWraper);
            assertNotNull(productWraper);
            System.out.println(productWraper);
            resultMap.put("productWraper", productWraper);
            // 铺货完成后，绑定店铺数据信息，方便下单后对应ID获取我们产品ID
            ShopifyBean shopifyBean = new ShopifyBean();
            shopifyBean.setShopifyName(shopname);
            shopifyBean.setShopifyPid(String.valueOf(productWraper.getProduct().getId()));
            shopifyBean.setPid(itemId);
            shopifyBean.setShopifyInfo(productWraper.getProduct().toString());
            shopifyService.insertShopifyIdWithPid(shopifyBean);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", 500);
            resultMap.put("message", e.getMessage().length() > 50 ? e.getMessage().substring(0,50) : e.getMessage());
            log.error("add product", e);
        }
        return resultMap;
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
                genShopifyOrderInfo(shopName, orders);
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