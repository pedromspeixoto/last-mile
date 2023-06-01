package com.lastmile.paymentservice.client.orders;

import com.lastmile.paymentservice.client.orders.feign.OrderServiceFeignClient;
import com.lastmile.paymentservice.dto.orders.PatchOrderPaymentRequestDto;
import com.lastmile.paymentservice.enums.PaymentStatus;
import com.lastmile.paymentservice.service.exception.FeignCommunicationException;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.context.ServiceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class OrdersBridge {

    @Autowired
    OrderServiceFeignClient orderServiceFeignClient;

    public boolean patchOrderPaymentStatus(String orderIdentification, String paymentIdentification, PaymentStatus paymentStatus, ServiceContext serviceContext) throws FeignCommunicationException {
        try {
            orderServiceFeignClient.updateOrder(serviceContext.getCorrelationId(),
                                                serviceContext.getUserId(),
                                                serviceContext.getPermissions(),
                                                serviceContext.getRequestId(),
                                                Constants.REQUEST_ORIGIN_INTERNAL,
                                                Constants.REQUEST_ORIGIN_ENTITY_PAYMENT,
                                                paymentIdentification,
                                                orderIdentification,
                                                new PatchOrderPaymentRequestDto(paymentStatus));    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }
        return true;
    }

}