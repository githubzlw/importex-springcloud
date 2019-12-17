package com.importexpress.cart.pojo;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author jack.luo
 * @date 2019/12/16
 */
@Data
@Slf4j
public class CartProduct implements Serializable {

    private Long productId;

    private float weight;

    private String wprice;

    private int productNum;

    private Long limitNum;

    private String checked;

    private String productName;

    private String productImg;


    public BigDecimal getPrice(){

        Assert.isTrue(StringUtils.isNotEmpty(this.wprice),"The wprice must not empty");

        //sample:[1-2 $ 3.68, 3-99 $ 3.35, ≥100 $ 3.14]
        String cleanStr = CharMatcher.anyOf("[]").removeFrom(this.wprice);
        Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(cleanStr);
        for(String i:split){
            Iterable<String> item = Splitter.on('$').trimResults().omitEmptyStrings().split(i);
            ImmutableList<String> lst = ImmutableList.copyOf(item);
            if(lst.size()==2){
                String priceRange = lst.get(0);
                if(priceRange.indexOf('-')>-1){
                    //sample:[1-2 $ 3.68]
                    String[] split1 = priceRange.split("-");
                    Assert.isTrue(split1.length==2,"The array length must be 2");
                    if(Integer.parseInt(split1[1])>this.productNum){
                        return new BigDecimal(lst.get(1));
                    }
                }else if(priceRange.indexOf('≥')>-1){
                    //sample:[≥100 $ 3.14]
                    return new BigDecimal(lst.get(1));
                }
            }else{
                log.error("wprice is rong,productId:{},wprice:{}",this.productId,this.wprice);
                throw new IllegalArgumentException("wprice error");
            }
        }
        throw new IllegalArgumentException("wprice error");
    }
}
