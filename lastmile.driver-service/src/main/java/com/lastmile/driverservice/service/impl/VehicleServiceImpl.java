package com.lastmile.driverservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lastmile.driverservice.client.ExternalVehiclesAPIMapper;
import com.lastmile.driverservice.domain.DocumentVehicleLicenseRegistration;
import com.lastmile.driverservice.domain.Driver;
import com.lastmile.driverservice.domain.DriverVehicle;
import com.lastmile.driverservice.domain.Vehicle;
import com.lastmile.driverservice.dto.vehicles.AddVehicleRequestDto;
import com.lastmile.driverservice.dto.vehicles.AddVehicleResponseDto;
import com.lastmile.driverservice.dto.vehicles.DriverVehicleResponseDto;
import com.lastmile.driverservice.dto.vehicles.UpdateVehicleStatusRequestDto;
import com.lastmile.driverservice.dto.vehicles.ValidateVehicleRequestDto;
import com.lastmile.driverservice.dto.documents.VehicleRegistrationResponseDto;
import com.lastmile.driverservice.dto.documents.VehicleRegistrationUpsertRequestDto;
import com.lastmile.driverservice.dto.vehicles.VehicleResponseDto;
import com.lastmile.driverservice.repository.DocumentVehicleLicenseRegistrationRepository;
import com.lastmile.driverservice.repository.DriverRepository;
import com.lastmile.driverservice.repository.DriverVehicleRepository;
import com.lastmile.driverservice.repository.VehicleRepository;
import com.lastmile.driverservice.service.VehicleService;
import com.lastmile.driverservice.service.exception.DocumentNotFoundException;
import com.lastmile.driverservice.service.exception.DriverForbiddenException;
import com.lastmile.driverservice.service.exception.DriverNotFoundException;
import com.lastmile.driverservice.service.exception.EntityNotValidatedException;
import com.lastmile.driverservice.service.exception.ExternalServerException;
import com.lastmile.driverservice.service.exception.GenericException;
import com.lastmile.driverservice.service.exception.VehicleAlreadyActiveException;
import com.lastmile.driverservice.service.exception.VehicleNotFoundException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.clients.aws.AWSS3Client;
import com.lastmile.utils.validations.Validator;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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
public class VehicleServiceImpl implements VehicleService {

    private static final Integer DEFAULT_VALUE_LIMIT = 10;
    private static final Integer DEFAULT_VALUE_OFFSET = 0;
    private static final String VEH_LICENSE_REG_S3_PATH = "vehicle-license-registrations/";

    Logger logger = LoggerFactory.getLogger(VehicleServiceImpl.class);

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final DriverVehicleRepository driverVehicleRepository;
    private final DocumentVehicleLicenseRegistrationRepository documentVehicleLicenseRegistrationRepository;
    private final ExternalVehiclesAPIMapper externalVehiclesAPIMapper;
    private final AWSS3Client awsS3Client;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public VehicleServiceImpl(final VehicleRepository vehicleRepository,
            final ExternalVehiclesAPIMapper externalVehiclesAPIMapper,
            final DriverRepository driverRepository,
            final DriverVehicleRepository driverVehicleRepository,
            final DocumentVehicleLicenseRegistrationRepository documentVehicleLicenseRegistrationRepository,
            final AWSS3Client awsS3Client) {
        this.vehicleRepository = vehicleRepository;
        this.externalVehiclesAPIMapper = externalVehiclesAPIMapper;
        this.driverRepository = driverRepository;
        this.driverVehicleRepository = driverVehicleRepository;
        this.documentVehicleLicenseRegistrationRepository = documentVehicleLicenseRegistrationRepository;
        this.awsS3Client = awsS3Client;
    }

