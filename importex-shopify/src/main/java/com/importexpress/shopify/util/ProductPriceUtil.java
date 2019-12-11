package com.importexpress.shopify.util;

import com.importexpress.comm.util.StrUtils;
import com.importexpress.shopify.pojo.ImportProductBean;
import com.importexpress.shopify.pojo.PriceBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.util
 * @date:2019/12/11
 */
@Slf4j
public class ProductPriceUtil {

    /**
     * Trail freight
     */
    public static final BigDecimal TRAILFREIGHT = BigDecimal.valueOf(4.31d);

    /**
     * Trail freight rate
     */
    public static final BigDecimal TRAILFREIGHTRATE = BigDecimal.valueOf(0.01d);

    /**
     * 入仓库手续费
     */
    public static final BigDecimal ENTERWAREHOUSEFEE = BigDecimal.valueOf(0.25d);
    /**
     * 出仓库手续费
     */
    public static final BigDecimal WAREHOUSEHANDLINGFEE = BigDecimal.valueOf(0.06d);


    /**
     * 获取海外仓价格
     *
     * @param product
     */
    public static ImportProductBean processOverSeaPrice(ImportProductBean product) {

        try {
            String firstPrice;
            if (StringUtils.isNotBlank(product.getRange_price_free())) {
                //Rewriter
                //Rewrite <V1.0.1> Start：cjc 2019/11/28 11:27 上午 错误修改
                if (product.getRange_price_free().contains("-")) {
                    firstPrice = product.getRange_price_free().split("-")[1];
                } else {
                    firstPrice = product.getRange_price_free();
                }
                //End：
							/*if (product.getRange_price().contains("-")) {
								firstPrice = product.getRange_price_free().split("-")[1];
							} else {
								firstPrice = product.getRange_price_free();
							}*/
            } else if (StringUtils.isNotBlank(product.getFeeprice())) {
                List<PriceBean> modefideWholesalePrice = modefideWholesalePrice(product.getFeeprice());
                if (modefideWholesalePrice != null && modefideWholesalePrice.size() > 0) {
                    firstPrice = modefideWholesalePrice.get(0).getPrice();
                    modefideWholesalePrice.clear();
                } else {
                    firstPrice = product.getPrice();
                }
            } else {
                firstPrice = product.getPrice();
            }
            if (StringUtils.isNotBlank(firstPrice)) {
                try {
                    BigDecimal price = new BigDecimal(firstPrice);
                } catch (Exception e) {
                    log.error("setOverSeaPrice product error ,pid:[{}] ,firstPrice:[{}]", product.getPid(), firstPrice);
                    firstPrice = StringUtils.EMPTY;
                }
            }
            String overSeaPrice = getOverseasWarehouseProeuctPrice(firstPrice, Double.parseDouble(product.getFinal_weight()));
            product.setOverSeaPrice(overSeaPrice);
            return product;
        } catch (Exception e) {
            log.error("pid:" + product.getPid() + "setOverSeaPrice error", e);
            return product;
        }

    }


    /**
     * 将多区间价格字符串转换多区间价格List
     *
     * @param wprice 多区间价格字符串
     * @return
     */

