package com.importexpress.pay.service.impl;

import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.pay.service.OrderService;
import com.importexpress.pay.service.enumc.ClientTypeEnum;
import com.importexpress.pay.service.enumc.TradeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 订单
 *
 * @author luohao
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter fmtyyMMdd = DateTimeFormatter.ofPattern("yyMMdd");

    private final OrderNoHelp orderNoHelp;

    public OrderServiceImpl(OrderNoHelp orderNoHelp) {
        this.orderNoHelp = orderNoHelp;
    }

    @Override
    public boolean clearRedisCache(){
        return orderNoHelp.clearRedisCache();
    }

    /**
     * 新版订单号编制
     *
     * @param clientTypeEnum 终端类型
     * @param tradeTypeEnum  交易类型
     * @return
     */
    @Override
    public String generateOrderNumber(SiteEnum site, ClientTypeEnum clientTypeEnum, TradeTypeEnum tradeTypeEnum) throws Exception {

        //clientType
        char clientType;
        switch (clientTypeEnum) {
            case MOBILE:
                clientType = '1';
                break;
            case PC:
                clientType = '2';
                break;
            default:
                throw new IllegalArgumentException("input clientTypeEnum is error");
        }

        //tradeType
        char tradeType;
        switch (tradeTypeEnum) {
            case NORMAL:
                tradeType = '1';
                break;
            case DROPSHIP:
                tradeType = '2';
                break;
            case BUYFORME:
                tradeType = '3';
                break;
            case REORDER:
                tradeType = '4';
                break;
            case RECHARGE:
                tradeType = '5';
                break;
            case OTHER:
                tradeType = '9';
                break;
            case MEMBERRENWAL:
                tradeType = '8';
                break;
            default:
                throw new IllegalArgumentException("input tradeType is error");
        }

        String yyMMdd = LocalDate.now().format(fmtyyMMdd);
        if (site == SiteEnum.IMPORTX) {
            //10位（兼容老格式）
            return String.valueOf(clientType) +
                    tradeType +
                    yyMMdd.substring(1, 6) +
                    orderNoHelp.getOrderNoFromRedis();
        } else {
            //11位
            return String.valueOf(clientType) +
                    tradeType +
                    yyMMdd.substring(1, 6) +
                    site.getName().charAt(0) +
                    orderNoHelp.getOrderNoFromRedis();
        }
    }

}
