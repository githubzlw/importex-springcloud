package com.importexpress.cart.service.impl;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.importexpress.cart.feign.ProductServiceFeign;
import com.importexpress.cart.pojo.Cart;
import com.importexpress.cart.pojo.CartItem;
import com.importexpress.cart.service.CartService;
import com.importexpress.cart.util.Config;
import com.importexpress.comm.pojo.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;


/**
 * @author jack.luo
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private final StringRedisTemplate redisTemplate;

    private final Config config;

    private final ProductServiceFeign productServiceFeign;

    public CartServiceImpl(StringRedisTemplate redisTemplate, Config config, ProductServiceFeign productServiceFeign) {
        this.redisTemplate = redisTemplate;
        this.config = config;
        this.productServiceFeign = productServiceFeign;
    }

    @Override
    public int addCart(char site,long userId, String itemId, long num) {

        checkItemId(itemId);

        String userCartKey = config.CART_PRE + ":" + site + ":" + userId;
        Boolean hexists =
                redisTemplate.opsForHash().hasKey(userCartKey, itemId);

        //如果存在数量相加itemId
        if (hexists) {
            String json = (String)redisTemplate.opsForHash().get(userCartKey, itemId + "");
            if(json!=null){
                CartItem cartItem = new Gson().fromJson(json, CartItem.class);
                cartItem.setNum(cartItem.getNum() + num);
                redisTemplate.opsForHash().put(userCartKey, itemId, new Gson().toJson(cartItem));
            }else {
                return 0;
            }
            return 1;
        }
        //如果不存在，根据商品id取商品信息
        String[] split = itemId.split(":");
        Assert.isTrue(split.length>=2,"The itemId must be pid:subid1:subid2");
        Product product = productServiceFeign.findProduct(Long.parseLong(split[0]));
        CartItem cartItem= product2CartItem(product,num,split);
        redisTemplate.opsForHash().put(userCartKey, itemId, new Gson().toJson(cartItem));
        return 1;
    }

    /**
     * checkItemId
     * @param itemId
     */
    private void checkItemId(String itemId) {
        if(StringUtils.isEmpty(itemId)){
            throw new IllegalArgumentException("itemId is empty");
        }
        if(!StringUtils.contains(itemId,':')){
            throw new IllegalArgumentException("itemId format is invalid");
        }
    }

    /**
     * product2CartItem
     * @param product
     * @param num
     * @param split
     * @return
     */
    private CartItem product2CartItem(Product product, long num, String[] split){

        CartItem cartProduct =new CartItem();

        cartProduct.setPid(product.getPid());
        cartProduct.setShoipId(product.getShop_id());
        cartProduct.setName(product.getEnname());
        cartProduct.setWei(NumberUtils.toFloat(product.getWeight()));
        cartProduct.setWPrice(product.getWprice());
        cartProduct.setNum(num);
        cartProduct.setSubId1(NumberUtils.toLong(split[1]));
        if(split.length>=3){
            cartProduct.setSubId2(NumberUtils.toLong(split[2]));
        }
        cartProduct.setChecked(1);
        fillOtherInfo(product,cartProduct);

        return cartProduct;
    }

    /**
     * fillOtherInfo
     * @param product
     * @param cartItem
     */
    private void fillOtherInfo(Product product,CartItem cartItem) {
        //sample:[[id=32161, type=Color, value=White beard, img=560676334685/9168867283_2128907802.60x60.jpg], [id=32162, type=Color, value=greybeard, img=560676334685/9192394532_2128907802.60x60.jpg], [id=32163, type=Color, value=Blue wave point, img=560676334685/9210989827_2128907802.60x60.jpg], [id=32164, type=Color, value=Powder point, img=560676334685/9210995840_2128907802.60x60.jpg], [id=324511, type=Spec, value=59cm(23 inch | age 0-3M), img=], [id=324512, type=Spec, value=66cm(26 inch | age 3-6M), img=], [id=324513, type=Spec, value=73cm(29 inch | age 6-9M), img=], [id=324514, type=Spec, value=80cm(31 inch | age 9-12M), img=], [id=324515, type=Spec, value=85cm(33 inch | age 9-12M), img=], [id=324516, type=Spec, value=90cm(35 inch | age 1-2T), img=], [id=324517, type=Spec, value=95cm(37 inch | age 1-2T), img=]]
        final String str1 = "value=";
        final String str2 = "img=";
        String entype = product.getEntype();
        StringBuilder sb = new StringBuilder();
        ImmutableList<String> lst = ImmutableList.copyOf(Splitter.on("],").split(entype));
        for(String item:lst){
            String cleanStr = CharMatcher.anyOf("[]").removeFrom(item).trim();
            if(StringUtils.contains(cleanStr,"id="+cartItem.getSubId1())
            || StringUtils.contains(cleanStr,"id="+cartItem.getSubId2())){
                //img=
                int beginIndex = cleanStr.indexOf(str2);
                String tmp = cleanStr.substring(beginIndex + str2.length());
                if(StringUtils.isNotEmpty(tmp)){
                    cartItem.setImg(tmp);
                }

                //type
                beginIndex = cleanStr.indexOf(str1);
                sb.append(cleanStr, beginIndex+str1.length(), cleanStr.indexOf(',', beginIndex)).append(" ");
            }
        }
        cartItem.setTypeName(sb.toString().trim());

    }


    @Override
    public Cart getCartList(char site, long userId) {
        String userCartKey = config.CART_PRE + ":" + site + ":" + userId;

        List<Object> jsonList = redisTemplate.opsForHash().values(userCartKey);
        Cart cart = new Cart();
        for (Object json : jsonList) {
            CartItem cartItem = new Gson().fromJson(json.toString(), CartItem.class);
            cart.addItem(cartItem);
        }
        cart.fillPrice();
        return cart;
    }

    @Override
    public int updateCartNum(char site,long userId, String itemId, int num, int checked) {

        checkItemId(itemId);

        String userCartKey = config.CART_PRE + ":" + site + ":" + userId;

        String json = (String)redisTemplate.opsForHash().get(userCartKey, itemId + "");
        if(json==null){
            return 0;
        }
        CartItem cartItem = new Gson().fromJson(json, CartItem.class);
        cartItem.setNum(num);
        cartItem.setChecked(checked);
        redisTemplate.opsForHash().put(userCartKey, itemId , new Gson().toJson(cartItem));
        return 1;
    }

    @Override
    public int checkAll(char site,long userId,int checked) {

        String userCartKey = config.CART_PRE + ":" + site + ":" + userId;

        List<Object> jsonList = redisTemplate.opsForHash().values(userCartKey);

        for (Object json : jsonList) {
            CartItem cartItem = new Gson().fromJson(json.toString(), CartItem.class);
            cartItem.setChecked(checked);
            redisTemplate.opsForHash().put(userCartKey, cartItem.getItemId(), new Gson().toJson(cartItem));
        }

        return 1;
    }

    @Override
    public int deleteCartItem(char site,long userId, String itemId) {
        checkItemId(itemId);

        String userCartKey = config.CART_PRE + ":" + site + ":" + userId;

        Long lng = redisTemplate.opsForHash().delete(userCartKey, itemId + "");
        return Integer.parseInt(lng.toString());
    }

    @Override
    public int delChecked(char site,long userId) {
        String userCartKey = config.CART_PRE + ":" + site + ":" + userId;

        List<Object> jsonList = redisTemplate.opsForHash().values(userCartKey);
        for (Object json : jsonList) {
            CartItem cartItem = new Gson().fromJson(json.toString(), CartItem.class);
            if(cartItem.getChecked()==1) {
                redisTemplate.opsForHash().delete(userCartKey, cartItem.getItemId());
            }
        }
        return 1;
    }
}
