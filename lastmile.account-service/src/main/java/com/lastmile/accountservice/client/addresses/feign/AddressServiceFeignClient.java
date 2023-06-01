package com.lastmile.accountservice.client.addresses.feign;

import com.lastmile.accountservice.client.addresses.response.AddressResponse;
import com.lastmile.accountservice.client.addresses.response.CreateAddressResponse;
import com.lastmile.accountservice.dto.addresses.feign.CreateAddressFeignRequestDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "address-service")
public interface AddressServiceFeignClient {

    @PostMapping(value = "/addresses/create")
    ResponseEntity<CreateAddressResponse> createAddress(@RequestHeader("correlation_id") String correlationId,
                                                        @RequestHeader("user_id") String userId,
                                                        @RequestHeader("permissions") String permissions,
                                                        @RequestHeader("request_id") String requestId,
                                                        @RequestHeader("request_origin") String requestOrigin,
                                                        @RequestHeader("request_entity") String requestEntity,
                                                        @RequestHeader("request_entity_id") String requestEntityId,
                                                        @RequestBody CreateAddressFeignRequestDto addressDto);


    @GetMapping(value = "/addresses/{addressIdentification}")
    ResponseEntity<AddressResponse> getAddress(@RequestHeader("correlation_id") String correlationId,
                                               @RequestHeader("user_id") String userId,
                                               @RequestHeader("permissions") String permissions,
                                               @RequestHeader("request_id") String requestId,
                                               @RequestHeader("request_origin") String requestOrigin,
                                               @RequestHeader("request_entity") String requestEntity,
                                               @RequestHeader("request_entity_id") String requestEntityId,
                                               @PathVariable(value = "addressIdentification") String addressIdentification);

}