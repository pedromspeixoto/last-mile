package com.lastmile.orderengine.client.orders.feign;

import java.util.Date;

import com.lastmile.orderengine.dto.controller.OrderHistoryResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service")
public interface OrderServiceFeignClient {

    @GetMapping(value = "/orders/{orderIdentification}/history")
    ResponseEntity<OrderHistoryResponse> getOrders(@RequestHeader("correlation_id") String correlationId,
                                                   @RequestHeader("user_id") String userId,
                                                   @RequestHeader("permissions") String permissions,
                                                   @RequestHeader("request_id") String requestId,
                                                   @RequestHeader("request_origin") String requestOrigin,
                                                   @RequestHeader("request_entity") String requestEntity,
                                                   @RequestHeader("request_entity_id") String requestEntityId,
                                                   @PathVariable("orderIdentification") String orderIdentification,
                                                   @RequestParam(value = "startDate", required = false) Date startDate,
                                                   @RequestParam(value = "endDate", required = false) Date endDate,
                                                   @RequestParam(value = "driverIdentification", required = false) String driverIdentification);

}