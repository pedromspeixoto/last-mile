package com.lastmile.driverservice.client.payments.feign;

import com.lastmile.driverservice.controller.response.ListPaymentResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service")
public interface PaymentServiceFeignClient {

    @GetMapping(value = "/payments/outbound/")
    ResponseEntity<ListPaymentResponse> getOutboundPayments(@RequestHeader("correlation_id") String correlationId,
                                                            @RequestHeader("user_id") String userId,
                                                            @RequestHeader("permissions") String permissions,
                                                            @RequestHeader("request_id") String requestId,
                                                            @RequestHeader("request_origin") String requestOrigin,
                                                            @RequestHeader("request_entity") String requestEntity,
                                                            @RequestHeader("request_entity_id") String requestEntityId,
                                                            @RequestParam(value = "limit", required = false) Integer limit,
                                                            @RequestParam(value = "offset", required = false) Integer offset,
                                                            @RequestParam(value = "outPaymentIdentification", required = false) String outPaymentIdentification,
                                                            @RequestParam(value = "entityIdentification", required = false) String entityIdentification,
                                                            @RequestParam(value = "entityType", required = false) String entityType);
                                                          
}