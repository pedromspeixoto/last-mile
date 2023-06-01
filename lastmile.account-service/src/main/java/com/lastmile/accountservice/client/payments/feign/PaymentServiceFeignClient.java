package com.lastmile.accountservice.client.payments.feign;

import com.lastmile.accountservice.client.payments.response.PaymentResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment-service")
public interface PaymentServiceFeignClient {

    @GetMapping(value = "/payments/details/{paymentDetailIdentification}")
    ResponseEntity<PaymentResponse> getPaymentDetail(@RequestHeader("correlation_id") String correlationId,
                                                     @RequestHeader("user_id") String userId,
                                                     @RequestHeader("permissions") String permissions,
                                                     @RequestHeader("request_id") String requestId,
                                                     @RequestHeader("request_origin") String requestOrigin,
                                                     @RequestHeader("request_entity") String requestEntity,
                                                     @RequestHeader("request_entity_id") String requestEntityId,
                                                     @PathVariable(value = "paymentDetailIdentification") String paymentDetailIdentification);

}