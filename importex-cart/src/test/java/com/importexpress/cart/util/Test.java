package com.importexpress.cart.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.springframework.util.Assert;

/**
 * @Author jack.luo
 * @create 2020/8/7 15:36
 * Description
 */
public class Test {

    public static void main(String[] args){
        //sample:[1-2 $ 3.68, 3-99 $ 3.35, ≥100 $ 3.14]
        //String str = "[1-2 $ 3.68, 3-99 $ 3.35, ≥100 $ 3.14]";
        String str = "[1-2 $ 4.00]";
        String cleanStr = CharMatcher.anyOf("[]").removeFrom(str);
        Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(cleanStr);
        System.out.println(split);

        int productNum=10;
        long reseult=0;
        for (String i : split) {
            Iterable<String> item = Splitter.on('$').trimResults().omitEmptyStrings().split(i);
            ImmutableList<String> lst = ImmutableList.copyOf(item);
            if (lst.size() == 2) {
                String priceRange = lst.get(0);
                if (priceRange.indexOf('-') > -1 && priceRange.indexOf('≥')==-1) {
                    //sample:[1-2 $ 3.68]
                    String[] split1 = priceRange.split("-");
                    Assert.isTrue(split1.length == 2, "The array length must be 2");
                    if (Integer.parseInt(split1[1]) >= productNum) {
                        reseult= Math.round(Double.parseDouble(lst.get(1)) * 100);
                        break;
                    }
                } else if (priceRange.indexOf('≥') > -1) {
                    //sample:[≥100 $ 3.14]
                    reseult= Math.round(Double.parseDouble(lst.get(1)) * 100);
                    break;
                } else {
                    if (Integer.parseInt(priceRange) >= productNum) {
                        reseult= Math.round(Double.parseDouble(priceRange) * 100);
                        break;
                    }
                }
            } else {
                throw new NumberFormatException("wprice error");
            }
        }
        System.out.println(reseult);
    }
}
