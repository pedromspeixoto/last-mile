package com.lastmile.customerservice.client.orders.feign;

import com.lastmile.customerservice.controller.response.OrderResponse;
import com.lastmile.customerservice.dto.orders.feign.OrderFeignRequestDto;
import com.lastmile.customerservice.dto.orders.feign.OrderFeignUpdateRequestDto;

import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service")
public interface OrderServiceFeignClient {

    @PostMapping(value = "/orders/create")
    ResponseEntity<SuccessResponse> createOrder(@RequestHeader("correlation_id") String correlationId,
                                                @RequestHeader("user_id") String userId,
                                                @RequestHeader("permissions") String permissions,
                                                @RequestHeader("request_id") String requestId,
                                                @RequestHeader("request_origin") String requestOrigin,
                                                @RequestHeader("request_entity") String requestEntity,
                                                @RequestHeader("request_entity_id") String requestEntityId,
                                                @RequestBody OrderFeignRequestDto orderFeignRequestDto);

    @GetMapping(value = "/orders/")
    ResponseEntity<OrderResponse> getOrders(@RequestHeader("correlation_id") String correlationId,
                                            @RequestHeader("user_id") String userId,
                                            @RequestHeader("permissions") String permissions,
                                            @RequestHeader("request_id") String requestId,
                                            @RequestHeader("request_origin") String requestOrigin,
                                            @RequestHeader("request_entity") String requestEntity,
                                            @RequestHeader("request_entity_id") String requestEntityId,
                                            @RequestParam(value = "limit", required = false) Integer limit,
                                            @RequestParam(value = "offset", required = false) Integer offset,
                                            @RequestParam(value = "status", required = false) String status,
                                            @RequestParam(value = "orderIdentification", required = false) String orderIdentification,
                                            @RequestParam(value = "requesterIdentification", required = false) String requesterIdentification,
                                            @RequestParam(value = "assignedDriver", required = false) String assignedDriverIdentification);

    @PutMapping(value = "/orders/{orderIdentification}")
    ResponseEntity<SuccessResponse> updateOrder(@RequestHeader("correlation_id") String correlationId,
                                                @RequestHeader("user_id") String userId,
                                                @RequestHeader("permissions") String permissions,
                                                @RequestHeader("request_id") String requestId,
                                                @RequestHeader("request_origin") String requestOrigin,
                                                @RequestHeader("request_entity") String requestEntity,
                                                @RequestHeader("request_entity_id") String requestEntityId,
                                                @PathVariable(value = "orderIdentification") String orderIdentification,
                                                @RequestBody OrderFeignUpdateRequestDto orderFeignRequestDto);

}