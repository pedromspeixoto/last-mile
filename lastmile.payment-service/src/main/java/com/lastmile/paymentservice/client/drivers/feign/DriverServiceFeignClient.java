package com.lastmile.paymentservice.client.drivers.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "driver-service")
public interface DriverServiceFeignClient {

    @GetMapping(value = "/drivers/{driverIdentification}/fiscal-entity")
    ResponseEntity<FiscalEntityResponse> getDriverFiscalEntity(@RequestHeader("correlation_id") String correlationId,
                                                               @RequestHeader("user_id") String userId,
                                                               @RequestHeader("permissions") String permissions,
                                                               @RequestHeader("request_id") String requestId,
                                                               @RequestHeader("request_origin") String requestOrigin,
                                                               @RequestHeader("request_entity") String requestEntity,
                                                               @RequestHeader("request_entity_id") String requestEntityId,
                                                               @PathVariable(value = "driverIdentification") String driverIdentification);

}