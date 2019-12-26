package com.importexpress.cart.rest;

import com.importexpress.cart.pojo.Cart;
import com.importexpress.cart.service.CartService;
import com.importexpress.comm.domain.CommonResult;
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

    @GetMapping("/hello")
    public String hello() {

        return "hello world!";
    }

    @PostMapping("/{site}/{userId}/{itemId}")
    @ApiOperation("添加商品到购物车")
    public CommonResult addCartItem(@PathVariable(value = "site") char site,
                                    @PathVariable(value = "userId") long userId,
                                    @PathVariable(value = "itemId") String itemId, int num) {

        int result = cartService.addCartItem(site, userId, itemId, num);
        if (result == SUCCESS) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @DeleteMapping("/{site}/{userId}/{itemId}")
    @ApiOperation("从购物车删除指定商品")
    public CommonResult delCartItem(@PathVariable(value = "site") char site,
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
    public CommonResult updateCartItem(@PathVariable(value = "site") char site,
                                       @PathVariable(value = "userId") long userId,
                                       @PathVariable(value = "itemId") String itemId, int num, int checked) {

        int result = cartService.updateCartItem(site, userId, itemId, num, checked);
        if (result == SUCCESS) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @GetMapping("/{site}/{userId}")
    @ApiOperation("查询购物车中所有商品")
    public CommonResult getCart(@PathVariable(value = "site") char site,
                                @PathVariable(value = "userId") long userId) {

        Cart cart = cartService.getCart(site, userId);
        return CommonResult.success(cart);

    }

    @PatchMapping("/{site}/{userId}/checkall")
    @ApiOperation("勾选/反勾选全部商品")
    public CommonResult checkAll(@PathVariable(value = "site") char site,
                                 @PathVariable(value = "userId") long userId, int checked) {

        int result = cartService.checkAll(site, userId, checked);
        if (result == SUCCESS) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @PatchMapping("/{site}/{userId}/delchecked")
    @ApiOperation("删除勾选的商品")
    public CommonResult delChecked(@PathVariable(value = "site") char site,
                                   @PathVariable(value = "userId") long userId) {

        int result = cartService.delChecked(site, userId);
        if (result == SUCCESS) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }
}
