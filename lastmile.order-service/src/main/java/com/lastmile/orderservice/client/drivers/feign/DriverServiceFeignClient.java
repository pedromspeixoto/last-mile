package com.lastmile.orderservice.client.drivers.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "driver-service")
public interface DriverServiceFeignClient {

    @GetMapping(value = "/drivers/location")
    ResponseEntity<DriverResponse> getAvailableDrivers(@RequestHeader("correlation_id") String correlationId,
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

    @GetMapping(value = "/drivers/{driverIdentification}")
    ResponseEntity<SingleDriverResponse> getDriver(@RequestHeader("correlation_id") String correlationId,
                                                   @RequestHeader("user_id") String userId,
                                                   @RequestHeader("permissions") String permissions,
                                                   @RequestHeader("request_id") String requestId,
                                                   @RequestHeader("request_origin") String requestOrigin,
                                                   @RequestHeader("request_entity") String requestEntity,
                                                   @RequestHeader("request_entity_id") String requestEntityId,
                                                   @RequestParam(value="includeUserProfile") Boolean includeUserProfile,
                                                   @PathVariable(value = "driverIdentification") String driverIdentification);

}