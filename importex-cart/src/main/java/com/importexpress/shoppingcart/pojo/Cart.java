package com.importexpress.shoppingcart.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luohao
 * @date 2019/12/16
 */
public class Cart implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 商品结果集
     */
    private List<CartItem> items = new ArrayList<>();

    /**
     * 添加购物项到购物车
     * @param item
     */
    public void addItem(CartItem item){
        //判断是否包含同款
        if (items.contains(item)) {
            //追加数量
            for (CartItem buyerItem : items) {
                if (buyerItem.equals(item)) {
                    buyerItem.setAmount(item.getAmount() + buyerItem.getAmount());
                }
            }
        }else {
            items.add(item);
        }

    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }


    /**
     * 商品数量
     * @return
     */
    @JsonIgnore
    public Integer getProductAmount(){
        Integer result = 0;
        //计算
        for (CartItem buyerItem : items) {
            result += buyerItem.getAmount();
        }
        return result;
    }

    /**
     * 商品金额
     * @return
     */
    @JsonIgnore
    public Float getProductPrice(){
        float result = 0f;
        //计算
        for (CartItem buyerItem : items) {
            result += buyerItem.getAmount()*buyerItem.getSku().getPrice();
        }
        return result;
    }

    /**
     *     //
     * @return
     */
    @JsonIgnore
    public Float getFee(){
        float result = 0f;
        //计算
        if (getProductPrice() < 79) {
            result = 5f;
        }

        return result;
    }

    /**
     * 总价
     * @return
     */
    @JsonIgnore
    public Float getTotalPrice(){
        return getProductPrice() + getFee();
    }

}

