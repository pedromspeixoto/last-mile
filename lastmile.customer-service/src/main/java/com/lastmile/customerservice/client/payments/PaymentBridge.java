package com.lastmile.customerservice.client.payments;

import java.util.List;
import java.util.Optional;

import com.lastmile.customerservice.client.payments.feign.PaymentServiceFeignClient;
import com.lastmile.customerservice.controller.response.CreatePaymentResponse;
import com.lastmile.customerservice.controller.response.ListPaymentResponse;
import com.lastmile.customerservice.controller.response.PaymentResponse;
import com.lastmile.customerservice.dto.payments.CreatePaymentDetailRequestDto;
import com.lastmile.customerservice.dto.payments.CreatePaymentDetailResponseDto;
import com.lastmile.customerservice.dto.payments.GetPaymentDetailResponseDto;
import com.lastmile.customerservice.dto.payments.UpdatePaymentDetailRequestDto;
import com.lastmile.customerservice.dto.payments.feign.CreatePaymentDetailFeignRequestDto;
import com.lastmile.customerservice.dto.payments.feign.UpdatePaymentDetailFeignRequestDto;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class PaymentBridge {

    private static final String REQUEST_ORIGIN_INTERNAL = "internal";
    private static final String REQUEST_ORIGIN_ENTITY_MARKETPLACE = "marketplace";

    @Autowired
    PaymentServiceFeignClient paymentServiceFeignClient;

    public Optional<GetPaymentDetailResponseDto> getPaymentDetail(String customerIdentification, String paymentDetailIdentification, ServiceContext serviceContext) throws FeignCommunicationException {

        try {

            ResponseEntity<PaymentResponse> paymentResponse = paymentServiceFeignClient.getPaymentDetail(serviceContext.getCorrelationId(),
                                                                                                         serviceContext.getUserId(),
                                                                                                         serviceContext.getPermissions(),
                                                                                                         serviceContext.getRequestId(),
                                                                                                         REQUEST_ORIGIN_INTERNAL,
                                                                                                         REQUEST_ORIGIN_ENTITY_MARKETPLACE,
                                                                                                         customerIdentification,
                                                                                                         paymentDetailIdentification);

            if (paymentResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }

            if (paymentResponse.getStatusCode() != HttpStatus.OK && paymentResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("payment-service");
            }

            return Optional.of(paymentResponse.getBody().getData());
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

    public CreatePaymentDetailResponseDto createPaymentDetail(String entityId, EntityType entityType, String customerName, String customerEmail, String customerPhoneNumber, String customerFiscalNumber, CreatePaymentDetailRequestDto createPaymentDetailRequestDto, ServiceContext serviceContext) throws FeignCommunicationException {

        try {

            ResponseEntity<CreatePaymentResponse> paymentResponse = paymentServiceFeignClient.createPaymentDetail(serviceContext.getCorrelationId(),
                                                                                                                  serviceContext.getUserId(),
                                                                                                                  serviceContext.getPermissions(),
                                                                                                                  serviceContext.getRequestId(),
                                                                                                                  REQUEST_ORIGIN_INTERNAL,
                                                                                                                  REQUEST_ORIGIN_ENTITY_MARKETPLACE,
                                                                                                                  entityId,
                                                                                                                  CreatePaymentDetailFeignRequestDto.mapToFeignRequest(createPaymentDetailRequestDto, entityId, entityType, customerName, customerPhoneNumber, customerEmail, customerFiscalNumber));

            if (paymentResponse.getStatusCode() != HttpStatus.OK && paymentResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + paymentResponse.getStatusCode());
            }

            return new CreatePaymentDetailResponseDto(paymentResponse.getBody().getData().getPaymentDetailIdentification(),
                                                      paymentResponse.getBody().getData().getPaymentUrl());
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

    public List<GetPaymentDetailResponseDto> getPaymentDetails(String entityId, Optional<Integer> limit, Optional<Integer> offset, Optional<String> paymentDetailIdentification, ServiceContext serviceContext) throws FeignCommunicationException {

        try {

            ResponseEntity<ListPaymentResponse> paymentResponse = paymentServiceFeignClient.getPaymentDetails(serviceContext.getCorrelationId(),
                                                                                                             serviceContext.getUserId(),
                                                                                                             serviceContext.getPermissions(),
                                                                                                             serviceContext.getRequestId(),
                                                                                                             REQUEST_ORIGIN_INTERNAL,
                                                                                                             REQUEST_ORIGIN_ENTITY_MARKETPLACE,
                                                                                                             entityId,
                                                                                                             limit.orElse(10),
                                                                                                             offset.orElse(0),
                                                                                                             paymentDetailIdentification.orElse(""),
                                                                                                             entityId,
                                                                                                             EntityType.MARKETPLACE.toString());      

            if (paymentResponse.getStatusCode() != HttpStatus.OK && paymentResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + paymentResponse.getStatusCode());
            }

            return paymentResponse.getBody().getData();
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

    public void updatePaymentDetail(String entityId, String paymentDetailIdentification, UpdatePaymentDetailRequestDto updatePaymentDetailRequestDto, ServiceContext serviceContext) throws FeignCommunicationException {

        try {

            ResponseEntity<SuccessResponse> paymentResponse = paymentServiceFeignClient.updatePaymentDetail(serviceContext.getCorrelationId(),
                                                                                                            serviceContext.getUserId(),
                                                                                                            serviceContext.getPermissions(),
                                                                                                            serviceContext.getRequestId(),
                                                                                                            REQUEST_ORIGIN_INTERNAL,
                                                                                                            REQUEST_ORIGIN_ENTITY_MARKETPLACE,
                                                                                                            entityId,
                                                                                                            paymentDetailIdentification,
                                                                                                            UpdatePaymentDetailFeignRequestDto.mapToFeignRequest(updatePaymentDetailRequestDto));

            if (paymentResponse.getStatusCode() != HttpStatus.OK && paymentResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + paymentResponse.getStatusCode());
            }
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

    public void deletePaymentDetail(String entityId, String paymentDetailIdentification, ServiceContext serviceContext) throws FeignCommunicationException {

        try {

            ResponseEntity<SuccessResponse> paymentResponse = paymentServiceFeignClient.deletePaymentDetail(serviceContext.getCorrelationId(),
                                                                                                            serviceContext.getUserId(),
                                                                                                            serviceContext.getPermissions(),
                                                                                                            serviceContext.getRequestId(),
                                                                                                            REQUEST_ORIGIN_INTERNAL,
                                                                                                            REQUEST_ORIGIN_ENTITY_MARKETPLACE,
                                                                                                            entityId,
                                                                                                            paymentDetailIdentification);

            if (paymentResponse.getStatusCode() != HttpStatus.OK && paymentResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + paymentResponse.getStatusCode());
            }
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

}