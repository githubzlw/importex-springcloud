package com.importexpress.pay.service;

import com.importexpress.pay.bean.StripeModel;
import com.stripe.exception.*;
import com.stripe.model.Charge;

public interface StripeService {

    /**
     * do pay
     * @param stripeModel
     * @return
     */
    Charge doPay(StripeModel stripeModel) throws StripeException;
}
