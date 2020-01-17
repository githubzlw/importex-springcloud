package com.importexpress.search.common;

import com.importexpress.comm.util.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 不同网站计算不同 moq & price
 */
@Slf4j
@Component
public class CalculatePrice {

    /**
     * import solr提高moq
     *
     * @param solrDocument
     */
    public  void raiseMoqSearchGoods(SolrDocument solrDocument, int site) {
        if (solrDocument == null) {
            return ;
        }
        //import提高moq start
        if(site > 2 || site <1){
            return ;
        }
        if(StringUtils.isBlank(StrUtils.object2Str(solrDocument.get("custom_range_price")))){
            /*if(site == 1){
                solrDocument.setField("custom_wprice",
                        LimitHighMOQ(StrUtils.object2Str(solrDocument.get("custom_wprice")),
                                StrUtils.object2Str(solrDocument.get("custom_wholesale_price"))));
                solrDocument.setField("custom_feeprice",
                        LimitHighMOQ(StrUtils.object2Str(solrDocument.get("custom_feeprice")),
                                StrUtils.object2Str(solrDocument.get("custom_wholesale_price"))));
            }*/
            solrDocument.setField("custom_morder",
                    String.valueOf(StrUtils.matchStr(Arrays.asList(StrUtils.object2Str(
                            solrDocument.get("custom_wprice")).split(","))
                            .get(0).split("\\$")[0].trim(), "(\\d+)")));
            //免邮价计算
            solrDocument.setField("custom_feeprice",
                    freePricePrc(StrUtils.object2Str(solrDocument.get("custom_final_weight")),
                            StrUtils.object2Str(solrDocument.get("custom_morder")),
                            StrUtils.object2Str(solrDocument.get("custom_wholesale_price"))
                    ,StrUtils.object2Str(solrDocument.get("custom_feeprice")),
                            StrUtils.object2Str(solrDocument.get("custom_range_price")),site));

        }else{
            /*if(site == 1){
                solrDocument.setField("custom_morder",
                        String.valueOf(getMinMoq(StrUtils.object2Str(solrDocument.get("custom_wholesale_price")))));
            }*/
        }
        //import提高moq end
    }

    //取得1688 P1价格
    public static double getFactoryPrice(String wholsePrice){

        double factoryPrice = 0;
        //[2-19 $ 19.00, 20-99 $ 18.00, ≥100 $ 16.00]
        if(wholsePrice.contains(",")){
            wholsePrice = wholsePrice.replace("[", "").replace("]","");
            factoryPrice = Double.valueOf(wholsePrice.split(",")[0].split("\\$")[1]);
        }else{
            //[≥2 $ 13.99-28.00] 或者 [≥3 $ 15.0]
            if(wholsePrice.contains("-")){
                wholsePrice = wholsePrice.replace("[", "").replace("]","");
                factoryPrice = Double.valueOf(wholsePrice.split("\\$")[1].split("-")[1]);
            }else{
                wholsePrice = wholsePrice.replace("[", "").replace("]","");
                factoryPrice = Double.valueOf(wholsePrice.split("\\$")[1]);
            }
        }
        return factoryPrice;
    }


