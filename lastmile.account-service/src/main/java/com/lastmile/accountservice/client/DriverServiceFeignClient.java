package com.lastmile.accountservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "driver-service")
public interface DriverServiceFeignClient {

    @DeleteMapping(value = "/drivers/")
    ResponseEntity<?> deleteDriver(@RequestParam(value="userIdentification") String userIdentification);
    
}