package com.lastmile.addressservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.addressservice.dto.CreateAddressRequestDto;
import com.lastmile.addressservice.dto.CreateAddressResponseDto;
import com.lastmile.addressservice.dto.GetAddressResponseDto;
import com.lastmile.addressservice.dto.UpdateAddressRequestDto;
import com.lastmile.addressservice.service.exception.GenericException;
import com.lastmile.addressservice.service.exception.AddressForbiddenException;
import com.lastmile.addressservice.service.exception.AddressNotFoundException;
import com.lastmile.utils.context.ServiceContext;

public interface AddressService {

    // create a new address
    CreateAddressResponseDto createAddress(CreateAddressRequestDto addressDto, ServiceContext serviceContext) throws GenericException;

    // get all addresses
    List<GetAddressResponseDto> getAddresses(Optional<Integer> limit, Optional<Integer> offset, Optional<String> addressIdentification, Optional<String> entityIdentification, Optional<String> entityType, ServiceContext serviceContext) throws AddressForbiddenException, GenericException;

    // get address from address identification
    GetAddressResponseDto getAddress(String addressIdentification, ServiceContext serviceContext) throws AddressForbiddenException, AddressNotFoundException, GenericException;

    // delete address by address identification
    void deleteAddress(String addressIdentification, ServiceContext serviceContext) throws AddressForbiddenException, AddressNotFoundException, GenericException;

    // update address by address identification
    void updateAddress(String addressIdentification, UpdateAddressRequestDto updateAddressRequestDto, ServiceContext serviceContext) throws AddressForbiddenException, AddressNotFoundException, GenericException;

}