    public static String freePricePrc(String finalWeight,String morder,String wholesalePrice,String feePrice,String rangePrice,int site) {
        //初始邮费 = (JCEX运费（MOQ*单件重量）-$3)/ MOQ  这3美元是我们本来就给的首重减免
        double initialFreight=0;
        double basePrice = 60;
        double ratioPrice = 22;
        double baseWeight = 500;
        double weight = Double.valueOf(finalWeight)*1000;
        //站点
        int moq = Integer.valueOf(morder);
        //1688 P1价格
        double factoryPrice=getFactoryPrice(wholesalePrice);
        //用户要买99美元东西（大概700人民币，假设都买这个产品）
        int moqX= (int) Math.ceil(700/Double.valueOf(factoryPrice));
        //kids网站
//        if(site ==2){
            moq = moqX;
//        }
        double exchangerate = 6.6;
        //basePrice：首重运费, ratioPrice：续重运费, baseWeight：续重重量, weight：需要计算的重量
        BigDecimal jcexPostFreight = FreightUtility.getShippingFormula( new BigDecimal(basePrice),
                new BigDecimal(ratioPrice),new BigDecimal(baseWeight),new BigDecimal(weight*moq));
        //初始邮费 = (JCEX运费（MOQ*单件重量）-$3)/ MOQ  这3美元是我们本来就给的首重减免
        initialFreight = (jcexPostFreight.doubleValue()/exchangerate -7) /moq;
        //免邮价 = 工厂价*加价率+初始邮费    （这里的加价率 比现在的少 10%，
        // 这里的工厂价 取 MOQ数量对应的工厂价，或者各个价格区间 对应的工厂价）
        //加价率
        double addPriceLv=1.37;

        /*if(site ==1){
            addPriceLv=1.32;
        }else if(site == 2){
            addPriceLv=1.42;
        }*/

        //加价率
        addPriceLv =getAddPriceLv(addPriceLv,factoryPrice);
        //新的免邮价
        return setNewFreePrice(addPriceLv,initialFreight,feePrice,wholesalePrice, rangePrice, basePrice
                , ratioPrice, baseWeight, weight, exchangerate,moq,site);

    }
    //新的免邮价
    public static String setNewFreePrice(double addPriceLv,double initialFreight,String price,
                                         String wholsePrice,String rangePrice,double basePrice
            ,double ratioPrice,double baseWeight,double weight,double exchangerate,int moqX,int site){

        DecimalFormat priceFormat = new DecimalFormat("#0.00");
        List<String> listNew = new ArrayList<String>();
        //区间价情况
        if(!StringUtils.isBlank(rangePrice)){

        }else{
            price = price.replace("[", "").replace("]","");
            List<String> list = Arrays.asList(price.split(","));

            wholsePrice = wholsePrice.replace("[", "").replace("]","");
            List<String> wholsePricelist = Arrays.asList(wholsePrice.split(","));

            if(list == null || list.isEmpty()){
                return list.toString();
            }
            int listSize = list.size();
            //不包含批发价格
            if(listSize < 1){
                return list.toString();
            }

            //第一区间 moq+price [2-49 $ 1.28, 50-199 $ 1.12, ≥200 $ 1.00]
            String[] wprice0 = list.get(0).split("\\$");
//            String[] quantity0 = wprice0[0].trim().split("-");
//            int quantity0_0 = Integer.valueOf(quantity0[0].replace("≥", ""));
            String moq1 =wprice0[0].trim().replace("≥", "");

            //1688第一区间 moq+price
            String[] wholsePrice1 = wholsePricelist.get(0).split("\\$");
//            String[] wholseQuantity0 = wholsePrice1[0].trim().split("-");
//            int wholseQuantity0_0 = Integer.valueOf(wholseQuantity0[0].replace("≥", ""));


            //只有一个区间价格 [≥3 $ 15.0]
            if(listSize == 1){
                //免邮价 = 工厂价*加价率+初始邮费    （这里的加价率 比现在的少 10%， 这里的工厂价 取 MOQ数量对应的工厂价，或者各个价格区间 对应的工厂价）
                String wholsePriceQj="";
                if(wholsePrice1[1].contains("-")){
                    wholsePriceQj = wholsePrice1[1].trim().split("-")[1];
                }else{
                    wholsePriceQj = wholsePrice1[1].trim();
                }
                double factoryPrice = Double.valueOf(wholsePriceQj);
                double freePrice = 0;
                freePrice = factoryPrice/exchangerate* addPriceLv +initialFreight;
                listNew.add(0, "≥"+moq1+" $ "+priceFormat.format(freePrice));

            }else if(listSize == 2){
                //[5-49 $ 9.20, ≥50 $ 7.82]

                String[] wprice1 = list.get(1).split("\\$");
                String[] quantity1 = wprice1[0].trim().split("-");
                int quantity1_0 = Integer.valueOf(quantity1[0].replace("≥", ""));
                String moq2 =wprice1[0].trim().replace("≥", "");

                String[] wholsePrice2 = wholsePricelist.get(1).split("\\$");
                //站点
                //kids第二区间moq
//                if(site == 2){
                    quantity1_0 = Math.max(moqX,quantity1_0);
//                }
                BigDecimal jcexPostFreight = FreightUtility.getShippingFormula( new BigDecimal(basePrice),
                        new BigDecimal(ratioPrice),new BigDecimal(baseWeight),new BigDecimal(weight*quantity1_0));
                //初始邮费 = (JCEX运费（MOQ*单件重量）-$3)/ MOQ  这3美元是我们本来就给的首重减免
                double initialFreight2 = (jcexPostFreight.doubleValue()/exchangerate -7) /quantity1_0;

                //免邮价P2 = 工厂价*加价率+初始邮费    （这里的加价率 比现在的少 10%，
                // 这里的工厂价 取 MOQ数量对应的工厂价，或者各个价格区间 对应的工厂价）
                double factoryPrice1 = Double.valueOf(wholsePrice1[1].trim());
                double freePrice1 = 0;
                freePrice1 = factoryPrice1/exchangerate * addPriceLv +initialFreight;

                double factoryPrice2 = Double.valueOf(wholsePrice2[1].trim());
                double freePrice2 = 0;
//                if(site ==1){
//                    addPriceLv=1.32;
//                }else if(site == 2){
                    addPriceLv=1.37;
//                }
                double addPriceLv2 = getAddPriceLv(addPriceLv, factoryPrice2);
                freePrice2 = factoryPrice2/exchangerate * addPriceLv2 +initialFreight2;

                listNew.add(0, moq1+" $ "+priceFormat.format(freePrice1));
                listNew.add(1, "≥"+moq2+" $ "+priceFormat.format(freePrice2));

            }else if(listSize == 3){
                //[2-49 $ 35.00, 50-99 $ 32.00, ≥100 $ 29.00]
                String[] wprice1 = list.get(1).split("\\$");
                String[] quantity1 = wprice1[0].trim().split("-");
                int quantity1_0 = Integer.valueOf(quantity1[0].replace("≥", ""));
                String[] wprice2 = list.get(2).split("\\$");
                String[] quantity2 = wprice2[0].trim().split("-");
                int quantity2_0 = Integer.valueOf(quantity2[0].replace("≥", ""));
                String moq2 =wprice1[0].trim().replace("≥", "");
                String moq3 =wprice2[0].trim().replace("≥", "");

                //1688价格
                String[] wholsePrice2 = wholsePricelist.get(1).split("\\$");
                String[] wholsePrice3 = wholsePricelist.get(2).split("\\$");

                //免邮价 = 工厂价*加价率+初始邮费    （这里的加价率 比现在的少 10%，
                // 这里的工厂价 取 MOQ数量对应的工厂价，或者各个价格区间 对应的工厂价）
                double factoryPrice1 = Double.valueOf(wholsePrice1[1].trim());
                double freePrice1 = 0;
                freePrice1 = factoryPrice1/exchangerate * addPriceLv +initialFreight;

                double factoryPrice2 = Double.valueOf(wholsePrice2[1].trim());
                double freePrice2 = 0;
                //站点
//                if(site ==1){
//                    addPriceLv=1.32;
//                }else if(site == 2){
                    addPriceLv=1.37;
//                }
                double addPriceLv2 = getAddPriceLv(addPriceLv, factoryPrice2);
                //kids第二区间moq
//                if(site == 2){
                    quantity1_0 = Math.max(moqX,quantity1_0);
//                }
                BigDecimal jcexPostFreight = FreightUtility.getShippingFormula( new BigDecimal(basePrice),
                        new BigDecimal(ratioPrice),new BigDecimal(baseWeight),new BigDecimal(weight*quantity1_0));
                //初始邮费 = (JCEX运费（MOQ*单件重量）-$3)/ MOQ  这3美元是我们本来就给的首重减免
                double initialFreight2 = (jcexPostFreight.doubleValue()/exchangerate - 7) /quantity1_0;
                freePrice2 = factoryPrice2/exchangerate * addPriceLv2 +initialFreight2;

                double factoryPrice3 = Double.valueOf(wholsePrice3[1].trim());
                double freePrice3 = 0;
                double addPriceLv3 = getAddPriceLv(addPriceLv, factoryPrice3);
                //kids第三区间moq
//                if(site == 2){
                    quantity2_0 = Math.max(moqX,quantity2_0);
//                }
                BigDecimal jcexPostFreight3 = FreightUtility.getShippingFormula( new BigDecimal(basePrice),
                        new BigDecimal(ratioPrice),new BigDecimal(baseWeight),new BigDecimal(weight*quantity2_0));
                //初始邮费 = (JCEX运费（MOQ*单件重量）-$3)/ MOQ  这3美元是我们本来就给的首重减免
                double initialFreight3 = (jcexPostFreight.doubleValue()/exchangerate - 7) /quantity2_0;
                freePrice3 = factoryPrice3/exchangerate * addPriceLv3 +initialFreight3;

                listNew.add(0, moq1+" $ "+priceFormat.format(freePrice1));
                listNew.add(1, moq2+" $ "+priceFormat.format(freePrice2));
                listNew.add(2, "≥"+moq3+" $ "+priceFormat.format(freePrice3));

            }

        }
        return listNew.toString();
    }

