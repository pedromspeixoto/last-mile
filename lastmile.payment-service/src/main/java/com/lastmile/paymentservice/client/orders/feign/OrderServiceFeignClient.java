package com.lastmile.paymentservice.client.orders.feign;

import com.lastmile.utils.models.response.SuccessResponse;
import com.lastmile.paymentservice.dto.orders.PatchOrderPaymentRequestDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "order-service")
public interface OrderServiceFeignClient {

    @PatchMapping(value = "/orders/{orderIdentification}/payment-status")
    ResponseEntity<SuccessResponse> updateOrder(@RequestHeader("correlation_id") String correlationId,
                                                @RequestHeader("user_id") String userId,
                                                @RequestHeader("permissions") String permissions,
                                                @RequestHeader("request_id") String requestId,
                                                @RequestHeader("request_origin") String requestOrigin,
                                                @RequestHeader("request_entity") String requestEntity,
                                                @RequestHeader("request_entity_id") String requestEntityId,
                                                @PathVariable(value = "orderIdentification") String orderIdentification,
                                                @RequestBody PatchOrderPaymentRequestDto patchOrderPaymentRequestDto);

}