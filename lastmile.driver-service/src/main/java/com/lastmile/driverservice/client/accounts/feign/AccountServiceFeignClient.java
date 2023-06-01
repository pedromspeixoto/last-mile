package com.lastmile.driverservice.client.accounts.feign;

import com.lastmile.driverservice.controller.response.AccountResponse;
import com.lastmile.driverservice.dto.accounts.PatchAccountRoleRequestDto;
import com.lastmile.driverservice.dto.accounts.UpdateAccountRequestDto;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "account-service")
public interface AccountServiceFeignClient {

    @GetMapping(value = "/accounts/{userIdentification}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> getAccount(@RequestHeader("correlation_id") String correlationId,
                                               @RequestHeader("user_id") String userId,
                                               @RequestHeader("permissions") String permissions,
                                               @RequestHeader("request_id") String requestId,
                                               @RequestHeader("request_origin") String requestOrigin,
                                               @RequestHeader("request_entity") String requestEntity,
                                               @RequestHeader("request_entity_id") String requestEntityId,
                                               @PathVariable("userIdentification") String userIdentification);


    @PutMapping(value = "/accounts/{userIdentification}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse> updateAccount(@RequestHeader("correlation_id") String correlationId,
                                                  @RequestHeader("user_id") String userId,
                                                  @RequestHeader("permissions") String permissions,
                                                  @RequestHeader("request_id") String requestId,
                                                  @RequestHeader("request_origin") String requestOrigin,
                                                  @RequestHeader("request_entity") String requestEntity,
                                                  @RequestHeader("request_entity_id") String requestEntityId,
                                                  @RequestBody UpdateAccountRequestDto updateAccountRequestDto, 
                                                  @PathVariable("userIdentification") String userIdentification);

    @PatchMapping(value = "/accounts/{userIdentification}/role", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse> updateAccountRole(@RequestHeader("correlation_id") String correlationId,
                                                      @RequestHeader("user_id") String userId,
                                                      @RequestHeader("permissions") String permissions,
                                                      @RequestHeader("request_id") String requestId,
                                                      @RequestHeader("request_origin") String requestOrigin,
                                                      @RequestHeader("request_entity") String requestEntity,
                                                      @RequestHeader("request_entity_id") String requestEntityId,
                                                      @RequestBody PatchAccountRoleRequestDto accountRoleRequestDto, 
                                                      @PathVariable("userIdentification") String userIdentification);


}