package com.lastmile.driverservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.lastmile.driverservice.service.DriverService;
import com.lastmile.driverservice.service.exception.DocumentNotFoundException;
import com.lastmile.driverservice.service.exception.DriverAlreadyExistsException;
import com.lastmile.driverservice.service.exception.DriverForbiddenException;
import com.lastmile.driverservice.service.exception.DriverNotFoundException;
import com.lastmile.driverservice.service.exception.DriverStatusInvalidException;
import com.lastmile.driverservice.service.exception.EntityNotValidatedException;
import com.lastmile.driverservice.service.exception.ExternalServerException;
import com.lastmile.driverservice.service.exception.FeignCommunicationException;
import com.lastmile.driverservice.service.exception.FiscalEntityNotFoundException;
import com.lastmile.driverservice.service.exception.GenericException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.logs.interceptor.PreHandleValidation;
import com.lastmile.utils.models.response.ErrorResponse;
import com.lastmile.utils.models.response.SuccessResponse;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class DriverController {

    private final DriverService driverService;
    private final CustomLogging logger;

    public DriverController(DriverService driverService, CustomLogging logger) {
        this.driverService = driverService;
        this.logger = logger;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewDriver(HttpServletRequest httpRequest,
                                             @Valid @RequestBody CreateDriverRequestDto driverDto) throws FeignCommunicationException, DriverForbiddenException, DriverAlreadyExistsException, FiscalEntityNotFoundException {

        CreateDriverResponseDto driverResponse = new CreateDriverResponseDto();
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + driverDto.toString(), httpRequest);

        try {
            driverResponse = driverService.create(driverDto, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (DriverAlreadyExistsException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Driver already exists for this user", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Unexpected error creating a new driver.", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        logger.info("response body: " + driverResponse.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Driver created successfully", driverResponse), HttpStatus.CREATED);

    }

    /*
     * Endpoint to get all registered drivers - protected for ADMINS only
     */
    @GetMapping("")
    public ResponseEntity<?> getDrivers(HttpServletRequest httpRequest,
                                        @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                        @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                        @RequestParam(value = "status", required = false) Optional<String> status,
                                        @RequestParam(value = "userIdentification", required = false) Optional<String> userIdentification,
                                        @RequestParam(value = "driverIdentification", required = false) Optional<String> driverIdentification) throws DriverForbiddenException, DriverNotFoundException, GenericException {

        List<DriverDto> drivers;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        // if no filters, validate that user is admin and that communication is not internal
        if (!userIdentification.isPresent() && !driverIdentification.isPresent() && !PreHandleValidation.hasAdminAuthority(httpRequest) && !PreHandleValidation.isFeignRequest(httpRequest)) {
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action"), HttpStatus.FORBIDDEN);
        }

        try {
            drivers = driverService.getDrivers(limit, offset, status, userIdentification, driverIdentification, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving drivers", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Drivers retrieved successfully", drivers), HttpStatus.OK);

    }

    /*
     * Endpoint to get drivers by location
     */
    @GetMapping("/location")
    public ResponseEntity<?> getDriversByLocation(HttpServletRequest httpRequest,
                                                  @RequestParam(name = "latitude") Optional<Float> latitude,
                                                  @RequestParam(name = "longitude") Optional<Float> longitude,
                                                  @RequestParam(name = "radius") Optional<Integer> radius,
                                                  @RequestParam(name = "limit") Optional<Integer> limitParam,
                                                  @RequestParam(name = "status") Optional<String> status) throws  GenericException {

        List<DriverDto> nearestDrivers;

        if (!PreHandleValidation.hasAdminAuthority(httpRequest) && !PreHandleValidation.isFeignRequest(httpRequest)) {
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action"), HttpStatus.FORBIDDEN);
        }

        try {
            nearestDrivers = driverService.getDriversByLocation(latitude, longitude, radius, limitParam, status);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving drivers", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Drivers retrieved successfully", nearestDrivers), HttpStatus.OK);

    }

    /*
     * Endpoint to get driver fiscal entity details by driver identification
     */
    @GetMapping("/{driverIdentification}/fiscal-entity")
    public ResponseEntity<?> getDriverFiscalEntity(HttpServletRequest httpRequest,
                                                   @PathVariable(value = "driverIdentification") String driverIdentification) throws FiscalEntityNotFoundException, DriverNotFoundException, GenericException, DriverForbiddenException {

        FiscalEntityResponseDto fiscalEntity;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            fiscalEntity = driverService.getDriverFiscalEntity(driverIdentification, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (DriverNotFoundException | FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving driver", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + fiscalEntity.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver retrieved successfully", fiscalEntity), HttpStatus.OK);

    }

    /*
     * Endpoint to get driver details by driver identification
     */
    @GetMapping("/{driverIdentification}")
    public ResponseEntity<?> getDriver(HttpServletRequest httpRequest,
                                       @PathVariable(value = "driverIdentification") String driverIdentification,
                                       @RequestParam(defaultValue = "false") Boolean includeUserProfile) throws DriverNotFoundException, GenericException, DriverForbiddenException {

        DriverDto driver;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            driver = driverService.getDriver(driverIdentification, includeUserProfile, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving driver", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver retrieved successfully", driver), HttpStatus.OK);

    }

    /*
     * Endpoint to delete driver by driver identification - protected for ADMINS only
     */
    @DeleteMapping("/{driverIdentification}")
    public ResponseEntity<?> deleteDriver(HttpServletRequest httpRequest,
                                          @PathVariable(value = "driverIdentification") String driverIdentification) throws DriverForbiddenException, DriverNotFoundException, GenericException {


        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            driverService.deleteDriver(driverIdentification, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting driver", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver deleted successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to update driver
     */
    @PutMapping("/{driverIdentification}")
    public ResponseEntity<?> updateDriver(HttpServletRequest httpRequest,
                                          @PathVariable(value = "driverIdentification") String driverIdentification,
                                          @Valid @RequestBody DriverUpdateRequestDto driverUpdateRequestDto) throws DriverStatusInvalidException, EntityNotValidatedException, FiscalEntityNotFoundException, DriverForbiddenException, DriverNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + driverUpdateRequestDto.toString(), httpRequest);

        try {
            driverService.updateDriver(driverIdentification, driverUpdateRequestDto, serviceContext);
        } catch (DriverStatusInvalidException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Invalid status transition - please ensure driver has an active fiscal entity and an active vehicle assigned", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (EntityNotValidatedException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Entity is not validated", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating driver", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to update driver location by driver identification
     */
    @PatchMapping("/{driverIdentification}/location")
    public ResponseEntity<?> updateDriverLocation(HttpServletRequest httpRequest,
                                                  @PathVariable(value = "driverIdentification") String driverIdentification,
                                                  @Valid @RequestBody LocationDto location) throws DriverForbiddenException, DriverNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + location.toString(), httpRequest);

        try {
            driverService.updateDriverLocation(driverIdentification, location, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating driver location", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver location updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to validate driver by driver identification
     */
    @PatchMapping("/{driverIdentification}/validate")
    public ResponseEntity<?> patchDriverValidated(HttpServletRequest httpRequest,
                                                  @PathVariable(value = "driverIdentification") String driverIdentification,
                                                  @Valid @RequestBody PatchDriverStatusValidatedDto patchDriverStatusValidatedDto) throws DriverStatusInvalidException, DriverForbiddenException, DriverNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + patchDriverStatusValidatedDto.toString(), httpRequest);

        try {
            driverService.patchDriverValidated(driverIdentification, patchDriverStatusValidatedDto, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating driver validated field", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver validated field updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to upsert new driver license
     */
    @RequestMapping(value = "/{driverIdentification}/driver-license", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upsertDriverLicense(HttpServletRequest httpRequest,
                                                 @PathVariable(value = "driverIdentification") String driverIdentification,
                                                 @RequestPart(value = "documentFront", required = false) MultipartFile documentFront,
                                                 @RequestPart(value = "documentBack", required = false) MultipartFile documentBack,
                                                 @RequestPart(value = "requestBody", required = false) String driverLicenseUpsertRequestString) throws ExternalServerException, DriverNotFoundException, GenericException {

        ObjectMapper objectMapper = new ObjectMapper();
        DriverLicenseUpsertRequestDto driverLicenseUpsertRequestDto = new DriverLicenseUpsertRequestDto();
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            if (null != driverLicenseUpsertRequestString && !driverLicenseUpsertRequestString.isEmpty()) {
                driverLicenseUpsertRequestDto = objectMapper.readValue(driverLicenseUpsertRequestString, DriverLicenseUpsertRequestDto.class);
            }
        } catch (Exception ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Could not map request body to DTO", ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            driverService.upsertDriverLicense(driverIdentification, documentFront, documentBack, driverLicenseUpsertRequestDto, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (ExternalServerException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error uploading file to AWS S3 Bucket", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error upserting driver license", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to delete driver license
     */
    @DeleteMapping("/{driverIdentification}/driver-license")
    public ResponseEntity<?> deleteDriverLicense(HttpServletRequest httpRequest,
                                                 @PathVariable(value = "driverIdentification") String driverIdentification) throws DocumentNotFoundException, ExternalServerException, DriverNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            driverService.deleteDriverLicense(driverIdentification, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (ExternalServerException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error deleting file from AWS S3 Bucket", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting driver license", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver license removed successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to upsert new identification card
     */
    @RequestMapping(value = "/{driverIdentification}/identification-card", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upsertIdentificationCard(HttpServletRequest httpRequest,
                                                      @PathVariable(value = "driverIdentification") String driverIdentification,
                                                      @RequestPart(value = "documentFront", required = false) MultipartFile documentFront,
                                                      @RequestPart(value = "documentBack", required = false) MultipartFile documentBack,
                                                      @RequestPart(value = "requestBody", required = false) String identificationCardUpsertRequestString) throws ExternalServerException, DriverNotFoundException, GenericException {

        ObjectMapper objectMapper = new ObjectMapper();
        IdentificationCardUpsertRequestDto identificationCardUpsertRequestDto = new IdentificationCardUpsertRequestDto();

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            if (null != identificationCardUpsertRequestString && !identificationCardUpsertRequestString.isEmpty()) {
                identificationCardUpsertRequestDto = objectMapper.readValue(identificationCardUpsertRequestString, IdentificationCardUpsertRequestDto.class);
            }
        } catch (Exception ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Could not map request body to DTO", ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            driverService.upsertIdentificationCard(driverIdentification, documentFront, documentBack, identificationCardUpsertRequestDto, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (ExternalServerException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error uploading file to AWS S3 Bucket", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error upserting identification card", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to delete identification card
     */
    @DeleteMapping("/{driverIdentification}/identification-card")
    public ResponseEntity<?> deleteIdentificationCard(HttpServletRequest httpRequest,
                                                      @PathVariable(value = "driverIdentification") String driverIdentification) throws DriverForbiddenException, DocumentNotFoundException, ExternalServerException, DriverNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            driverService.deleteIdentificationCard(driverIdentification, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (ExternalServerException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error uploading file to AWS S3 Bucket", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting identification card", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Identification card removed successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to upsert new criminal record
     */
    @RequestMapping(value = "/{driverIdentification}/criminal-record", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upsertCriminalRecord(HttpServletRequest httpRequest,
                                                  @PathVariable(value = "driverIdentification") String driverIdentification,
                                                  @RequestPart(value = "documentFront", required = false) MultipartFile documentFront,
                                                  @RequestPart(value = "documentBack", required = false) MultipartFile documentBack,
                                                  @RequestPart(value = "requestBody", required = false) String criminalRecordUpsertRequestString) throws ExternalServerException, DriverNotFoundException, GenericException {

        ObjectMapper objectMapper = new ObjectMapper();
        CriminalRecordUpsertRequestDto criminalRecordUpsertRequestDto = new CriminalRecordUpsertRequestDto();
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            if (null != criminalRecordUpsertRequestString && !criminalRecordUpsertRequestString.isEmpty()) {
                criminalRecordUpsertRequestDto = objectMapper.readValue(criminalRecordUpsertRequestString, CriminalRecordUpsertRequestDto.class);
            }
        } catch (Exception ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Could not map request body to DTO", ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            driverService.upsertCriminalRecord(driverIdentification, documentFront, documentBack, criminalRecordUpsertRequestDto, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (ExternalServerException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error uploading file to AWS S3 Bucket", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error upserting criminal record", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Criminal record updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to delete criminal record
     */
    @DeleteMapping("/{driverIdentification}/criminal-record")
    public ResponseEntity<?> deleteCriminalRecord(HttpServletRequest httpRequest,
                                                  @PathVariable(value = "driverIdentification") String driverIdentification) throws DriverForbiddenException, DocumentNotFoundException, ExternalServerException, DriverNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            driverService.deleteCriminalRecord(driverIdentification, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (ExternalServerException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error uploading file to AWS S3 Bucket", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting criminal record", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Criminal record deleted successfully"), HttpStatus.OK);

    }

}