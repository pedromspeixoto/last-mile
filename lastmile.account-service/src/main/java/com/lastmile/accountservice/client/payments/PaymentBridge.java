package com.lastmile.accountservice.client.payments;

import java.util.Optional;

import com.lastmile.accountservice.client.payments.feign.PaymentServiceFeignClient;
import com.lastmile.accountservice.client.payments.response.PaymentResponse;
import com.lastmile.accountservice.dto.payments.GetPaymentDetailResponseDto;
import com.lastmile.utils.exceptions.FeignCommunicationException;
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.context.ServiceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class PaymentBridge {

    private static final String REQUEST_ORIGIN_INTERNAL = "internal";
    private static final String REQUEST_ORIGIN_ENTITY_ACCOUNT = "account";

    @Autowired
    PaymentServiceFeignClient paymentServiceFeignClient;

    @Autowired
    private CustomLogging logger;

    public Optional<GetPaymentDetailResponseDto> getPaymentDetail(String userIdentification, String paymentDetailIdentification, ServiceContext serviceContext) throws FeignCommunicationException {
        logger.info("calling get payment detail for user " + userIdentification, serviceContext);
        try {
            ResponseEntity<PaymentResponse> paymentResponse = paymentServiceFeignClient.getPaymentDetail(serviceContext.getCorrelationId(),
                                                                                                         serviceContext.getUserId(),
                                                                                                         serviceContext.getPermissions(),
                                                                                                         serviceContext.getRequestId(),
                                                                                                         REQUEST_ORIGIN_INTERNAL,
                                                                                                         REQUEST_ORIGIN_ENTITY_ACCOUNT,
                                                                                                         userIdentification,
                                                                                                         paymentDetailIdentification);
            logger.info("payment service responded with status: " + paymentResponse.getStatusCode(), serviceContext);
            return Optional.of(paymentResponse.getBody().getData());
        } catch (FeignException ex) {
            logger.error("error processing payment feign request " + ex.getMessage(), serviceContext);
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

}