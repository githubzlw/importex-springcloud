package com.importexpress.cart.service.impl;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.importexpress.cart.feign.ProductServiceFeign;
import com.importexpress.cart.pojo.Cart;
import com.importexpress.cart.pojo.CartItem;
import com.importexpress.cart.service.CartService;
import com.importexpress.cart.util.Config;
import com.importexpress.comm.pojo.Product;
import com.importexpress.comm.pojo.SiteEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author jack.luo
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private final Config config;

    private final StringRedisTemplate redisTemplate;

    private final ProductServiceFeign productServiceFeign;

    public CartServiceImpl(StringRedisTemplate redisTemplate, Config config, ProductServiceFeign productServiceFeign) {
        this.config = config;
        this.redisTemplate = redisTemplate;
        this.productServiceFeign = productServiceFeign;
    }

    @Override
    public int addCartItem(SiteEnum site, long userId, String itemId, long num) {

        try {
            checkItemId(itemId);

            String userCartKey = getCartKey(site, userId);
            //如果存在数量相加itemId
            if (redisTemplate.opsForHash().hasKey(userCartKey, itemId)) {
                String json = (String) redisTemplate.opsForHash().get(userCartKey, itemId);
                if (StringUtils.isNotEmpty(json)) {
                    CartItem cartItem = new Gson().fromJson(json, CartItem.class);
                    cartItem.setNum(cartItem.getNum() + num);
                    cartItem.setUt(Instant.now().toEpochMilli());
                    redisTemplate.opsForHash().put(userCartKey, itemId, new Gson().toJson(cartItem));
                } else {
                    return FAILUT;
                }
                return SUCCESS;
            }
            //如果不存在，根据商品id取商品信息
            String[] split = itemId.split(":");
            Assert.isTrue(split.length >= 2, "The itemId invalid:" + itemId);
            Product product = productServiceFeign.findProduct(Long.parseLong(split[0]));
            CartItem cartItem = product2CartItem(product, num, split);
            //查找同pid商品做排序处理
            List<CartItem> lstCartItem = getCartItems(site, userId);
            for (CartItem item : lstCartItem) {
                if (item.getPid() == cartItem.getPid()) {
                    cartItem.setCt(item.getCt());
                    break;
                }
            }
            redisTemplate.opsForHash().put(userCartKey, itemId, new Gson().toJson(cartItem));
            return SUCCESS;
        } catch (Exception e) {
            log.error("addCartItem", e);
            return FAILUT;
        }
    }

    /**
     * checkItemId
     *
     * @param itemId
     */
    private void checkItemId(String itemId) {

        if (StringUtils.isEmpty(itemId)) {
            throw new IllegalArgumentException("itemId(" + itemId + ") is empty");
        }
        if (!StringUtils.contains(itemId, ':')) {
            throw new IllegalArgumentException("itemId(" + itemId + ") format is invalid");
        }
    }

    /**
     * getCartKey
     *
     * @param site
     * @param id
     * @return
     */
    private String getCartKey(SiteEnum site, long id) {
        return config.CART_PRE + ':' + site.toString().substring(0, 1).toLowerCase() + ':' + id;
    }


    /**
     * getTouristKey
     *
     * @param site
     * @return
     */
    private String getTouristKey(SiteEnum site) {
        return config.CART_PRE + ":touristid:" + site.toString().substring(0, 1).toLowerCase();
    }

    /**
     * product2CartItem
     *
     * @param product
     * @param num
     * @param split
     * @return
     */
    private CartItem product2CartItem(Product product, long num, String[] split) {

        CartItem cartItem = new CartItem();
        cartItem.setPid(product.getPid());
        cartItem.setSi(product.getShop_id());
        cartItem.setSn(product.getShop_enname());
        cartItem.setName(product.getEnname());
        cartItem.setWei(NumberUtils.toFloat(product.getWeight()));
        cartItem.setWpri(product.getWprice());
        cartItem.setNum(num);
        cartItem.setSid1(NumberUtils.toLong(split[1]));
        if (split.length >= 3) {
            cartItem.setSid2(NumberUtils.toLong(split[2]));
        }
        cartItem.setChk(1);
        if ("0".equals(product.getValid())) {
            //下架商品
            cartItem.setSt(0);
        }
        //美加限制区分(1可搜索，0不可搜索)
        cartItem.setSl(product.getSalable());
        //add field
        cartItem.setSu(product.getSellunit());
        cartItem.setRp(product.getRemotpath());
        cartItem.setMo(product.getMorder());
        cartItem.setRpe(product.getRange_price());
        cartItem.setFp(product.getFeeprice());
        long now = Instant.now().toEpochMilli();
        cartItem.setCt(now);
        cartItem.setUt(now);
        fillOthersInfoToProduct(product, cartItem);
        return cartItem;
    }

    /**
     * getCartItems
     *
     * @param site
     * @param userId
     * @return
     */
    private List<CartItem> getCartItems(SiteEnum site, long userId) {
        String userCartKey = getCartKey(site, userId);

        List<Object> jsonList = redisTemplate.opsForHash().values(userCartKey);
        List<CartItem> lstCartItem = new ArrayList<>(jsonList.size());
        for (Object json : jsonList) {
            lstCartItem.add(new Gson().fromJson(json.toString(), CartItem.class));
        }
        return lstCartItem;
    }

    /**
     * fillOthers
     *
     * @param product
     * @param cartItem
     */
    private void fillOthersInfoToProduct(Product product, CartItem cartItem) {
        //Entype:[[id=32161, type=Color, value=White beard, img=560676334685/9168867283_2128907802.60x60.jpg], [id=32162, type=Color, value=greybeard, img=560676334685/9192394532_2128907802.60x60.jpg], [id=32163, type=Color, value=Blue wave point, img=560676334685/9210989827_2128907802.60x60.jpg], [id=32164, type=Color, value=Powder point, img=560676334685/9210995840_2128907802.60x60.jpg], [id=324511, type=Spec, value=59cm(23 inch | age 0-3M), img=], [id=324512, type=Spec, value=66cm(26 inch | age 3-6M), img=], [id=324513, type=Spec, value=73cm(29 inch | age 6-9M), img=], [id=324514, type=Spec, value=80cm(31 inch | age 9-12M), img=], [id=324515, type=Spec, value=85cm(33 inch | age 9-12M), img=], [id=324516, type=Spec, value=90cm(35 inch | age 1-2T), img=], [id=324517, type=Spec, value=95cm(37 inch | age 1-2T), img=]]
        final String str1 = "value=";
        final String str2 = "img=";
        final String str3 = "[]";
        final String str4 = "id=";
        String enType = product.getEntype();
        StringBuilder sb = new StringBuilder();
        ImmutableList<String> lst = ImmutableList.copyOf(Splitter.on("],").split(enType));
        for (String item : lst) {
            String cleanStr = CharMatcher.anyOf(str3).removeFrom(item).trim();
            if (StringUtils.contains(cleanStr, str4 + cartItem.getSid1() + ",")
                    || StringUtils.contains(cleanStr, str4 + cartItem.getSid2() + ",")) {
                //parse img path
                int beginIndex = cleanStr.indexOf(str2);
                String strImg = cleanStr.substring(beginIndex + str2.length());
                if (StringUtils.isNotEmpty(strImg)) {
                    //fill img path
                    cartItem.setImg(strImg);
                }
                //fill type
                beginIndex = cleanStr.indexOf(str1);
                sb.append(cleanStr, beginIndex + str1.length(), cleanStr.indexOf(',', beginIndex)).append(" ");
            }
        }
        cartItem.setTn(sb.toString().trim());

        if (str3.equals(product.getWprice())) {
            ImmutablePair<Float, Long> weiAndPri = getWeiAndPri(product.getSku(), cartItem);
            Assert.isTrue(weiAndPri != null, "weiAndPri is null.pid=" + product.getPid());
            cartItem.setWei(weiAndPri.getLeft());
            cartItem.setPri(weiAndPri.getRight());
        }
    }

    /**
     * 不同规格价格不一样情况,需要从sku字段中解析出价格信息
     *
     * @param sku
     * @param cartItem
     * @return
     */
    private ImmutablePair<Float, Long> getWeiAndPri(String sku, CartItem cartItem) {
        //sample: [{"skuAttr":"3216:32168", "skuPropIds":"32168", "specId":"3757601142926", "skuId":"3757601142926", "fianlWeight":"0.12","volumeWeight":"0.12", "wholesalePrice":"[≥1 $ 7.0-14.0]", "skuVal":{"actSkuCalPrice":"2.76", "actSkuMultiCurrencyCalPrice":"2.76", "actSkuMultiCurrencyDisplayPrice":"2.76", "availQuantity":0, "inventory":0, "isActivity":true, "skuCalPrice":"2.76", "skuMultiCurrencyCalPrice":"2.76", "skuMultiCurrencyDisplayPrice":"2.76", "costPrice":"14.0", "freeSkuPrice":"3.96"}]
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String>[] mapsType = new Map[0];
        Map<String, String>[] maps = new Gson().fromJson(sku, mapsType.getClass());
        for (Map<String, String> map : maps) {
            for (String key : map.keySet()) {
                if ("skuPropIds".equals(key)) {
                    if (cartItem.getItemId().equals(cartItem.getPid() + ":" + map.get(key).replace(',', ':'))) {
                        //找到规格
                        //重新设置重量
                        Float wei = NumberUtils.toFloat(String.valueOf(map.get("fianlWeight")));
                        //重新设置价格
                        Map<String, String> skuVal = new Gson().fromJson(String.valueOf(map.get("skuVal")), type);
                        Long price = Math.round(NumberUtils.toDouble(String.valueOf(skuVal.get("skuCalPrice"))) * 100);
                        return ImmutablePair.of(wei, price);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Cart getCart(SiteEnum site, long userId) {

        Cart cart = new Cart();
        try {
            List<CartItem> lstCartItem = getCartItems(site, userId);

            List<CartItem> collect = lstCartItem.stream().sorted(Comparator.comparingLong(CartItem::getCt).thenComparingLong(CartItem::getUt)).collect(Collectors.toList());
            cart.setItems(ImmutableList.copyOf(collect));
            lstCartItem.clear();
            collect.clear();
            cart.new CalculatePrice().fillCartItemsPrice();
            return cart;
        } catch (Exception e) {
            log.error("getCart", e);
            return cart;
        }
    }

    @Override
    public int updateCartItem(SiteEnum site, long userId, String itemId, int num) {

        return updateCartItem(site, userId, itemId, num, -1);
    }

    @Override
    public int updateCartItem(SiteEnum site, long userId, String itemId, int num, int checked) {
        try {
            checkItemId(itemId);

            String userCartKey = getCartKey(site, userId);

            String json = (String) redisTemplate.opsForHash().get(userCartKey, itemId);
            if (StringUtils.isEmpty(json)) {
                return FAILUT;
            }
            CartItem cartItem = new Gson().fromJson(json, CartItem.class);
            cartItem.setNum(num);
            if (checked != -1) {
                cartItem.setChk(checked);
            }
            cartItem.setUt(Instant.now().toEpochMilli());
            redisTemplate.opsForHash().put(userCartKey, itemId, new Gson().toJson(cartItem));
            return SUCCESS;
        } catch (Exception e) {
            log.error("updateCartItem", e);
            return FAILUT;
        }
    }

    /**
     * 更新购物车
     *
     * @param site
     * @param userId
     * @param cartItem
     * @return
     */
    private int updateCartItem(SiteEnum site, long userId, CartItem cartItem) {
        try {

            String userCartKey = getCartKey(site, userId);
            cartItem.setUt(Instant.now().toEpochMilli());
            redisTemplate.opsForHash().put(userCartKey, cartItem.getItemId(), new Gson().toJson(cartItem));
            return SUCCESS;
        } catch (Exception e) {
            log.error("updateCartItem", e);
            return FAILUT;
        }
    }

    @Override
    public int delCartItem(SiteEnum site, long userId, String itemId) {
        try {
            checkItemId(itemId);

            String userCartKey = getCartKey(site, userId);

            Long lng = redisTemplate.opsForHash().delete(userCartKey, itemId);
            return NumberUtils.toInt(lng.toString());
        } catch (Exception e) {
            log.error("delCartItem", e);
            return FAILUT;
        }
    }

    @Override
    public int checkAll(SiteEnum site, long userId, int checked) {

        try {
            String userCartKey = getCartKey(site, userId);

            List<Object> jsonList = redisTemplate.opsForHash().values(userCartKey);

            for (Object json : jsonList) {
                CartItem cartItem = new Gson().fromJson(json.toString(), CartItem.class);
                cartItem.setChk(checked);
                redisTemplate.opsForHash().put(userCartKey, cartItem.getItemId(), new Gson().toJson(cartItem));
            }
            return SUCCESS;

        } catch (Exception e) {
            log.error("checkAll", e);
            return FAILUT;
        }
    }

    @Override
    public int delChecked(SiteEnum site, long userId) {
        try {
            String userCartKey = getCartKey(site, userId);

            List<Object> jsonList = redisTemplate.opsForHash().values(userCartKey);
            for (Object json : jsonList) {
                CartItem cartItem = new Gson().fromJson(json.toString(), CartItem.class);
                if (cartItem.getChk() == 1) {
                    redisTemplate.opsForHash().delete(userCartKey, cartItem.getItemId());
                }
            }
            return SUCCESS;

        } catch (Exception e) {
            log.error("delChecked", e);
            return FAILUT;
        }
    }

    @Override
    public int delAllCartItem(SiteEnum site, long userId) {
        try {
            String userCartKey = getCartKey(site, userId);
            redisTemplate.delete(userCartKey);
            return SUCCESS;
        } catch (Exception e) {
            log.error("delChecked", e);
            return FAILUT;
        }
    }

    @Override
    public int renameCartItem(SiteEnum site, long oldId, long newId) {
        try {
            String newKey = getCartKey(site, newId);
            String oldKey = getCartKey(site, oldId);
            redisTemplate.rename(oldKey, newKey);
            return SUCCESS;
        } catch (Exception e) {
            log.error("delChecked", e);
            return FAILUT;
        }
    }


    /**
     * 为游客生成ID
     * <p>
     * Id规则： 一共12位，前1位为9，接着3位为网站code，最后8位为递增位
     * 例如：
     * kids网站 900200000001
     *
     * @param site
     * @return
     */
    @Override
    public long generateTouristId(SiteEnum site) {
        try {
            long userId = site.getCode() * 100000000 + 900000000000L;
            Long increment = redisTemplate.opsForValue().increment(getTouristKey(site), 1);
            Objects.requireNonNull(increment);
            return userId + increment;
        } catch (Exception e) {
            log.error("generateTouristId", e);
            return FAILUT;
        }
    }

    @Override
    public int mergeCarts(SiteEnum site, long userId, long touristId) {
        try {
            List<CartItem> cartItemsTourist = this.getCartItems(site, touristId);
            for (CartItem item : cartItemsTourist) {
                this.addCartItem(site, userId, item.getItemId(), item.getNum());
            }
            //删除游客购物车key
            redisTemplate.delete(getCartKey(site, touristId));
            return SUCCESS;
        } catch (Exception e) {
            log.error("mergeCarts", e);
            return FAILUT;
        }
    }

    /**
     * 刷新购物车（下架商品检查）
     *
     * @param site
     * @param userId
     * @return 1:发现下架（发现下架商品,购物车刷新) 0:未发现（未发现下架商品) -1:执行失败
     */
    @Override
    public int refreshCart(SiteEnum site, long userId) {
        int result = 0;
        Cart cart = this.getCart(site, userId);
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getSt() == 0) {
                //已经下架状态
                continue;
            }
            Product product = productServiceFeign.findProduct(cartItem.getPid());
            if ("0".equals(product.getValid())) {
                //下架商品
                cartItem.setSt(0);
                cartItem.setChk(0);
                if (SUCCESS == this.updateCartItem(site, userId, cartItem)) {
                    result = 1;
                } else {
                    result = -1;
                    break;
                }

            }
        }
        return result;
    }
}
