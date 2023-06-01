package com.lastmile.driverservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.driverservice.dto.addresses.CreateAddressRequestDto;
import com.lastmile.driverservice.dto.addresses.CreateAddressResponseDto;
import com.lastmile.driverservice.dto.addresses.GetAddressResponseDto;
import com.lastmile.driverservice.dto.addresses.UpdateAddressRequestDto;
import com.lastmile.driverservice.dto.drivers.DriverDto;
import com.lastmile.driverservice.dto.fiscalentities.AddUserToFiscalEntityRequestDto;
import com.lastmile.driverservice.dto.fiscalentities.CreateFiscalEntityRequestDto;
import com.lastmile.driverservice.dto.fiscalentities.CreateFiscalEntityResponseDto;
import com.lastmile.driverservice.dto.fiscalentities.FiscalEntityResponseDto;
import com.lastmile.driverservice.dto.fiscalentities.PatchFiscalEntityValidatedRequestDto;
import com.lastmile.driverservice.dto.fiscalentities.UpdateFiscalEntityRequestDto;
import com.lastmile.driverservice.dto.payments.GetOutPaymentResponseDto;
import com.lastmile.driverservice.service.exception.AddressNotFoundException;
import com.lastmile.driverservice.service.exception.DriverNotFoundException;
import com.lastmile.driverservice.service.exception.FeignCommunicationException;
import com.lastmile.driverservice.service.exception.GenericException;
import com.lastmile.driverservice.service.exception.EntityNotValidatedException;
import com.lastmile.driverservice.service.exception.LinkNotFoundException;
import com.lastmile.driverservice.service.exception.FiscalEntityAlreadyExistsException;
import com.lastmile.driverservice.service.exception.FiscalEntityHasDriversDeleteException;
import com.lastmile.driverservice.service.exception.FiscalEntityNotActiveException;
import com.lastmile.driverservice.service.exception.FiscalEntityNotFoundException;

public interface FiscalEntityService {

    // create new fiscal entity
    public CreateFiscalEntityResponseDto createNewFiscalEntity(CreateFiscalEntityRequestDto fiscalEntity, ServiceContext serviceContext) throws FeignCommunicationException, GenericException, FiscalEntityAlreadyExistsException;
    
    // get all fiscal entities
    public List<FiscalEntityResponseDto> getFiscalEntities(Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> userIdentification, ServiceContext serviceContext) throws LinkNotFoundException, GenericException;

    // get fiscal entity by fiscal id
    public FiscalEntityResponseDto getFiscalEntity(String fiscalEntityIdentification, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, GenericException;

    // get all fiscal entity drivers
    public List<DriverDto> getFiscalEntityDrivers(String fiscalEntityIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> driverIdentification, ServiceContext serviceContext) throws FiscalEntityNotFoundException, LinkNotFoundException, GenericException;

    // delete fiscal entity by fiscal entity identification
    public void deleteFiscalEntity(String fiscalEntityIdentification, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityHasDriversDeleteException, FiscalEntityNotFoundException, GenericException;

    // update fiscal entity by fiscal entity identification
    public void updateFiscalEntity(String fiscalEntityIdentification, UpdateFiscalEntityRequestDto fiscalEntity, ServiceContext serviceContext) throws EntityNotValidatedException, AddressNotFoundException, FeignCommunicationException, LinkNotFoundException, FiscalEntityNotFoundException, GenericException;

    // patch fiscal entity validated by fiscal entity identification
    public void patchFiscalEntityValidated(String fiscalEntityIdentification, PatchFiscalEntityValidatedRequestDto fiscalEntityValidatedRequestDto, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, GenericException;

    // add driver to fiscal entity
    public void addDriverToFiscalEntity(String fiscalEntityIdentification, String driverIdentifdication, ServiceContext serviceContext) throws FiscalEntityNotActiveException, LinkNotFoundException, DriverNotFoundException, FiscalEntityNotFoundException, GenericException;

    // remove driver from fiscal entity
    public void removeDriverFromFiscalEntity(String fiscalEntityIdentification, String driverIdentifdication, ServiceContext serviceContext) throws LinkNotFoundException, DriverNotFoundException, FiscalEntityNotFoundException, GenericException;

    // link an account to a fiscal entity
    public void addLinkUserToFiscalEntity(String fiscalEntityIdentification, AddUserToFiscalEntityRequestDto linkAccountDto, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, GenericException;

    // remove an account from a fiscal entity
    public void removeLinkUserToFiscalEntity(String fiscalEntityIdentification, AddUserToFiscalEntityRequestDto linkAccountDto, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, GenericException;

    // create fiscal entity address
    public CreateAddressResponseDto createFiscalEntityAddress(String fiscalEntityIdentification, CreateAddressRequestDto addressDto, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException;

    // update fiscal entity address
    public void updateFiscalEntityAddress(String fiscalEntityIdentification, String addressIdentification, UpdateAddressRequestDto addressDto, ServiceContext serviceContext) throws AddressNotFoundException, LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException;

    // delete fiscal entity address
    public void deleteFiscalEntityAddress(String fiscalEntityIdentification, String addressIdentification, ServiceContext serviceContext) throws AddressNotFoundException, LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException;

    // get individual fiscal entity address
    public GetAddressResponseDto getFiscalEntityAddress(String fiscalEntityIdentification, String addressIdentification, ServiceContext serviceContext) throws AddressNotFoundException, LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException;

    // get all fiscal entity addresses
    public List<GetAddressResponseDto> getAllFiscalEntityAddresses(String fiscalEntityIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> addressIdentification, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException;

    // get fiscal entity outbound payment transactions
    public List<GetOutPaymentResponseDto> getAllOutboundPayments(String fiscalEntityIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> outPaymentIdentification, ServiceContext serviceContext) throws LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException;

}