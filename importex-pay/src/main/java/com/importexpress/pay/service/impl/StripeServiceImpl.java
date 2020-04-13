package com.importexpress.pay.service.impl;

import com.importexpress.pay.bean.StripeModel;
import com.importexpress.pay.service.StripeService;
import com.importexpress.pay.util.Config;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lhao
 */
@Service
@Slf4j
public class StripeServiceImpl implements StripeService {

    private final Config config;

    private static final String ORDER_NO = "order_no";

    public StripeServiceImpl(Config config) {
        this.config = config;
    }

    /**
     * do pay
     *
     * @param stripeModel
     * @return
     */
    @Override
    public Charge doPay(StripeModel stripeModel) throws StripeException {
        Stripe.apiKey = config.stripeSk;

        Map<String, Object> params = new HashMap<>(5);
        params.put("amount", stripeModel.getAmount());
        params.put("currency", "usd");
        params.put("description", "import-express.com");
        params.put("source", stripeModel.getToken());

        Map<String, String> metadata = new HashMap<>(3);
        metadata.put(ORDER_NO, stripeModel.getOrderNo());

        params.put("metadata", metadata);
        params.put("receipt_email", stripeModel.getReceiptEmail());
        try {
            Charge charge = Charge.create(params);
            log.debug("charge=[{}]", charge.toJson());
            log.info("charge.Status=[{}]", charge.getStatus());
            //test
//            if(true) {
//                throw new CardException(" Your card was declined", "", "", "",
//                        "", "", 400, null);
//            }
            return charge;
        } catch (StripeException e) {
            log.error("StripeException:", e);
            throw e;
        }
    }

}
