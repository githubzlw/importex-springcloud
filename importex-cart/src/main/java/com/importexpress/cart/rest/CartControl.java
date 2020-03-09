package com.importexpress.cart.rest;

import com.importexpress.cart.pojo.Cart;
import com.importexpress.cart.service.CartService;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.pojo.SiteEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.importexpress.cart.service.CartService.SUCCESS;


/**
 * @author jack.luo
 * @date 2019/12/25
 */
@RestController
@Slf4j
@Api("购物车操作接口")
@RequestMapping("/cart")
public class CartControl {


    private CartService cartService;

    @Autowired
    public CartControl(CartService shoppingCartService) {

        this.cartService = shoppingCartService;
    }

    @PostMapping("/{site}/{userId}/{itemId}")
    @ApiOperation("添加商品到购物车")
    public CommonResult addCartItem(@PathVariable(value = "site") SiteEnum site,
                                    @PathVariable(value = "userId") long userId,
                                    @PathVariable(value = "itemId") String itemId,@RequestParam Integer num) {

        int result = cartService.addCartItem(site, userId, itemId, num);
        if (result == SUCCESS) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @DeleteMapping("/{site}/{userId}/{itemId}")
    @ApiOperation("从购物车删除指定商品")
    public CommonResult delCartItem(@PathVariable(value = "site") SiteEnum site,
                                    @PathVariable(value = "userId") long userId,
                                    @PathVariable(value = "itemId") String itemId) {

        int result = cartService.delCartItem(site, userId, itemId);
        if (result == SUCCESS) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @PutMapping("/{site}/{userId}/{itemId}")
    @ApiOperation("更新购物车中指定商品")
    public CommonResult updateCartItem(@PathVariable(value = "site") SiteEnum site,
                                       @PathVariable(value = "userId") long userId,
                                       @PathVariable(value = "itemId") String itemId,
                                       @RequestParam Integer num,
                                       @RequestParam Integer checked,
                                       @RequestParam String memo) {

        int result = cartService.updateCartItem(site, userId, itemId, num, checked,memo);
        if (result == SUCCESS) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @GetMapping("/{site}/{userId}")
    @ApiOperation("查询购物车中所有商品")
    public CommonResult getCart(@PathVariable(value = "site") SiteEnum site,
                                @PathVariable(value = "userId") long userId) {

        Cart cart = cartService.getCart(site, userId);
        return CommonResult.success(cart);

    }

    @PatchMapping("/{site}/{userId}/check_all")
    @ApiOperation("勾选/反勾选全部商品")
    public CommonResult checkAll(@PathVariable(value = "site") SiteEnum site,
                                 @PathVariable(value = "userId") long userId,@RequestParam Integer checked) {

        int result = cartService.checkAll(site, userId, checked);
        if (result == SUCCESS) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @PatchMapping("/{site}/{userId}/del_checked")
    @ApiOperation("删除勾选的商品")
    public CommonResult delChecked(@PathVariable(value = "site") SiteEnum site,
                                   @PathVariable(value = "userId") long userId) {

        int result = cartService.delChecked(site, userId);
        if (result == SUCCESS) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @GetMapping("/{site}/get_tourist_id")
    @ApiOperation("生成游客的id")
    public CommonResult generateTouristId(@PathVariable(value = "site") SiteEnum site) {

        long touristId = cartService.generateTouristId(site);
        if (touristId > 0) {
            return CommonResult.success(touristId);
        } else {
            return CommonResult.failed();
        }
    }

    @DeleteMapping("/{site}/{userId}")
    @ApiOperation("清空购物车")
    public CommonResult delCart(@PathVariable(value = "site") SiteEnum site,
                                @PathVariable(value = "userId") long userId) {

        int result = cartService.delAllCartItem(site, userId);
        if (result == SUCCESS) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @GetMapping("/{site}/merge_carts")
    @ApiOperation("合并游客购物车到注册用户购物车")
    public CommonResult mergeCarts(@PathVariable(value = "site") SiteEnum site,
                                   @RequestParam Long userId,
                                   @RequestParam Long touristId) {

        int result = cartService.mergeCarts(site, userId, touristId);
        if (result == SUCCESS) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @GetMapping("/{site}/{userId}/refresh")
    @ApiOperation("刷新购物车（下架商品检查） 返回结果中如果内容有‘refreshed’则代表有下架商品，已刷新")
    public CommonResult refreshCart(@PathVariable(value = "site") SiteEnum site,
                                    @PathVariable(value = "userId") long userId) {

        int result = cartService.refreshCart(site, userId);
        if (result != -1) {
            if (result == 1) {
                return CommonResult.success("refreshed");
            } else {
                return CommonResult.success();
            }
        } else {
            return CommonResult.failed();
        }
    }

}
