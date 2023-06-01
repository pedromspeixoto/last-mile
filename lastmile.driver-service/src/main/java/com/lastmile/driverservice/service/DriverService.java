package com.lastmile.driverservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.utils.context.ServiceContext;

import org.springframework.web.multipart.MultipartFile;

import com.lastmile.driverservice.dto.drivers.CreateDriverRequestDto;
import com.lastmile.driverservice.dto.drivers.CreateDriverResponseDto;
import com.lastmile.driverservice.dto.documents.CriminalRecordUpsertRequestDto;
import com.lastmile.driverservice.dto.drivers.DriverDto;
import com.lastmile.driverservice.dto.documents.DriverLicenseUpsertRequestDto;
import com.lastmile.driverservice.dto.drivers.DriverUpdateRequestDto;
import com.lastmile.driverservice.dto.documents.IdentificationCardUpsertRequestDto;
import com.lastmile.driverservice.dto.drivers.LocationDto;
import com.lastmile.driverservice.dto.drivers.PatchDriverStatusValidatedDto;
import com.lastmile.driverservice.dto.fiscalentities.FiscalEntityResponseDto;
import com.lastmile.driverservice.dto.orders.OrderActionRequestDto;
import com.lastmile.driverservice.service.exception.DocumentNotFoundException;
import com.lastmile.driverservice.service.exception.DriverAlreadyExistsException;
import com.lastmile.driverservice.service.exception.DriverForbiddenException;
import com.lastmile.driverservice.service.exception.GenericException;
import com.lastmile.driverservice.service.exception.DriverNotFoundException;
import com.lastmile.driverservice.service.exception.DriverStatusInvalidException;
import com.lastmile.driverservice.service.exception.EntityNotValidatedException;
import com.lastmile.driverservice.service.exception.ExternalServerException;
import com.lastmile.driverservice.service.exception.FeignCommunicationException;
import com.lastmile.driverservice.service.exception.FiscalEntityNotFoundException;

public interface DriverService {

    // create a new driver
    CreateDriverResponseDto create(CreateDriverRequestDto driver, ServiceContext serviceContext) throws DriverForbiddenException, FiscalEntityNotFoundException, DriverAlreadyExistsException, FeignCommunicationException;

    // get all drivers
    List<DriverDto> getDrivers(Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> userIdentification, Optional<String> driverIdentification, ServiceContext serviceContext) throws DriverForbiddenException, GenericException;

    // get drivers by location
    List<DriverDto> getDriversByLocation(Optional<Float> latitude, Optional<Float> longitude, Optional<Integer> radius, Optional<Integer> limitParam, Optional<String> status) throws GenericException;

    // get single driver information by driver identification
    DriverDto getDriver(String driverIdentification, Boolean includeUserProfile, ServiceContext serviceContext) throws DriverForbiddenException, DriverNotFoundException, GenericException;

    // get fiscal entity information by driver identification
    FiscalEntityResponseDto getDriverFiscalEntity(String driverIdentification, ServiceContext serviceContext) throws FiscalEntityNotFoundException, DriverForbiddenException, DriverNotFoundException, GenericException;

    // deletes driver by driver identification
    void deleteDriver(String driverIdentification, ServiceContext serviceContext) throws DriverForbiddenException, DriverNotFoundException, GenericException;

    // update driver location by driver identification
    void updateDriverLocation(String driverIdentification, LocationDto location, ServiceContext serviceContext) throws DriverForbiddenException, DriverNotFoundException, GenericException;

    // update driver status by driver identification
    void patchDriverValidated(String driverIdentification, PatchDriverStatusValidatedDto patchDriverStatusValidatedDto, ServiceContext serviceContext) throws DriverForbiddenException, DriverNotFoundException, GenericException;

    // update driver details by driver identification
    void updateDriver(String driverIdentification, DriverUpdateRequestDto driverUpdateRequestDto, ServiceContext serviceContext) throws DriverStatusInvalidException, EntityNotValidatedException, FiscalEntityNotFoundException, DriverForbiddenException, DriverNotFoundException, GenericException;

    // assign an order to a driver - TO DO
    void assignOrderToDriver(String driverIdentification, String orderIdentification, ServiceContext serviceContext) throws DriverStatusInvalidException, DriverForbiddenException, FeignCommunicationException, DriverNotFoundException, GenericException;

    // perform an action on an assigned order
    void manageOrder(String driverIdentification, String orderIdentification, OrderActionRequestDto orderActionRequestDto, ServiceContext serviceContext) throws FeignCommunicationException, DriverForbiddenException, DriverNotFoundException, GenericException;

    // upsert driver license by driver identification
    void upsertDriverLicense(String driverIdentification, MultipartFile documentFront, MultipartFile documentBack, DriverLicenseUpsertRequestDto driverLicenseUpsertRequestDto, ServiceContext serviceContext) throws DriverForbiddenException, ExternalServerException, DriverNotFoundException, GenericException;

    // delete driver license by driver identification
    void deleteDriverLicense(String driverIdentification, ServiceContext serviceContext) throws DriverForbiddenException, DocumentNotFoundException, ExternalServerException, DriverNotFoundException, GenericException;

    // upsert identification card by driver identification
    void upsertIdentificationCard(String driverIdentification, MultipartFile documentFront, MultipartFile documentBack, IdentificationCardUpsertRequestDto identificationCardUpsertRequestDto, ServiceContext serviceContext) throws DriverForbiddenException, ExternalServerException, DriverNotFoundException, GenericException;

    // delete identification card by driver identification
    void deleteIdentificationCard(String driverIdentification, ServiceContext serviceContext) throws DriverForbiddenException, DocumentNotFoundException, ExternalServerException, DriverNotFoundException, GenericException;

    // upsert criminal record by driver identification
    void upsertCriminalRecord(String driverIdentification, MultipartFile documentFront, MultipartFile documentBack, CriminalRecordUpsertRequestDto criminalRecordUpsertRequestDto, ServiceContext serviceContext) throws DriverForbiddenException, ExternalServerException, DriverNotFoundException, GenericException;

    // delete criminal record by driver identification
    void deleteCriminalRecord(String driverIdentification, ServiceContext serviceContext) throws DriverForbiddenException, DocumentNotFoundException, ExternalServerException, DriverNotFoundException, GenericException;

}