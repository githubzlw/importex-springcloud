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
import com.importexpress.cart.util.BigDecimalUtil;
import com.importexpress.cart.util.Config;
import com.importexpress.cart.util.RedisHelper;
import com.importexpress.comm.pojo.Product;
import com.importexpress.comm.pojo.SiteEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    public int addCartItem(SiteEnum site, long userId, String itemId, long num, Integer chk) {

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
                    setTouristExpire(userId, userCartKey);
                } else {
                    return FAILUT;
                }
                return SUCCESS;
            }
            //如果不存在，根据商品id取商品信息
            String[] split = itemId.split(":");
            Assert.isTrue(split.length >= 2, "The itemId invalid:" + itemId);
            Assert.isTrue(StringUtils.isNotEmpty(split[0]), "The itemId invalid:" + itemId);
            Product product = productServiceFeign.findProduct(Long.parseLong(split[0]));
            Objects.requireNonNull(product);
            CartItem cartItem = product2CartItem(site,product, num, split,chk);
            //查找同pid商品做排序处理
            List<CartItem> lstCartItem = getCartItems(site, userId);
            for (CartItem item : lstCartItem) {
                if (item.getPid() == cartItem.getPid()) {
                    cartItem.setCt(item.getCt());
                    break;
                }
            }
            redisTemplate.opsForHash().put(userCartKey, itemId, new Gson().toJson(cartItem));
            setTouristExpire(userId, userCartKey);
            return SUCCESS;
        } catch(Exception iae){
            log.error("addCartItem", iae);
            return FAILUT;
        }
    }

    /**
     * 游客的情况下，设置购物车有效期(14 days)
     * @param userId
     * @param userCartKey
     */
    private void setTouristExpire(long userId, String userCartKey) {
        String strUserid = String.valueOf(userId);
        if(StringUtils.length(strUserid)==12){
            //游客情况下有效期为14天
            redisTemplate.expire(userCartKey, 14, TimeUnit.DAYS);
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
     * getCartKeys
     * @param site
     * @return
     */
    private String getCartKeys(SiteEnum site) {
        return config.CART_PRE + ':' + site.toString().substring(0, 1).toLowerCase() + ":*";
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
    private CartItem product2CartItem(SiteEnum site,Product product, long num, String[] split, Integer chk) {

        CartItem cartItem = new CartItem();
        cartItem.setMs(NumberUtils.toInt(product.getMatchSource()));
        cartItem.setPid(product.getPid());
        cartItem.setSi(product.getShop_id());
        cartItem.setSn(product.getShop_enname());
        cartItem.setName(product.getEnname());
        cartItem.setWpri(product.getWprice());
        cartItem.setNum(num);
        cartItem.setSid1(NumberUtils.toLong(split[1]));
        if (split.length >= 3) {
            cartItem.setSid2(NumberUtils.toLong(split[2]));
        }
        cartItem.setChk(chk);
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
        changePrice(site, product, cartItem);
        long now = Instant.now().toEpochMilli();
        cartItem.setCt(now);
        cartItem.setUt(now);

        fillOthersInfoToProduct(product, cartItem, site);
        return cartItem;
    }

    /**
     * 不同网站价格不一样
     * @param site
     * @param product
     * @param cartItem
     */
    private void changePrice(SiteEnum site, Product product, CartItem cartItem) {
        if (SiteEnum.HOME == site) {
//range_price
            if (StringUtils.isNotEmpty(product.getRange_price())) {
                cartItem.setRpe(product.getRange_price());
            }
            //feeprice
            if (StringUtils.isNotEmpty(product.getWprice())) {
                cartItem.setFp(product.getWprice());
            }
        } else {
            //range_price
            if (StringUtils.isNotEmpty(product.getRange_price_free_new())) {
                cartItem.setRpe(product.getRange_price_free_new());
            }
            //feeprice
            if (StringUtils.isNotEmpty(product.getFree_price_new())) {
                cartItem.setFp(product.getFree_price_new());
            }
        }
        //sku
        if (StringUtils.isNotEmpty(product.getSku_new())) {
            cartItem.setSku(product.getSku_new());
        }


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
     * getAllUsersKey
     * @param site
     * @return
     */
    private List<String> getAllUsersKey(SiteEnum site) {

        String usersCartKey = getCartKeys(site);
        Set<String> keys = redisTemplate.keys(usersCartKey);
        assert keys != null;
        return keys.stream().map(key -> key.substring(key.lastIndexOf(':') + 1)).collect(Collectors.toList());
    }

    /**
     * fillOthers
     *
     * @param product
     * @param cartItem
     */
    private void fillOthersInfoToProduct(Product product, CartItem cartItem, SiteEnum site) {

        //Entype:[[id=32161, type=Color, value=White beard, img=560676334685/9168867283_2128907802.60x60.jpg], [id=32162, type=Color, value=greybeard, img=560676334685/9192394532_2128907802.60x60.jpg], [id=32163, type=Color, value=Blue wave point, img=560676334685/9210989827_2128907802.60x60.jpg], [id=32164, type=Color, value=Powder point, img=560676334685/9210995840_2128907802.60x60.jpg], [id=324511, type=Spec, value=59cm(23 inch | age 0-3M), img=], [id=324512, type=Spec, value=66cm(26 inch | age 3-6M), img=], [id=324513, type=Spec, value=73cm(29 inch | age 6-9M), img=], [id=324514, type=Spec, value=80cm(31 inch | age 9-12M), img=], [id=324515, type=Spec, value=85cm(33 inch | age 9-12M), img=], [id=324516, type=Spec, value=90cm(35 inch | age 1-2T), img=], [id=324517, type=Spec, value=95cm(37 inch | age 1-2T), img=]]
        final String str1 = "value=";
        final String str2 = "img=";
        final String str3 = "[]";
        final String str4 = "id=";
        String enType = product.getEntype();
        StringBuilder sb = new StringBuilder();
        ImmutableList<String> lst = ImmutableList.copyOf(Splitter.on("], [").split(enType));
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
                int endPosi=cleanStr.indexOf(',', beginIndex);
                if(endPosi == -1){
                    //后面没有内容时候
                    endPosi = cleanStr.length();
                }
                sb.append(cleanStr, beginIndex + str1.length(), endPosi).append("@");
            }
        }

        if(StringUtils.isEmpty(cartItem.getImg())){
            //无规格情况下，用主图替代
            cartItem.setImg(product.getCustom_main_image());
        }

        //设置规格
        cartItem.setTn(sb.toString().trim());

        if (StringUtils.isNotBlank(product.getRange_price_free_new())) {
            ImmutablePair<Float, Long> weiAndPri = getWeiAndPri(product.getSku_new(), cartItem, site);
            Assert.isTrue(weiAndPri != null, "weiAndPri is null. product.getSku()="+product.getSku_new() + ",cartItem=" + cartItem);
            cartItem.setWei(weiAndPri.getLeft());
            cartItem.setPri(weiAndPri.getRight());
        }else{
            //单个重量
            float finalWeight = NumberUtils.toFloat(product.getFinal_weight());
            float volumeWeight = NumberUtils.toFloat(product.getVolume_weight());
            cartItem.setVlm(BigDecimalUtil.truncateDouble(volumeWeight, 2));
            if(SiteEnum.HOME == site){
                // 如果是home网站，则volumeWeight是体积
                cartItem.setWei(Math.max(finalWeight, 0.01F));
            } else{
                cartItem.setWei(Math.max(volumeWeight, finalWeight));
            }

        }
    }

    /**
     * 不同规格价格不一样情况,需要从sku字段中解析出价格信息
     *
     * @param sku
     * @param cartItem
     * @return
     */
    private ImmutablePair<Float, Long> getWeiAndPri(String sku, CartItem cartItem, SiteEnum site) {
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
                        float finalWeight = NumberUtils.toFloat(String.valueOf(map.get("fianlWeight")));
                        float volumeWeight = NumberUtils.toFloat(String.valueOf(map.get("volumeWeight")));
                        float wei = Math.max(volumeWeight, finalWeight);
                        if (SiteEnum.HOME == site){
                            // 如果是home网站，volumeWeight表示体积
                            wei = Math.max(finalWeight, 0.01F);
                        }
                        //重新设置价格
                        Map<String, String> skuVal = new Gson().fromJson(String.valueOf(map.get("skuVal")), type);
                        Long price = Math.round(NumberUtils.toDouble(String.valueOf(skuVal.get("skuCalPrice"))) * 100);
                        cartItem.setVlm(BigDecimalUtil.truncateDouble(volumeWeight, 2));
                        return ImmutablePair.of(wei, price);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Cart> getCart(SiteEnum site) throws Exception {

        List<Cart> lstCarts = new ArrayList<>();
        Set<String> letUsers = RedisHelper.scan(this.redisTemplate,getCartKeys(site));
        for(String userId : letUsers){
            long lngUserId = Long.parseLong(userId.substring(userId.lastIndexOf(':') + 1));
            Cart cart = this.getCart(site, lngUserId);
            cart.setUserid(lngUserId);
            lstCarts.add(cart);
        }
        return lstCarts;
    }

    @Override
    public Cart getCart(SiteEnum site, long userId) throws Exception {

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
            throw e;
        }
    }

    @Override
    public int updateCartItem(SiteEnum site, long userId, String itemId, int num) {

        return updateCartItem(site, userId, itemId, num, -1,null);
    }

    @Override
    public int updateCartItem(SiteEnum site, long userId, String itemId, int num, int checked) {

        return updateCartItem(site, userId, itemId, num, checked,null);
    }

    @Override
    public int updateCartItem(SiteEnum site, long userId, String itemId, int num, int checked,String memo) {
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
            cartItem.setMemo(memo);
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
                this.addCartItem(site, userId, item.getItemId(), item.getNum(),item.getChk());
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
     * 刷新购物车（下架，价格，重量，图片）
     *
     * @param site
     * @param userId
     * @return 刷新次数
     */
    @Override
    public int refreshCart(SiteEnum site, long userId) {
        int count=0;
        try{
            Cart cart = this.getCart(site, userId);
            for (CartItem cartItem : cart.getItems()) {

                //备份bean
                CartItem cartItemOld = new CartItem();
                BeanUtils.copyProperties(cartItem, cartItemOld);

                Product product = productServiceFeign.findProduct(cartItem.getPid());

                //刷新图片，价格，重量
                fillOthersInfoToProduct(product, cartItem, site);
                //改变价格
                changePrice(site, product, cartItem);

                if ("0".equals(product.getValid())) {
                    //下架商品
                    if(cartItem.getSt() !=0 || cartItem.getChk() !=0){
                        cartItem.setSt(0);
                        cartItem.setChk(0);
                        count += this.updateCartItem(site, userId, cartItem);
                        continue;
                    }
                }

                if(!cartItemOld.equals(cartItem)){
                    //有变化的情况
                    log.info("changed cart find:userId：[{}], old [{}], new [{}]",userId,cartItemOld,cartItem);
                    count += this.updateCartItem(site, userId, cartItem);
                }
            }
        }catch(Exception e){
            log.warn("refreshCart",e);

        }
        return count;
    }

    /**
     * 刷新全网站购物车（下架，价格，重量，图片）
     *
     * @param site
     * @return 刷新次数
     */
    @Override
    public int refreshAllCarts(SiteEnum site) {

        List<String> allUsersId = this.getAllUsersKey(site);

        return allUsersId.stream().mapToInt(id -> this.refreshCart(site, Long.parseLong(id))).sum();

    }

    public static void main(String[] args){
        String sku = "[{\"specId\":\"3738129308109\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"3.71\",\"skuCalPrice\":\"3.71\",\"freeSkuPrice\":\"4.29\",\"skuMultiCurrencyDisplayPrice\":\"3.71\",\"actSkuMultiCurrencyDisplayPrice\":\"3.71\",\"skuMultiCurrencyCalPrice\":\"3.71\",\"actSkuMultiCurrencyCalPrice\":\"3.71\",\"costPrice\":\"20.0\",\"availQuantity\":678,\"isActivity\":true,\"inventory\":678},\"skuPropIds\":\"32167\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:32167\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308109\"},{\"specId\":\"3738129308113\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"3.53\",\"skuCalPrice\":\"3.53\",\"freeSkuPrice\":\"4.10\",\"skuMultiCurrencyDisplayPrice\":\"3.53\",\"actSkuMultiCurrencyDisplayPrice\":\"3.53\",\"skuMultiCurrencyCalPrice\":\"3.53\",\"actSkuMultiCurrencyCalPrice\":\"3.53\",\"costPrice\":\"19.0\",\"availQuantity\":0,\"isActivity\":true,\"inventory\":0},\"skuPropIds\":\"321614\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:321614\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308113\"},{\"specId\":\"3738129308111\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"1.67\",\"skuCalPrice\":\"1.67\",\"freeSkuPrice\":\"2.14\",\"skuMultiCurrencyDisplayPrice\":\"1.67\",\"actSkuMultiCurrencyDisplayPrice\":\"1.67\",\"skuMultiCurrencyCalPrice\":\"1.67\",\"actSkuMultiCurrencyCalPrice\":\"1.67\",\"costPrice\":\"9.0\",\"availQuantity\":0,\"isActivity\":true,\"inventory\":0},\"skuPropIds\":\"32169\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:32169\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308111\"},{\"specId\":\"3738129308115\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"5.20\",\"skuCalPrice\":\"5.20\",\"freeSkuPrice\":\"5.86\",\"skuMultiCurrencyDisplayPrice\":\"5.20\",\"actSkuMultiCurrencyDisplayPrice\":\"5.20\",\"skuMultiCurrencyCalPrice\":\"5.20\",\"actSkuMultiCurrencyCalPrice\":\"5.20\",\"costPrice\":\"28.0\",\"availQuantity\":0,\"isActivity\":true,\"inventory\":0},\"skuPropIds\":\"321613\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:321613\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308115\"},{\"specId\":\"3738129308103\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"0.85\",\"skuCalPrice\":\"0.85\",\"freeSkuPrice\":\"1.28\",\"skuMultiCurrencyDisplayPrice\":\"0.85\",\"actSkuMultiCurrencyDisplayPrice\":\"0.85\",\"skuMultiCurrencyCalPrice\":\"0.85\",\"actSkuMultiCurrencyCalPrice\":\"0.85\",\"costPrice\":\"4.6\",\"availQuantity\":8060,\"isActivity\":true,\"inventory\":8060},\"skuPropIds\":\"32161\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:32161\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308103\"},{\"specId\":\"3738129308104\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"0.85\",\"skuCalPrice\":\"0.85\",\"freeSkuPrice\":\"1.28\",\"skuMultiCurrencyDisplayPrice\":\"0.85\",\"actSkuMultiCurrencyDisplayPrice\":\"0.85\",\"skuMultiCurrencyCalPrice\":\"0.85\",\"actSkuMultiCurrencyCalPrice\":\"0.85\",\"costPrice\":\"4.6\",\"availQuantity\":6465,\"isActivity\":true,\"inventory\":6465},\"skuPropIds\":\"32162\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:32162\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308104\"},{\"specId\":\"3738129308105\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"1.11\",\"skuCalPrice\":\"1.11\",\"freeSkuPrice\":\"1.55\",\"skuMultiCurrencyDisplayPrice\":\"1.11\",\"actSkuMultiCurrencyDisplayPrice\":\"1.11\",\"skuMultiCurrencyCalPrice\":\"1.11\",\"actSkuMultiCurrencyCalPrice\":\"1.11\",\"costPrice\":\"6.0\",\"availQuantity\":6793,\"isActivity\":true,\"inventory\":6793},\"skuPropIds\":\"32163\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:32163\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308105\"},{\"specId\":\"3738129308112\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"1.67\",\"skuCalPrice\":\"1.67\",\"freeSkuPrice\":\"2.14\",\"skuMultiCurrencyDisplayPrice\":\"1.67\",\"actSkuMultiCurrencyDisplayPrice\":\"1.67\",\"skuMultiCurrencyCalPrice\":\"1.67\",\"actSkuMultiCurrencyCalPrice\":\"1.67\",\"costPrice\":\"9.0\",\"availQuantity\":2782,\"isActivity\":true,\"inventory\":2782},\"skuPropIds\":\"321610\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:321610\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308112\"},{\"specId\":\"3738129308108\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"2.38\",\"skuCalPrice\":\"2.38\",\"freeSkuPrice\":\"2.88\",\"skuMultiCurrencyDisplayPrice\":\"2.38\",\"actSkuMultiCurrencyDisplayPrice\":\"2.38\",\"skuMultiCurrencyCalPrice\":\"2.38\",\"actSkuMultiCurrencyCalPrice\":\"2.38\",\"costPrice\":\"12.8\",\"availQuantity\":4005,\"isActivity\":true,\"inventory\":4005},\"skuPropIds\":\"32166\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:32166\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308108\"},{\"specId\":\"3738129308106\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"1.11\",\"skuCalPrice\":\"1.11\",\"freeSkuPrice\":\"1.55\",\"skuMultiCurrencyDisplayPrice\":\"1.11\",\"actSkuMultiCurrencyDisplayPrice\":\"1.11\",\"skuMultiCurrencyCalPrice\":\"1.11\",\"actSkuMultiCurrencyCalPrice\":\"1.11\",\"costPrice\":\"6.0\",\"availQuantity\":5364,\"isActivity\":true,\"inventory\":5364},\"skuPropIds\":\"32164\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:32164\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308106\"},{\"specId\":\"3738129308116\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"5.20\",\"skuCalPrice\":\"5.20\",\"freeSkuPrice\":\"5.86\",\"skuMultiCurrencyDisplayPrice\":\"5.20\",\"actSkuMultiCurrencyDisplayPrice\":\"5.20\",\"skuMultiCurrencyCalPrice\":\"5.20\",\"actSkuMultiCurrencyCalPrice\":\"5.20\",\"costPrice\":\"28.0\",\"availQuantity\":3499,\"isActivity\":true,\"inventory\":3499},\"skuPropIds\":\"321611\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:321611\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308116\"},{\"specId\":\"3738129308107\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"2.38\",\"skuCalPrice\":\"2.38\",\"freeSkuPrice\":\"2.88\",\"skuMultiCurrencyDisplayPrice\":\"2.38\",\"actSkuMultiCurrencyDisplayPrice\":\"2.38\",\"skuMultiCurrencyCalPrice\":\"2.38\",\"actSkuMultiCurrencyCalPrice\":\"2.38\",\"costPrice\":\"12.8\",\"availQuantity\":5612,\"isActivity\":true,\"inventory\":5612},\"skuPropIds\":\"32165\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:32165\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308107\"},{\"specId\":\"3738129308110\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"3.71\",\"skuCalPrice\":\"3.71\",\"freeSkuPrice\":\"4.29\",\"skuMultiCurrencyDisplayPrice\":\"3.71\",\"actSkuMultiCurrencyDisplayPrice\":\"3.71\",\"skuMultiCurrencyCalPrice\":\"3.71\",\"actSkuMultiCurrencyCalPrice\":\"3.71\",\"costPrice\":\"20.0\",\"availQuantity\":1692,\"isActivity\":true,\"inventory\":1692},\"skuPropIds\":\"32168\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:32168\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308110\"},{\"specId\":\"3738129308114\",\"fianlWeight\":\"0.03\",\"skuVal\":{\"actSkuCalPrice\":\"3.53\",\"skuCalPrice\":\"3.53\",\"freeSkuPrice\":\"4.10\",\"skuMultiCurrencyDisplayPrice\":\"3.53\",\"actSkuMultiCurrencyDisplayPrice\":\"3.53\",\"skuMultiCurrencyCalPrice\":\"3.53\",\"actSkuMultiCurrencyCalPrice\":\"3.53\",\"costPrice\":\"19.0\",\"availQuantity\":2979,\"isActivity\":true,\"inventory\":2979},\"skuPropIds\":\"321612\",\"volumeWeight\":\"0.03\",\"skuAttr\":\"3216:321612\",\"wholesalePrice\":\"[≥1 $ 4.6-28.0]\",\"skuId\":\"3738129308114\"}]";
        CartItem cartItem = new CartItem();
        cartItem.setPid(573130192364L);
        cartItem.setSid1(321612);
//        ImmutablePair<Float, Long> weiAndPri = getWeiAndPri(sku, cartItem);
//        System.out.printf("weiAndPri=%s", weiAndPri);
    }
}
