package com.lastmile.quartzservice.client.orders.feign;

import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "order-service")
public interface OrderServiceFeignClient {

    @PostMapping(value = "/orders/process-assigned-orders")
    ResponseEntity<SuccessResponse> processAssignedOrders(@RequestHeader("correlation_id") String correlationId,
                                                          @RequestHeader("user_id") String userId,
                                                          @RequestHeader("permissions") String permissions,
                                                          @RequestHeader("request_id") String requestId,
                                                          @RequestHeader("request_origin") String requestOrigin,
                                                          @RequestHeader("request_entity") String requestEntity,
                                                          @RequestHeader("request_entity_id") String requestEntityId);

    @PostMapping(value = "/orders/process-scheduled-orders")
    ResponseEntity<SuccessResponse> processScheduledOrders(@RequestHeader("correlation_id") String correlationId,
                                                           @RequestHeader("user_id") String userId,
                                                           @RequestHeader("permissions") String permissions,
                                                           @RequestHeader("request_id") String requestId,
                                                           @RequestHeader("request_origin") String requestOrigin,
                                                           @RequestHeader("request_entity") String requestEntity,
                                                           @RequestHeader("request_entity_id") String requestEntityId);


}