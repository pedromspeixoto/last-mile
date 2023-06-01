package com.lastmile.driverservice.client.payments;

import java.util.List;
import java.util.Optional;

import com.lastmile.driverservice.client.payments.feign.PaymentServiceFeignClient;
import com.lastmile.driverservice.controller.response.ListPaymentResponse;
import com.lastmile.driverservice.dto.payments.GetOutPaymentResponseDto;
import com.lastmile.driverservice.service.exception.FeignCommunicationException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.EntityType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class PaymentBridge {

    private static final String REQUEST_ORIGIN_INTERNAL = "internal";
    private static final String REQUEST_ORIGIN_ENTITY_FISCAL_ENTITY = "fiscalentity";

    @Autowired
    PaymentServiceFeignClient paymentServiceFeignClient;

    public List<GetOutPaymentResponseDto> getOutboundPayments(String entityId, Optional<Integer> limit, Optional<Integer> offset, Optional<String> paymentDetailIdentification, ServiceContext serviceContext) throws FeignCommunicationException {

        try {

            ResponseEntity<ListPaymentResponse> paymentResponse = paymentServiceFeignClient.getOutboundPayments(serviceContext.getCorrelationId(),
                                                                                                               serviceContext.getUserId(),
                                                                                                               serviceContext.getPermissions(),
                                                                                                               serviceContext.getRequestId(),
                                                                                                               REQUEST_ORIGIN_INTERNAL,
                                                                                                               REQUEST_ORIGIN_ENTITY_FISCAL_ENTITY,
                                                                                                               entityId,
                                                                                                               limit.orElse(10),
                                                                                                               offset.orElse(0),
                                                                                                               paymentDetailIdentification.orElse(""),
                                                                                                               entityId,
                                                                                                               EntityType.FISCALENTITY.toString());      

            if (paymentResponse.getStatusCode() != HttpStatus.OK && paymentResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + paymentResponse.getStatusCode());
            }

            return paymentResponse.getBody().getData();
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

}