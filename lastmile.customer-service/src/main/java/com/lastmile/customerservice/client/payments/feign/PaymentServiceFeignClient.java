package com.lastmile.customerservice.client.payments.feign;

import com.lastmile.customerservice.controller.response.CreatePaymentResponse;
import com.lastmile.customerservice.controller.response.ListPaymentResponse;
import com.lastmile.customerservice.controller.response.PaymentResponse;
import com.lastmile.customerservice.dto.payments.feign.CreatePaymentDetailFeignRequestDto;
import com.lastmile.customerservice.dto.payments.feign.UpdatePaymentDetailFeignRequestDto;

import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service")
public interface PaymentServiceFeignClient {

    @PostMapping(value = "/payments/details/create")
    ResponseEntity<CreatePaymentResponse> createPaymentDetail(@RequestHeader("correlation_id") String correlationId,
                                                              @RequestHeader("user_id") String userId,
                                                              @RequestHeader("permissions") String permissions,
                                                              @RequestHeader("request_id") String requestId,
                                                              @RequestHeader("request_origin") String requestOrigin,
                                                              @RequestHeader("request_entity") String requestEntity,
                                                              @RequestHeader("request_entity_id") String requestEntityId,
                                                              @RequestBody CreatePaymentDetailFeignRequestDto paymentDetailRequestDto);

    @GetMapping(value = "/payments/details/{paymentDetailIdentification}")
    ResponseEntity<PaymentResponse> getPaymentDetail(@RequestHeader("correlation_id") String correlationId,
                                                     @RequestHeader("user_id") String userId,
                                                     @RequestHeader("permissions") String permissions,
                                                     @RequestHeader("request_id") String requestId,
                                                     @RequestHeader("request_origin") String requestOrigin,
                                                     @RequestHeader("request_entity") String requestEntity,
                                                     @RequestHeader("request_entity_id") String requestEntityId,
                                                     @PathVariable(value = "paymentDetailIdentification") String paymentDetailIdentification);

    @PutMapping(value = "/payments/details/{paymentDetailIdentification}")
    ResponseEntity<SuccessResponse> updatePaymentDetail(@RequestHeader("correlation_id") String correlationId,
                                                        @RequestHeader("user_id") String userId,
                                                        @RequestHeader("permissions") String permissions,
                                                        @RequestHeader("request_id") String requestId,
                                                        @RequestHeader("request_origin") String requestOrigin,
                                                        @RequestHeader("request_entity") String requestEntity,
                                                        @RequestHeader("request_entity_id") String requestEntityId,
                                                        @PathVariable(value = "paymentDetailIdentification") String paymentDetailIdentification,
                                                        @RequestBody UpdatePaymentDetailFeignRequestDto updatePaymentDetailFeignRequestDto);

    @DeleteMapping(value = "/payments/details/{paymentDetailIdentification}")
    ResponseEntity<SuccessResponse> deletePaymentDetail(@RequestHeader("correlation_id") String correlationId,
                                                        @RequestHeader("user_id") String userId,
                                                        @RequestHeader("permissions") String permissions,
                                                        @RequestHeader("request_id") String requestId,
                                                        @RequestHeader("request_origin") String requestOrigin,
                                                        @RequestHeader("request_entity") String requestEntity,
                                                        @RequestHeader("request_entity_id") String requestEntityId,
                                                        @PathVariable(value = "paymentDetailIdentification") String paymentDetailIdentification);
         
    @GetMapping(value = "/payments/details/")
    ResponseEntity<ListPaymentResponse> getPaymentDetails(@RequestHeader("correlation_id") String correlationId,
                                                          @RequestHeader("user_id") String userId,
                                                          @RequestHeader("permissions") String permissions,
                                                          @RequestHeader("request_id") String requestId,
                                                          @RequestHeader("request_origin") String requestOrigin,
                                                          @RequestHeader("request_entity") String requestEntity,
                                                          @RequestHeader("request_entity_id") String requestEntityId,
                                                          @RequestParam(value = "limit", required = false) Integer limit,
                                                          @RequestParam(value = "offset", required = false) Integer offset,
                                                          @RequestParam(value = "paymentDetailIdentification", required = false) String paymentDetailIdentification,
                                                          @RequestParam(value = "entityIdentification", required = false) String entityIdentification,
                                                          @RequestParam(value = "entityType", required = false) String entityType);

}