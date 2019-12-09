package com.importexpress.pay.service.impl;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.braintreepayments.http.HttpResponse;
import com.braintreepayments.http.exceptions.HttpException;
import com.braintreepayments.http.exceptions.SerializeException;
import com.braintreepayments.http.serializer.Json;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.pay.service.PaypalService;
import com.importexpress.pay.util.Config;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.payments.CapturesRefundRequest;
import com.paypal.payments.Money;
import com.paypal.payments.Refund;
import com.paypal.payments.RefundRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


/**
 * @author luohao
 */
@Service
@Slf4j
public class PaypalServiceImpl implements PaypalService {

    private final Config config;

    public PaypalServiceImpl(Config config) {
        this.config = config;
    }

    private static String strId =null;

    @Override
    public Payment createPayment(
            Double total,
            String cancelUrl,
            String successUrl,
            String orderNO,String customMsg
    ) throws PayPalRESTException {

        return createPayment(
                total,
                "USD",
                PayPalPaymentMethodEnum.paypal,
                PayPalPaymentIntentEnum.sale,
                "",
                cancelUrl,
                successUrl,
                orderNO,customMsg);

    }

    @Override
    public Payment createPayment(
            Double total,
            String currency,
            PayPalPaymentMethodEnum method,
            PayPalPaymentIntentEnum intent,
            String description,
            String cancelUrl,
            String successUrl,
            String orderNO,
            String customMsg) throws PayPalRESTException {
        log.info("createPayment():[{}],[{}],[{}],[{}],[{}],[{}],[{}]", total, currency, method, intent, description, cancelUrl, successUrl);

        APIContext apiContext = getApiContext();

        // ###Details
        Details details = new Details();
        details.setShipping("0");
        String strTotal = String.format("%.2f", total);
        details.setSubtotal(strTotal);
        details.setTax("0");

        // ###Amount
        Amount amount = new Amount();
        amount.setCurrency(currency);
        // Total must be equal to sum of shipping, tax and subtotal.
        amount.setTotal(strTotal);
        amount.setDetails(details);

        // ###Transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setCustom(customMsg);
        // ###Transactions
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        // ### Items
        Item item = new Item();
        item.setName(orderNO).setQuantity("1").setCurrency(currency).setPrice(strTotal);
        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();
        items.add(item);
        itemList.setItems(items);
        transaction.setItemList(itemList);

        // ###Payer
        Payer payer = new Payer();
        payer.setPaymentMethod(method.toString());

        // ###Payment
        Payment payment = new Payment();
        payment.setIntent(intent.toString());
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        // ###Redirect URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        //不显示收货地址信息
        //payment.setExperienceProfileId(getWebProfile(apiContext));

        return payment.create(apiContext);
    }

    @Override
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {

        log.info("executePayment():[{}],[{}]", paymentId, payerId);

        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        return payment.execute(getApiContext(), paymentExecute);

    }

    private APIContext getApiContext() {

        log.info("getApiContext()");

        Map<String, String> sdkConfig = new HashMap<>(1);
        sdkConfig.put("mode", config.paypalMode);
        return new APIContext(config.PaypalClientId, config.PaypalClientSecret, config.paypalMode, sdkConfig);
    }


    private synchronized static String getWebProfile(APIContext apiContext) throws PayPalRESTException {

        if(strId==null) {
            WebProfile webProfile = new WebProfile();
            InputFields inputField = new InputFields();
            inputField.setNoShipping(1);
            webProfile.setInputFields(inputField);
            String name="WebProfile"+ UUID.randomUUID();
            log.info("getWebProfile name="+name);
            webProfile.setName(name);
            strId= webProfile.create(apiContext).getId();
        }

        log.debug("webProfile ID:"+strId);
        return strId;
    }

    @Override
    public String getRandomUUID(){
        return UUID.randomUUID().toString();
    }

    @Override
    public CommonResult refund(String captureId, Double amount) throws SerializeException {

        CapturesRefundRequest request = new CapturesRefundRequest(captureId);
        request.prefer("return=representation");

        Money money = new Money();
        money.currencyCode("USD");
        money.value(amount.toString());
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.amount(money);
        request.requestBody(refundRequest);

        HttpResponse<Refund> response;
        try {
            response = this.getPayPalHttpClient().execute(request);
        } catch(IOException ioe){
            log.error("IOException",ioe);
            return CommonResult.failed(ioe.getClass().getName()+":"+ ioe.getMessage());
        }
        log.info(new JSONObject(new Json()
                .serialize(response.result())).toString(4));
        String strRes = MoreObjects.toStringHelper(response)
                .add("StatusCode", response.statusCode())
                .add("Status", response.result().status())
                .add("currency", response.result().amount().currencyCode())
                .add("amount", response.result().amount().value())
                .add("RefundId", response.result().id()).toString();
        log.info("response: [{}]",strRes);
        if (response.statusCode() == 201) {
            if(amount.equals(Double.parseDouble(response.result().amount().value()))){
                return CommonResult.success();
            }else{
                log.error("The refund amount is wrong");
                return CommonResult.failed("The amount is wrong");
            }
        } else {
            return CommonResult.failed(strRes);
        }
    }

    private PayPalHttpClient getPayPalHttpClient() {

        PayPalEnvironment env;
        if (config.isPaypalSandbox) {
            env = new PayPalEnvironment.Sandbox(
                    config.PaypalClientId, config.PaypalClientSecret);
        } else {
            env = new PayPalEnvironment.Live(
                    config.PaypalClientId, config.PaypalClientSecret);
        }
        return new PayPalHttpClient(env);
    }

}