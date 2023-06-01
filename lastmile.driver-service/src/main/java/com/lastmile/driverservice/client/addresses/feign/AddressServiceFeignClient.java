package com.lastmile.driverservice.client.addresses.feign;

import com.lastmile.driverservice.controller.response.AddressListResponse;
import com.lastmile.driverservice.controller.response.AddressResponse;
import com.lastmile.driverservice.controller.response.CreateAddressResponse;
import com.lastmile.driverservice.dto.addresses.UpdateAddressRequestDto;
import com.lastmile.driverservice.dto.addresses.feign.CreateAddressFeignRequestDto;
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

    @PutMapping(value = "/addresses/{addressIdentification}")
    ResponseEntity<CreateAddressResponse> updateAddress(@RequestHeader("correlation_id") String correlationId,
                                                        @RequestHeader("user_id") String userId,
                                                        @RequestHeader("permissions") String permissions,
                                                        @RequestHeader("request_id") String requestId,
                                                        @RequestHeader("request_origin") String requestOrigin,
                                                        @RequestHeader("request_entity") String requestEntity,
                                                        @RequestHeader("request_entity_id") String requestEntityId,
                                                        @PathVariable(value = "addressIdentification") String addressIdentification,
                                                        @RequestBody UpdateAddressRequestDto updateAddressRequestDto);

    @GetMapping(value = "/addresses/{addressIdentification}")
    ResponseEntity<AddressResponse> getAddress(@RequestHeader("correlation_id") String correlationId,
                                               @RequestHeader("user_id") String userId,
                                               @RequestHeader("permissions") String permissions,
                                               @RequestHeader("request_id") String requestId,
                                               @RequestHeader("request_origin") String requestOrigin,
                                               @RequestHeader("request_entity") String requestEntity,
                                               @RequestHeader("request_entity_id") String requestEntityId,
                                               @PathVariable(value = "addressIdentification") String addressIdentification);

    @DeleteMapping(value = "/addresses/{addressIdentification}")
    ResponseEntity<SuccessResponse> deleteAddress(@RequestHeader("correlation_id") String correlationId,
                                                  @RequestHeader("user_id") String userId,
                                                  @RequestHeader("permissions") String permissions,
                                                  @RequestHeader("request_id") String requestId,
                                                  @RequestHeader("request_origin") String requestOrigin,
                                                  @RequestHeader("request_entity") String requestEntity,
                                                  @RequestHeader("request_entity_id") String requestEntityId,
                                                  @PathVariable(value = "addressIdentification") String addressIdentification);


    @GetMapping(value = "/addresses/")
    ResponseEntity<AddressListResponse> getAddresses(@RequestHeader("correlation_id") String correlationId,
                                                     @RequestHeader("user_id") String userId,
                                                     @RequestHeader("permissions") String permissions,
                                                     @RequestHeader("request_id") String requestId,
                                                     @RequestHeader("request_origin") String requestOrigin,
                                                     @RequestHeader("request_entity") String requestEntity,
                                                     @RequestHeader("request_entity_id") String requestEntityId,
                                                     @RequestParam(value = "limit", required = false) Integer limit,
                                                     @RequestParam(value = "offset", required = false) Integer offset,
                                                     @RequestParam(value = "addressIdentification", required = false) String addressIdentification,
                                                     @RequestParam(value = "entityIdentification", required = false) String entityIdentification,
                                                     @RequestParam(value = "entityType", required = false) String entityType);

}