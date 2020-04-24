package com.importexpress.email.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ShopCarMarketing {
    private Integer id;

    private Integer goodsdataId;

    private Integer userid;

    private String sessionid;

    private String catid;

    private String itemid;

    private String shopid;

    private String goodsUrl;

    private String goodsTitle;

    private String googsSeller;

    private String googsImg;

    private String googsPrice;

    private Integer googsNumber;

    private String googsSize;

    private String googsColor;

    private Double freight;

    private String deliveryTime;

    private String normMost;

    private String normLeast;

    private Integer state;

    private String remark;

    private Date datatime;

    private Integer flag;

    private String pwprice;

    private String trueShipping;

    private Integer freightFree;

    private String width;

    private String seilunit;

    private String goodsunit;

    private String bulkVolume;

    private String totalWeight;

    private String perWeight;

    private String goodsEmail;

    private String freeShoppingCompany;

    private String freeScDays;

    private String preferential;

    private Integer depositRate;

    private String guid;

    private String goodsType;

    private String feeprice;

    private String currency;

    private Integer goodsClass;

    private Double extraFreight;

    private Integer sourceUrl;

    private Integer isshippingPromote;

    private Double methodFeight;

    private Double price1;

    private Double price2;

    private Double price3;

    private Double notfreeprice;

    private Double theproductfrieght;

    private Integer isvolume;

    private Double freeprice;

    private Double firstprice;

    private Integer firstnumber;

    private Date updatetime;

    private Double addprice;

    private Integer isfeight;

    private Integer isbattery;

    private String aliposttime;

    private Double price4;

    private String goodsurlmd5;

    private String bizpricediscount;

    private Double spiderprice;

    private String pricelistsize;

    private String comparealiprice;

    private Integer groupBuyId;

    private String skuid1688;

    private Integer isfreeshipproduct;

    private Double samplefee;

    private Integer samplemoq;

    private double totalPrice;

    private List<String> typeList;

    private String priceNew;

    private int adminId;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"goodsdataId\":")
                .append(goodsdataId);
        sb.append(",\"userid\":")
                .append(userid);
        sb.append(",\"sessionid\":\"")
                .append(sessionid).append('\"');
        sb.append(",\"catid\":\"")
                .append(catid).append('\"');
        sb.append(",\"itemid\":\"")
                .append(itemid).append('\"');
        sb.append(",\"shopid\":\"")
                .append(shopid).append('\"');
        sb.append(",\"goodsUrl\":\"")
                .append(goodsUrl).append('\"');
        sb.append(",\"goodsTitle\":\"")
                .append(goodsTitle).append('\"');
        sb.append(",\"googsSeller\":\"")
                .append(googsSeller).append('\"');
        sb.append(",\"googsImg\":\"")
                .append(googsImg).append('\"');
        sb.append(",\"googsPrice\":\"")
                .append(googsPrice).append('\"');
        sb.append(",\"googsNumber\":")
                .append(googsNumber);
        sb.append(",\"googsSize\":\"")
                .append(googsSize).append('\"');
        sb.append(",\"googsColor\":\"")
                .append(googsColor).append('\"');
        sb.append(",\"freight\":")
                .append(freight);
        sb.append(",\"deliveryTime\":\"")
                .append(deliveryTime).append('\"');
        sb.append(",\"normMost\":\"")
                .append(normMost).append('\"');
        sb.append(",\"normLeast\":\"")
                .append(normLeast).append('\"');
        sb.append(",\"state\":")
                .append(state);
        sb.append(",\"remark\":\"")
                .append(remark).append('\"');
        sb.append(",\"datatime\":\"")
                .append(datatime).append('\"');
        sb.append(",\"flag\":")
                .append(flag);
        sb.append(",\"pwprice\":\"")
                .append(pwprice).append('\"');
        sb.append(",\"trueShipping\":\"")
                .append(trueShipping).append('\"');
        sb.append(",\"freightFree\":")
                .append(freightFree);
        sb.append(",\"width\":\"")
                .append(width).append('\"');
        sb.append(",\"seilunit\":\"")
                .append(seilunit).append('\"');
        sb.append(",\"goodsunit\":\"")
                .append(goodsunit).append('\"');
        sb.append(",\"bulkVolume\":\"")
                .append(bulkVolume).append('\"');
        sb.append(",\"totalWeight\":\"")
                .append(totalWeight).append('\"');
        sb.append(",\"perWeight\":\"")
                .append(perWeight).append('\"');
        sb.append(",\"goodsEmail\":\"")
                .append(goodsEmail).append('\"');
        sb.append(",\"freeShoppingCompany\":\"")
                .append(freeShoppingCompany).append('\"');
        sb.append(",\"freeScDays\":\"")
                .append(freeScDays).append('\"');
        sb.append(",\"preferential\":\"")
                .append(preferential).append('\"');
        sb.append(",\"depositRate\":")
                .append(depositRate);
        sb.append(",\"guid\":\"")
                .append(guid).append('\"');
        sb.append(",\"goodsType\":\"")
                .append(goodsType).append('\"');
        sb.append(",\"feeprice\":\"")
                .append(feeprice).append('\"');
        sb.append(",\"currency\":\"")
                .append(currency).append('\"');
        sb.append(",\"goodsClass\":")
                .append(goodsClass);
        sb.append(",\"extraFreight\":")
                .append(extraFreight);
        sb.append(",\"sourceUrl\":")
                .append(sourceUrl);
        sb.append(",\"isshippingPromote\":")
                .append(isshippingPromote);
        sb.append(",\"methodFeight\":")
                .append(methodFeight);
        sb.append(",\"price1\":")
                .append(price1);
        sb.append(",\"price2\":")
                .append(price2);
        sb.append(",\"price3\":")
                .append(price3);
        sb.append(",\"notfreeprice\":")
                .append(notfreeprice);
        sb.append(",\"theproductfrieght\":")
                .append(theproductfrieght);
        sb.append(",\"isvolume\":")
                .append(isvolume);
        sb.append(",\"freeprice\":")
                .append(freeprice);
        sb.append(",\"firstprice\":")
                .append(firstprice);
        sb.append(",\"firstnumber\":")
                .append(firstnumber);
        sb.append(",\"updatetime\":\"")
                .append(updatetime).append('\"');
        sb.append(",\"addprice\":")
                .append(addprice);
        sb.append(",\"isfeight\":")
                .append(isfeight);
        sb.append(",\"isbattery\":")
                .append(isbattery);
        sb.append(",\"aliposttime\":\"")
                .append(aliposttime).append('\"');
        sb.append(",\"price4\":")
                .append(price4);
        sb.append(",\"goodsurlmd5\":\"")
                .append(goodsurlmd5).append('\"');
        sb.append(",\"bizpricediscount\":\"")
                .append(bizpricediscount).append('\"');
        sb.append(",\"spiderprice\":")
                .append(spiderprice);
        sb.append(",\"pricelistsize\":\"")
                .append(pricelistsize).append('\"');
        sb.append(",\"comparealiprice\":\"")
                .append(comparealiprice).append('\"');
        sb.append(",\"groupBuyId\":")
                .append(groupBuyId);
        sb.append(",\"skuid1688\":\"")
                .append(skuid1688).append('\"');
        sb.append(",\"isfreeshipproduct\":")
                .append(isfreeshipproduct);
        sb.append(",\"samplefee\":")
                .append(samplefee);
        sb.append(",\"samplemoq\":")
                .append(samplemoq);
        sb.append(",\"totalPrice\":")
                .append(totalPrice);
        sb.append(",\"typeList\":")
                .append(typeList);
        sb.append(",\"priceNew\":\"")
                .append(priceNew).append('\"');
        sb.append(",\"adminId\":")
                .append(adminId);
        sb.append('}');
        return sb.toString();
    }
}