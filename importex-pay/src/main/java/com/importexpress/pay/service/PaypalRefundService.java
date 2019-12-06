package com.importexpress.pay.service;

import com.alibaba.fastjson.JSONObject;
import com.braintreepayments.http.HttpResponse;
import com.braintreepayments.http.serializer.Json;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.payments.*;

import java.io.IOException;


public class PaypalRefundService {

    /**
     *Set up the PayPal Java SDK environment with PayPal access credentials.
     *This sample uses SandboxEnvironment. In production, use LiveEnvironment.
     */
    private PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
            "YOUR APPLICATION CLIENT ID",
            "YOUR APPLICATION CLIENT SECRET");

    /**
     *PayPal HTTP client instance with environment that has access
     *credentials context. Use to invoke PayPal APIs.
     */
    PayPalHttpClient client = new PayPalHttpClient(environment);

    //2. Set up your server to receive a call from the client
    // Method to refund the capture. Pass a valid capture ID.
    //
    // @param captureId Capture ID from authorizeOrder response
    // @param debug     true = print response data
    // @return HttpResponse<Capture> response received from API
    // @throws IOException Exceptions from API if any

    public HttpResponse<Refund> refundOrder(String captureId, boolean debug) throws IOException {
        CapturesRefundRequest request = new CapturesRefundRequest(captureId);
        request.prefer("return=representation");
        request.requestBody(buildRequestBody());

        HttpResponse<Refund> response = client().execute(request);
        if (debug) {
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Status: " + response.result().status());
            System.out.println("Refund Id: " + response.result().id());
            System.out.println("Links: ");
            for (LinkDescription link : response.result().links()) {
                System.out.println("\t" + link.rel() + ": " + link.href() + "\tCall Type: " + link.method());
            }

        }
        return response;
    }

    // Creating a body for partial refund request.
    // For full refund, pass the empty body.
    //
    // @return OrderRequest request with empty body

    public RefundRequest buildRequestBody() {
        RefundRequest refundRequest = new RefundRequest();
        Money money = new Money();
        money.currencyCode("USD");
        money.value("20.00");
        refundRequest.amount(money);

        return refundRequest;
    }

    /**
     *Method to get client object
     *
     *@return PayPalHttpClient client
     */
    public PayPalHttpClient client() {
        return this.client;
    }

    // This function initiates capture refund.
    // Replace Capture ID with a valid capture ID.
    //
    // @param args

    public static void main(String[] args) {
        try {
            new PaypalRefundService().refundOrder(" REPLACE-WITH-VALID-CAPTURE-ID", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}