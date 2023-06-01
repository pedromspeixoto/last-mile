package com.lastmile.orderengine.client.drivers.feign;

import com.lastmile.orderengine.dto.AssignOrderRequestDto;
import com.lastmile.orderengine.dto.controller.SuccessResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "driver-service")
public interface DriverServiceFeignClient {

    @PutMapping(value = "/drivers/{driverIdentification}/orders")
    ResponseEntity<SuccessResponse> assignOrderToDriver(@RequestHeader("correlation_id") String correlationId,
                                                        @RequestHeader("user_id") String userId,
                                                        @RequestHeader("permissions") String permissions,
                                                        @RequestHeader("request_id") String requestId,
                                                        @RequestHeader("request_origin") String requestOrigin,
                                                        @RequestHeader("request_entity") String requestEntity,
                                                        @RequestHeader("request_entity_id") String requestEntityId,
                                                        @RequestBody AssignOrderRequestDto assignOrderRequestDto,
                                                        @PathVariable("driverIdentification") String driverIdentification);

    @GetMapping(value = "/drivers/location")
    ResponseEntity<DriverResponse> getDriversByLocation(@RequestHeader("correlation_id") String correlationId,
                                                        @RequestHeader("user_id") String userId,
                                                        @RequestHeader("permissions") String permissions,
                                                        @RequestHeader("request_id") String requestId,
                                                        @RequestHeader("request_origin") String requestOrigin,
                                                        @RequestHeader("request_entity") String requestEntity,
                                                        @RequestHeader("request_entity_id") String requestEntityId,
                                                        @RequestParam(value="latitude") Double latitude,
                                                        @RequestParam(value="longitude") Double longitude,
                                                        @RequestParam(value="radius") Integer radius,
                                                        @RequestParam(value="limit") Integer limit,
                                                        @RequestParam(value="status") String status);

    @GetMapping(value = "/drivers/")
    ResponseEntity<DriverResponse> getDrivers(@RequestHeader("correlation_id") String correlationId,
                                              @RequestHeader("user_id") String userId,
                                              @RequestHeader("permissions") String permissions,
                                              @RequestHeader("request_id") String requestId,
                                              @RequestHeader("request_origin") String requestOrigin,
                                              @RequestHeader("request_entity") String requestEntity,
                                              @RequestHeader("request_entity_id") String requestEntityId,
                                              @RequestParam(value="limit") Integer limit,
                                              @RequestParam(value="offset") Integer offset,
                                              @RequestParam(value="status") String status);

}