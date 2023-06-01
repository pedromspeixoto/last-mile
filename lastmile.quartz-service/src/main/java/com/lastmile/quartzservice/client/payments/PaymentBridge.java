package com.lastmile.quartzservice.client.payments;

import java.util.UUID;

import com.lastmile.quartzservice.client.payments.feign.PaymentServiceFeignClient;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.logs.CustomLogging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class PaymentBridge {

    @Autowired
    CustomLogging logger;

    @Autowired
    PaymentServiceFeignClient paymentServiceFeignClient;

    public Boolean callOutboundPaymentsBatch(String batchId) throws FeignException {
        try {
            paymentServiceFeignClient.processOutboundPayments(batchId,
                                                              Constants.REQUEST_ORIGIN_QUARTZ,
                                                              null,
                                                              UUID.randomUUID().toString(),
                                                              Constants.REQUEST_ORIGIN_BATCH,
                                                              null,
                                                              null);
            return true;
        } catch (Exception e) {
            logger.error("batch id: " + batchId + ". error during payments feign execution: " + e.getMessage());
            return false;
        }
    }
}