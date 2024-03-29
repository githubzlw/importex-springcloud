package com.importexpress.pay.service;


import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.pay.service.enumc.ClientTypeEnum;
import com.importexpress.pay.service.enumc.TradeTypeEnum;

/**
 * 订单号生成器
 */
public interface OrderService {


    /**
     * 清理redis缓存
     * @return
     */
    boolean clearRedisCache();

    /**
     * 订单号编制
     * @param site
     * @param clientTypeEnum
     * @param tradeTypeEnum
     * @return
     */
    String generateOrderNumber(SiteEnum site,ClientTypeEnum clientTypeEnum, TradeTypeEnum tradeTypeEnum) throws Exception;
}
