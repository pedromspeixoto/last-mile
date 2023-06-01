package com.lastmile.driverservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lastmile.driverservice.client.addresses.AddressBridge;
import com.lastmile.driverservice.client.payments.PaymentBridge;
import com.lastmile.driverservice.domain.Driver;
import com.lastmile.driverservice.domain.FiscalEntity;
import com.lastmile.driverservice.domain.FiscalEntityUserLink;
import com.lastmile.driverservice.dto.fiscalentities.AddUserToFiscalEntityRequestDto;
import com.lastmile.driverservice.dto.addresses.CreateAddressRequestDto;
import com.lastmile.driverservice.dto.addresses.CreateAddressResponseDto;
import com.lastmile.driverservice.dto.addresses.GetAddressResponseDto;
import com.lastmile.driverservice.dto.addresses.UpdateAddressRequestDto;
import com.lastmile.driverservice.dto.drivers.DriverDto;
import com.lastmile.driverservice.dto.fiscalentities.CreateFiscalEntityRequestDto;
import com.lastmile.driverservice.dto.fiscalentities.CreateFiscalEntityResponseDto;
import com.lastmile.driverservice.dto.fiscalentities.UpdateFiscalEntityRequestDto;
import com.lastmile.driverservice.dto.payments.GetOutPaymentResponseDto;
import com.lastmile.driverservice.dto.fiscalentities.FiscalEntityResponseDto;
import com.lastmile.driverservice.dto.fiscalentities.PatchFiscalEntityValidatedRequestDto;
import com.lastmile.driverservice.enums.FiscalEntityStatus;
import com.lastmile.driverservice.repository.DriverRepository;
import com.lastmile.driverservice.repository.FiscalEntityRepository;
import com.lastmile.driverservice.repository.FiscalEntityUserLinkRepository;
import com.lastmile.driverservice.service.FiscalEntityService;
import com.lastmile.driverservice.service.exception.AddressNotFoundException;
import com.lastmile.driverservice.service.exception.DriverNotFoundException;
import com.lastmile.driverservice.service.exception.FeignCommunicationException;
import com.lastmile.driverservice.service.exception.GenericException;
import com.lastmile.driverservice.service.exception.EntityNotValidatedException;
import com.lastmile.driverservice.service.exception.LinkNotFoundException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.validations.Validator;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lastmile.driverservice.service.exception.FiscalEntityAlreadyExistsException;
import com.lastmile.driverservice.service.exception.FiscalEntityHasDriversDeleteException;
import com.lastmile.driverservice.service.exception.FiscalEntityNotActiveException;
import com.lastmile.driverservice.service.exception.FiscalEntityNotFoundException;

@Service
public class FiscalEntityServiceImpl implements FiscalEntityService {

    private static final Integer DEFAULT_VALUE_LIMIT = 10;
    private static final Integer DEFAULT_VALUE_OFFSET = 0;

    Logger logger = LoggerFactory.getLogger(DriverServiceImpl.class);

    private final FiscalEntityRepository fiscalEntityRepository;
    private final DriverRepository driverRepository;
    private final FiscalEntityUserLinkRepository fiscalEntityUserLinkRepository;
    private final AddressBridge addressBridge;
    private final PaymentBridge paymentBridge;