    private static List<PriceBean> modefideWholesalePrice(String wprice) {
        if (wprice == null) {
            return null;
        }
        wprice = wprice.replaceAll("[\\[\\]]", "").trim();
        if (StringUtils.isBlank(wprice)) {
            return null;
        }

        List<PriceBean> priceList = new ArrayList<PriceBean>();
        String[] prices = wprice.split(",\\s*");
        int wPriceSize = 0;
        String preQuantity = "";
        String prePrice = "";
        String quantity = "";
        for (int i = 0; i < prices.length; i++) {
            if (StringUtils.isBlank(prices[i])) {
                continue;
            }
            String[] wholePrices = prices[i].split("(\\$)");
            if (wholePrices.length < 2) {
                continue;
            }
            quantity = wholePrices[0].trim();
            if (quantity.indexOf("-") > -1 && StringUtils.isNotBlank(StrUtils.matchStr(quantity, "(\\d+-\\d+)"))) {
                String[] quantitys = quantity.split("-");
                if (quantitys[0].trim().equals(quantitys[1].trim())) {
                    quantity = quantitys[0];
                }
            }
            if (wPriceSize > 2) {
                break;
            }
            if (!StrUtils.isFind(quantity, "(\\d+)") || !StrUtils.isMatch(wholePrices[1].trim(), "(\\d+(\\.\\d+){0,1})")) {
                continue;
            }
            /*如果后一个区间定量与前一个区间定量值一样 或者  前一区间的价格与后一个去加的价格一样，说明多区间价格不合理，不使用多区间价格，直接使用单一价格*/
            if (preQuantity.equals(quantity) || prePrice.equals(wholePrices[1].trim())) {
                continue;
            }
            prePrice = wholePrices[1].trim();
            preQuantity = quantity;
            priceList.add(new PriceBean(wholePrices[1].trim(), quantity));
            wPriceSize++;
        }
        if (wPriceSize == 1) {
            //只有一个区间价
            return priceList;
        } else if (wPriceSize > 1) {
            List<String> matchStrList = StrUtils.matchStrList("(\\d+)", priceList.get(0).getQuantity());
            int quantity0_0 = Integer.valueOf(matchStrList.get(0));
            int quantity0_1 = Integer.valueOf(matchStrList.get(matchStrList.size() - 1));

            matchStrList = StrUtils.matchStrList("(\\d+)", priceList.get(1).getQuantity());
            int quantity1_0 = Integer.valueOf(StrUtils.matchStr(priceList.get(1).getQuantity(), "(\\d+)"));
            int quantity1_1 = Integer.valueOf(matchStrList.get(matchStrList.size() - 1));
            int quantity2 = wPriceSize > 2 ? Integer.valueOf(StrUtils.matchStr(priceList.get(2).getQuantity(), "(\\d+)")) : 0;
            boolean userWprice = quantity1_0 > quantity0_1 && (wPriceSize == 2 || (wPriceSize == 3 && quantity2 > quantity1_1));
            if (userWprice) {
                int quantityTemp = 0;
                /*第一区间 价格 定量计算*/
                quantity = priceList.get(0).getQuantity();
                quantity0_0 = Integer.valueOf(StrUtils.matchStr(quantity, "(\\d+)"));
                quantity0_1 = quantity.indexOf("-") > -1 ?
                        Integer.valueOf(StrUtils.matchStr(quantity, "(-\\d+)").replace("-", "")) : quantity0_0;
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
                priceList.get(0).setQuantity(String.valueOf(quantity0_0) + "-" + quantity0_1);
                /*第一区间或者 第二区间 ,类似1piece 的情况下改为  1-1piece--2017-10-18*/
                if (wPriceSize == 2) {
                    priceList.get(1).setMoq(quantity0_1 + 1);
                    priceList.get(1).setQuantity("≥" + (quantity0_1 + 1));
                } else {
                    quantity = priceList.get(1).getQuantity();
                    quantity1_0 = Integer.valueOf(StrUtils.matchStr(quantity, "(\\d+)"));
                    quantity1_1 = quantity.indexOf("-") > -1 ?
                            Integer.valueOf(StrUtils.matchStr(quantity, "(-\\d+)").replace("-", "")) : quantity1_0;
                    if (quantity1_0 < quantity0_1 + 1) {
                        quantity1_0 = quantity1_0 + quantityTemp;
                        quantity1_1 = quantity1_1 < quantity1_0 ? quantity1_1 + quantityTemp : quantity1_1;
                    }
                    priceList.get(1).setQuantity(String.valueOf(quantity1_0) + "-" + quantity1_1);
                    priceList.get(1).setMoq(quantity1_0);
                    priceList.get(2).setQuantity("≥" + (quantity1_1 + 1));
                    priceList.get(2).setMoq(quantity1_1 + 1);
                }
                /*第一区间或者 第二区间 ,1-1piece 的情况下改为  1piece(1-1、3-3之类的，都改掉，ed要求的改掉吧)--2017-11-08*/
                quantity = StrUtils.matchStr(priceList.get(0).getQuantity(), "(\\d+)");
                if ((quantity + "-" + quantity).equals(priceList.get(0).getQuantity())) {
                    priceList.get(0).setQuantity(quantity);
                }
                quantity = StrUtils.matchStr(priceList.get(1).getQuantity(), "(\\d+)");
                if ((quantity + "-" + quantity).equals(priceList.get(1).getQuantity())) {
                    priceList.get(1).setQuantity(quantity);
                }
                return priceList;
            }
        }
        return null;
    }


    private static String getOverseasWarehouseProeuctPrice(String priceStr, double perWeight) {
        if (StringUtils.isBlank(priceStr)) {
            return "0.0";
        }
        BigDecimal price = new BigDecimal(priceStr);
        // @author: cjc @date：2019/11/6 7:49 下午   Description : 计算尾程运费
        BigDecimal shippingCost;
        if (perWeight * 2 - 0.226 <= 0) {
            shippingCost = TRAILFREIGHT.divide(BigDecimal.valueOf(2));
        } else {
            shippingCost = (TRAILFREIGHT.add(TRAILFREIGHTRATE.multiply(BigDecimal.valueOf((perWeight * 2 - 0.226) * 1000)))).divide(BigDecimal.valueOf(2));
        }
        return price.add(ENTERWAREHOUSEFEE).add(WAREHOUSEHANDLINGFEE).add(shippingCost).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

}
