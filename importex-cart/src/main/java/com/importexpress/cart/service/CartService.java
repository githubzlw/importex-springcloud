package com.importexpress.cart.service;

import com.importexpress.cart.pojo.Cart;
import com.importexpress.comm.pojo.SiteEnum;

import java.util.List;

/**
 * @author jack.luo
 */
public interface CartService {

    /**
     * 1:成功
     */
    public static final int SUCCESS = 1;

    /**
     * 0:失败
     */
    public static final int FAILUT = 0;

    /**
     * 添加
     *
     * @param site
     * @param userId
     * @param itemId
     * @param num
     * @return 1:成功 0:失败
     */
    int addCartItem(SiteEnum site, long userId, String itemId, long num);

    /**
     * 获取购物车数据
     * @param site
     * @return
     */
    List<Cart> getCart(SiteEnum site) throws Exception;

    /**
     * 获取购物车数据
     *
     * @param site
     * @param userId
     * @return
     */
    Cart getCart(SiteEnum site, long userId) throws Exception;

    /**
     * 更新
     *
     * @param site
     * @param userId
     * @param itemId
     * @param num
     * @return 1:成功 0:失败
     */
    int updateCartItem(SiteEnum site, long userId, String itemId, int num);

    /**
     * 更新
     *
     * @param site
     * @param userId
     * @param itemId
     * @param num
     * @param checked
     * @return 1:成功 0:失败
     */
    int updateCartItem(SiteEnum site, long userId, String itemId, int num, int checked);

    /**
     * 更新
     *
     * @param site
     * @param userId
     * @param itemId
     * @param num
     * @param checked
     * @param memo
     * @return 1:成功 0:失败
     */
    int updateCartItem(SiteEnum site, long userId, String itemId, int num, int checked,String memo);

    /**
     * 删除单个
     *
     * @param site
     * @param userId
     * @param itemId
     * @return 1:成功 0:失败
     */
    int delCartItem(SiteEnum site, long userId, String itemId);

    /**
     * 全选反选
     *
     * @param site
     * @param userId
     * @param checked
     * @return 1:成功 0:失败
     */
    int checkAll(SiteEnum site, long userId, int checked);

    /**
     * 删除全部勾选的
     *
     * @param site
     * @param userId
     * @return 1:成功 0:失败
     */
    int delChecked(SiteEnum site, long userId);

    /**
     * 清空购物车
     *
     * @param site
     * @param userId
     * @return
     */
    int delAllCartItem(SiteEnum site, long userId);

    /**
     * 购物车key重命名
     *
     * @param site
     * @param oldId
     * @param newId
     * @return
     */
    int renameCartItem(SiteEnum site, long oldId, long newId);

    /**
     * 为游客生成ID
     *
     * @param site
     * @return
     */
    long generateTouristId(SiteEnum site);

    /**
     * 合并游客购物车到用户购物车
     *
     * @param site
     * @param userId    用户id
     * @param touristId 游客id
     * @return 1:成功 0:失败
     */
    int mergeCarts(SiteEnum site, long userId, long touristId);

    /**
     * 刷新购物车（下架，价格，重量，图片）
     *
     * @param site
     * @param userId
     * @return 刷新次数
     */
    int refreshCart(SiteEnum site, long userId);

    /**
     * 刷新全网站购物车（下架，价格，重量，图片）
     *
     * @param site
     * @return 刷新次数
     */
    int refreshAllCarts(SiteEnum site);
}
