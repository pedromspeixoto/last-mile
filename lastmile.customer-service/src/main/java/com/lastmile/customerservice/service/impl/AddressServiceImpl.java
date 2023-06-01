package com.lastmile.customerservice.service.impl;

import java.util.List;
import java.util.Optional;

import com.lastmile.customerservice.client.addresses.AddressBridge;
import com.lastmile.customerservice.domain.Customer;
import com.lastmile.customerservice.domain.CustomerUserLink;
import com.lastmile.customerservice.dto.addresses.CreateAddressRequestDto;
import com.lastmile.customerservice.dto.addresses.CreateAddressResponseDto;
import com.lastmile.customerservice.dto.addresses.GetAddressResponseDto;
import com.lastmile.customerservice.dto.addresses.UpdateAddressRequestDto;
import com.lastmile.customerservice.service.exception.LinkNotFoundException;
import com.lastmile.customerservice.service.exception.AddressNotFoundException;
import com.lastmile.customerservice.service.exception.CustomerNotFoundException;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;
import com.lastmile.customerservice.repository.CustomerRepository;
import com.lastmile.customerservice.repository.CustomerUserLinkRepository;
import com.lastmile.customerservice.service.AddressService;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.validations.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressServiceImpl implements AddressService {

    Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final CustomerUserLinkRepository customerUserLinkRepository;
    private final AddressBridge addressBridge;

    public AddressServiceImpl(final CustomerRepository customerRepository,
                              final CustomerUserLinkRepository customerUserLinkRepository,
                              final AddressBridge addressBridge) {
        this.customerRepository = customerRepository;
        this.customerUserLinkRepository = customerUserLinkRepository;
        this.addressBridge = addressBridge;
    }
    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, CustomerNotFoundException.class, FeignCommunicationException.class})
    public CreateAddressResponseDto createCustomerAddress(String customerIdentification, CreateAddressRequestDto addressDto, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException {

        // validate if customer exists
        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to create address
        String addressIdentification = "";
        try {
            addressIdentification = addressBridge.createAddress(customerIdentification, EntityType.MARKETPLACE, addressDto, serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }

        return new CreateAddressResponseDto(addressIdentification);
    }


    @Override
    @Transactional(rollbackFor = {AddressNotFoundException.class, LinkNotFoundException.class, CustomerNotFoundException.class, FeignCommunicationException.class})
    public void updateCustomerAddress(String customerIdentification, String addressIdentification, UpdateAddressRequestDto addressDto, ServiceContext serviceContext) throws AddressNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException {

        // validate if customer exists
        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to update address
        try {
            addressBridge.updateAddress(customerIdentification, EntityType.MARKETPLACE.toString(), addressDto, serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = {AddressNotFoundException.class, LinkNotFoundException.class, CustomerNotFoundException.class, FeignCommunicationException.class})
    public void deleteCustomerAddress(String customerIdentification, String addressIdentification, ServiceContext serviceContext) throws AddressNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException {
        
        // validate if customer exists
        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to delete address
        try {
            addressBridge.deleteAddress(customerIdentification, EntityType.MARKETPLACE.toString(), serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }
    }

    @Override
    public GetAddressResponseDto getCustomerAddress(String customerIdentification, String addressIdentification, ServiceContext serviceContext) throws AddressNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException {
        // validate if customer exists
        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to get address
        Optional<GetAddressResponseDto> getAddressResponseDto;
        try {
            getAddressResponseDto = addressBridge.getAddress(customerIdentification, EntityType.MARKETPLACE.toString(), serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }

        // validate that address exists
        if (!getAddressResponseDto.isPresent()) {
            throw new AddressNotFoundException(addressIdentification);
        }

        return getAddressResponseDto.get();
    }

    @Override
    public List<GetAddressResponseDto> getAllCustomerAddresses(String customerIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> addressIdentification, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException {
        // validate if customer exists
        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to get addresses
        List<GetAddressResponseDto> addresses;
        try {
            addresses = addressBridge.getAddresses(customerIdentification, limit, offset, addressIdentification, serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }

        return addresses;
    }

    // validate if user that performed the request is linked to customer and is not admin
    private boolean isAccountLinkedToCustomer(ServiceContext serviceContext, String customerIdentification) {
        Optional<CustomerUserLink> customerUserLink = customerUserLinkRepository.findByUserIdentificationAndCustomerIdentification(serviceContext.getUserId(), customerIdentification);
        if (!customerUserLink.isPresent() && !Validator.isAdmin(serviceContext)) {
            return false;
        } else {
            return true;
        }
    }

}