    //价格高的商品，我们少加点利润
    public static double getAddPriceLv(double addPriceLv,double factory){

        //如 最小订量时  工厂价=>200元，第一区间加价率 = 1.42-0.12
        if(factory>=200){
            addPriceLv = addPriceLv-0.08;
            //如 最小订量时  工厂价=>90元，第一区间加价率 = 1.42-0.06
        }else if(factory>=90){
            addPriceLv = addPriceLv-0.04;
            //如 最小订量时 工厂价<90元，第一区间加价率 = 1.42
        }else if(factory<90){
            addPriceLv = addPriceLv;
        }
        //注意限制加价率 不能小于  1.05
        if(addPriceLv<1.05){
            addPriceLv = 1.05;
        }
        return addPriceLv;

    }

    //区间价取得最小moq
    public static int getMinMoq(String wholsePrice){

        wholsePrice = wholsePrice.replace("[", "").replace("]","");
        List<String> wholsePricelist = Arrays.asList(wholsePrice.split(","));

        String[] wholsePrice0 = wholsePricelist.get(0).split("\\$");
        String[] wholseQuantity0 = wholsePrice0[0].trim().split("-");
        int wholseQuantity0_0 = Integer.valueOf(wholseQuantity0[0].replace("≥", ""));
        String[] wholsePriceAry = wholsePrice0[1].trim().split("-");
        double priceMax =0;
        if(wholsePriceAry.length==1){
            priceMax= Double.valueOf(wholsePriceAry[0].trim());
        }else{
            priceMax= Double.valueOf(wholsePriceAry[1].trim());
        }

        int temp = Math.max(wholseQuantity0_0, (int) Math.ceil(500/priceMax));
        return temp;

    }
    //import moq改成500人民币起批量
    public static String LimitHighMOQ(String price,String wholsePrice){

        price = price.replace("[", "").replace("]","");
        List<String> list = Arrays.asList(price.split(","));

        wholsePrice = wholsePrice.replace("[", "").replace("]","");
        List<String> wholsePricelist = Arrays.asList(wholsePrice.split(","));

        if(list == null || list.isEmpty()){
            return list.toString();
        }
        int listSize = list.size();
        //不包含批发价格
        if(listSize < 1){
            return list.toString();
        }
        //第一区间 moq+price
        String[] wprice0 = list.get(0).split("\\$");
        String[] quantity0 = wprice0[0].trim().split("-");
        int quantity0_0 = Integer.valueOf(quantity0[0].replace("≥", ""));

        //提高 MOQ 到  MOQ = MAX(1688MOQ, 取整（500人民币/1688产品价）)
        //1688第一区间 moq+price
        String[] wholsePrice0 = wholsePricelist.get(0).split("\\$");
        String[] wholseQuantity0 = wholsePrice0[0].trim().split("-");
        int wholseQuantity0_0 = Integer.valueOf(wholseQuantity0[0].replace("≥", ""));

        String[] wholsePriceAry = wholsePrice0[1].trim().split("-");
        double priceMax =0;
        if(wholsePriceAry.length==1){
            priceMax= Double.valueOf(wholsePriceAry[0].trim());
        }else{
            priceMax= Double.valueOf(wholsePriceAry[1].trim());
        }

        int moqMax = Math.max(wholseQuantity0_0, (int) Math.ceil(500/Double.valueOf(priceMax)));

        //如果第一个moq 满足$1条件，不做任何处理
        if(moqMax == 0 ){
            return list.toString();
        }

        List<String> listNew = new ArrayList<String>();

        //只有一个区间价格
        if(listSize == 1){
            // 如果 MOQ >= 第3价格区间的起始数量， 那么  价格区间为 大于MOQ
            if(moqMax>=quantity0_0){
                listNew.add(0, "≥"+moqMax+" $ "+wprice0[1].trim());
            }else{
                listNew.add(0, "≥"+quantity0_0+" $ "+wprice0[1].trim());
            }

        }else if(listSize == 2){
            String[] wprice1 = list.get(1).split("\\$");
            String[] quantity1 = wprice1[0].trim().split("-");
            int quantity1_0 = Integer.valueOf(quantity1[0].replace("≥", ""));
            int tempQuantity1_0=quantity1_0-1;
            //如果 MOQ < 第2价格区间的起始数量，那么  价格区间为
            // (MOQ ~ 第2价格区间的起始数量）,  大于第3价格区间的起始数量
            // 如果 MOQ >= 第2价格区间的起始数量，那么  价格区间为
            //   大于第3价格区间的起始数量
            if(moqMax<quantity1_0){
                listNew.add(0, moqMax+"-"+tempQuantity1_0+" $ "+wprice0[1].trim());
                listNew.add(1, "≥"+quantity1_0+" $ "+wprice1[1].trim());
            }
            if(moqMax>=quantity1_0){
                listNew.add(0, "≥"+quantity1_0+" $ "+wprice1[1].trim());
            }
        }else if(listSize == 3){
            String[] wprice1 = list.get(1).split("\\$");
            String[] quantity1 = wprice1[0].trim().split("-");
            int quantity1_0 = Integer.valueOf(quantity1[0].replace("≥", ""));
            int tempQuantity1_0=quantity1_0-1;
            String[] wprice2 = list.get(2).split("\\$");
            String[] quantity2 = wprice2[0].trim().split("-");
            int quantity2_0 = Integer.valueOf(quantity2[0].replace("≥", ""));
            int tempQuantity2_0=quantity2_0-1;
            //如果 MOQ < 第2价格区间的起始数量，那么  价格区间为
            // (MOQ ~ 第2价格区间的起始数量）, (第2价格区间的起始数量~ 第3价格区间的起始数量), 大于第3价格区间的起始数量
            // 如果 MOQ >= 第2价格区间的起始数量，那么  价格区间为
            //   (MOQ~ 第3价格区间的起始数量), 大于第3价格区间的起始数量
            //	如果 MOQ >= 第3价格区间的起始数量， 那么  价格区间为
            //	大于MOQ （或者取消价格区间）
            if(moqMax<quantity1_0){
                listNew.add(0, moqMax+"-"+tempQuantity1_0+" $ "+wprice0[1].trim());
                listNew.add(1, quantity1_0+"-"+tempQuantity2_0+" $ "+wprice1[1].trim());
                listNew.add(2, "≥"+quantity2_0+" $ "+wprice2[1].trim());
            }
            if(moqMax>=quantity1_0){
                listNew.add(0, moqMax+"-"+tempQuantity2_0+" $ "+wprice1[1].trim());
                listNew.add(1, "≥"+quantity2_0+" $ "+wprice2[1].trim());
            }
            if(moqMax>=quantity2_0){
                listNew.clear();
                listNew.add(0, "≥"+moqMax+" $ "+wprice2[1].trim());
            }
        }

        return listNew.toString();
    }
}
