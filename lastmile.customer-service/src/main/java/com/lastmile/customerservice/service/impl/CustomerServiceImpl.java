package com.lastmile.customerservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lastmile.customerservice.enums.CustomerStatus;
import com.lastmile.customerservice.enums.WarehouseStatus;
import com.lastmile.customerservice.client.addresses.AddressBridge;
import com.lastmile.customerservice.client.payments.PaymentBridge;
import com.lastmile.customerservice.domain.Customer;
import com.lastmile.customerservice.domain.CustomerUserLink;
import com.lastmile.customerservice.domain.Warehouse;
import com.lastmile.customerservice.dto.UpdateCustomerRequestDto;
import com.lastmile.customerservice.dto.UpdateWarehouseRequestDto;
import com.lastmile.customerservice.dto.WarehouseDto;
import com.lastmile.customerservice.dto.addresses.GetAddressResponseDto;
import com.lastmile.customerservice.dto.payments.GetPaymentDetailResponseDto;
import com.lastmile.customerservice.dto.CreateCustomerRequestDto;
import com.lastmile.customerservice.dto.CreateCustomerResponseDto;
import com.lastmile.customerservice.dto.CreateNewApiKeyResponseDto;
import com.lastmile.customerservice.dto.CreateWarehouseRequestDto;
import com.lastmile.customerservice.dto.CreateWarehouseResponseDto;
import com.lastmile.customerservice.dto.CustomerDto;
import com.lastmile.customerservice.dto.CustomerUserResponseDto;
import com.lastmile.customerservice.dto.LinkAccountCustomerRequestDto;
import com.lastmile.customerservice.dto.PatchCustomerValidatedRequestDto;
import com.lastmile.customerservice.service.exception.GenericException;
import com.lastmile.customerservice.service.exception.LinkNotFoundException;
import com.lastmile.customerservice.service.exception.PaymentDetailNotFoundException;
import com.lastmile.customerservice.service.exception.WarehouseNotFoundException;
import com.lastmile.customerservice.service.exception.AddressNotFoundException;
import com.lastmile.customerservice.service.exception.CustomerInvalidStatusException;
import com.lastmile.customerservice.service.exception.CustomerNotFoundException;
import com.lastmile.customerservice.service.exception.EntityNotValidatedException;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;
import com.lastmile.customerservice.repository.CustomerRepository;
import com.lastmile.customerservice.repository.CustomerUserLinkRepository;
import com.lastmile.customerservice.repository.WarehouseRepository;
import com.lastmile.customerservice.service.CustomerService;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.validations.Validator;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

    Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final CustomerUserLinkRepository customerUserLinkRepository;
    private final WarehouseRepository warehouseRepository;
    private final AddressBridge addressBridge;
    private final PaymentBridge paymentBridge;

    private static final Integer DEFAULT_VALUE_LIMIT = 10;
    private static final Integer DEFAULT_VALUE_OFFSET = 0;

    public CustomerServiceImpl(final CustomerRepository customerRepository, final CustomerUserLinkRepository customerUserLinkRepository,
                               final WarehouseRepository warehouseRepository, final AddressBridge addressBridge,
                               final PaymentBridge paymentBridge) {
        this.customerRepository = customerRepository;
        this.customerUserLinkRepository = customerUserLinkRepository;
        this.warehouseRepository = warehouseRepository;
        this.addressBridge = addressBridge;
        this.paymentBridge = paymentBridge;
    }

    @Override
    @Transactional(rollbackFor = GenericException.class)
    public CreateCustomerResponseDto create(ServiceContext serviceContext, final CreateCustomerRequestDto customerDto) throws FeignCommunicationException, GenericException {

        ModelMapper modelMapper = new ModelMapper();

        try {

            // map from DTO
            Customer newCustomer = modelMapper.map(customerDto, Customer.class);

            // set default values for customer
            newCustomer.setCustomerIdentification(UUID.randomUUID().toString());
            newCustomer.setStatus(CustomerStatus.PENDING.toString());
            newCustomer.setEntityValidated(false);

            // generate random api key for customer using constructor
            newCustomer.setApiKey();

            // generate private key for customer using constructor
            newCustomer.setPrivateKey();

            // try to create active address id
            if (null != customerDto.getActiveAddress() && !customerDto.getActiveAddress().getAddressLine1().isEmpty()){
                String activeAddressId;
                try {
                    activeAddressId = addressBridge.createAddress(newCustomer.getCustomerIdentification(),
                                                                  EntityType.MARKETPLACE,
                                                                  customerDto.getActiveAddress(),
                                                                  serviceContext);
                } catch (Exception ex) {
                    throw new FeignCommunicationException(ex.getMessage());
                }
                newCustomer.setActiveAddressId(activeAddressId);
            }

            // try to create active billing address id
            if (null != customerDto.getActiveBillingAddress() && !customerDto.getActiveBillingAddress().getAddressLine1().isEmpty()){
                String activeBillingAddressId;
                try {
                    activeBillingAddressId = addressBridge.createAddress(newCustomer.getCustomerIdentification(),
                                                                         EntityType.MARKETPLACE,
                                                                         customerDto.getActiveBillingAddress(),
                                                                         serviceContext);
                } catch (Exception ex) {
                    throw new FeignCommunicationException(ex.getMessage());
                }
                newCustomer.setActiveBillingAddressId(activeBillingAddressId);
            }

            // save in db
            customerRepository.save(newCustomer);

            // if user identification is not null link the user id with the created customer
            if (null != customerDto.getUserIdentification() && !customerDto.getUserIdentification().isEmpty()) {

                // add first account link to customer
                CustomerUserLink customerUserLink = new CustomerUserLink(newCustomer.getCustomerIdentification(), customerDto.getUserIdentification());
                customerUserLinkRepository.save(customerUserLink);

            }

            return new CreateCustomerResponseDto(newCustomer.getCustomerIdentification(),
                                                 newCustomer.getApiKey(),
                                                 newCustomer.getPrivateKey());

        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    public List<CustomerDto> getCustomers(Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> userIdentification, ServiceContext serviceContext) throws LinkNotFoundException, GenericException {

        List<Customer> customers;
        ModelMapper modelMapper = new ModelMapper();
        Pageable pageable = PageRequest.of(offset.orElse(DEFAULT_VALUE_OFFSET), limit.orElse(DEFAULT_VALUE_LIMIT));

        // user without admin permission cannot view all customers
        if (!Validator.isAdmin(serviceContext) && !userIdentification.isPresent()) {
            throw new LinkNotFoundException("", serviceContext.getUserId());
        }

        // validate that user is trying to view his own information
        if (!Validator.isAdmin(serviceContext) && userIdentification.isPresent() && !userIdentification.get().equals(serviceContext.getUserId())) {
            throw new LinkNotFoundException("", serviceContext.getUserId());
        }

        try {

            customers = customerRepository.findAllCustomers(status.orElse(""), userIdentification.orElse(""), pageable);

        } catch (final Exception e) {

            throw new GenericException(e.getMessage(), e.getCause());

        }

        return customers.stream().map(customer -> modelMapper.map(customer, CustomerDto.class)).collect(Collectors.toList());

    }

    @Override
    public CustomerDto getCustomer(String customerIdentification, ServiceContext serviceContext) throws GenericException, CustomerNotFoundException, LinkNotFoundException {

        Customer customer;
        ModelMapper modelMapper = new ModelMapper();

        // validate if customer exists
        if (!customerRepository.findByCustomerIdentification(customerIdentification).isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        try {

            customer = customerRepository.findByCustomerIdentification(customerIdentification).get();

        } catch (final Exception e) {

            throw new GenericException(e.getMessage(), e.getCause());

        }

        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, CustomerNotFoundException.class, GenericException.class})
    public void deleteCustomer(String customerIdentification, ServiceContext serviceContext) throws CustomerNotFoundException, GenericException, LinkNotFoundException {

        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);

        // validate if customer exists
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        try {

            customerRepository.deleteById(customer.get().getId());

        } catch(final Exception e) {

            throw new GenericException(e.getMessage(), e.getCause());

        }

    }

    @Override
    @Transactional(rollbackFor = {CustomerInvalidStatusException.class, PaymentDetailNotFoundException.class, LinkNotFoundException.class, CustomerNotFoundException.class, GenericException.class, AddressNotFoundException.class, EntityNotValidatedException.class})
    public void updateCustomer(String customerIdentification, UpdateCustomerRequestDto customerDto, ServiceContext serviceContext) throws CustomerInvalidStatusException, EntityNotValidatedException, PaymentDetailNotFoundException, FeignCommunicationException, AddressNotFoundException, LinkNotFoundException, CustomerNotFoundException, GenericException {

        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);

        // validate if customer exists
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        Customer updatedCustomer = customer.get();

        // name
        if (customerDto.getName() != null && !customerDto.getName().isEmpty()) {
            updatedCustomer.setName(customerDto.getName());
        }

        // public name
        if (customerDto.getPublicName() != null && !customerDto.getPublicName().isEmpty()) {
            updatedCustomer.setPublicName(customerDto.getPublicName());
        }

        // nif
        if (customerDto.getNif() != null && !customerDto.getNif().isEmpty()) {
            updatedCustomer.setNif(customerDto.getNif());
        }

        // customer email
        if (customerDto.getCustomerEmail() != null && !customerDto.getCustomerEmail().isEmpty()) {
            updatedCustomer.setCustomerEmail(customerDto.getCustomerEmail());
        }

        // customer phone number
        if (customerDto.getCustomerPhoneNumber() != null && !customerDto.getCustomerPhoneNumber().isEmpty()) {
            updatedCustomer.setCustomerPhoneNumber(customerDto.getCustomerPhoneNumber());
        }

        // customer website
        if (customerDto.getCustomerWebsite() != null && !customerDto.getCustomerWebsite().isEmpty()) {
            updatedCustomer.setCustomerWebsite(customerDto.getCustomerWebsite());
        }

        // active address id
        if (customerDto.getActiveAddressId() != null && !customerDto.getActiveAddressId().isEmpty()) {
            Optional<GetAddressResponseDto> address;
            // try to fetch address
            try {
                address = addressBridge.getAddress(customerIdentification, customerDto.getActiveAddressId(), serviceContext);
            } catch (Exception ex) {
                throw new FeignCommunicationException(ex.getMessage());
            }

            // validate that address exists
            if (!address.isPresent()) {
                throw new AddressNotFoundException(customerDto.getActiveAddressId());
            }

            updatedCustomer.setActiveAddressId(customerDto.getActiveAddressId());
        }

        // active billing address id
        if (customerDto.getActiveBillingAddressId() != null && !customerDto.getActiveBillingAddressId().isEmpty()) {
            Optional<GetAddressResponseDto> address;
            // try to fetch address
            try {
                address = addressBridge.getAddress(customerIdentification, customerDto.getActiveBillingAddressId(), serviceContext);
            } catch (Exception ex) {
                throw new FeignCommunicationException(ex.getMessage());
            }

            // validate that address exists
            if (!address.isPresent()) {
                throw new AddressNotFoundException(customerDto.getActiveAddressId());
            }

            updatedCustomer.setActiveBillingAddressId(customerDto.getActiveBillingAddressId());
        }

        // active payment details id
        if (customerDto.getActivePaymentDetailsId() != null && !customerDto.getActivePaymentDetailsId().isEmpty()) {
            Optional<GetPaymentDetailResponseDto> paymentDetail;
            // try to fetch payment details
            try {
                paymentDetail = paymentBridge.getPaymentDetail(customerIdentification, customerDto.getActivePaymentDetailsId(), serviceContext);
            } catch (Exception ex) {
                throw new FeignCommunicationException(ex.getMessage());
            }

            // validate that payment details exists
            if (!paymentDetail.isPresent()) {
                throw new PaymentDetailNotFoundException(customerDto.getActivePaymentDetailsId());
            }

            updatedCustomer.setActivePaymentDetailsId(customerDto.getActivePaymentDetailsId());
        }

        // status
        if (null != customerDto.getStatus() && !customerDto.getStatus().toString().isEmpty()) {
            if (!updatedCustomer.getEntityValidated()) {
                throw new EntityNotValidatedException();
            }
            if ( customerDto.getStatus().equals(CustomerStatus.ACTIVE) 
                && (null == updatedCustomer.getActivePaymentDetailsId() || updatedCustomer.getActivePaymentDetailsId().isEmpty())) {
                throw new CustomerInvalidStatusException(updatedCustomer.getStatus(), customerDto.getStatus().toString());
            }
            updatedCustomer.setStatus(customerDto.getStatus().toString());
        }

        try {
            customerRepository.save(updatedCustomer);
        } catch(final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, CustomerNotFoundException.class, GenericException.class})
    public void patchCustomerValidated(String customerIdentification, PatchCustomerValidatedRequestDto patchCustomerValidatedRequestDto, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException {

        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);

        // validate if customer exists
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate that is admin
        if (!Validator.isAdmin(serviceContext)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        Customer updatedCustomer = customer.get();

        try {
            if (!patchCustomerValidatedRequestDto.getEntityValidated()) {
                updatedCustomer.setStatus(CustomerStatus.PENDING.toString());
            }
            updatedCustomer.setEntityValidated(patchCustomerValidatedRequestDto.getEntityValidated());
            customerRepository.save(updatedCustomer);
        } catch (Exception ex) {
            throw new GenericException(ex.getMessage(), ex.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, CustomerNotFoundException.class, GenericException.class})
    public CreateNewApiKeyResponseDto createNewCustomerApiKey(String customerIdentification, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException {

        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);

        // validate if customer exists
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // create new keys for customer
        Customer updatedCustomer = customer.get();
        try {
            updatedCustomer.setApiKey();
            updatedCustomer.setPrivateKey();
            customerRepository.save(updatedCustomer);
        } catch (Exception ex) {
            throw new GenericException(ex.getMessage(), ex.getCause());
        }

        return new CreateNewApiKeyResponseDto(updatedCustomer.getApiKey(),
                                              updatedCustomer.getPrivateKey());

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, CustomerNotFoundException.class, GenericException.class})
    public void addLinkUserToCustomer(String customerIdentification, LinkAccountCustomerRequestDto linkAccountDto, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException {

        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);

        // validate if customer exists
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        try {

            CustomerUserLink customerUserLink = new CustomerUserLink(customerIdentification, linkAccountDto.getUserIdentification());

            // save in db
            customerUserLinkRepository.save(customerUserLink);

        } catch(final Exception e) {

            throw new GenericException(e.getMessage(), e.getCause());

        }

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, CustomerNotFoundException.class, GenericException.class})
    public void removeLinkUserToCustomer(String customerIdentification, LinkAccountCustomerRequestDto linkAccountDto, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException {

        // validate if customer exists
        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        try {

            // delete link from db
            customerUserLinkRepository.delete(customerUserLinkRepository.findByUserIdentificationAndCustomerIdentification(serviceContext.getUserId(), customerIdentification).get());

        } catch(final Exception e) {

            throw new GenericException(e.getMessage(), e.getCause());

        }

    }

    @Override
    @Transactional(rollbackFor = {FeignCommunicationException.class, LinkNotFoundException.class, CustomerNotFoundException.class, GenericException.class})
    public CreateWarehouseResponseDto addWarehouseCustomer(String customerIdentification, CreateWarehouseRequestDto createWarehouseRequestDto, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException, FeignCommunicationException {

        // validate if user that performed the request is linked to customer
        Optional<CustomerUserLink> customerUserLink = customerUserLinkRepository.findByUserIdentificationAndCustomerIdentification(serviceContext.getUserId(), customerIdentification);
        if (!customerUserLink.isPresent() && !Validator.isAdmin(serviceContext)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        Warehouse warehouse = new Warehouse();

        try {
            ModelMapper modelMapper = new ModelMapper();
            warehouse = modelMapper.map(createWarehouseRequestDto, Warehouse.class);

            // set uuid, customer identification and status
            warehouse.setCustomerIdentification(customerIdentification);
            warehouse.setStatus(WarehouseStatus.ACTIVE.toString());
            warehouse.setWarehouseIdentification(UUID.randomUUID().toString());
        } catch(final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        // try to create address
        if (createWarehouseRequestDto.getAddress() != null && !createWarehouseRequestDto.getAddress().getAddressLine1().isEmpty()) {
            String addressId;
            try {
                addressId = addressBridge.createAddress(customerIdentification,
                                                        EntityType.MARKETPLACE,
                                                        createWarehouseRequestDto.getAddress(),
                                                        serviceContext);
            } catch (Exception ex) {
                throw new FeignCommunicationException(ex.getMessage());
            }
            warehouse.setAddressId(addressId);
        }

        warehouseRepository.save(warehouse);

        return new CreateWarehouseResponseDto(warehouse.getWarehouseIdentification());

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, WarehouseNotFoundException.class, GenericException.class})
    public void updateWarehouse(String customerIdentification, String warehouseIdentification, UpdateWarehouseRequestDto updateWarehouseRequestDto, ServiceContext serviceContext) throws LinkNotFoundException, WarehouseNotFoundException, GenericException {

        // validate if warehouse exists
        if (!warehouseRepository.findByWarehouseIdentification(warehouseIdentification).isPresent()) {
            throw new WarehouseNotFoundException(warehouseIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        try {
            ModelMapper modelMapper = new ModelMapper();
            Warehouse updatedWarehouse = modelMapper.map(updateWarehouseRequestDto, Warehouse.class);
            // name
            if (null != updateWarehouseRequestDto.getName() && !updateWarehouseRequestDto.getName().isEmpty()) {
                updatedWarehouse.setName(updateWarehouseRequestDto.getName());
            }
            // description
            if (null != updateWarehouseRequestDto.getDescription() && !updateWarehouseRequestDto.getDescription().isEmpty()) {
                updatedWarehouse.setDescription(updateWarehouseRequestDto.getDescription());
            }
            // address id
            if (null != updateWarehouseRequestDto.getAddressId() && !updateWarehouseRequestDto.getAddressId().isEmpty()) {
                Optional<GetAddressResponseDto> address;
                // try to fetch address
                try {
                    address = addressBridge.getAddress(customerIdentification, updateWarehouseRequestDto.getAddressId(), serviceContext);
                } catch (Exception ex) {
                    throw new FeignCommunicationException(ex.getMessage());
                }
                // validate that address exists
                if (!address.isPresent()) {
                    throw new AddressNotFoundException(updateWarehouseRequestDto.getAddressId());
                }
                updatedWarehouse.setAddressId(updateWarehouseRequestDto.getAddressId());
            }
            // latitude
            if (null != updateWarehouseRequestDto.getLatitude() && !updateWarehouseRequestDto.getLatitude().toString().isEmpty()) {
                updatedWarehouse.setLatitude(updateWarehouseRequestDto.getLatitude());
            }
            // longitude
            if (null != updateWarehouseRequestDto.getLongitude() && !updateWarehouseRequestDto.getLongitude().toString().isEmpty()) {
                updatedWarehouse.setLongitude(updateWarehouseRequestDto.getLongitude());
            }
            // status
            if (null != updateWarehouseRequestDto.getStatus() && !updateWarehouseRequestDto.getStatus().toString().isEmpty()) {
                updatedWarehouse.setStatus(updateWarehouseRequestDto.getStatus().toString());
            }
            warehouseRepository.save(updatedWarehouse);
        } catch(final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, WarehouseNotFoundException.class, GenericException.class})
    public void removeWarehouse(String customerIdentification, String warehouseIdentification, ServiceContext serviceContext) throws LinkNotFoundException, WarehouseNotFoundException, GenericException {

        // validate if warehouse exists
        if (!warehouseRepository.findByWarehouseIdentification(warehouseIdentification).isPresent()) {
            throw new WarehouseNotFoundException(warehouseIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        try {
            warehouseRepository.delete(warehouseRepository.findByWarehouseIdentification(warehouseIdentification).get());
        } catch(final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, CustomerNotFoundException.class, GenericException.class})
    public List<WarehouseDto> getWarehouses(Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> customerIdentification, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException {

        // if the customer identification is not null, perform validations
        if (customerIdentification.isPresent()) {

            // validate link
            if (!isAccountLinkedToCustomer(serviceContext, customerIdentification.get())) {
                throw new LinkNotFoundException(customerIdentification.get(), serviceContext.getUserId());
            }

            // validate if customer exists
            if (!customerRepository.findByCustomerIdentification(customerIdentification.get()).isPresent()) {
                throw new CustomerNotFoundException(customerIdentification.get());
            }

        }

        List<Warehouse> warehouses;
        Pageable pageable = PageRequest.of(offset.orElse(DEFAULT_VALUE_OFFSET), limit.orElse(DEFAULT_VALUE_LIMIT));
        ModelMapper modelMapper = new ModelMapper();

        try {
            warehouses = warehouseRepository.findAllWarehouses(status.orElse(""), customerIdentification.orElse(""), pageable);
        } catch(final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return warehouses.stream().map(warehouse -> modelMapper.map(warehouse, WarehouseDto.class)).collect(Collectors.toList());

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, WarehouseNotFoundException.class, CustomerNotFoundException.class, GenericException.class})
    public WarehouseDto getWarehouse(String customerIdentification, String warehouseIdentification, ServiceContext serviceContext) throws LinkNotFoundException, WarehouseNotFoundException, CustomerNotFoundException, GenericException {

        // validate if warehouse exists
        Optional<Warehouse> warehouse = warehouseRepository.findByWarehouseIdentification(warehouseIdentification);
        if (!warehouse.isPresent()) {
            throw new WarehouseNotFoundException(warehouseIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        ModelMapper modelMapper = new ModelMapper();
        WarehouseDto warehouseDto;

        try {
            warehouseDto = modelMapper.map(warehouse, WarehouseDto.class);
        } catch(final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return warehouseDto;

    }

    @Override
    public List<CustomerUserResponseDto> getCustomerUsers(String customerIdentification, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, GenericException {

        // validate if customer exists
        if (!customerRepository.findByCustomerIdentification(customerIdentification).isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        List<CustomerUserLink> customerUserLinkList;
        ModelMapper modelMapper = new ModelMapper();

        try {
            customerUserLinkList = customerUserLinkRepository.findByCustomerIdentification(customerIdentification);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return customerUserLinkList.stream().map(customerUserLink -> modelMapper.map(customerUserLink, CustomerUserResponseDto.class)).collect(Collectors.toList());
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