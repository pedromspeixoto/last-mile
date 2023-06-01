package com.lastmile.accountservice.client.addresses;

import java.util.Optional;

import com.lastmile.accountservice.client.addresses.feign.AddressServiceFeignClient;
import com.lastmile.accountservice.client.addresses.response.AddressResponse;
import com.lastmile.accountservice.client.addresses.response.CreateAddressResponse;
import com.lastmile.accountservice.dto.addresses.CreateAddressRequestDto;
import com.lastmile.accountservice.dto.addresses.GetAddressResponseDto;
import com.lastmile.accountservice.dto.addresses.feign.CreateAddressFeignRequestDto;
import com.lastmile.utils.exceptions.FeignCommunicationException;
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.constants.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class AddressBridge {

    @Autowired
    private CustomLogging logger;

    @Autowired
    AddressServiceFeignClient addressServiceFeignClient;

    public Optional<GetAddressResponseDto> getAddress(String addressIdentification, ServiceContext serviceContext) throws FeignCommunicationException {
        logger.info("calling get address for address " + addressIdentification, serviceContext);
        try {

            ResponseEntity<AddressResponse> addressResponse = addressServiceFeignClient.getAddress(serviceContext.getCorrelationId(),
                                                                                                   serviceContext.getUserId(),
                                                                                                   serviceContext.getPermissions(),
                                                                                                   serviceContext.getRequestId(),
                                                                                                   Constants.REQUEST_ORIGIN_INTERNAL,
                                                                                                   Constants.REQUEST_ORIGIN_ENTITY_ACCOUNT,
                                                                                                   serviceContext.getUserId(),
                                                                                                   addressIdentification);
            logger.info("address service responded with status: " + addressResponse.getStatusCode(), serviceContext);
            return Optional.of(addressResponse.getBody().getData());
        } catch (FeignException ex) {
            logger.error("error processing address feign request " + ex.getMessage(), serviceContext);
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

    public String createAddress(String entityId, EntityType entityType, CreateAddressRequestDto createAddressRequestDto, ServiceContext serviceContext) throws FeignCommunicationException {
        logger.info("calling create address service for entity " + entityId, serviceContext);
        try {

            ResponseEntity<CreateAddressResponse> addressResponse = addressServiceFeignClient.createAddress(serviceContext.getCorrelationId(),
                                                                                                            serviceContext.getUserId(),
                                                                                                            serviceContext.getPermissions(),
                                                                                                            serviceContext.getRequestId(),
                                                                                                            Constants.REQUEST_ORIGIN_INTERNAL,
                                                                                                            Constants.REQUEST_ORIGIN_ENTITY_ACCOUNT,
                                                                                                            entityId,
                                                                                                            CreateAddressFeignRequestDto.mapToFeignRequest(entityId, entityType, createAddressRequestDto));
            logger.info("address service responded with status: " + addressResponse.getStatusCode(), serviceContext);
            return addressResponse.getBody().getData().getAddressIdentification();
        } catch (FeignException ex) {
            logger.error("error processing address feign request " + ex.getMessage(), serviceContext);
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }


}