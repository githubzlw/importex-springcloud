package com.importexpress.cart.util;

import com.importexpress.cart.pojo.CartProduct;
import com.importexpress.cart.pojo.TbItem;


/**
 * @author Exrick
 * @date 2017/8/25
 */
public class DtoUtil {




    public static CartProduct TbItem2CartProduct(TbItem tbItem){

        CartProduct cartProduct =new CartProduct();

        cartProduct.setProductId(tbItem.getId());
        cartProduct.setProductName(tbItem.getTitle());
        cartProduct.setSalePrice(tbItem.getPrice());
//        cartProduct.setProductImg(tbItem.getImages());
        if(tbItem.getLimitNum()==null){
            cartProduct.setLimitNum(Long.valueOf(tbItem.getNum()));
        }else if(tbItem.getLimitNum()<0&&tbItem.getNum()<0) {
            cartProduct.setLimitNum((long) 10);
        }else{
            cartProduct.setLimitNum(Long.valueOf(tbItem.getLimitNum()));
        }
        return cartProduct;
    }


}
