package com.lastmile.customerservice.client.addresses;

import java.util.List;
import java.util.Optional;

import com.lastmile.customerservice.client.addresses.feign.AddressServiceFeignClient;
import com.lastmile.customerservice.controller.response.AddressListResponse;
import com.lastmile.customerservice.controller.response.AddressResponse;
import com.lastmile.customerservice.controller.response.CreateAddressResponse;
import com.lastmile.customerservice.dto.addresses.CreateAddressRequestDto;
import com.lastmile.customerservice.dto.addresses.GetAddressResponseDto;
import com.lastmile.customerservice.dto.addresses.UpdateAddressRequestDto;
import com.lastmile.customerservice.dto.addresses.feign.CreateAddressFeignRequestDto;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class AddressBridge {

    private static final String REQUEST_ORIGIN_INTERNAL = "internal";
    private static final String REQUEST_ORIGIN_ENTITY_MARKETPLACE = "marketplace";

    @Autowired
    AddressServiceFeignClient addressServiceFeignClient;

    public String createAddress(String entityId, EntityType entityType, CreateAddressRequestDto createAddressRequestDto, ServiceContext serviceContext) throws FeignCommunicationException {

        try {

            ResponseEntity<CreateAddressResponse> addressResponse = addressServiceFeignClient.createAddress(serviceContext.getCorrelationId(),
                                                                                                            serviceContext.getUserId(),
                                                                                                            serviceContext.getPermissions(),
                                                                                                            serviceContext.getRequestId(),
                                                                                                            REQUEST_ORIGIN_INTERNAL,
                                                                                                            REQUEST_ORIGIN_ENTITY_MARKETPLACE,
                                                                                                            entityId,
                                                                                                            CreateAddressFeignRequestDto.mapToFeignRequest(entityId, entityType, createAddressRequestDto));

            if (addressResponse.getStatusCode() != HttpStatus.OK && addressResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + addressResponse.getStatusCode());
            }

            return addressResponse.getBody().getData().getAddressIdentification();
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

    public void updateAddress(String entityId, String addressIdentification, UpdateAddressRequestDto updateAddressRequestDto, ServiceContext serviceContext) throws FeignCommunicationException {

        try {

            ResponseEntity<CreateAddressResponse> addressResponse = addressServiceFeignClient.updateAddress(serviceContext.getCorrelationId(),
                                                                                                            serviceContext.getUserId(),
                                                                                                            serviceContext.getPermissions(),
                                                                                                            serviceContext.getRequestId(),
                                                                                                            REQUEST_ORIGIN_INTERNAL,
                                                                                                            REQUEST_ORIGIN_ENTITY_MARKETPLACE,
                                                                                                            entityId,
                                                                                                            addressIdentification,
                                                                                                            updateAddressRequestDto);

            if (addressResponse.getStatusCode() != HttpStatus.OK && addressResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + addressResponse.getStatusCode());
            }
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

    public void deleteAddress(String entityId, String addressIdentification, ServiceContext serviceContext) throws FeignCommunicationException {

        try {

            ResponseEntity<SuccessResponse> addressResponse = addressServiceFeignClient.deleteAddress(serviceContext.getCorrelationId(),
                                                                                                      serviceContext.getUserId(),
                                                                                                      serviceContext.getPermissions(),
                                                                                                      serviceContext.getRequestId(),
                                                                                                      REQUEST_ORIGIN_INTERNAL,
                                                                                                      REQUEST_ORIGIN_ENTITY_MARKETPLACE,
                                                                                                      entityId,
                                                                                                      addressIdentification);

            if (addressResponse.getStatusCode() != HttpStatus.OK && addressResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + addressResponse.getStatusCode());
            }
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }


    public Optional<GetAddressResponseDto> getAddress(String entityId, String addressIdentification, ServiceContext serviceContext) throws FeignCommunicationException {

        try {

            ResponseEntity<AddressResponse> addressResponse = addressServiceFeignClient.getAddress(serviceContext.getCorrelationId(),
                                                                                                   serviceContext.getUserId(),
                                                                                                   serviceContext.getPermissions(),
                                                                                                   serviceContext.getRequestId(),
                                                                                                   REQUEST_ORIGIN_INTERNAL,
                                                                                                   REQUEST_ORIGIN_ENTITY_MARKETPLACE,
                                                                                                   entityId,
                                                                                                   addressIdentification);

            if (addressResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }

            if (addressResponse.getStatusCode() != HttpStatus.OK && addressResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + addressResponse.getStatusCode());
            }

            return Optional.of(addressResponse.getBody().getData());
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

    public List<GetAddressResponseDto> getAddresses(String entityId, Optional<Integer> limit, Optional<Integer> offset, Optional<String> addressIdentification, ServiceContext serviceContext) throws FeignCommunicationException {

        try {

            ResponseEntity<AddressListResponse> addressResponse = addressServiceFeignClient.getAddresses(serviceContext.getCorrelationId(),
                                                                                                         serviceContext.getUserId(),
                                                                                                         serviceContext.getPermissions(),
                                                                                                         serviceContext.getRequestId(),
                                                                                                         REQUEST_ORIGIN_INTERNAL,
                                                                                                         REQUEST_ORIGIN_ENTITY_MARKETPLACE,
                                                                                                         entityId,
                                                                                                         limit.orElse(10),
                                                                                                         offset.orElse(0),
                                                                                                         addressIdentification.orElse(""),
                                                                                                         entityId,
                                                                                                         EntityType.MARKETPLACE.toString());      

            if (addressResponse.getStatusCode() != HttpStatus.OK && addressResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + addressResponse.getStatusCode());
            }

            return addressResponse.getBody().getData();
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

}