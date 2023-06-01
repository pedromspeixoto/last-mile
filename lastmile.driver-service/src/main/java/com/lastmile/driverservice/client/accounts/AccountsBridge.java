package com.lastmile.driverservice.client.accounts;

import com.lastmile.driverservice.client.accounts.feign.AccountServiceFeignClient;
import com.lastmile.driverservice.controller.response.AccountResponse;
import com.lastmile.driverservice.dto.accounts.GetAccountDto;
import com.lastmile.driverservice.dto.accounts.PatchAccountRoleRequestDto;
import com.lastmile.driverservice.enums.Authorities;
import com.lastmile.driverservice.service.exception.FeignCommunicationException;
import com.lastmile.utils.models.response.SuccessResponse;
import com.lastmile.utils.context.ServiceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class AccountsBridge {

    private static final String REQUEST_ORIGIN_INTERNAL = "internal";
    private static final String REQUEST_ORIGIN_ENTITY_DRIVER = "driver";

    @Autowired
    AccountServiceFeignClient accountServiceFeignClient;

    public GetAccountDto getAccount(ServiceContext serviceContext, String userIdentification, String driverIdentification) throws FeignCommunicationException {

        try {

            ResponseEntity<AccountResponse> accountResponse = accountServiceFeignClient.getAccount(serviceContext.getCorrelationId(),
                                                                                                   serviceContext.getUserId(),
                                                                                                   serviceContext.getPermissions(),
                                                                                                   serviceContext.getRequestId(),
                                                                                                   REQUEST_ORIGIN_INTERNAL,
                                                                                                   REQUEST_ORIGIN_ENTITY_DRIVER,
                                                                                                   driverIdentification,
                                                                                                   userIdentification);

            if ((accountResponse.getStatusCode() != HttpStatus.OK && accountResponse.getStatusCode() != HttpStatus.CREATED)) {
                throw new FeignCommunicationException("account-service");
            }

            return accountResponse.getBody().getData();

        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

    public void updateAccountRole(ServiceContext serviceContext, String userIdentification, String driverIdentification, Authorities role) throws FeignCommunicationException {

        PatchAccountRoleRequestDto accountRoleRequestDto = new PatchAccountRoleRequestDto();
        accountRoleRequestDto.setRole(role.getAuthority());

        try {

            ResponseEntity<SuccessResponse> accountResponse = accountServiceFeignClient.updateAccountRole(serviceContext.getCorrelationId(),
                                                                                                          serviceContext.getUserId(),
                                                                                                          serviceContext.getPermissions(),
                                                                                                          serviceContext.getRequestId(),
                                                                                                          REQUEST_ORIGIN_INTERNAL,
                                                                                                          REQUEST_ORIGIN_ENTITY_DRIVER,
                                                                                                          driverIdentification,
                                                                                                          accountRoleRequestDto,
                                                                                                          userIdentification);

            if ((accountResponse.getStatusCode() != HttpStatus.OK && accountResponse.getStatusCode() != HttpStatus.CREATED)) {
                throw new FeignCommunicationException("account-service");
            }

        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

}