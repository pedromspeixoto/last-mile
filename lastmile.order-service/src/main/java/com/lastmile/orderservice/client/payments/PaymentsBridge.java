package com.lastmile.orderservice.client.payments;

import com.lastmile.orderservice.client.payments.feign.CreateOutPaymentResponse;
import com.lastmile.orderservice.client.payments.feign.CreatePaymentResponse;
import com.lastmile.orderservice.client.payments.feign.PaymentServiceFeignClient;
import com.lastmile.orderservice.dto.OrderRequestDto;
import com.lastmile.orderservice.dto.payments.CreateOutPaymentRequestDto;
import com.lastmile.orderservice.dto.payments.CreateOutPaymentResponseDto;
import com.lastmile.orderservice.dto.payments.CreatePaymentRequestDto;
import com.lastmile.orderservice.dto.payments.CreatePaymentResponseModel;
import com.lastmile.orderservice.service.exception.FeignCommunicationException;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.context.ServiceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class PaymentsBridge {

    @Autowired
    PaymentServiceFeignClient paymentServiceFeignClient;

    public CreatePaymentResponseModel createPayment(ServiceContext serviceContext, OrderRequestDto orderRequestDto, String orderIdentification, Double paymentValue) throws FeignCommunicationException {

        CreatePaymentRequestDto paymentRequestDto = new CreatePaymentRequestDto(orderRequestDto, orderIdentification, paymentValue);

        try {

            ResponseEntity<CreatePaymentResponse> paymentResponse = paymentServiceFeignClient.createPayment(serviceContext.getCorrelationId(),
                                                                                                            serviceContext.getUserId(),
                                                                                                            serviceContext.getPermissions(),
                                                                                                            serviceContext.getRequestId(),
                                                                                                            Constants.REQUEST_ORIGIN_INTERNAL,
                                                                                                            Constants.REQUEST_ORIGIN_ENTITY_ORDER,
                                                                                                            null,
                                                                                                            paymentRequestDto);
            return paymentResponse.getBody().getData();
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

    public CreateOutPaymentResponseDto createOutPayment(ServiceContext serviceContext,
                                                        String entityIdentification,
                                                        EntityType entityType,
                                                        String transactionIdentification,
                                                        Double paymentValue) throws FeignCommunicationException {

        CreateOutPaymentRequestDto outPaymentRequestDto = new CreateOutPaymentRequestDto(entityIdentification, entityType, transactionIdentification, paymentValue);

        try {

            ResponseEntity<CreateOutPaymentResponse> outPaymentResponse = paymentServiceFeignClient.createOutPayment(serviceContext.getCorrelationId(),
                                                                                                                     serviceContext.getUserId(),
                                                                                                                     serviceContext.getPermissions(),
                                                                                                                     serviceContext.getRequestId(),
                                                                                                                     Constants.REQUEST_ORIGIN_INTERNAL,
                                                                                                                     entityType.toString().toLowerCase(),
                                                                                                                     entityIdentification,
                                                                                                                     outPaymentRequestDto);
            return outPaymentResponse.getBody().getData();
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

}