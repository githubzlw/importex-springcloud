package com.importexpress.shoppingcart.service.impl;

import com.google.gson.Gson;
import com.importexpress.shoppingcart.pojo.CartProduct;
import com.importexpress.shoppingcart.pojo.TbItem;
import com.importexpress.shoppingcart.service.CartService;
import com.importexpress.shoppingcart.util.Config;
import com.importexpress.shoppingcart.util.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CartServiceImpl implements CartService {

    private final StringRedisTemplate redisTemplate;

    private final Config config;

    public CartServiceImpl(StringRedisTemplate redisTemplate, Config config) {
        this.redisTemplate = redisTemplate;
        this.config = config;
    }

    @Override
    public int addCart(char site,long userId, long itemId, int num) {

        String userCartKey = config.CART_PRE + ":" + site + ":" + userId;
        String hashKey=itemId + "";
        Boolean hexists =
                redisTemplate.opsForHash().hasKey(userCartKey, hashKey);

        //如果存在数量相加
        if (hexists) {
            String json = (String)redisTemplate.opsForHash().get(userCartKey, itemId + "");
            if(json!=null){
                CartProduct cartProduct = new Gson().fromJson(json,CartProduct.class);
                cartProduct.setProductNum(cartProduct.getProductNum() + num);
                redisTemplate.opsForHash().put(userCartKey, hashKey, new Gson().toJson(cartProduct));
            }else {
                return 0;
            }

            return 1;
        }


        //如果不存在，根据商品id取商品信息
        TbItem item = null;
        //itemMapper.selectByPrimaryKey(itemId);
        if(item==null){
            return 0;
        }
        CartProduct cartProduct= DtoUtil.TbItem2CartProduct(item);
        cartProduct.setProductNum((long) num);
        cartProduct.setChecked("1");
        redisTemplate.opsForHash().put(userCartKey, hashKey, new Gson().toJson(cartProduct));
        return 1;
    }

    @Override
    public List<CartProduct> getCartList(char site,long userId) {
        String userCartKey = config.CART_PRE + ":" + site + ":" + userId;

        List<Object> jsonList = redisTemplate.opsForHash().values(userCartKey);
        List<CartProduct> list = new ArrayList<>();
        for (Object json : jsonList) {
            CartProduct cartProduct = new Gson().fromJson(json.toString(),CartProduct.class);
            list.add(cartProduct);
        }
        return list;
    }

    @Override
    public int updateCartNum(char site,long userId, long itemId, int num, String checked) {
        String userCartKey = config.CART_PRE + ":" + site + ":" + userId;

        String json = (String)redisTemplate.opsForHash().get(userCartKey, itemId + "");
        if(json==null){
            return 0;
        }
        CartProduct cartProduct = new Gson().fromJson(json,CartProduct.class);
        cartProduct.setProductNum((long) num);
        cartProduct.setChecked(checked);
        redisTemplate.opsForHash().put(userCartKey, itemId + "", new Gson().toJson(cartProduct));
        return 1;
    }

    @Override
    public int checkAll(char site,long userId,String checked) {

        String userCartKey = config.CART_PRE + ":" + site + ":" + userId;

        List<Object> jsonList = redisTemplate.opsForHash().values(userCartKey);

        for (Object json : jsonList) {
            CartProduct cartProduct = new Gson().fromJson(json.toString(),CartProduct.class);
            if("true".equals(checked)) {
                cartProduct.setChecked("1");
            }else if("false".equals(checked)) {
                cartProduct.setChecked("0");
            }else {
                return 0;
            }
            redisTemplate.opsForHash().put(userCartKey, cartProduct.getProductId() + "", new Gson().toJson(cartProduct));
        }

        return 1;
    }

    @Override
    public int deleteCartItem(char site,long userId, long itemId) {
        String userCartKey = config.CART_PRE + ":" + site + ":" + userId;

        Long lng = redisTemplate.opsForHash().delete(userCartKey, itemId + "");
        return Integer.parseInt(lng.toString());
    }

    @Override
    public int delChecked(char site,long userId) {
        String userCartKey = config.CART_PRE + ":" + site + ":" + userId;

        List<Object> jsonList = redisTemplate.opsForHash().values(userCartKey);
        for (Object json : jsonList) {
            CartProduct cartProduct = new Gson().fromJson(json.toString(),CartProduct.class);
            if("1".equals(cartProduct.getChecked())) {
                redisTemplate.opsForHash().delete(userCartKey, cartProduct.getProductId()+"");
            }
        }
        return 1;
    }
}