    @Override
    @Transactional(rollbackFor = GenericException.class)
    public void refreshVehicles(ServiceContext serviceContext) throws DriverForbiddenException, ExternalServerException, GenericException {

        // validate that user is admin
        if (!Validator.isAdmin(serviceContext)) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        try {
            // clean vehicles table
            vehicleRepository.deleteAll();
            // recreate table in new thread
            Thread thread = new Thread(() -> {
                JSONObject vehiclesData = new JSONObject();
                try {
                    vehiclesData = externalVehiclesAPIMapper.getVehicles();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Vehicle vehicle = new Vehicle();
                JSONArray vehicleDataArray = vehiclesData.getJSONArray("results");

                for (int i = 0 ; i < vehicleDataArray.length(); i++) {
                    JSONObject vehicleJson = vehicleDataArray.getJSONObject(i);
                    vehicle = new Vehicle();
                    vehicle.setMake(vehicleJson.getString("Make"));
                    vehicle.setModel(vehicleJson.getString("Model"));
                    vehicle.setCategory(vehicleJson.getString("Category"));
                    vehicle.setYear(vehicleJson.getInt("Year"));
                    vehicleRepository.save(vehicle);
                }
            });
            thread.setName("vehicles-refresh");
            thread.start();   
        } catch (final Exception e) {
            throw new ExternalServerException("parseapi.back4app.com", e.getCause());
        }

    }

    @Override
    public List<VehicleResponseDto> searchVehicles(Optional<Integer> limit, Optional<Integer> offset,
                                                   Optional<String> make, Optional<String> model, 
                                                   Optional<String> category, Optional<String> year) throws GenericException {

        List<VehicleResponseDto> vehiclesResponseDto;
        List<Vehicle> vehicleList;
        ModelMapper modelMapper = new ModelMapper();

        Pageable pageable = PageRequest.of(offset.orElse(DEFAULT_VALUE_OFFSET), limit.orElse(DEFAULT_VALUE_LIMIT));

        try {
            vehicleList = vehicleRepository.search(make.orElse(""), model.orElse(""), category.orElse(""), year.orElse(""), pageable);
            logger.info("Number of vehicles retrieved: " + String.valueOf(vehicleList.size()));
            vehiclesResponseDto = vehicleList
                .stream()
                .map(vehicle -> modelMapper.map(vehicle, VehicleResponseDto.class))
                .collect(Collectors.toList());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return vehiclesResponseDto;
    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, GenericException.class, DriverNotFoundException.class})
    public AddVehicleResponseDto addVehicle(String driverIdentification, AddVehicleRequestDto addVehicleRequestDto, ServiceContext serviceContext) throws DriverForbiddenException, GenericException, DriverNotFoundException {

        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        ModelMapper modelMapper = new ModelMapper();
        
        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user that performed the request permission to change driver
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        DriverVehicle driverVehicle = new DriverVehicle();
        try {
            driverVehicle = modelMapper.map(addVehicleRequestDto, DriverVehicle.class);
            driverVehicle.setVehicleIdentification(UUID.randomUUID().toString());
            driverVehicle.setDriverIdentification(driverIdentification);
            driverVehicle.setVehicleActive(false);
            driverVehicleRepository.save(driverVehicle);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return new AddVehicleResponseDto(driverVehicle.getVehicleIdentification());

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, GenericException.class, VehicleNotFoundException.class, DriverNotFoundException.class})
    public void removeVehicle(String driverIdentification, String vehicleIdentification, ServiceContext serviceContext) throws DriverForbiddenException, GenericException, VehicleNotFoundException, DriverNotFoundException {

        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);

        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user that performed the request permission to change driver
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        Optional<DriverVehicle> driverVehicle = driverVehicleRepository.findByVehicleIdentification(vehicleIdentification);
        
        // validate that vehicle exists
        if (!driverVehicle.isPresent()) {
            throw new VehicleNotFoundException(vehicleIdentification);
        }

        // validate that user that performed the request has permission to change vehicle
        if (!isAccountLinkedToVehicleOrAdmin(serviceContext, vehicleIdentification)) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        try {
            driverVehicleRepository.delete(driverVehicle.get());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    public DriverVehicleResponseDto getVehicle(String vehicleIdentification, ServiceContext serviceContext) throws DriverForbiddenException, GenericException, VehicleNotFoundException {
        
        Optional<DriverVehicle> driverVehicle = driverVehicleRepository.findByVehicleIdentification(vehicleIdentification);
        ModelMapper modelMapper = new ModelMapper();
        DriverVehicleResponseDto vehicleDto = new DriverVehicleResponseDto();
        
        // validate that vehicle exists
        if (!driverVehicle.isPresent()) {
            throw new VehicleNotFoundException(vehicleIdentification);
        }

        // validate that user that performed the request has permission to change vehicle
        if (!isAccountLinkedToVehicleOrAdmin(serviceContext, vehicleIdentification)) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        try {
            vehicleDto = modelMapper.map(driverVehicle, DriverVehicleResponseDto.class);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        // get vehicle registration
        Optional<DocumentVehicleLicenseRegistration> documentVehicleLicenseRegistration = documentVehicleLicenseRegistrationRepository.findByVehicleIdentification(vehicleIdentification);
        String vehicleLicenseRegistrationPath = VEH_LICENSE_REG_S3_PATH + vehicleIdentification + "/";
        if (documentVehicleLicenseRegistration.isPresent()) {
            
            VehicleRegistrationResponseDto vehicleRegistrationResponseDto = modelMapper.map(documentVehicleLicenseRegistration.get(),
                                                                                            VehicleRegistrationResponseDto.class);
            // get documents from AWS - document back
            if (null != documentVehicleLicenseRegistration.get().getDocumentBackFileId() && !documentVehicleLicenseRegistration.get().getDocumentBackFileId().isEmpty()) {
                vehicleRegistrationResponseDto.setDocumentBack(awsS3Client.downloadFile(vehicleLicenseRegistrationPath + documentVehicleLicenseRegistration.get().getDocumentBackFileId()));
            }

            // get documents from AWS - document front
            if (null != documentVehicleLicenseRegistration.get().getDocumentFrontFileId() && !documentVehicleLicenseRegistration.get().getDocumentFrontFileId().isEmpty()) {
                vehicleRegistrationResponseDto.setDocumentFront(awsS3Client.downloadFile(vehicleLicenseRegistrationPath + documentVehicleLicenseRegistration.get().getDocumentFrontFileId()));
            }

            vehicleDto.setVehicleRegistration(vehicleRegistrationResponseDto);
        }

        return vehicleDto;
    }

    @Override
    public List<DriverVehicleResponseDto> getVehicles(String driverIdentification, ServiceContext serviceContext) throws DriverForbiddenException, GenericException, DriverNotFoundException {
        
        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverIdentification);
        List<DriverVehicleResponseDto> vehicleListResponseDto;
        List<DriverVehicle> vehicleList;
        ModelMapper modelMapper = new ModelMapper();
        
        // validate that driver exists
        if (!driver.isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user that performed the request permission to change driver
        if (!isAccountLinkedToDriverOrAdmin(serviceContext, driver.get().getUserIdentification())) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        try {
            vehicleList = driverVehicleRepository.findByDriverIdentification(driverIdentification);
            vehicleListResponseDto = vehicleList
                .stream()
                .map(vehicle -> modelMapper.map(vehicle, DriverVehicleResponseDto.class))
                .collect(Collectors.toList());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return vehicleListResponseDto;
    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, GenericException.class, VehicleNotFoundException.class})
    public void updateVehicle(String vehicleIdentification, AddVehicleRequestDto addVehicleRequestDto, ServiceContext serviceContext) throws DriverForbiddenException, GenericException, VehicleNotFoundException {

        Optional<DriverVehicle> driverVehicle = driverVehicleRepository.findByVehicleIdentification(vehicleIdentification);

        // validate that vehicle exists
        if (!driverVehicle.isPresent()) {
            throw new VehicleNotFoundException(vehicleIdentification);
        }

        // validate that user that performed the request has permission to change vehicle
        if (!isAccountLinkedToVehicleOrAdmin(serviceContext, vehicleIdentification)) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        try {
            DriverVehicle newVehicle = driverVehicle.get();
            // make
            if (null != addVehicleRequestDto.getMake() && !addVehicleRequestDto.getMake().isEmpty()){
                newVehicle.setMake(addVehicleRequestDto.getMake());
            }
            // model
            if (null != addVehicleRequestDto.getModel() && !addVehicleRequestDto.getModel().isEmpty()){
                newVehicle.setModel(addVehicleRequestDto.getModel());
            }
            // category
            if (null != addVehicleRequestDto.getCategory() && !addVehicleRequestDto.getCategory().isEmpty()){
                newVehicle.setCategory(addVehicleRequestDto.getCategory());
            }
            // year
            if (null != addVehicleRequestDto.getYear() && !addVehicleRequestDto.getYear().toString().isEmpty()) {
                newVehicle.setYear(addVehicleRequestDto.getYear());
            }
            // vehicle type
            if (null != addVehicleRequestDto.getVehicleType() && !addVehicleRequestDto.getVehicleType().toString().isEmpty()){
                newVehicle.setVehicleType(addVehicleRequestDto.getVehicleType().toString());
            }
            // save in database
            driverVehicleRepository.save(newVehicle);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, GenericException.class, VehicleNotFoundException.class, VehicleAlreadyActiveException.class})
    public void updateVehicleStatus(String vehicleIdentification, UpdateVehicleStatusRequestDto updateVehicleStatusRequestDto, ServiceContext serviceContext) throws EntityNotValidatedException, DriverForbiddenException, GenericException, VehicleNotFoundException, VehicleAlreadyActiveException {

        Boolean vehicleActive = updateVehicleStatusRequestDto.isVehicleActive();

        Optional<DriverVehicle> driverVehicle = driverVehicleRepository.findByVehicleIdentification(vehicleIdentification);
    
        // validate that vehicle exists
        if (!driverVehicle.isPresent()) {
            throw new VehicleNotFoundException(vehicleIdentification);
        }

        // validate that user that performed the request has permission to change vehicle
        if (!isAccountLinkedToVehicleOrAdmin(serviceContext, vehicleIdentification)) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        // validate that vehicle is validated in the backoffice
        if (!driverVehicle.get().isEntityValidated()) {
            throw new EntityNotValidatedException();
        }

        String driverIdentification = driverVehicle.get().getDriverIdentification();

        // validate that vehicle is not already active
        if (vehicleActive && driverVehicleRepository.findByDriverIdentificationAndVehicleActive(driverIdentification, true).isPresent()) {
            throw new VehicleAlreadyActiveException(driverIdentification);
        }

        try {
            DriverVehicle updatedVehicle = driverVehicle.get();
            updatedVehicle.setVehicleActive(vehicleActive);
            driverVehicleRepository.save(updatedVehicle);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, GenericException.class, VehicleNotFoundException.class, VehicleAlreadyActiveException.class})
    public void validateVehicle(String vehicleIdentification, ValidateVehicleRequestDto validateVehicleRequestDto, ServiceContext serviceContext) throws DriverForbiddenException, GenericException, VehicleNotFoundException {

        Optional<DriverVehicle> driverVehicle = driverVehicleRepository.findByVehicleIdentification(vehicleIdentification);
    
        // validate that vehicle exists
        if (!driverVehicle.isPresent()) {
            throw new VehicleNotFoundException(vehicleIdentification);
        }

        // validate that user that performed the request has permission to change vehicle
        if (!Validator.isAdmin(serviceContext)) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        try {
            DriverVehicle updatedVehicle = driverVehicle.get();
            if (validateVehicleRequestDto.isValidated()) {
                updatedVehicle.setEntityValidated(true);
                // validate if another vehicle is already active
                if (!driverVehicleRepository.findByDriverIdentificationAndVehicleActive(driverVehicle.get().getDriverIdentification(), true).isPresent()) {
                    updatedVehicle.setVehicleActive(true);
                }
            } else {
                updatedVehicle.setEntityValidated(false);
                updatedVehicle.setVehicleActive(false);
            }
            driverVehicleRepository.save(updatedVehicle);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, ExternalServerException.class, VehicleNotFoundException.class, GenericException.class})
    public void upsertVehicleRegistration(String vehicleIdentification, MultipartFile documentFront, MultipartFile documentBack, VehicleRegistrationUpsertRequestDto vehicleRegistrationUpsertRequestDto, ServiceContext serviceContext) throws DriverForbiddenException, ExternalServerException, VehicleNotFoundException, GenericException {

        ModelMapper modelMapper = new ModelMapper();
        Optional<DriverVehicle> driverVehicle = driverVehicleRepository.findByVehicleIdentification(vehicleIdentification);

        Optional<DocumentVehicleLicenseRegistration> documentVehicleLicenseRegistration;
        DocumentVehicleLicenseRegistration newDocumentVehicleLicenseRegistration = new DocumentVehicleLicenseRegistration();

        // validate that vehicle exists
        if (!driverVehicle.isPresent()) {
            throw new VehicleNotFoundException(vehicleIdentification);
        }

        // validate that user that performed the request has permission to change vehicle
        if (!isAccountLinkedToVehicleOrAdmin(serviceContext, vehicleIdentification)) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        String documentPath = VEH_LICENSE_REG_S3_PATH + vehicleIdentification + "/";

        try {
            // see if there is already a vehicle license registration for this vehicle
            documentVehicleLicenseRegistration = documentVehicleLicenseRegistrationRepository.findByVehicleIdentification(vehicleIdentification);
            // if exists, update
            if (documentVehicleLicenseRegistration.isPresent()) {
                newDocumentVehicleLicenseRegistration = documentVehicleLicenseRegistration.get();

                // document front file id
                if (null != documentFront && !documentFront.isEmpty()) {
                    String documentFrontFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentFront.getOriginalFilename());

                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentFront, documentPath + documentFrontFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentVehicleLicenseRegistration.setDocumentFrontFileId(documentFrontFileId);
                }
                
                // document back file id
                if (null != documentBack && !documentBack.isEmpty()) {
                    String documentBackFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentBack.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentBack, documentPath + documentBackFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentVehicleLicenseRegistration.setDocumentFrontFileId(documentBackFileId);
                }

                // set validation field
                if (vehicleRegistrationUpsertRequestDto.isValidated() != vehicleRegistrationUpsertRequestDto.isValidated()) {
                    newDocumentVehicleLicenseRegistration.setValidated(!vehicleRegistrationUpsertRequestDto.getValidated());
                }

            // if it does not exist, insert
            } else {

                // save new document
                newDocumentVehicleLicenseRegistration = modelMapper.map(vehicleRegistrationUpsertRequestDto, DocumentVehicleLicenseRegistration.class);
                newDocumentVehicleLicenseRegistration.setVehicleLicenseRegIdentification(UUID.randomUUID().toString());
                newDocumentVehicleLicenseRegistration.setVehicleIdentification(vehicleIdentification);

                // document front file id
                if (null != documentFront && !(documentFront.isEmpty())) {
                    String documentFrontFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentFront.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentFront, documentPath + documentFrontFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentVehicleLicenseRegistration.setDocumentFrontFileId(documentFrontFileId);
                }
                
                // document back file id
                if (null != documentBack && !(documentBack.isEmpty())) {
                    String documentBackFileId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(documentBack.getOriginalFilename());
                    // upload to AWS
                    if (!awsS3Client.uploadFile(documentBack, documentPath + documentBackFileId)){
                        throw new ExternalServerException("Error uploading file to AWS S3");
                    }
                    newDocumentVehicleLicenseRegistration.setDocumentFrontFileId(documentBackFileId);
                }
                documentVehicleLicenseRegistrationRepository.save(newDocumentVehicleLicenseRegistration);
            }
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DriverForbiddenException.class, DocumentNotFoundException.class, ExternalServerException.class, VehicleNotFoundException.class, GenericException.class})
    public void deleteVehicleRegistration(String vehicleIdentification, ServiceContext serviceContext) throws DriverForbiddenException, DocumentNotFoundException, ExternalServerException, VehicleNotFoundException, GenericException {

        Optional<DriverVehicle> driverVehicle = driverVehicleRepository.findByVehicleIdentification(vehicleIdentification);
        Optional<DocumentVehicleLicenseRegistration> documentVehicleLicenseRegistration = documentVehicleLicenseRegistrationRepository.findByVehicleIdentification(vehicleIdentification);

        // validate that vehicle exists
        if (!driverVehicle.isPresent()) {
            throw new VehicleNotFoundException(vehicleIdentification);
        }

        // validate that user that performed the request has permission to change vehicle
        if (!isAccountLinkedToVehicleOrAdmin(serviceContext, vehicleIdentification)) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        // validate that the document exists
        if (!documentVehicleLicenseRegistration.isPresent()) {
            throw new DocumentNotFoundException("Vehicle License Registration");
        }

        try {
            documentVehicleLicenseRegistrationRepository.delete(documentVehicleLicenseRegistration.get());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    // validate if user that performed the request is linked to the vehicle or is admin
    private boolean isAccountLinkedToVehicleOrAdmin(ServiceContext serviceContext, String vehicleIdentification) {

        Optional<DriverVehicle> driverVehicle = driverVehicleRepository.findByVehicleIdentification(vehicleIdentification);
        Optional<Driver> driver = driverRepository.findByDriverIdentification(driverVehicle.get().getDriverIdentification());

        if (driver.isPresent() && !serviceContext.getUserId().equals(driver.get().getUserIdentification()) && !Validator.isAdmin(serviceContext)) {
            return false;
        } else {
            return true;
        }

    }

    // validate if user that performed the request is linked to driver or is admin
    private boolean isAccountLinkedToDriverOrAdmin(ServiceContext serviceContext, String userIdentification) {
        if (!serviceContext.getUserId().equals(userIdentification) && !Validator.isAdmin(serviceContext)) {
            return false;
        } else {
            return true;
        }
    }

}