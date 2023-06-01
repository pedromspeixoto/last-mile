package com.lastmile.customerservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.customerservice.dto.CreateCustomerRequestDto;
import com.lastmile.customerservice.dto.CreateCustomerResponseDto;
import com.lastmile.customerservice.dto.CreateNewApiKeyResponseDto;
import com.lastmile.customerservice.dto.CreateWarehouseRequestDto;
import com.lastmile.customerservice.dto.CreateWarehouseResponseDto;
import com.lastmile.customerservice.dto.CustomerDto;
import com.lastmile.customerservice.dto.CustomerUserResponseDto;
import com.lastmile.customerservice.dto.LinkAccountCustomerRequestDto;
import com.lastmile.customerservice.dto.PatchCustomerValidatedRequestDto;
import com.lastmile.customerservice.dto.UpdateCustomerRequestDto;
import com.lastmile.customerservice.dto.UpdateWarehouseRequestDto;
import com.lastmile.customerservice.dto.WarehouseDto;
import com.lastmile.customerservice.service.exception.GenericException;
import com.lastmile.customerservice.service.exception.LinkNotFoundException;
import com.lastmile.customerservice.service.exception.PaymentDetailNotFoundException;
import com.lastmile.customerservice.service.exception.WarehouseNotFoundException;
import com.lastmile.customerservice.service.exception.AddressNotFoundException;
import com.lastmile.customerservice.service.exception.CustomerInvalidStatusException;
import com.lastmile.customerservice.service.exception.CustomerNotFoundException;
import com.lastmile.customerservice.service.exception.EntityNotValidatedException;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;

import com.lastmile.utils.context.ServiceContext;

public interface CustomerService {

    // creates a new customer
    public CreateCustomerResponseDto create(ServiceContext serviceContext, CreateCustomerRequestDto customer) throws FeignCommunicationException, GenericException;

    // returns all customers
    public List<CustomerDto> getCustomers(Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> userIdentification, ServiceContext serviceContext) throws LinkNotFoundException, GenericException;

    // return customer details by customer identification
    public CustomerDto getCustomer(String customerIdentification, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException;

    // delete customer by customer identification
    public void deleteCustomer(String customerIdentification, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException;

    // update customer by customer identification 
    public void updateCustomer(String customerIdentification, UpdateCustomerRequestDto customer, ServiceContext serviceContext) throws CustomerInvalidStatusException, EntityNotValidatedException, PaymentDetailNotFoundException, FeignCommunicationException, AddressNotFoundException, LinkNotFoundException, CustomerNotFoundException, GenericException;

    // patch customer status by customer identification 
    public void patchCustomerValidated(String customerIdentification, PatchCustomerValidatedRequestDto patchCustomerValidatedRequestDto, ServiceContext serviceContext) throws CustomerInvalidStatusException, LinkNotFoundException, CustomerNotFoundException, GenericException;

    // create new api key for customer 
    public CreateNewApiKeyResponseDto createNewCustomerApiKey(String customerIdentification, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException;

    // link an account to a customer entity
    public void addLinkUserToCustomer(String customerIdentification, LinkAccountCustomerRequestDto linkAccountDto, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException;

    // remove an account from a customer entity
    public void removeLinkUserToCustomer(String customerIdentification, LinkAccountCustomerRequestDto linkAccountDto, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException;

    // create warehouse linked to customer
    public CreateWarehouseResponseDto addWarehouseCustomer(String customerIdentification, CreateWarehouseRequestDto createWarehouseRequestDto, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException, FeignCommunicationException;

    // update warehouse information
    public void updateWarehouse(String customerIdentification, String warehouseIdentification, UpdateWarehouseRequestDto updateWarehouseRequestDto, ServiceContext serviceContext) throws FeignCommunicationException, AddressNotFoundException, LinkNotFoundException, CustomerNotFoundException, WarehouseNotFoundException, GenericException;

    // delete warehouse
    public void removeWarehouse(String customerIdentification, String warehouseIdentification, ServiceContext serviceContext) throws LinkNotFoundException, WarehouseNotFoundException, CustomerNotFoundException, GenericException;

    // retrieve all warehouses
    public List<WarehouseDto> getWarehouses(Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> customerIdentification, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException; 

    // retrieve single warehouse
    public WarehouseDto getWarehouse(String customerIdentification, String warehouseIdentification, ServiceContext serviceContext) throws LinkNotFoundException, WarehouseNotFoundException, CustomerNotFoundException, GenericException; 

    // return customer linked users by customer identification
    public List<CustomerUserResponseDto> getCustomerUsers(String customerIdentification, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException;
    
}