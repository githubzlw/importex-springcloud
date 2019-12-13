package com.importexpress.search.common;

import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.pojo.Currency;
import com.importexpress.search.pojo.GoodsPriceRange;
import com.importexpress.search.pojo.Price;
import com.importexpress.search.pojo.Product;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 价格切换货币
 */
@Component
public class ChangeCurrency {

    /**搜索结果数据切换货币
     * @param product
     * @param currency
     * @return
     */
    public static Product chang(Product product,Currency currency){
        Assert.notNull(product,"product is null");
        double exchangeRate = currency.getExchangeRate();
        product.setPrice(rangePrice(product.getPrice(),exchangeRate));
        product.setWholesalePrice(wholesalePrice(product.getWholesalePrice(),exchangeRate));
        product.setCurrencySymbol(currency.getSymbol());
        product.setWholesaleMiddlePrice(rangePrice(product.getWholesaleMiddlePrice(),exchangeRate));
        return product;
    }
    /**搜索结果数据切换货币
     * @param goodsPriceRange
     * @param currency
     * @return
     */
   public static GoodsPriceRange chang(GoodsPriceRange goodsPriceRange, Currency currency){
        Assert.notNull(goodsPriceRange,"goodsPriceRange is null");
       double exchangeRate = currency.getExchangeRate();
        goodsPriceRange.setSectionOnePrice(
                Double.parseDouble(rangePrice(String.valueOf(goodsPriceRange.getSectionOnePrice()),exchangeRate)));
        goodsPriceRange.setSectionTwoPrice(
                Double.parseDouble(rangePrice(String.valueOf(goodsPriceRange.getSectionTwoPrice()),exchangeRate)));
        goodsPriceRange.setSectionThreePrice(
                Double.parseDouble(rangePrice(String.valueOf(goodsPriceRange.getSectionThreePrice()),exchangeRate)));
        return goodsPriceRange;
    }

    /**区间价格汇率
     * @param price
     * @param exchangeRate
     * @return
     */
    private static String rangePrice(String price,double exchangeRate){
        if(!StrUtils.isRangePrice(price)){
            return null;
        }
        String[] priceArray = price.split("-");
        price = calculation(priceArray[0],exchangeRate);

        if(priceArray.length > 1){
            price = price+"-"+calculation(priceArray[1],exchangeRate);
        }
        return price;
    }
    /**批发价格汇率
     * @param lstPrice
     * @param exchangeRate
     * @return
     */
    private static List<Price> wholesalePrice(List<Price> lstPrice, double exchangeRate){
        if(lstPrice == null){
            return lstPrice;
        }
        lstPrice.stream().forEach(l ->{
            String price = l.getPrice();
            l.setPrice(calculation(price,exchangeRate));
            l.setFactoryPrice(calculation(l.getFactoryPrice(),exchangeRate));
        });
        return lstPrice;
    }


    private static DecimalFormat format = new DecimalFormat("#0.00");
    /**
     * 计算汇率后价格
     * @param price
     * @param exchangeRate
     * @return
     */
    private static String calculation(String price,double exchangeRate){
        price = StrUtils.isMatch(price,"(\\d+(\\.\\d+){0,1})")?price : "0";
        BigDecimal resultPrice = new BigDecimal(price);
        resultPrice = resultPrice.multiply(new BigDecimal(exchangeRate));
        return format.format(resultPrice.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
    }
    /**
     * 计算汇率后价格
     * @param price
     * @param exchangeRate
     * @return
     */
    private static String calculationBefore(String price,double exchangeRate){
        price = StrUtils.isMatch(price,"(\\d+(\\.\\d+){0,1})")?price : "0";
        BigDecimal resultPrice = new BigDecimal(price);
        resultPrice = resultPrice.divide(new BigDecimal(String.valueOf(exchangeRate)),2,BigDecimal.ROUND_HALF_UP);
        return resultPrice.toString();
    }
    /**美元价格
     * @param price
     * @param exchangeRate
     * @return
     */
    public static String priceToUSD(String price,double exchangeRate){
        if(!StrUtils.isRangePrice(price)){
            return null;
        }
        price = calculationBefore(price,exchangeRate);
        return price;
    }

}
