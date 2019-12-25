package com.importexpress.cart.service;

import com.importexpress.cart.pojo.Cart;

/**
 * @author jack.luo
 */
public interface CartService {

    /**
     * 添加
     *
     * @param site
     * @param userId
     * @param itemId
     * @param num
     * @return
     */
    int addCartItem(char site, long userId, String itemId, long num);

    /**
     * 获取
     *
     * @param site
     * @param userId
     * @return
     */
    Cart getCart(char site, long userId);

    /**
     * 更新
     *
     * @param site
     * @param userId
     * @param itemId
     * @param num
     * @return
     */
    int updateCartItem(char site, long userId, String itemId, int num);

    /**
     * 更新
     *
     * @param site
     * @param userId
     * @param itemId
     * @param num
     * @param checked
     * @return
     */
    int updateCartItem(char site, long userId, String itemId, int num, int checked);

    /**
     * 删除单个
     *
     * @param site
     * @param userId
     * @param itemId
     * @return
     */
    int delCartItem(char site, long userId, String itemId);

    /**
     * 全选反选
     *
     * @param site
     * @param userId
     * @param checked
     * @return
     */
    int checkAll(char site, long userId, int checked);

    /**
     * 删除全部勾选的
     *
     * @param site
     * @param userId
     * @return
     */
    int delChecked(char site, long userId);

}
