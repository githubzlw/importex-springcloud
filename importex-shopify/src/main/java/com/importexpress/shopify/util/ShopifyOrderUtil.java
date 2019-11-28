//package com.importexpress.shopify.util;
//
//import com.importexpress.shopify.pojo.orders.Orders;
//import com.importexpress.shopify.pojo.orders.OrdersWraper;
//import com.importexpress.shopify.pojo.product.ShopifyBean;
//import com.importexpress.user.pojo.UserBean;
//import com.importexpress.user.service.UserService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @author: JiangXW
// * @version: v1.0
// * @description: com.importexpress.shopify.util
// * @date:2019/11/27
// */
//@Slf4j
//@Service
//public class ShopifyOrderUtil {
//
//    @Autowired
//    private UserService userService;
//
//    /**
//     * 根据ShopifyOrder生成对应dropship订单
//     *
//     * @param shopName : shopify店铺名称
//     * @param orders   : shopify订单bean
//     */
//    private void genShopifyOrderInfo(String shopName, OrdersWraper orders) {
//
//        // 1.查询店铺对应的客户ID
//        UserBean userBean = userService.getUserByShopifyName(shopName);
//        // 2.根据订单获取全部订单信息
//        List<Orders> shopifyOrderList = orders.getOrders();
//
//        // 保存shopify信息到数据库
//
//
//
//        int userId = userBean.getId();
//        // 2.2获取客户已经下单的dp订单数据
//        List<DropshipOrderList> dpOldList = orderService.queryDropshipOrderForList(userId);
//        Set<Long> shopifyOrders = new HashSet<>(dpOldList.size());
//        for (DropshipOrderList dpOd : dpOldList) {
//            if (StringUtils.isNotBlank(dpOd.getOrderNo())) {
//                shopifyOrders.add(Long.valueOf(dpOd.getOrderNo()));
//            }
//        }
//        // 2.2获取含有我们网站商品的信息的数据,并且过滤已经下单的dp商品
//        // 过滤存在的shopify订单
//        List<Orders> nwfilterList = new ArrayList<>(shopifyOrderList.size());
//        if (shopifyOrders.size() > 0) {
//            shopifyOrderList.stream().filter(nwOd -> shopifyOrders.contains(nwOd.getId()))
//                    .forEach(e -> nwfilterList.add(e));
//        } else {
//            shopifyOrderList.stream().forEach(e -> nwfilterList.add(e));
//        }
//
//        // 3.订单相关数据生成
//        genOrderInfoAndOrderDetails(shopName, userId, nwfilterList);
//    }
//
//    /**
//     * 根据shopify订单数据 生成订单信息
//     *
//     * @param userId      : 客户ID
//     * @param shopifyList : shopify订单集合
//     */
//    private void genOrderInfoAndOrderDetails(String shopName, int userId, List<Orders> shopifyList) {
//        for (Orders pOrder : shopifyList) {
//
//            List<OrderDetailsBean> orderdetails = new ArrayList<>();
//            List<GoodsCarActiveBean> list_active = new ArrayList<>();
//            // 1.订单号
//            String orderNo = orderNoGenerator.generateOrderNumber(ClientTypeEnum.PC, TradeTypeEnum.DROPSHIP);
//            // 插入日志
//            dropshiporderService.insertIntoShopifyInfoLog(userId, orderNo, String.valueOf(pOrder.getId()), pOrder.toString());
//            // 获取对应shopify的Id对应的PID数据
//            List<ShopifyBean> shopifyBeanList = shopifyService.queryPidbyShopifyName(shopName);
//
//            Map<String, String> pidMap = shopifyBeanList.stream().collect(Collectors.toMap(ShopifyBean::getShopifyPid, ShopifyBean::getPid));
//            shopifyBeanList.clear();
//
//            // 2.获取产品详情
//            pOrder.getLine_items().stream().filter(product -> StringUtils.isNotBlank(product.getVendor())
//                    && product.getVendor().contains("import-express.com"))
//                    .forEach(e -> {
//                        if (pidMap.containsKey(String.valueOf(e.getProduct_id()))) {
//                            GoodsBean dao2Bean = customGoodsDriver.goodsDriver(pidMap.get(String.valueOf(e.getProduct_id())));
//                            if (dao2Bean == null) {
//                                System.err.println("pid:" + ", is null --------");
//                            } else if (dao2Bean.getValid() == 0) {
//                                System.err.println("pid:" + dao2Bean.getpID() + ", is offline----------");
//                            } else {
//                                // orderDetails的bean信息
//                                genOrderDetailsInfo(userId, orderNo, e, dao2Bean, orderdetails, list_active);
//                            }
//                        }
//
//                    });
//            pidMap.clear();
//            // 2.1保存详情表
//            if (orderdetails.size() > 0) {
//                orderService.add(orderdetails, userId, orderNo);
//                // 3.生成地址信息
//                Map<String, Object> orderAddressMap = genAddressInfo(pOrder, orderNo);
//
//                // 4.生成dp订单数据 -- 生成订单地址，计算商品总价和运费
//                genOrderInfo(userId, orderNo, orderdetails, orderAddressMap, pOrder, list_active);
//                orderAddressMap.clear();
//            }
//        }
//    }
//
//    /**
//     * shopify的Line_items数据获取产品详情
//     *
//     * @param userId       : 客户ID
//     * @param orderNo      : 订单号
//     * @param product      : shopify订单的产品bean
//     * @param dao2Bean     : 根据我司网站PID查询出来bean
//     * @param orderdetails : 生成我司订单详情的集合
//     */
//    private void genOrderDetailsInfo(int userId, String orderNo, Line_items product, GoodsBean dao2Bean,
//                                     List<OrderDetailsBean> orderdetails, List<GoodsCarActiveBean> list_active) {
//
//        String catid = dao2Bean.getCatid1();
//        if (StringUtils.isBlank(catid)) {
//            catid = dao2Bean.getCatid2();
//        }
//        if (StringUtils.isBlank(catid)) {
//            catid = dao2Bean.getCatid3();
//        }
//        if (StringUtils.isBlank(catid)) {
//            catid = dao2Bean.getCatid4();
//        }
//
//        double price = 0;
//        if (StringUtils.isNotBlank(dao2Bean.getDpNowPrice())) {
//            price = Double.parseDouble(dao2Bean.getDpNowPrice());
//        } else {
//            price = Double.parseDouble(dao2Bean.getpSprice());
//        }
//
//        SpiderBean spider = new SpiderBean();
//        spider.setUrlMD5(dao2Bean.getGoodsUuid());
//        spider.setState(1);
//        spider.setUserId(userId);
//        spider.setGuId(UUID.randomUUID().toString().replaceAll("-", ""));
//        spider.setGoods_catid(catid);
//        spider.setItemId(dao2Bean.getpID());
//        spider.setUrl(dao2Bean.getpUrl());
//        spider.setName(dao2Bean.getpName());
//        spider.setSeller("dd");
//        spider.setImg_url(dao2Bean.getMainImg());
//        spider.setPrice(new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//        spider.setNumber(product.getQuantity());
//        spider.setNorm_least("1");
//        spider.setFreight_free(0);
//        spider.setWidth(dao2Bean.getWidth());
//        spider.setPerWeight(dao2Bean.getPerWeight());
//        spider.setSeilUnit(dao2Bean.getSellUnits());
//        spider.setGoodsUnit(dao2Bean.getSellUnits());
//        spider.setBulk_volume(dao2Bean.getVolume());
//        String goodsWeight = dao2Bean.getPerWeight();
//        int quantityNum = product.getQuantity();
//        spider.setTotal_weight(new BigDecimal(Double.valueOf(goodsWeight) * quantityNum).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//        spider.setWeight(dao2Bean.getPerWeight());
//        if (StringUtils.isBlank(product.getSku()) || StringUtils.isBlank(product.getVariant_title())) {
//            spider.setTypes(product.getSku() + "||||" + product.getVariant_title());
//        } else {
//            String[] tempSkuStr = product.getSku().split("_");
//            String[] tempTypeStr = product.getVariant_title().split("/");
//            StringBuffer enType = new StringBuffer();
//            if (tempSkuStr.length == tempTypeStr.length) {
//                for (int i = 0; i < tempSkuStr.length; i++) {
//                    for (TypeBean typeBean : dao2Bean.getType()) {
//                        if (tempSkuStr[i].trim().equals(typeBean.getId())) {
//                            enType.append("," + typeBean.getType() + ":" + tempTypeStr[i].trim() + "@" + tempSkuStr[i].trim());
//                            break;
//                        }
//                    }
//                }
//                spider.setTypes(enType.toString().substring(1));
//            } else {
//                spider.setTypes(product.getSku() + "||||" + product.getVariant_title());
//            }
//        }
//        //spider.setTypes(product.getSku() + "||||" + product.getVariant_title());
//        spider.setFeeprice("0");
//        spider.setCurrency("USD");
//        spider.setSource_url(dao2Bean.getCid());
//        spider.setMethod_feight(0);
//        spider.setIsvolume(0);
//        spider.setFirstprice(Double.valueOf(product.getPrice()));
//        spider.setFirstnumber(Integer.parseInt(dao2Bean.getMinOrder()));
//
//        int goodCarId = spiderService.addGoogs_car(spider);
//        // 生成订单详情信息
//        OrderDetailsBean orderDetailsBean = new OrderDetailsBean();
//        orderDetailsBean.setGoodsUrlMD5(dao2Bean.getGoodsUuid());
//        orderDetailsBean.setGoods_pid(dao2Bean.getpID());
//        orderDetailsBean.setUserid(userId);
//        orderDetailsBean.setGoodsid(goodCarId);
//        orderDetailsBean.setGoodsname(dao2Bean.getpName());
//        orderDetailsBean.setGoods_url(dao2Bean.getpUrl());
//        orderDetailsBean.setGoods_img(dao2Bean.getMainImg());
//        orderDetailsBean.setGoods_type(spider.getTypes());
//        orderDetailsBean.setOrderid(orderNo);
//        orderDetailsBean.setDropshipid(orderNo + "_1");
//        orderDetailsBean.setGoodscatid(catid);
//        orderDetailsBean.setYourorder(product.getQuantity());
//        orderDetailsBean.setGoodsprice(new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//        orderDetailsBean.setBulk_volume(parseGoodsUrl.calculateVolume(product.getQuantity(), dao2Bean.getVolume(), dao2Bean.getSellUnits(), dao2Bean.getSellUnits()));
//        orderDetailsBean.setTotal_weight(new BigDecimal(dao2Bean.getPerWeight()).multiply(new BigDecimal(product.getQuantity())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//        orderdetails.add(orderDetailsBean);
//
//        // 合并运费接口
//        //将dropship产品封装成 GoodsCarActiveBean,以便走曹继操购物车计算接口
//        GoodsCarActiveBean activeBean = new GoodsCarActiveBean();
//        activeBean.setGoodsUrlMD5(dao2Bean.getGoodsUuid());
//        activeBean.setState(0);
//        activeBean.setGuId(spider.getGuId());
//        activeBean.setItemId(dao2Bean.getpID());
//        activeBean.setPrice(spider.getPrice());//产品单页9-15天的startPrice
//        // activeBean.setFirstprice(dropship.getGoodsPrice());
//        activeBean.setFreight(0);
//        activeBean.setNumber(product.getQuantity());
//        activeBean.setNorm_least("0");
//        activeBean.setPerWeight(dao2Bean.getPerWeight());
//        activeBean.setIsBattery(0);
//        activeBean.setSeilUnit(dao2Bean.getSellUnits());
//        activeBean.setPriceListSize("0");
//        activeBean.setPriceList(null);
//        activeBean.setPerWeight(dao2Bean.getPerWeight());
//        activeBean.setTotal_weight(orderDetailsBean.getTotal_weight());
//        list_active.add(activeBean);
//    }
//
//    /**
//     * 生成订单地址信息
//     *
//     * @param pOrder  : shopify订单bean
//     * @param orderNo : 我司订单号
//     */
//    private Map<String, Object> genAddressInfo(Orders pOrder, String orderNo) {
//        Shipping_address sfAddress = pOrder.getShipping_address();
//        Map<String, Object> orderAddressMap = new HashMap<>();
//        orderAddressMap.put("addressid", 0);
//        // 存子订单号
//        orderAddressMap.put("orderno", orderNo + "_1");
//        orderAddressMap.put("country", sfAddress.getCountry());
//        orderAddressMap.put("statename", sfAddress.getProvince());
//        orderAddressMap.put("address2", sfAddress.getCity());
//        orderAddressMap.put("phoneNumber", sfAddress.getPhone());
//        orderAddressMap.put("zipcode", sfAddress.getZip());
//        orderAddressMap.put("street", sfAddress.getAddress1() + " " + sfAddress.getAddress2());
//        orderAddressMap.put("recipients", sfAddress.getFirst_name() + " " + sfAddress.getLast_name());
//        orderAddressMap.put("countryCode", sfAddress.getCountry_code());
//        orderService.addOrderAddress(orderAddressMap);
//        return orderAddressMap;
//    }
//
//    /**
//     * 根据shopify生成的订单详情数据来生成dropship订单
//     *
//     * @param userId          : 客户ID
//     * @param orderNo         : 我司订单号
//     * @param orderdetails    : 订单详情集合
//     * @param orderAddressMap : 地址Map
//     */
//    private void genOrderInfo(int userId, String orderNo, List<OrderDetailsBean> orderdetails, Map<String, Object> orderAddressMap,
//                              Orders pOrder, List<GoodsCarActiveBean> list_active) {
//        double weightSum = 0;
//        int totalSum = orderdetails.size();
//
//        double productCost = 0;
//        double totalFreight = 0;
//        double totalWeight = 0;
//
//        Integer addressId = Integer.valueOf(orderAddressMap.get("id").toString());
//        // 计算商品总价
//        // 计算商品总价
//        Set<String> pisSet = new HashSet<>();
//        for (OrderDetailsBean odd : orderdetails) {
//            productCost += 1.0D * odd.getYourorder() * Double.valueOf(odd.getGoodsprice());
//            totalWeight += Double.valueOf(odd.getTotal_weight());
//            if (!pisSet.contains(odd.getGoods_pid())) {
//                pisSet.add(odd.getGoods_pid());
//            }
//        }
//
//        // 计算其他费用
//        double otherFee = 0;
//        otherFee += Utility.DROPSHIPOTERFEE * pisSet.size();
//
//        // 匹配国家ID
//        int countryId = 36;
//        String shopifyCountryCode = orderAddressMap.get("countryCode").toString();
//
//        if (zoneBeanList == null || zoneBeanList.size() == 0) {
//            zoneBeanList = new ArrayList<>();
//            Map<Object, Object> hmGet = redisUtil.hmget("application:zone");
//            Map<String, ZoneBean> zoneMap = (Map<String, ZoneBean>) JSONObject.fromObject(hmGet.get("zone"));
//            for (String ketVal : zoneMap.keySet()) {
//                Object zoneBean = zoneMap.get(ketVal);
//                List<ZoneBean> tempList = (List<ZoneBean>) JSONArray.toCollection(JSONArray.fromObject(zoneBean), ZoneBean.class);
//                if (tempList != null && tempList.size() > 0) {
//                    zoneBeanList.addAll(tempList);
//                }
//            }
//            zoneMap.clear();
//        }
//
//        for (ZoneBean zoneBean : zoneBeanList) {
//            if (StringUtils.isNotBlank(zoneBean.getShorthand()) && zoneBean.getShorthand().equalsIgnoreCase(shopifyCountryCode)) {
//                countryId = zoneBean.getId();
//                break;
//            }
//        }
//        // 计算总运费和运输方式
//        /*double[] goodsCarListWeight = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//        // 下标 3和9 非免邮商品非正常类别的重量
//        goodsCarListWeight[3] = totalWeight;
//        goodsCarListWeight[9] = totalWeight;
//        GetShippingResult freightResult = freightUtility.getDifferentShippingList(goodsCarListWeight, countryId, false);
//        List<TransitPricecost> transitPricecostsList = freightResult.getTransitPricecostList();
//        String shippingMethod = transitPricecostsList.get(0).getShippingmethod();
//        String deliveryTime = transitPricecostsList.get(0).getDelivery_time();
//        BigDecimal extraFreight = new BigDecimal(transitPricecostsList.get(0).getShippingCost()).setScale(2, BigDecimal.ROUND_HALF_UP);
//        transitPricecostsList.clear();*/
//
//        List<TransitPricecost> transitPricecostList = dropshipService.dropshipCaculAmount(list_active, countryId);
//        String shippingMethod = transitPricecostList.get(0).getShippingmethod();
//        String deliveryTime = transitPricecostList.get(0).getDelivery_time();
//        BigDecimal extraFreight = new BigDecimal(transitPricecostList.get(0).getShippingCost()).setScale(2, BigDecimal.ROUND_HALF_UP);
//        transitPricecostList.clear();
//
//        // 1.生成dp订单信息
//        Dropshiporder dropshiporder = new Dropshiporder();
//        dropshiporder.setOrderNo(String.valueOf(pOrder.getId()));
//        dropshiporder.setParentOrderNo(orderNo);
//        dropshiporder.setChildOrderNo(orderNo + "_1");
//        dropshiporder.setUserId(userId);
//        dropshiporder.setAddressId(addressId);
//
//        dropshiporder.setServiceFee(new BigDecimal(otherFee).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//
//        dropshiporder.setDeliveryTime(deliveryTime);
//        String modetransport = shippingMethod + "@" + deliveryTime + "@"
//                + orderAddressMap.get("country").toString().toUpperCase() + "@@USA@" + extraFreight.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "@all";
//        dropshiporder.setModeTransport(modetransport);
//        dropshiporder.setForeignFreight(extraFreight.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//
//        dropshiporder.setProductCost(new BigDecimal(productCost).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//        double payPrice = otherFee + productCost + extraFreight.doubleValue();
//        dropshiporder.setPayPrice(new BigDecimal(payPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//        dropshiporder.setActualFfreight("0");
//        dropshiporder.setExtraFreight(extraFreight.doubleValue());
//        dropshiporder.setCreateTime(new Date());
//        dropshiporder.setIp("");
//        dropshiporder.setState("0");
//        dropshiporder.setDetailsNumber(totalSum);
//        // dropshiporder.setPackagNumber(freight.getResult1());
//        // dropshiporder.setActualWeightEstimate(actual_weight_estimate.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//        dropshiporderService.addDropshiporder(dropshiporder);
//
//        // 2.生成主订单信息
//        List<OrderBean> orderinfo = new ArrayList<>();
//        OrderBean orderBean = new OrderBean();
//        orderBean.setOrderNo(orderNo);
//        orderBean.setUserid(userId);
//        orderBean.setDeliveryTime(deliveryTime);
//        orderBean.setAddressid(addressId);
//        // orderBean.setMode_transport("mix@" + transTime + "@mix@@USA@" + dataMap.get("totalFeight").toString() + "@mix");
//        // 总运费
//        orderBean.setForeign_freight(new BigDecimal(totalFreight).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//        // 总商品价格
//        orderBean.setProduct_cost(new BigDecimal(productCost).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//        orderBean.setPay_price(0.0);
//        orderBean.setActual_ffreight("0");
//        orderBean.setCreatetime(new Date().toString());
//        orderBean.setIp("");
//        orderBean.setState(0);
//        orderBean.setDetails_number(totalSum);
//        orderBean.setCurrency("USD");
//        orderBean.setActual_weight_estimate(new BigDecimal(weightSum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//        orderBean.setIsDropshipOrder(1);
//        orderBean.setExtra_discount(0.00);
//        orderinfo.add(orderBean);
//        orderService.addOrderInfo(orderinfo, addressId, totalSum, userId, orderNo);
//    }
//
//}