    public FiscalEntityServiceImpl(final FiscalEntityRepository fiscalEntityRepository,
                                   final DriverRepository driverRepository,
                                   final FiscalEntityUserLinkRepository fiscalEntityUserLinkRepository,
                                   final AddressBridge addressBridge,
                                   final PaymentBridge paymentBridge) {
        this.driverRepository = driverRepository;
        this.fiscalEntityRepository = fiscalEntityRepository;
        this.fiscalEntityUserLinkRepository = fiscalEntityUserLinkRepository;
        this.addressBridge = addressBridge;
        this.paymentBridge = paymentBridge;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    @Transactional(rollbackFor = { GenericException.class, FiscalEntityAlreadyExistsException.class, FeignCommunicationException.class })
    public CreateFiscalEntityResponseDto createNewFiscalEntity(CreateFiscalEntityRequestDto fiscalEntityDto, ServiceContext serviceContext) throws FeignCommunicationException, GenericException, FiscalEntityAlreadyExistsException {

        ModelMapper modelMapper = new ModelMapper();

        // save details for this fiscal entity
        FiscalEntity fiscalEntity = modelMapper.map(fiscalEntityDto, FiscalEntity.class);
        fiscalEntity.setFiscalEntityIdentification(UUID.randomUUID().toString());
        fiscalEntity.setStatus(FiscalEntityStatus.PENDING.toString());
        fiscalEntity.setEntityValidated(false);

        // create active address id in bridge
        if (fiscalEntityDto.getActiveAddress() != null && !fiscalEntityDto.getActiveAddress().getAddressLine1().isEmpty()) {
            try {
                String activeAddressId;
                activeAddressId = addressBridge.createAddress(fiscalEntity.getFiscalEntityIdentification(),
                                                              EntityType.FISCALENTITY,
                                                              fiscalEntityDto.getActiveAddress(),
                                                              serviceContext);
                fiscalEntity.setActiveAddressId(activeAddressId);
            } catch(Exception ex) {
                throw new FeignCommunicationException(ex.getMessage());
            }
        }

        // create active billing address id in bridge
        if (fiscalEntityDto.getActiveBillingAddress() != null && !fiscalEntityDto.getActiveBillingAddress().getAddressLine1().isEmpty()) {
            try {
                String activeBillingAddressId;
                activeBillingAddressId = addressBridge.createAddress(fiscalEntity.getFiscalEntityIdentification(),
                                                              EntityType.FISCALENTITY,
                                                              fiscalEntityDto.getActiveBillingAddress(),
                                                              serviceContext);
                fiscalEntity.setActiveBillingAddressId(activeBillingAddressId);
            } catch(Exception ex) {
                throw new FeignCommunicationException(ex.getMessage());
            }
        }

        fiscalEntityRepository.save(fiscalEntity);

        try {
            if (null != fiscalEntityDto.getUserIdentification() && !fiscalEntityDto.getUserIdentification().isEmpty()) {
                // add first account link to fiscal entity
                FiscalEntityUserLink fiscalEntityUserLink = new FiscalEntityUserLink(fiscalEntity.getFiscalEntityIdentification(), fiscalEntityDto.getUserIdentification());
                fiscalEntityUserLinkRepository.save(fiscalEntityUserLink);
            }
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return new CreateFiscalEntityResponseDto(fiscalEntity.getFiscalEntityIdentification());

    }

    @Override
    public List<FiscalEntityResponseDto> getFiscalEntities(Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> userIdentification, ServiceContext serviceContext) throws LinkNotFoundException, GenericException {

        List<FiscalEntity> fiscalEntities;
        ModelMapper modelMapper = new ModelMapper();

        // user without admin permission cannot view all customers
        if ((!Validator.isAdmin(serviceContext) && !userIdentification.isPresent())
            && userIdentification.isPresent() && !userIdentification.get().equals(serviceContext.getUserId())) {
            throw new LinkNotFoundException("", serviceContext.getUserId());
        }

        Pageable pageable = PageRequest.of(offset.orElse(DEFAULT_VALUE_OFFSET), limit.orElse(DEFAULT_VALUE_LIMIT));

        try {
            fiscalEntities = fiscalEntityRepository.findAllFiscalEntities(status.orElse(""), userIdentification.orElse(""), pageable);
            System.out.println(String.valueOf(fiscalEntities.size()));
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return fiscalEntities.stream().map(fiscalEntity -> modelMapper.map(fiscalEntity, FiscalEntityResponseDto.class)).collect(Collectors.toList());

    }

    @Override
    public FiscalEntityResponseDto getFiscalEntity(String fiscalEntityIdentification, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, GenericException {

        FiscalEntityResponseDto fiscalEntityResponseDto;
        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);
        ModelMapper modelMapper = new ModelMapper();

        // validate that fiscal entity exists
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate that account is linked to this fiscal entity
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        try {
            fiscalEntityResponseDto = modelMapper.map(fiscalEntity.get(), FiscalEntityResponseDto.class);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return fiscalEntityResponseDto;

    }

    @Override
    public List<DriverDto> getFiscalEntityDrivers(String fiscalEntityIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> driverIdentification, ServiceContext serviceContext) throws FiscalEntityNotFoundException, LinkNotFoundException, GenericException {

        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);

        // validate that fiscal entity exists
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate that account is linked to this fiscal entity
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        ModelMapper modelMapper = new ModelMapper();
        List<DriverDto> driversDto;
        Pageable pageable = PageRequest.of(offset.orElse(DEFAULT_VALUE_OFFSET), limit.orElse(DEFAULT_VALUE_LIMIT));

        try {
            List<Driver> drivers = driverRepository.findByFiscalEntityPageable(fiscalEntityIdentification, driverIdentification.orElse(""), pageable);
            driversDto = drivers.stream()
                                .map(driver -> modelMapper.map(driver, DriverDto.class))
                                .collect(Collectors.toList());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return driversDto;

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, FiscalEntityHasDriversDeleteException.class, FiscalEntityNotFoundException.class, GenericException.class})
    public void deleteFiscalEntity(String fiscalEntityIdentification, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityHasDriversDeleteException, FiscalEntityNotFoundException, GenericException {

        // validate that fiscal entity exists
        if (!fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification).isPresent()){
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate that account is linked to this fiscal entity
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        // validate that fiscal entity does not have drivers
        if (!driverRepository.findByFiscalEntityIdentification(fiscalEntityIdentification).isEmpty()) {
            throw new FiscalEntityHasDriversDeleteException(fiscalEntityIdentification);
        }

        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);

        try {

            fiscalEntityRepository.delete(fiscalEntity.get());

        } catch (final Exception e) {

            throw new GenericException(e.getMessage(), e.getCause());

        }

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, FiscalEntityNotFoundException.class, GenericException.class, AddressNotFoundException.class, FeignCommunicationException.class})
    public void updateFiscalEntity(String fiscalEntityIdentification, UpdateFiscalEntityRequestDto fiscalEntityDto, ServiceContext serviceContext) throws EntityNotValidatedException, AddressNotFoundException, FeignCommunicationException, LinkNotFoundException, FiscalEntityNotFoundException, GenericException {

        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);
        FiscalEntity newFiscalEntity = new FiscalEntity();

        // validate that fiscal entity exists
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate that account is linked to this fiscal entity
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        newFiscalEntity = fiscalEntity.get();

        // name
        if (fiscalEntityDto.getName() != null && !fiscalEntityDto.getName().isEmpty()){
            newFiscalEntity.setName(fiscalEntityDto.getName());
        }

        // fiscal number
        if (fiscalEntityDto.getFiscalNumber() != null && !fiscalEntityDto.getFiscalNumber().isEmpty()){
            newFiscalEntity.setFiscalNumber(fiscalEntityDto.getFiscalNumber());
        }

        // bank account holder name
        if (fiscalEntityDto.getBankAccountHolderName() != null && !fiscalEntityDto.getBankAccountHolderName().isEmpty()){
            newFiscalEntity.setBankAccountHolderName(fiscalEntityDto.getBankAccountHolderName());
        }

        // bank account iban
        if (fiscalEntityDto.getBankAccountIban() != null && !fiscalEntityDto.getBankAccountIban().isEmpty()){
            newFiscalEntity.setBankAccountIban(fiscalEntityDto.getBankAccountIban());
        }

        // bank account country code
        if (fiscalEntityDto.getBankAccountCountryCode() != null && !fiscalEntityDto.getBankAccountCountryCode().isEmpty()){
            newFiscalEntity.setBankAccountCountryCode(fiscalEntityDto.getBankAccountCountryCode());
        }

        // bank account country code
        if (fiscalEntityDto.getPaymentFrequency() != null && !fiscalEntityDto.getPaymentFrequency().toString().isEmpty()){
            newFiscalEntity.setPaymentFrequency(fiscalEntityDto.getPaymentFrequency().toString());
        }

        // active address id
        if (fiscalEntityDto.getActiveAddressId() != null && !fiscalEntityDto.getActiveAddressId().isEmpty()){
            // validate that address exists
            if (!addressBridge.getAddress(fiscalEntity.get().getFiscalEntityIdentification(), fiscalEntityDto.getActiveAddressId(), serviceContext).isPresent()) {
                throw new AddressNotFoundException(fiscalEntityDto.getActiveAddressId());
            }
            newFiscalEntity.setActiveAddressId(fiscalEntityDto.getActiveAddressId());
        }

        // active billing address id
        if (fiscalEntityDto.getActiveBillingAddressId() != null && !fiscalEntityDto.getActiveBillingAddressId().isEmpty()){
            // validate that address exists
            if (!addressBridge.getAddress(fiscalEntity.get().getFiscalEntityIdentification(), fiscalEntityDto.getActiveBillingAddressId(), serviceContext).isPresent()) {
                throw new AddressNotFoundException(fiscalEntityDto.getActiveBillingAddressId());
            }
            newFiscalEntity.setActiveBillingAddressId(fiscalEntityDto.getActiveBillingAddressId());
        }

        // status
        if (fiscalEntityDto.getStatus() != null && !fiscalEntityDto.getStatus().toString().isEmpty()){
            if (!newFiscalEntity.isEntityValidated()) {
                throw new EntityNotValidatedException();
            }
            newFiscalEntity.setStatus(fiscalEntityDto.getStatus().toString());
        }

        try {
            fiscalEntityRepository.save(newFiscalEntity);
        } catch (final Exception e){
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, FiscalEntityNotFoundException.class, GenericException.class})
    public void patchFiscalEntityValidated(String fiscalEntityIdentification, PatchFiscalEntityValidatedRequestDto fiscalEntityValidatedRequestDto, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, GenericException {

        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);
        FiscalEntity newFiscalEntity = new FiscalEntity();

        // validate that fiscal entity exists
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }
        newFiscalEntity = fiscalEntity.get();

        // validate that the user is admin (only admins can validate entities)
        if (!Validator.isAdmin(serviceContext)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        try {
            if (!fiscalEntityValidatedRequestDto.getEntityValidated()) {
                newFiscalEntity.setStatus(FiscalEntityStatus.PENDING.toString());
            }
            newFiscalEntity.setStatus(FiscalEntityStatus.ACTIVE.toString());
            newFiscalEntity.setEntityValidated(fiscalEntityValidatedRequestDto.getEntityValidated());
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }
    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, DriverNotFoundException.class, FiscalEntityNotFoundException.class, GenericException.class, FiscalEntityNotActiveException.class})
    public void addDriverToFiscalEntity(String fiscalEntityIdentification, String driverIdentification,ServiceContext serviceContext) throws FiscalEntityNotActiveException, LinkNotFoundException, DriverNotFoundException, FiscalEntityNotFoundException, GenericException {

        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that fiscal entity exists
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate that account is linked to this fiscal entity
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        // validate that fiscal entity is validated
        if (!fiscalEntity.get().getEntityValidated()) {
            throw new FiscalEntityNotActiveException(fiscalEntityIdentification);
        }

        try {
            Driver newDriver = driver.get();
            newDriver.setFiscalEntityIdentification(fiscalEntityIdentification);
            driverRepository.save(newDriver);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, DriverNotFoundException.class, FiscalEntityNotFoundException.class, GenericException.class})
    public void removeDriverFromFiscalEntity(String fiscalEntityIdentification, String driverIdentification, ServiceContext serviceContext) throws LinkNotFoundException, DriverNotFoundException, FiscalEntityNotFoundException, GenericException {

        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that fiscal entity exists
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate that account is linked to this fiscal entity
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        try {

            Driver newDriver = driver.get();
            newDriver.setFiscalEntityIdentification(null);
            driverRepository.save(newDriver);

        } catch (final Exception e) {

            throw new GenericException(e.getMessage(), e.getCause());

        }

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, FiscalEntityNotFoundException.class, GenericException.class})
    public void addLinkUserToFiscalEntity(String fiscalEntityIdentification, AddUserToFiscalEntityRequestDto linkAccountDto, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, GenericException {

        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);

        // validate if fiscal entity exists
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate link
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        try {

            FiscalEntityUserLink fiscalEntityUserLink = new FiscalEntityUserLink(fiscalEntityIdentification, linkAccountDto.getUserIdentification());

            // save in db
            fiscalEntityUserLinkRepository.save(fiscalEntityUserLink);

        } catch(final Exception e) {

            throw new GenericException(e.getMessage(), e.getCause());

        }

    }


    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, FiscalEntityNotFoundException.class, GenericException.class})
    public void removeLinkUserToFiscalEntity(String fiscalEntityIdentification, AddUserToFiscalEntityRequestDto linkAccountDto, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, GenericException {

        // validate if fiscal entity exists
        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate link
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        try {

            // delete link from db
            fiscalEntityUserLinkRepository.delete(fiscalEntityUserLinkRepository.findByUserIdentificationAndFiscalEntityIdentification(serviceContext.getUserId(), fiscalEntityIdentification).get());

        } catch(final Exception e) {

            throw new GenericException(e.getMessage(), e.getCause());

        }

    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, FiscalEntityNotFoundException.class, FeignCommunicationException.class})
    public CreateAddressResponseDto createFiscalEntityAddress(String fiscalEntityIdentification, CreateAddressRequestDto addressDto, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException {

        // validate if fiscal entity exists
        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate link
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        // try to create address
        String addressIdentification = "";
        try {
            addressIdentification = addressBridge.createAddress(fiscalEntityIdentification, EntityType.FISCALENTITY, addressDto, serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }

        return new CreateAddressResponseDto(addressIdentification);
    }


    @Override
    @Transactional(rollbackFor = {AddressNotFoundException.class, LinkNotFoundException.class, FiscalEntityNotFoundException.class, FeignCommunicationException.class})
    public void updateFiscalEntityAddress(String fiscalEntityIdentification, String addressIdentification, UpdateAddressRequestDto addressDto, ServiceContext serviceContext) throws AddressNotFoundException, LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException {

        // validate if fiscal entity exists
        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate link
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        // try to update address
        try {
            addressBridge.updateAddress(fiscalEntityIdentification, EntityType.FISCALENTITY.toString(), addressDto, serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = {AddressNotFoundException.class, LinkNotFoundException.class, FiscalEntityNotFoundException.class, FeignCommunicationException.class})
    public void deleteFiscalEntityAddress(String fiscalEntityIdentification, String addressIdentification, ServiceContext serviceContext) throws AddressNotFoundException, LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException {
        
        // validate if fiscal entity exists
        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate link
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        // try to delete address
        try {
            addressBridge.deleteAddress(fiscalEntityIdentification, EntityType.FISCALENTITY.toString(), serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }
    }

    @Override
    public GetAddressResponseDto getFiscalEntityAddress(String fiscalEntityIdentification, String addressIdentification, ServiceContext serviceContext) throws AddressNotFoundException, LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException {
        // validate if fiscal entity exists
        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate link
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        // try to get address
        Optional<GetAddressResponseDto> getAddressResponseDto;
        try {
            getAddressResponseDto = addressBridge.getAddress(fiscalEntityIdentification, EntityType.FISCALENTITY.toString(), serviceContext);
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
    public List<GetAddressResponseDto> getAllFiscalEntityAddresses(String fiscalEntityIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> addressIdentification, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException {
        // validate if fiscal entity exists
        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate link
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        // try to get addresses
        List<GetAddressResponseDto> addresses;
        try {
            addresses = addressBridge.getAddresses(fiscalEntityIdentification, limit, offset, addressIdentification, serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }

        return addresses;
    }

    @Override
    public List<GetOutPaymentResponseDto> getAllOutboundPayments(String fiscalEntityIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> outPaymentIdentification, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException {
        // validate if fiscal entity exists
        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(fiscalEntityIdentification);
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(fiscalEntityIdentification);
        }

        // validate link
        if (!isAccountLinkedToFiscalEntityOrAdmin(serviceContext, fiscalEntityIdentification)) {
            throw new LinkNotFoundException(fiscalEntityIdentification, serviceContext.getUserId());
        }

        // try to get payments
        List<GetOutPaymentResponseDto> payments;
        try {
            payments = paymentBridge.getOutboundPayments(fiscalEntityIdentification, limit, offset, outPaymentIdentification, serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException("payment-service", ex.getCause());
        }

        return payments;
    }

    // validate if user that performed the request is linked to fiscal entity or is admin
    private boolean isAccountLinkedToFiscalEntityOrAdmin(ServiceContext serviceContext, String fiscalEntityIdentification) {
        Optional<FiscalEntityUserLink> fiscalEntityUserLink = fiscalEntityUserLinkRepository.findByUserIdentificationAndFiscalEntityIdentification(serviceContext.getUserId(), fiscalEntityIdentification);
        if (!fiscalEntityUserLink.isPresent() && !Validator.isAdmin(serviceContext)) {
            return false;
        } else {
            return true;
        }
    }

}