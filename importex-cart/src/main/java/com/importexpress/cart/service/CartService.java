package com.importexpress.cart.service;

import com.importexpress.cart.pojo.CartProduct;

import java.util.List;

public interface CartService {
    /**
     * 添加
     * @param site
     * @param userId
     * @param itemId
     * @param num
     * @return
     */
    int addCart(char site,long userId, long itemId, int num);

    /**
     * 获取
     * @param site
     * @param userId
     * @return
     */
    List<CartProduct> getCartList(char site,long userId);

    /**
     * 更新
     * @param site
     * @param userId
     * @param itemId
     * @param num
     * @param checked
     * @return
     */
    int updateCartNum(char site,long userId, long itemId, int num, String checked);

    /**
     * 删除单个
     * @param site
     * @param userId
     * @param itemId
     * @return
     */
    int deleteCartItem(char site,long userId, long itemId);

    /**
     * 全选反选
     * @param site
     * @param userId
     * @param checked
     * @return
     */
    int checkAll(char site,long userId, String checked);

    /**
     * 删除全部勾选的
     * @param site
     * @param userId
     * @return
     */
    int delChecked(char site,long userId);

}
