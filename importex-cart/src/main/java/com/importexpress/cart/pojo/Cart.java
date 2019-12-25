package com.importexpress.cart.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jack.luo
 * @date 2019/12/16
 */
@Slf4j
@Data
public class Cart {

    /**
     * 商品结果集
     */
    private List<CartItem> items = Lists.newArrayList();

    /**
     * 商品数量
     *
     * @return
     */
    @JsonIgnore
    public long getTotalAmount() {
        long result = 0;
        //计算
        for (CartItem cartItem : items) {
            result += cartItem.getNum();
        }
        return result;
    }

    /**
     * 总重量
     *
     * @return
     */
    @JsonIgnore
    public float getTotalWeight() {
        float result = 0.0f;
        //计算
        for (CartItem cartItem : items) {
            result += cartItem.getWei() * cartItem.getNum();
        }
        return result;
    }

    /**
     * 总价
     *
     * @return
     */
    @JsonIgnore
    public long getTotalPrice() {

        return getProductPrice() + getFee();
    }

    /**
     * 商品金额
     *
     * @return
     */
    @JsonIgnore
    public long getProductPrice() {
        long result = 0;
        //计算
        for (CartItem cartItem : items) {
            result += cartItem.getPri() * cartItem.getNum();
        }
        return result;
    }

    /**
     * 手续费
     *
     * @return
     */
    @JsonIgnore
    public long getFee() {
        return 0L;
    }

    /**
     * 计算购物车商品实际价格（数量，同pid）
     */
    public class CalculatePrice {

        /**
         * 根据数量和区间价格计算实际的价格
         */
        public void fillCartItemsPrice() {

            Map<Long, LongSummaryStatistics> collect = items.stream().collect(Collectors.groupingBy(CartItem::getPid, Collectors.summarizingLong(CartItem::getNum)));

            items.forEach(i -> {
                if (collect.containsKey(i.getPid())) {
                    i.setPri(calculatePrice(i.getWpri(), collect.get(i.getPid()).getSum()));
                }
            });
        }

        /**
         * calculatePrice
         *
         * @param wprice
         * @param productNum
         * @return
         */
        private long calculatePrice(String wprice, long productNum) {

            Assert.isTrue(StringUtils.isNotEmpty(wprice), "The wprice must not empty");

            //sample:[1-2 $ 3.68, 3-99 $ 3.35, ≥100 $ 3.14]
            String cleanStr = CharMatcher.anyOf("[]").removeFrom(wprice);
            Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(cleanStr);
            for (String i : split) {
                Iterable<String> item = Splitter.on('$').trimResults().omitEmptyStrings().split(i);
                ImmutableList<String> lst = ImmutableList.copyOf(item);
                if (lst.size() == 2) {
                    String priceRange = lst.get(0);
                    if (priceRange.indexOf('-') > -1) {
                        //sample:[1-2 $ 3.68]
                        String[] split1 = priceRange.split("-");
                        Assert.isTrue(split1.length == 2, "The array length must be 2");
                        if (Integer.parseInt(split1[1]) >= productNum) {
                            return Math.round(Double.parseDouble(lst.get(1)) * 100);
                        }
                    } else if (priceRange.indexOf('≥') > -1) {
                        //sample:[≥100 $ 3.14]
                        return Math.round(Double.parseDouble(lst.get(1)) * 100);
                    } else {
                        if (Integer.parseInt(priceRange) >= productNum) {
                            return Math.round(Double.parseDouble(priceRange) * 100);
                        }
                    }
                } else {
                    log.error("wprice is rong,wprice:{}", wprice);
                    throw new NumberFormatException("wprice error");
                }
            }
            throw new NumberFormatException("wprice error");
        }
    }

}

