package com.lastmile.customerservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.customerservice.dto.addresses.CreateAddressRequestDto;
import com.lastmile.customerservice.dto.addresses.CreateAddressResponseDto;
import com.lastmile.customerservice.dto.addresses.GetAddressResponseDto;
import com.lastmile.customerservice.dto.addresses.UpdateAddressRequestDto;
import com.lastmile.customerservice.service.exception.GenericException;
import com.lastmile.customerservice.service.exception.LinkNotFoundException;
import com.lastmile.customerservice.service.exception.AddressNotFoundException;
import com.lastmile.customerservice.service.exception.CustomerNotFoundException;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;

import com.lastmile.utils.context.ServiceContext;

public interface AddressService {

    // create customer address
    public CreateAddressResponseDto createCustomerAddress(String customerIdentification, CreateAddressRequestDto addressDto, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException;

    // update customer address
    public void updateCustomerAddress(String customerIdentification, String addressIdentification, UpdateAddressRequestDto addressDto, ServiceContext serviceContext) throws AddressNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException;

    // delete customer address
    public void deleteCustomerAddress(String customerIdentification, String addressIdentification, ServiceContext serviceContext) throws AddressNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException;

    // get individual customer address
    public GetAddressResponseDto getCustomerAddress(String customerIdentification, String addressIdentification, ServiceContext serviceContext) throws AddressNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException;

    // get all customer addresses
    public List<GetAddressResponseDto> getAllCustomerAddresses(String customerIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> addressIdentification, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException;

}