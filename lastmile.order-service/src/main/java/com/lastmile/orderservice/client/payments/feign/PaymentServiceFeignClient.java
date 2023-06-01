package com.lastmile.orderservice.client.payments.feign;

import javax.validation.Valid;

import com.lastmile.orderservice.dto.payments.CreateOutPaymentRequestDto;
import com.lastmile.orderservice.dto.payments.CreatePaymentRequestDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment-service")
public interface PaymentServiceFeignClient {

    @PostMapping(value = "/payments/create")
    ResponseEntity<CreatePaymentResponse> createPayment(@RequestHeader("correlation_id") String correlationId,
                                                        @RequestHeader("user_id") String userId,
                                                        @RequestHeader("permissions") String permissions,
                                                        @RequestHeader("request_id") String requestId,
                                                        @RequestHeader("request_origin") String requestOrigin,
                                                        @RequestHeader("request_entity") String requestEntity,
                                                        @RequestHeader("request_entity_id") String requestEntityId,
                                                        @Valid @RequestBody CreatePaymentRequestDto paymentDto);

    @PostMapping(value = "/payments/outbound/create")
    ResponseEntity<CreateOutPaymentResponse> createOutPayment(@RequestHeader("correlation_id") String correlationId,
                                                              @RequestHeader("user_id") String userId,
                                                              @RequestHeader("permissions") String permissions,
                                                              @RequestHeader("request_id") String requestId,
                                                              @RequestHeader("request_origin") String requestOrigin,
                                                              @RequestHeader("request_entity") String requestEntity,
                                                              @RequestHeader("request_entity_id") String requestEntityId,
                                                              @Valid @RequestBody CreateOutPaymentRequestDto outPaymentDto);


}