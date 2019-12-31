package com.importexpress.search.common;

import com.google.common.collect.Lists;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.pojo.Price;
import com.importexpress.search.util.DoubleUtil;
import com.importexpress.search.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import java.text.DecimalFormat;
import java.util.List;

@Component
public class ModefilePrice {

    private DecimalFormat format = new DecimalFormat("#0.00");

    /**
     * 将多区间价格字符串转换多区间价格List--2017-09-14
     * @param wprice 多区间价格字符串
     * @return
     */

    public List<Price> modefideWholesalePrice(String wprice) {
        if(wprice == null || "[]".equals(wprice)){
            return Lists.newArrayList();
        }
        wprice = wprice.replaceAll("[\\[\\]]", "").trim();
        if(StringUtils.isBlank(wprice)){
            return Lists.newArrayList();
        }

        List<Price> priceList = Lists.newArrayList();
        String[] prices = wprice.split(",\\s*");
        int wPriceSize = 0;
        String preQuantity = "";
        String prePrice = "";
        String quantity = "";
        for(int i=0;i<prices.length;i++){
            if(StringUtils.isBlank(prices[i])){
                continue;
            }
            String[] wholePrices = prices[i].split("(\\$)");
            if(wholePrices.length < 2){
                continue;
            }
            quantity = wholePrices[0].trim();
            if(quantity.indexOf("-") > -1 && StringUtils.isNotBlank(StrUtils.matchStr(quantity, "(\\d+-\\d+)"))){
                String[] quantitys = quantity.split("-");
                if(quantitys[0].trim().equals(quantitys[1].trim())){
                    quantity = quantitys[0];
                }
            }
            if(wPriceSize > 2){
                break;
            }
            if(!StrUtils.isFind(quantity, "(\\d+)") || !StrUtils.isMatch(wholePrices[1].trim(), "(\\d+(\\.\\d+){0,1})")){
                continue;
            }
            /*如果后一个区间定量与前一个区间定量值一样 或者  前一区间的价格与后一个去加的价格一样，
            说明多区间价格不合理，不使用多区间价格，直接使用单一价格*/
            if(preQuantity.equals(quantity) || prePrice.equals(wholePrices[1].trim())){
                continue;
            }
            prePrice = wholePrices[1].trim();
            preQuantity = quantity;
            priceList.add(new Price(prePrice,prePrice, quantity));
            wPriceSize++;
        }
        if(wPriceSize == 1){
            //只有一个区间价
            return priceList;
        }else if(wPriceSize > 1 ){
            List<String> matchStrList = StrUtils.matchStrList("(\\d+)",priceList.get(0).getQuantity());
            int quantity0_0 = Integer.valueOf(matchStrList.get(0));
            int quantity0_1 = Integer.valueOf(matchStrList.get(matchStrList.size()-1));

            matchStrList = StrUtils.matchStrList("(\\d+)",priceList.get(1).getQuantity());
            int quantity1_0 = Integer.valueOf(StrUtils.matchStr(priceList.get(1).getQuantity(), "(\\d+)"));
            int quantity1_1 = Integer.valueOf(matchStrList.get(matchStrList.size() - 1));
            int quantity2 = wPriceSize > 2 ? Integer.valueOf(StrUtils.matchStr(priceList.get(2).getQuantity(), "(\\d+)")) : 0;
            boolean userWprice = quantity1_0 > quantity0_1 && (wPriceSize == 2 || (wPriceSize == 3 && quantity2 > quantity1_1));
            if(userWprice){
                int quantityTemp = 0;
                /*第一区间 价格 定量计算*/
                quantity = priceList.get(0).getQuantity();
                quantity0_0 = Integer.valueOf(StrUtils.matchStr(quantity, "(\\d+)"));
                quantity0_1 = quantity.indexOf("-") > -1 ?
                        Integer.valueOf(StrUtils.matchStr(quantity, "(-\\d+)").replace("-","")) : quantity0_0;
                /*$3 限定定量--2017-10-20*/  //数据层面已做处理,不需要在此判断  qiqing 2018/09/21
//				if(Double.valueOf(priceList.get(0).getPrice()) * quantity0_0 < 3.00){
//					quantityTemp = (int)Math.ceil (3 / Double.valueOf(priceList.get(0).getPrice()));
//					quantityTemp = quantityTemp - quantity0_0;
//					quantity0_0 = quantity0_0 + quantityTemp;
//					if(quantity0_1 < quantity0_0){
//						quantity0_1 = quantity0_1 + quantityTemp;
//					}
//				}
                priceList.get(0).setMoq(quantity0_0);
                priceList.get(0).setQuantity(String.valueOf(quantity0_0)+"-"+quantity0_1);
                /*第一区间或者 第二区间 ,类似1piece 的情况下改为  1-1piece--2017-10-18*/
                if(wPriceSize == 2){
                    priceList.get(1).setMoq(quantity0_1+1);
                    priceList.get(1).setQuantity("≥"+(quantity0_1+1));
                }else{
                    quantity = priceList.get(1).getQuantity();
                    quantity1_0 = Integer.valueOf(StrUtils.matchStr(quantity, "(\\d+)"));
                    quantity1_1 = quantity.indexOf("-") > -1 ?
                            Integer.valueOf(StrUtils.matchStr(quantity, "(-\\d+)").replace("-","")) : quantity1_0;
                    if(quantity1_0 < quantity0_1+1){
                        quantity1_0 = quantity1_0 + quantityTemp;
                        quantity1_1 = quantity1_1 < quantity1_0 ? quantity1_1 + quantityTemp : quantity1_1;
                    }
                    priceList.get(1).setQuantity(String.valueOf(quantity1_0)+"-"+quantity1_1);
                    priceList.get(1).setMoq(quantity1_0);
                    priceList.get(2).setQuantity("≥"+(quantity1_1 + 1));
                    priceList.get(2).setMoq(quantity1_1 + 1);
                }
                /*第一区间或者 第二区间 ,1-1piece 的情况下改为  1piece(1-1、3-3之类的，都改掉，ed要求的改掉吧)--2017-11-08*/
                quantity= StrUtils.matchStr(priceList.get(0).getQuantity(), "(\\d+)");
                if((quantity+"-"+quantity).equals(priceList.get(0).getQuantity())){
                    priceList.get(0).setQuantity(quantity);
                }
                quantity= StrUtils.matchStr(priceList.get(1).getQuantity(), "(\\d+)");
                if((quantity+"-"+quantity).equals(priceList.get(1).getQuantity())){
                    priceList.get(1).setQuantity(quantity);
                }
                return priceList;
            }
        }
        return Lists.newArrayList();
    }
    public static String getRangePrice(String range_price, int isFreeShipProduct, String weight, String priceTem) {
        if(0 != isFreeShipProduct){
            if(range_price.indexOf("-")>-1){
                String[] split = range_price.split("-");
                if(split.length>1){
                    double sub1 = 0;
                    double sub2 = 0;
                    String s = split[0].replaceAll("\\s*", "");
                    if(StringUtils.isNotBlank(s)){
                        Double divide = DoubleUtil.divide(DoubleUtil.mul(Double.parseDouble(weight) * 1000, Utility.PERGRAMUSA), Utility.EXCHANGE_RATE,2);
                        sub1 = DoubleUtil.sub(Double.parseDouble(s), divide);
                    }
                    s = split[1].replaceAll("\\s*", "");
                    if(StringUtils.isNotBlank(s)){
                        Double divide = DoubleUtil.divide(DoubleUtil.mul(Double.parseDouble(weight) * 1000, Utility.PERGRAMUSA), Utility.EXCHANGE_RATE,2);
                        sub2 = DoubleUtil.sub(Double.parseDouble(s), divide);
                    }
                    priceTem = sub1 +"-"+ sub2;
                }
            }
            return range_price;
        }else {
            if(range_price.indexOf("-")>-1){
                String[] split = range_price.split("-");
                if(split.length>1){
                    double sub1 = 0;
                    double sub2 = 0;
                    String s = split[0].replaceAll("\\s*", "");
                    if(StringUtils.isNotBlank(s)){
                        Double divide = DoubleUtil.divide(DoubleUtil.mul(Double.parseDouble(weight) * 1000, Utility.PERGRAMUSA), Utility.EXCHANGE_RATE,2);
                        sub1 = DoubleUtil.add(Double.parseDouble(s), divide);
                    }
                    s = split[1].replaceAll("\\s*", "");
                    if(StringUtils.isNotBlank(s)){
                        Double divide = DoubleUtil.divide(DoubleUtil.mul(Double.parseDouble(weight) * 1000, Utility.PERGRAMUSA), Utility.EXCHANGE_RATE,2);
                        sub2 = DoubleUtil.add(Double.parseDouble(s), divide);
                    }
                    priceTem = sub1 +"-"+ sub2;
                }
            }else {
                range_price = range_price.replaceAll("\\s*", "");
                if(StringUtils.isNotBlank(range_price)){
                    Double divide = DoubleUtil.divide(DoubleUtil.mul(Double.parseDouble(weight) * 1000, Utility.PERGRAMUSA), Utility.EXCHANGE_RATE,2);
                    double sub1 = DoubleUtil.add(Double.parseDouble(range_price), divide);
                    priceTem = String.valueOf(sub1);
                }
            }
            return priceTem;
        }
    }
}
