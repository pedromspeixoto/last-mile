package com.lastmile.driverservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lastmile.driverservice.client.accounts.AccountsBridge;
import com.lastmile.driverservice.client.orders.OrdersBridge;
import com.lastmile.driverservice.domain.DocumentCriminalRecord;
import com.lastmile.driverservice.domain.DocumentDriverLicense;
import com.lastmile.driverservice.domain.DocumentIdentificationCard;
import com.lastmile.driverservice.domain.Driver;
import com.lastmile.driverservice.domain.DriverVehicle;
import com.lastmile.driverservice.domain.FiscalEntity;
import com.lastmile.driverservice.dto.drivers.CreateDriverRequestDto;
import com.lastmile.driverservice.dto.drivers.CreateDriverResponseDto;
import com.lastmile.driverservice.dto.accounts.GetAccountDto;
import com.lastmile.driverservice.dto.documents.CriminalRecordResponseDto;
import com.lastmile.driverservice.dto.documents.CriminalRecordUpsertRequestDto;
import com.lastmile.driverservice.dto.drivers.DriverDto;
import com.lastmile.driverservice.dto.documents.DriverLicenseResponseDto;
import com.lastmile.driverservice.dto.documents.DriverLicenseUpsertRequestDto;
import com.lastmile.driverservice.dto.drivers.DriverUpdateRequestDto;
import com.lastmile.driverservice.dto.documents.IdentificationCardResponseDto;
import com.lastmile.driverservice.dto.documents.IdentificationCardUpsertRequestDto;
import com.lastmile.driverservice.dto.drivers.LocationDto;
import com.lastmile.driverservice.dto.drivers.PatchDriverStatusValidatedDto;
import com.lastmile.driverservice.dto.fiscalentities.FiscalEntityResponseDto;
import com.lastmile.driverservice.dto.orders.OrderActionRequestDto;
import com.lastmile.driverservice.dto.orders.OrderUpdateRequestDto;
import com.lastmile.driverservice.dto.vehicles.DriverVehicleResponseDto;
import com.lastmile.driverservice.enums.Authorities;
import com.lastmile.driverservice.enums.DriverStatus;
import com.lastmile.driverservice.enums.FiscalEntityStatus;
import com.lastmile.driverservice.repository.DocumentCriminalRecordsRepository;
import com.lastmile.driverservice.repository.DocumentDriverLicenseRepository;
import com.lastmile.driverservice.repository.DocumentIdentificationCardsRepository;
import com.lastmile.driverservice.repository.DriverRepository;
import com.lastmile.driverservice.repository.DriverVehicleRepository;
import com.lastmile.driverservice.repository.FiscalEntityRepository;
import com.lastmile.driverservice.service.DriverService;
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
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.clients.aws.AWSS3Client;
import com.lastmile.utils.enums.orders.OrderAction;
import com.lastmile.utils.enums.orders.OrderStatus;
import com.lastmile.utils.validations.Validator;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DriverServiceImpl implements DriverService {

    Logger logger = LoggerFactory.getLogger(DriverServiceImpl.class);

    private static final Integer DEFAULT_VALUE_LIMIT = 10;
    private static final Integer DEFAULT_VALUE_OFFSET = 0;

    private static final String DRIVER_LICENSES_S3_PATH = "driver-licenses/";
    private static final String IDENTIFICATION_CARDS_S3_PATH = "identification-cards/";
    private static final String CRIMINAL_RECORDS_S3_PATH = "criminal-records/";

    private final DriverRepository driverRepository;
    private final FiscalEntityRepository fiscalEntityRepository;
    private final DriverVehicleRepository driverVehicleRepository;
    private final DocumentCriminalRecordsRepository documentCriminalRecordsRepository;
    private final DocumentDriverLicenseRepository documentDriverLicenseRepository;
    private final DocumentIdentificationCardsRepository documentIdentificationCardsRepository;
    private final OrdersBridge ordersBridge;
    private final AccountsBridge accountsBridge;
    private final AWSS3Client awsS3Client;

    public DriverServiceImpl(final DriverRepository driverRepository,
                             final FiscalEntityRepository fiscalEntityRepository,
                             final OrdersBridge ordersBridge,
                             final AccountsBridge accountsBridge,
                             final DocumentCriminalRecordsRepository documentCriminalRecordsRepository,
                             final DocumentDriverLicenseRepository documentDriverLicenseRepository,
                             final DocumentIdentificationCardsRepository documentIdentificationCardsRepository,
                             final AWSS3Client awsS3Client,
                             final DriverVehicleRepository driverVehicleRepository) {
        this.driverRepository = driverRepository;
        this.fiscalEntityRepository = fiscalEntityRepository;
        this.ordersBridge = ordersBridge;
        this.accountsBridge = accountsBridge;
        this.documentCriminalRecordsRepository = documentCriminalRecordsRepository;
        this.documentDriverLicenseRepository = documentDriverLicenseRepository;
        this.documentIdentificationCardsRepository = documentIdentificationCardsRepository;
        this.awsS3Client = awsS3Client;
        this.driverVehicleRepository = driverVehicleRepository;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    @Transactional(rollbackFor = {FiscalEntityNotFoundException.class, DriverForbiddenException.class, FeignCommunicationException.class, DriverAlreadyExistsException.class})
    public CreateDriverResponseDto create(final CreateDriverRequestDto driverDto, ServiceContext serviceContext) throws DriverForbiddenException, FiscalEntityNotFoundException, FeignCommunicationException, DriverAlreadyExistsException {

        ModelMapper modelMapper = new ModelMapper();
        Driver driver = new Driver();

        // validate that the user requesting the driver creation is the same as in the dto
        if (null != driverDto.getUserIdentification() && !driverDto.getUserIdentification().isEmpty() && !isAccountLinkedToDriverOrAdmin(serviceContext, driverDto.getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        // if user identification already has a driver throw an error
        if (driverRepository.findByUserIdentification(driverDto.getUserIdentification()).isPresent()) {
            throw new DriverAlreadyExistsException(serviceContext.getUserId());
        }

        // if request has a fiscal entity, validate that the fiscal entity exists
        if (null != driverDto.getFiscalEntityIdentification() && !driverDto.getFiscalEntityIdentification().isEmpty() && !fiscalEntityRepository.findByFiscalEntityIdentification(driverDto.getFiscalEntityIdentification()).isPresent()) {
            throw new FiscalEntityNotFoundException(serviceContext.getUserId());
        }

        // save details for this driver
        driver = modelMapper.map(driverDto, Driver.class);

        driver.setUserIdentification(driverDto.getUserIdentification());
        driver.setDriverIdentification(UUID.randomUUID().toString());
        driver.setStatus(DriverStatus.PENDING.toString());
        driver.setEntityValidated(false);
        driverRepository.save(driver);

        //  update role in account service
        try {
            accountsBridge.updateAccountRole(serviceContext, driverDto.getUserIdentification(), driver.getDriverIdentification(), Authorities.ROLE_DRIVER);
        } catch (final Exception e) {
            throw new FeignCommunicationException(e.getMessage(), e.getCause());
        }

        return modelMapper.map(driver, CreateDriverResponseDto.class);
    }

    @Override
    public List<DriverDto> getDrivers(Optional<Integer> limit, Optional<Integer> offset, Optional<String> status,
                                      Optional<String> userIdentification, Optional<String> driverIdentification,
                                      ServiceContext serviceContext) throws DriverForbiddenException, GenericException {

        ModelMapper modelMapper = new ModelMapper();
        List<Driver> drivers;
        List<DriverDto> driversDto;

        Pageable pageable = PageRequest.of(offset.orElse(DEFAULT_VALUE_OFFSET), limit.orElse(DEFAULT_VALUE_LIMIT));

        // if user identification exists validate context
        if (userIdentification.isPresent() && !userIdentification.get().isEmpty() && !isAccountLinkedToDriverOrAdmin(serviceContext, userIdentification.get())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        // if driver identification exists validate context
        if (driverIdentification.isPresent() && !driverIdentification.get().isEmpty() && !isAccountLinkedToDriverOrAdmin(serviceContext, driverRepository.findByDriverIdentification(driverIdentification.get()).get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        try {
            // try to fetch drivers
            drivers = driverRepository.findAllDrivers(status.orElse(""), userIdentification.orElse(""), driverIdentification.orElse("") ,pageable);

            driversDto = drivers.stream()
                                .map(driver -> modelMapper.map(driver, DriverDto.class))
                                .collect(Collectors.toList());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return driversDto;
    }

    @Override
    public List<DriverDto> getDriversByLocation(Optional<Float> latitude, Optional<Float> longitude, Optional<Integer> radius, Optional<Integer> limitParam, Optional<String> status) throws GenericException {

        ModelMapper modelMapper = new ModelMapper();
        List<Driver> nearestDrivers;

        try {
            // try to fetch drivers
            nearestDrivers = driverRepository.findDriversByLocationAndRadius(latitude.get(), longitude.get(), radius.get(), limitParam.orElse(DEFAULT_VALUE_LIMIT), status.orElse(""));
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return nearestDrivers.stream()
                      .map(driver -> modelMapper.map(driver, DriverDto.class))
                      .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, GenericException.class, DriverNotFoundException.class, FiscalEntityNotFoundException.class})
    public FiscalEntityResponseDto getDriverFiscalEntity(String driverIdentification, ServiceContext serviceContext) throws FiscalEntityNotFoundException, DriverForbiddenException, DriverNotFoundException, GenericException {

        ModelMapper modelMapper = new ModelMapper();
        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification()) && !Validator.isOriginOutboundPayment(serviceContext)) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        // validate that user has fiscal entity
        if (null == driver.get().getFiscalEntityIdentification() || driver.get().getFiscalEntityIdentification().isEmpty()){
            throw new FiscalEntityNotFoundException("");
        }

        Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(driver.get().getFiscalEntityIdentification());
        if (!fiscalEntity.isPresent()) {
            throw new FiscalEntityNotFoundException(driver.get().getFiscalEntityIdentification());
        }

        FiscalEntityResponseDto fiscalEntityResponseDto;
        try {
            fiscalEntityResponseDto = modelMapper.map(fiscalEntity.get(), FiscalEntityResponseDto.class);
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return fiscalEntityResponseDto;
    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, GenericException.class, DriverNotFoundException.class})
    public DriverDto getDriver(String driverIdentification, Boolean includeUserProfile, ServiceContext serviceContext) throws DriverForbiddenException, DriverNotFoundException, GenericException {

        ModelMapper modelMapper = new ModelMapper();
        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        DriverDto driverDto;

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!Validator.isInternalCommunication(serviceContext) && !isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        driverDto = modelMapper.map(driver.get(), DriverDto.class);

        // user profile
        if (includeUserProfile) {
            try {
                GetAccountDto profile = accountsBridge.getAccount(serviceContext, driver.get().getUserIdentification(), driverIdentification);
                driverDto.setProfile(profile);
            } catch (Exception e) {
                throw new GenericException(e.getMessage(), e.getCause());
            }
        }

        // criminal record
        Optional<DocumentCriminalRecord> documentCriminalRecord = documentCriminalRecordsRepository.findByDriverIdentification(driverIdentification);
        String criminalRecordDocumentPath = CRIMINAL_RECORDS_S3_PATH + driverIdentification + "/";
        if (documentCriminalRecord.isPresent()) {
            CriminalRecordResponseDto criminalRecordResponseDto = modelMapper.map(documentCriminalRecord.get(),
                                                                                    CriminalRecordResponseDto.class);
            // get documents from AWS - document back
            if (null != documentCriminalRecord.get().getDocumentBackFileId() && !documentCriminalRecord.get().getDocumentBackFileId().isEmpty()) {
                criminalRecordResponseDto.setDocumentBack(awsS3Client.downloadFile(criminalRecordDocumentPath + documentCriminalRecord.get().getDocumentBackFileId()));
            }
            // get documents from AWS - document front
            if (null != documentCriminalRecord.get().getDocumentFrontFileId() && !documentCriminalRecord.get().getDocumentFrontFileId().isEmpty()) {
                criminalRecordResponseDto.setDocumentFront(awsS3Client.downloadFile(criminalRecordDocumentPath + documentCriminalRecord.get().getDocumentFrontFileId()));
            }
            driverDto.setDocumentCriminalRecord(criminalRecordResponseDto);
        }

        // identification card
        Optional<DocumentIdentificationCard> documentIdentificationCard = documentIdentificationCardsRepository.findByDriverIdentification(driverIdentification);
        String identificationCardDocumentPath = IDENTIFICATION_CARDS_S3_PATH + driverIdentification + "/";
        if (documentIdentificationCard.isPresent()) {
            IdentificationCardResponseDto identificationCardResponseDto = modelMapper.map(documentIdentificationCard.get(),
                                                                                          IdentificationCardResponseDto.class);
            // get documents from AWS - document back
            if (null != documentIdentificationCard.get().getDocumentBackFileId() && !documentIdentificationCard.get().getDocumentBackFileId().isEmpty()) {
                identificationCardResponseDto.setDocumentBack(awsS3Client.downloadFile(identificationCardDocumentPath + documentIdentificationCard.get().getDocumentBackFileId()));
            }
            // get documents from AWS - document back
            if (null != documentIdentificationCard.get().getDocumentFrontFileId() && !documentIdentificationCard.get().getDocumentFrontFileId().isEmpty()) {
                identificationCardResponseDto.setDocumentFront(awsS3Client.downloadFile(identificationCardDocumentPath + documentIdentificationCard.get().getDocumentFrontFileId()));
            }
            driverDto.setDocumentIdentificationCard(identificationCardResponseDto);
        }

        // driver license
        Optional<DocumentDriverLicense> documentDriverLicense = documentDriverLicenseRepository.findByDriverIdentification(driverIdentification);
        String driverLicenseDocumentPath = DRIVER_LICENSES_S3_PATH + driverIdentification + "/";
        if (documentDriverLicense.isPresent()) {
            DriverLicenseResponseDto driverLicenseResponseDto = modelMapper.map(documentDriverLicense.get(),
                                                                                DriverLicenseResponseDto.class);
            // get documents from AWS - document back
            if (null != documentDriverLicense.get().getDocumentBackFileId() && !documentDriverLicense.get().getDocumentBackFileId().isEmpty()) {
                driverLicenseResponseDto.setDocumentBack(awsS3Client.downloadFile(driverLicenseDocumentPath + documentDriverLicense.get().getDocumentBackFileId()));
            }
            // get documents from AWS - document back
            if (null != documentDriverLicense.get().getDocumentFrontFileId() && !documentDriverLicense.get().getDocumentFrontFileId().isEmpty()) {
                driverLicenseResponseDto.setDocumentFront(awsS3Client.downloadFile(driverLicenseDocumentPath + documentDriverLicense.get().getDocumentFrontFileId()));
            }
            driverDto.setDriverLicense(driverLicenseResponseDto);
        }

        // active vehicle
        Optional<DriverVehicle> driverVehicle = driverVehicleRepository.findByDriverIdentificationAndVehicleActive(driverIdentification, true);
        if (driverVehicle.isPresent()) {
            DriverVehicleResponseDto driverVehicleResponseDto = modelMapper.map(driverVehicle.get(),
                                                                                DriverVehicleResponseDto.class);
            driverDto.setActiveVehicle(driverVehicleResponseDto);
        }

        // fiscal entity
        if (null != driver.get().getFiscalEntityIdentification() && !driver.get().getFiscalEntityIdentification().isEmpty()) {
            Optional<FiscalEntity> fiscalEntity = fiscalEntityRepository.findByFiscalEntityIdentification(driver.get().getFiscalEntityIdentification());
            if (fiscalEntity.isPresent()) {
                FiscalEntityResponseDto fiscalEntityResponseDto = modelMapper.map(fiscalEntity.get(),
                                                                                  FiscalEntityResponseDto.class);
                driverDto.setFiscalEntity(fiscalEntityResponseDto);
            }
        }

        return driverDto;
    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, DriverNotFoundException.class, GenericException.class})
    public void deleteDriver(String driverIdentification, ServiceContext serviceContext) throws DriverForbiddenException, DriverNotFoundException, GenericException {

        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        try {
            driverRepository.deleteById(driver.get().getId());
        } catch(final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, DriverNotFoundException.class, GenericException.class})
    public void updateDriverLocation(String driverIdentification, LocationDto location, ServiceContext serviceContext) throws DriverForbiddenException, DriverNotFoundException, GenericException {

        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        Driver saveDriver = driver.get();

        try {
            saveDriver.setLatitude(location.getLatitude());
            saveDriver.setLongitude(location.getLongitude());
            driverRepository.save(saveDriver);
        } catch(final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, DriverNotFoundException.class, GenericException.class})
    public void patchDriverValidated(String driverIdentification, PatchDriverStatusValidatedDto patchDriverStatusValidatedDto, ServiceContext serviceContext) throws DriverForbiddenException, DriverNotFoundException, GenericException {
        
        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!Validator.isAdmin(serviceContext)) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        Driver saveDriver = driver.get();

        try {
            if (!patchDriverStatusValidatedDto.getValidated()) {
                saveDriver.setStatus(DriverStatus.PENDING.toString());
            }
            saveDriver.setStatus(DriverStatus.OFFLINE.toString());
            saveDriver.setEntityValidated(patchDriverStatusValidatedDto.getValidated());
            driverRepository.save(saveDriver);
        } catch(final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }
    }


    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, DriverNotFoundException.class, GenericException.class, DriverStatusInvalidException.class})
    public void assignOrderToDriver(String driverIdentification, String orderIdentification, ServiceContext serviceContext) throws DriverStatusInvalidException, DriverForbiddenException, DriverNotFoundException, GenericException, FeignCommunicationException {

        // validate that is internal communication or admin
        if (!Validator.isAdmin(serviceContext) && !Validator.isInternalCommunication(serviceContext)) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that driver is available to receive order
        if(!driver.get().getStatus().equals(DriverStatus.AVAILABLE.toString()) && !driver.get().getStatus().equals(DriverStatus.IN_TRANSIT.toString())) {
            throw new DriverStatusInvalidException(driver.get().getStatus());
        }

        // TODO send notification to driver

        // assign driver to order in order service
        try {
            ordersBridge.assignDriverToOrder(serviceContext, driverIdentification, orderIdentification);
        } catch(final Exception e) {
            throw new FeignCommunicationException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {FeignCommunicationException.class, DriverForbiddenException.class, DriverNotFoundException.class, GenericException.class})
    public void manageOrder(String driverIdentification, String orderIdentification,
                            OrderActionRequestDto orderActionRequestDto, ServiceContext serviceContext) throws FeignCommunicationException, DriverForbiddenException, DriverNotFoundException, GenericException,
            FeignCommunicationException {

        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        OrderUpdateRequestDto orderUpdateRequestDto = new OrderUpdateRequestDto();
        OrderAction action = orderActionRequestDto.getOrderAction();

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        Driver saveDriver = driver.get();

        switch (action) {
            case ACCEPT:
                // set driver elements
                saveDriver.setStatus(DriverStatus.IN_TRANSIT.toString());
                // set order elements
                orderUpdateRequestDto.setAssignedDriver(driverIdentification);
                orderUpdateRequestDto.setOrderStatus(OrderStatus.ACCEPTED);
                break;

            case REJECT:
                // set order elements
                orderUpdateRequestDto.setOrderStatus(OrderStatus.REJECTED);
                break;

            case PICKUP:
                saveDriver.setStatus(DriverStatus.IN_TRANSIT.toString());
                orderUpdateRequestDto.setOrderStatus(OrderStatus.IN_TRANSIT);
                break;

            case FINALIZE:
                saveDriver.setStatus(DriverStatus.AVAILABLE.toString());
                orderUpdateRequestDto.setOrderStatus(OrderStatus.FINALIZED);
                break;
        }

        // updating order in order service
        try {
            ordersBridge.updateOrder(driverIdentification, orderIdentification, orderUpdateRequestDto, serviceContext);
        } catch(final Exception e) {
            throw new FeignCommunicationException(e.getMessage(), e.getCause());
        }

        try {
            driverRepository.save(saveDriver);
        } catch(final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {FiscalEntityNotFoundException.class, DriverForbiddenException.class, DriverNotFoundException.class, GenericException.class, EntityNotValidatedException.class, DriverStatusInvalidException.class})
    public void updateDriver(String driverIdentification, DriverUpdateRequestDto driverUpdateRequestDto, ServiceContext serviceContext) throws DriverStatusInvalidException, EntityNotValidatedException, FiscalEntityNotFoundException, DriverForbiddenException, DriverNotFoundException, GenericException {

        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        Driver newDriver = new Driver();

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        // validate that fiscal entity exists
        if (null != driverUpdateRequestDto.getFiscalEntityIdentification() && !driverUpdateRequestDto.getFiscalEntityIdentification().isEmpty()
                        && !fiscalEntityRepository.findByFiscalEntityIdentification(driverUpdateRequestDto.getFiscalEntityIdentification()).isPresent()) {
            throw new FiscalEntityNotFoundException(driverUpdateRequestDto.getFiscalEntityIdentification());
        }

        // status
        if (null != driverUpdateRequestDto.getStatus() && !driverUpdateRequestDto.getStatus().toString().isEmpty()
            && !driver.get().getEntityValidated()) {
                throw new EntityNotValidatedException();
        }

        newDriver = driver.get();
        // status
        if (null != driverUpdateRequestDto.getStatus() && !driverUpdateRequestDto.getStatus().toString().isEmpty()) {
            // to available -> validate that has driver and active fiscal entity
            if ( (driverUpdateRequestDto.getStatus().equals(DriverStatus.AVAILABLE) && !driverVehicleRepository.findByDriverIdentificationAndVehicleActive(newDriver.getDriverIdentification(), true).isPresent())
                || (driverUpdateRequestDto.getStatus().equals(DriverStatus.AVAILABLE) && (null == newDriver.getFiscalEntityIdentification() || newDriver.getFiscalEntityIdentification().isEmpty()))
                || (driverUpdateRequestDto.getStatus().equals(DriverStatus.AVAILABLE) && !fiscalEntityRepository.findByFiscalEntityIdentificationAndStatus(newDriver.getFiscalEntityIdentification(), FiscalEntityStatus.ACTIVE.toString()).isPresent() )) {
                    throw new DriverStatusInvalidException(driverUpdateRequestDto.getStatus().toString());
            }
            newDriver.setStatus(driverUpdateRequestDto.getStatus().toString());
        }

        try {
            
            // fiscal entity id
            if (driverUpdateRequestDto.getFiscalEntityIdentification() != null && !driverUpdateRequestDto.getFiscalEntityIdentification().isEmpty()){
                newDriver.setFiscalEntityIdentification(driverUpdateRequestDto.getFiscalEntityIdentification());
            }
            // latitude
            if (null != driverUpdateRequestDto.getLatitude() && !driverUpdateRequestDto.getLatitude().toString().isEmpty()) {
                newDriver.setLatitude(driverUpdateRequestDto.getLatitude());
            }
            // longitude
            if (null != driverUpdateRequestDto.getLongitude() && !driverUpdateRequestDto.getLongitude().toString().isEmpty()) {
                newDriver.setLongitude(driverUpdateRequestDto.getLongitude());
            }
            driverRepository.save(newDriver);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, ExternalServerException.class, DriverNotFoundException.class, GenericException.class})
    public void upsertDriverLicense(String driverIdentification, MultipartFile documentFront, MultipartFile documentBack, DriverLicenseUpsertRequestDto driverLicenseUpsertRequestDto, ServiceContext serviceContext) throws DriverForbiddenException, ExternalServerException, DriverNotFoundException, GenericException {

        ModelMapper modelMapper = new ModelMapper();
        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        Optional<DocumentDriverLicense> documentDriverLicense;
        DocumentDriverLicense newDocumentDriverLicense = new DocumentDriverLicense();

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        String documentPath = DRIVER_LICENSES_S3_PATH + driverIdentification + "/";

        try {

            // see if there is already a driver license for this user
            documentDriverLicense = documentDriverLicenseRepository.findByDriverIdentification(driverIdentification);

            // if exists, update
            if (documentDriverLicense.isPresent()) {
                newDocumentDriverLicense = documentDriverLicense.get();
                // name
                if (null != driverLicenseUpsertRequestDto.getName() && !driverLicenseUpsertRequestDto.getName().isEmpty()) {
                    newDocumentDriverLicense.setName(driverLicenseUpsertRequestDto.getName());
                }
                // surname
                if (null != driverLicenseUpsertRequestDto.getSurname() && !driverLicenseUpsertRequestDto.getSurname().isEmpty()) {
                    newDocumentDriverLicense.setSurname(driverLicenseUpsertRequestDto.getSurname());
                }
                // birth date
                if (null != driverLicenseUpsertRequestDto.getBirthDate()) {
                    newDocumentDriverLicense.setBirthDate(driverLicenseUpsertRequestDto.getBirthDate());
                }
                // issue date
                if (null != driverLicenseUpsertRequestDto.getIssueDate()) {
                    newDocumentDriverLicense.setIssueDate(driverLicenseUpsertRequestDto.getIssueDate());
                }
                // expiry date
                if (null != driverLicenseUpsertRequestDto.getExpiryDate()) {
                    newDocumentDriverLicense.setExpiryDate(driverLicenseUpsertRequestDto.getExpiryDate());
                }
                // issuing authority
                if (null != driverLicenseUpsertRequestDto.getIssuingAuthority() && !driverLicenseUpsertRequestDto.getIssuingAuthority().isEmpty()) {
                    newDocumentDriverLicense.setIssuingAuthority(driverLicenseUpsertRequestDto.getIssuingAuthority());
                }
                // personal number
                if (null != driverLicenseUpsertRequestDto.getPersonalNumber() && !driverLicenseUpsertRequestDto.getPersonalNumber().isEmpty()) {
                    newDocumentDriverLicense.setPersonalNumber(driverLicenseUpsertRequestDto.getPersonalNumber());
                }
                // license number
                if (null != driverLicenseUpsertRequestDto.getLicenseNumber() && !driverLicenseUpsertRequestDto.getLicenseNumber().isEmpty()) {
                    newDocumentDriverLicense.setLicenseNumber(driverLicenseUpsertRequestDto.getLicenseNumber());
                }
                // license address
                if (null != driverLicenseUpsertRequestDto.getLicenseAddress() && !driverLicenseUpsertRequestDto.getLicenseAddress().isEmpty()) {
                    newDocumentDriverLicense.setLicenseAddress(driverLicenseUpsertRequestDto.getLicenseAddress());
                }

                // document front file id
                if (null != documentFront && !(documentFront.isEmpty())) {
                    String documentFrontFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentFront.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentFront, documentPath + documentFrontFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentDriverLicense.setDocumentFrontFileId(documentFrontFileId);
                }

                // document back file id
                if (null != documentBack && !documentBack.isEmpty()) {
                    String documentBackFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentBack.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentBack, documentPath + documentBackFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentDriverLicense.setDocumentBackFileId(documentBackFileId);
                }

                // set validation field
                if (driverLicenseUpsertRequestDto.isValidated() != newDocumentDriverLicense.isValidated()) {
                    newDocumentDriverLicense.setValidated(!driverLicenseUpsertRequestDto.getValidated());
                }

            // if it does not exist, insert
            } else {

                // save new document
                newDocumentDriverLicense = modelMapper.map(driverLicenseUpsertRequestDto, DocumentDriverLicense.class);
                newDocumentDriverLicense.setDriverLicenseIdentification(UUID.randomUUID().toString());
                newDocumentDriverLicense.setDriverIdentification(driverIdentification);

                // document front file id
                if (null != documentFront && !documentFront.isEmpty()) {
                    String documentFrontFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentFront.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentFront, documentPath + documentFrontFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentDriverLicense.setDocumentFrontFileId(documentFrontFileId);
                }

                // document back file id
                if (null != documentBack && !documentBack.isEmpty()) {
                    String documentBackFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentBack.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentBack, documentPath + documentBackFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentDriverLicense.setDocumentBackFileId(documentBackFileId);
                }
                documentDriverLicenseRepository.save(newDocumentDriverLicense);
            }

        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, DocumentNotFoundException.class, ExternalServerException.class, DriverNotFoundException.class, GenericException.class})
    public void deleteDriverLicense(String driverIdentification, ServiceContext serviceContext) throws DriverForbiddenException, DocumentNotFoundException, ExternalServerException, DriverNotFoundException, GenericException {

        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        Optional<DocumentDriverLicense> documentDriverLicense = documentDriverLicenseRepository.findByDriverIdentification(driverIdentification);

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        if (!documentDriverLicense.isPresent()) {
            throw new DocumentNotFoundException("Driver License");
        }

        try {
            documentDriverLicenseRepository.delete(documentDriverLicense.get());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, ExternalServerException.class, DriverNotFoundException.class, GenericException.class})
    public void upsertIdentificationCard(String driverIdentification, MultipartFile documentFront, MultipartFile documentBack, IdentificationCardUpsertRequestDto identificationCardUpsertRequestDto, ServiceContext serviceContext) throws DriverForbiddenException, ExternalServerException, DriverNotFoundException, GenericException {

        ModelMapper modelMapper = new ModelMapper();
        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        Optional<DocumentIdentificationCard> documentIdentificationCard;
        DocumentIdentificationCard newDocumentIdentificationCard = new DocumentIdentificationCard();

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        String documentPath = IDENTIFICATION_CARDS_S3_PATH + driverIdentification + "/";

        try {

            // see if there is already an identification card for this user
            documentIdentificationCard = documentIdentificationCardsRepository.findByDriverIdentification(driverIdentification);

            // if exists, update
            if (documentIdentificationCard.isPresent()) {
                newDocumentIdentificationCard = documentIdentificationCard.get();
                // name
                if (null != identificationCardUpsertRequestDto.getName() && !identificationCardUpsertRequestDto.getName().isEmpty()) {
                    newDocumentIdentificationCard.setName(identificationCardUpsertRequestDto.getName());
                }
                // surname
                if (null != identificationCardUpsertRequestDto.getSurname() && !identificationCardUpsertRequestDto.getSurname().isEmpty()) {
                    newDocumentIdentificationCard.setSurname(identificationCardUpsertRequestDto.getSurname());
                }
                // birth date
                if (null != identificationCardUpsertRequestDto.getBirthDate()) {
                    newDocumentIdentificationCard.setBirthDate(identificationCardUpsertRequestDto.getBirthDate());
                }
                // document number
                if (null != identificationCardUpsertRequestDto.getDocumentNumber()) {
                    newDocumentIdentificationCard.setDocumentNumber(identificationCardUpsertRequestDto.getDocumentNumber());
                }
                // expiry date
                if (null != identificationCardUpsertRequestDto.getDocumentExpiryDate()) {
                    newDocumentIdentificationCard.setDocumentExpiryDate(identificationCardUpsertRequestDto.getDocumentExpiryDate());
                }

                // document front file id
                if (null != documentFront && !documentFront.isEmpty()) {
                    String documentFrontFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentFront.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentFront, documentPath + documentFrontFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentIdentificationCard.setDocumentFrontFileId(documentFrontFileId);

                }

                // document back file id
                if (null != documentBack && !documentBack.isEmpty()) {
                    String documentBackFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentBack.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentBack, documentPath + documentBackFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentIdentificationCard.setDocumentBackFileId(documentBackFileId);
                }

                // set validation field
                if (identificationCardUpsertRequestDto.isValidated() != identificationCardUpsertRequestDto.isValidated()) {
                    newDocumentIdentificationCard.setValidated(!identificationCardUpsertRequestDto.getValidated());
                }

            // if it does not exist, insert
            } else {
                // save new document
                newDocumentIdentificationCard = modelMapper.map(identificationCardUpsertRequestDto, DocumentIdentificationCard.class);
                newDocumentIdentificationCard.setIdentificationCardIdentification(UUID.randomUUID().toString());
                newDocumentIdentificationCard.setDriverIdentification(driverIdentification);

                // document front file id
                if (null != documentFront && !documentFront.isEmpty()) {
                    String documentFrontFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentFront.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentFront, documentPath + documentFrontFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentIdentificationCard.setDocumentFrontFileId(documentFrontFileId);
                }

                // document back file id
                if (null != documentBack && !documentBack.isEmpty()) {
                    String documentBackFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentBack.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentBack, documentPath + documentBackFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentIdentificationCard.setDocumentBackFileId(documentBackFileId);
                }
                documentIdentificationCardsRepository.save(newDocumentIdentificationCard);

            }

        } catch (final Exception e) {
            throw new GenericException("Error updating driver " + driverIdentification + " in database", e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, DocumentNotFoundException.class, ExternalServerException.class, DriverNotFoundException.class, GenericException.class})
    public void deleteIdentificationCard(String driverIdentification, ServiceContext serviceContext) throws DriverForbiddenException, DocumentNotFoundException, ExternalServerException, DriverNotFoundException, GenericException {

        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        Optional<DocumentIdentificationCard> documentIdentificationCard = documentIdentificationCardsRepository.findByDriverIdentification(driverIdentification);

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        // validate that the document exists
        if (!documentIdentificationCard.isPresent()) {
            throw new DocumentNotFoundException("Identification Card");
        }

        try {
            documentIdentificationCardsRepository.delete(documentIdentificationCard.get());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(),e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, ExternalServerException.class, DriverNotFoundException.class, GenericException.class})
    public void upsertCriminalRecord(String driverIdentification, MultipartFile documentFront, MultipartFile documentBack, CriminalRecordUpsertRequestDto criminalRecordUpsertRequestDto, ServiceContext serviceContext) throws DriverForbiddenException, ExternalServerException, DriverNotFoundException, GenericException {

        ModelMapper modelMapper = new ModelMapper();
        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        Optional<DocumentCriminalRecord> documentCriminalRecord;
        DocumentCriminalRecord newDocumentCriminalRecord = new DocumentCriminalRecord();

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        String documentPath = CRIMINAL_RECORDS_S3_PATH + driverIdentification + "/";

        try {
            // see if there is already a criminal record document for this driver
            documentCriminalRecord = documentCriminalRecordsRepository.findByDriverIdentification(driverIdentification);
            // if exists, update
            if (documentCriminalRecord.isPresent()) {
                newDocumentCriminalRecord = documentCriminalRecord.get();
                // document front file id
                if (null != documentFront && !(documentFront.isEmpty())) {
                    String documentFrontFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentFront.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentFront, documentPath + documentFrontFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentCriminalRecord.setDocumentFrontFileId(documentFrontFileId);
                }

                // document back file id
                if (null != documentBack && !(documentBack.isEmpty())) {
                    String documentBackFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentBack.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentBack, documentPath + documentBackFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentCriminalRecord.setDocumentBackFileId(documentBackFileId);
                }
                // validated
                if (null != criminalRecordUpsertRequestDto.getValidated() && (criminalRecordUpsertRequestDto.getValidated() != newDocumentCriminalRecord.getValidated())) {
                    newDocumentCriminalRecord.setValidated(criminalRecordUpsertRequestDto.getValidated());
                }
                documentCriminalRecordsRepository.save(newDocumentCriminalRecord);

            // if it does not exist, insert
            } else {

                // save new document
                newDocumentCriminalRecord = modelMapper.map(criminalRecordUpsertRequestDto, DocumentCriminalRecord.class);
                newDocumentCriminalRecord.setCriminalRecordIdentification(UUID.randomUUID().toString());
                newDocumentCriminalRecord.setDriverIdentification(driverIdentification);

                // document front file id
                if (null != documentFront && !(documentFront.isEmpty())) {
                    String documentFrontFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentFront.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentFront, documentPath + documentFrontFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentCriminalRecord.setDocumentFrontFileId(documentFrontFileId);
                }

                // document back file id
                if (null != documentBack && !(documentBack.isEmpty())) {
                    String documentBackFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentBack.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentBack, documentPath + documentBackFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentCriminalRecord.setDocumentBackFileId(documentBackFileId);
                }
                documentCriminalRecordsRepository.save(newDocumentCriminalRecord);
            }

        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, DocumentNotFoundException.class, ExternalServerException.class, DriverNotFoundException.class, GenericException.class})
    public void deleteCriminalRecord(String driverIdentification, ServiceContext serviceContext) throws DriverForbiddenException, DocumentNotFoundException, ExternalServerException, DriverNotFoundException, GenericException {

        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        Optional<DocumentCriminalRecord> documentCriminalRecord = documentCriminalRecordsRepository.findByDriverIdentification(driverIdentification);

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user has permission
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        // validate that the document exists
        if (!documentCriminalRecord.isPresent()) {
            throw new DocumentNotFoundException("Criminal Record");
        }

        try {
            documentCriminalRecordsRepository.delete(documentCriminalRecord.get());
        } catch (final Exception e) {
            throw new GenericException("Error deleting document from database", e.getCause());
        }

    }

    // validate if user that performed the request is linked to driver or is admin
    private boolean isAccountLinkedToDriverOrAdmin(ServiceContext serviceContext, String userIdentification) {
        if (!serviceContext.getUserId().equals(userIdentification) && !Validator.isAdmin(serviceContext) && !Validator.isInternalCommunication(serviceContext)) {
            return false;
        } else {
            return true;
        }
    }

}