package com.lastmile.driverservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.lastmile.driverservice.dto.addresses.CreateAddressRequestDto;
import com.lastmile.driverservice.dto.addresses.CreateAddressResponseDto;
import com.lastmile.driverservice.dto.addresses.GetAddressResponseDto;
import com.lastmile.driverservice.dto.addresses.UpdateAddressRequestDto;
import com.lastmile.driverservice.dto.drivers.DriverDto;
import com.lastmile.driverservice.dto.fiscalentities.AddRemoveDriverToFiscalEntityRequestDto;
import com.lastmile.driverservice.dto.fiscalentities.AddUserToFiscalEntityRequestDto;
import com.lastmile.driverservice.dto.fiscalentities.CreateFiscalEntityRequestDto;
import com.lastmile.driverservice.dto.fiscalentities.CreateFiscalEntityResponseDto;
import com.lastmile.driverservice.dto.fiscalentities.FiscalEntityResponseDto;
import com.lastmile.driverservice.dto.fiscalentities.PatchFiscalEntityValidatedRequestDto;
import com.lastmile.driverservice.dto.fiscalentities.UpdateFiscalEntityRequestDto;
import com.lastmile.driverservice.dto.payments.GetOutPaymentResponseDto;
import com.lastmile.driverservice.service.FiscalEntityService;
import com.lastmile.driverservice.service.exception.AddressNotFoundException;
import com.lastmile.driverservice.service.exception.DriverNotFoundException;
import com.lastmile.driverservice.service.exception.EntityNotValidatedException;
import com.lastmile.driverservice.service.exception.FeignCommunicationException;
import com.lastmile.driverservice.service.exception.FiscalEntityAlreadyExistsException;
import com.lastmile.driverservice.service.exception.FiscalEntityHasDriversDeleteException;
import com.lastmile.driverservice.service.exception.FiscalEntityNotActiveException;
import com.lastmile.driverservice.service.exception.FiscalEntityNotFoundException;
import com.lastmile.driverservice.service.exception.GenericException;
import com.lastmile.driverservice.service.exception.LinkNotFoundException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.models.response.ErrorResponse;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/fiscal-entities")
public class FiscalEntityController {

    private final FiscalEntityService fiscalEntityService;
    private final CustomLogging logger;

    public FiscalEntityController(FiscalEntityService fiscalEntityService, CustomLogging logger) {
        this.fiscalEntityService = fiscalEntityService;
        this.logger = logger;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewFiscalEntity(HttpServletRequest httpRequest,
                                                   @Valid @RequestBody CreateFiscalEntityRequestDto fiscalEntity) throws GenericException, FiscalEntityAlreadyExistsException, FeignCommunicationException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        CreateFiscalEntityResponseDto createFiscalEntityResponseDto = new CreateFiscalEntityResponseDto();
        logger.info("request body: " + fiscalEntity.toString(), httpRequest);

        try {
            createFiscalEntityResponseDto = fiscalEntityService.createNewFiscalEntity(fiscalEntity, serviceContext);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (FiscalEntityAlreadyExistsException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Fiscal entity already exists", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Error creating fiscal entity", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        logger.info("response body: " + createFiscalEntityResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Fiscal entity created successfully", createFiscalEntityResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to get all fiscal entities - protected for ADMINS only
     */
    @GetMapping("/")
    public ResponseEntity<?> getFiscalEntities(HttpServletRequest httpRequest,
                                               @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                               @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                               @RequestParam(value = "status", required = false) Optional<String> status,
                                               @RequestParam(value = "userIdentification", required = false) Optional<String> userIdentification) throws LinkNotFoundException, GenericException {

        List<FiscalEntityResponseDto> fiscalEntities;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            fiscalEntities = fiscalEntityService.getFiscalEntities(limit, offset, status, userIdentification, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving fiscal entities", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Fiscal entities retrieved successfully", fiscalEntities), HttpStatus.OK);

    }

    /*
     * Endpoint to get fiscal entity details by fiscal entity identification
     */
    @GetMapping("/{fiscalEntityIdentification}")
    public ResponseEntity<?> getFiscalEntityByFiscalId(HttpServletRequest httpRequest, 
                                                       @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification) throws LinkNotFoundException, GenericException {

        FiscalEntityResponseDto fiscalEntity;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            fiscalEntity = fiscalEntityService.getFiscalEntity(fiscalEntityIdentification, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unexpected error retrieving fiscal entity", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        logger.info("response body: " + fiscalEntity.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Fiscal entity retrieved successfully", fiscalEntity), HttpStatus.OK);

    }

    /*
     * Endpoint to get all fiscal entity drivers
     */
    @GetMapping("/{fiscalEntityIdentification}/drivers")
    public ResponseEntity<?> getFiscalEntityDrivers(HttpServletRequest httpRequest,
                                                    @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                                    @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                                    @RequestParam(value = "driverIdentification", required = false) Optional<String> driverIdentification,
                                                    @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification) throws FiscalEntityNotFoundException, LinkNotFoundException, GenericException {

        List<DriverDto> drivers;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            drivers = fiscalEntityService.getFiscalEntityDrivers(fiscalEntityIdentification, limit, offset, driverIdentification, serviceContext);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        }  catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving fiscal entity drivers", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Fiscal entity drivers retrieved successfully", drivers), HttpStatus.OK);

    }

    /*
     * Endpoint to delete fiscal entity by fiscal id
     */
    @DeleteMapping("/{fiscalEntityIdentification}")
    public ResponseEntity<?> deleteFiscalEntity(HttpServletRequest httpRequest, 
                                                @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification) throws LinkNotFoundException, FiscalEntityHasDriversDeleteException, FiscalEntityNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            fiscalEntityService.deleteFiscalEntity(fiscalEntityIdentification, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FiscalEntityHasDriversDeleteException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Unable to delete fiscal entity", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unexpected error deleting fiscal entity", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Fiscal entity deleted successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to update fiscal entity
     */
    @PutMapping("/{fiscalEntityIdentification}")
    public ResponseEntity<?> updateFiscalEntity(HttpServletRequest httpRequest, 
                                                @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification,
                                                @Valid @RequestBody UpdateFiscalEntityRequestDto fiscalEntity) throws AddressNotFoundException, FeignCommunicationException, LinkNotFoundException, FiscalEntityNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + fiscalEntity.toString(), httpRequest);

        try {
            fiscalEntityService.updateFiscalEntity(fiscalEntityIdentification, fiscalEntity, serviceContext);
        } catch (EntityNotValidatedException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Fiscal entity is not validated", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Invalid address", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unexpected error deleting fiscal entity", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Fiscal entity updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to validate fiscal entity
     */
    @PatchMapping("/{fiscalEntityIdentification}/validate")
    public ResponseEntity<?> patchFiscalEntityValidated(HttpServletRequest httpRequest, 
                                                        @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification,
                                                        @Valid @RequestBody PatchFiscalEntityValidatedRequestDto fiscalEntityValidatedRequestDto) throws FiscalEntityNotFoundException, LinkNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + fiscalEntityValidatedRequestDto.toString(), httpRequest);

        try {
            fiscalEntityService.patchFiscalEntityValidated(fiscalEntityIdentification, fiscalEntityValidatedRequestDto, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unexpected error deleting fiscal entity", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Fiscal entity updated successfully"), HttpStatus.OK);

    }


    /*
     * Endpoint to add a driver to a fiscal entity
     */
    @PutMapping("/{fiscalEntityIdentification}/drivers")
    public ResponseEntity<?> addDriverToFiscalEntity(HttpServletRequest httpRequest, 
                                                     @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification,
                                                     @Valid @RequestBody AddRemoveDriverToFiscalEntityRequestDto addRemoveDriverToFiscalEntityRequestDto) throws FiscalEntityNotActiveException, LinkNotFoundException, DriverNotFoundException, FiscalEntityNotFoundException, DriverNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + addRemoveDriverToFiscalEntityRequestDto.toString(), httpRequest);

        try {
            fiscalEntityService.addDriverToFiscalEntity(fiscalEntityIdentification, addRemoveDriverToFiscalEntityRequestDto.getDriverIdentification(), serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unexpected error adding driver to fiscal entity", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (FiscalEntityNotActiveException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "It is only possible to add drivers to fiscal entity once the last is validated", ex.getMessage()),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver added to fiscal entity successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to remove a driver from a fiscal entity
     */
    @DeleteMapping("/{fiscalEntityIdentification}/drivers")
    public ResponseEntity<?> removeDriverFromFiscalEntity(HttpServletRequest httpRequest, 
                                                          @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification,
                                                          @Valid @RequestBody AddRemoveDriverToFiscalEntityRequestDto addRemoveDriverToFiscalEntityRequestDto) throws LinkNotFoundException, DriverNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + addRemoveDriverToFiscalEntityRequestDto.toString(), httpRequest);

        try {
            fiscalEntityService.removeDriverFromFiscalEntity(fiscalEntityIdentification, addRemoveDriverToFiscalEntityRequestDto.getDriverIdentification(), serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unexpected error removing driver from fiscal entity", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver removed from fiscal entity successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to add a user account to a fiscal entity
     */
    @PutMapping("{fiscalEntityIdentification}/users")
    public ResponseEntity<?> addUserAccountToFiscalEntity(HttpServletRequest httpRequest,
                                                          @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification,
                                                          @Valid @RequestBody AddUserToFiscalEntityRequestDto addUserToFiscalEntityDto) throws LinkNotFoundException, FiscalEntityNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + addUserToFiscalEntityDto.toString(), httpRequest);

        try {
            fiscalEntityService.addLinkUserToFiscalEntity(fiscalEntityIdentification, addUserToFiscalEntityDto, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unexpected error linking account to fiscal entity", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Account linked to fiscal entity successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to add a user account to a fiscal entity
     */
    @DeleteMapping("{fiscalEntityIdentification}/users")
    public ResponseEntity<?> removeUserAccountFromFiscalEntity(HttpServletRequest httpRequest,
                                                               @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification,
                                                               @Valid @RequestBody AddUserToFiscalEntityRequestDto addUserToFiscalEntityDto) throws LinkNotFoundException, FiscalEntityNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + addUserToFiscalEntityDto.toString(), httpRequest);

        try {
            fiscalEntityService.removeLinkUserToFiscalEntity(fiscalEntityIdentification, addUserToFiscalEntityDto, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unexpected error linking account to fiscal entity", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Account linked to fiscal entity successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to create a fiscal entity address
     */
    @PostMapping("/{fiscalEntityIdentification}/addresses")
    public ResponseEntity<?> createFiscalEntityAddress(HttpServletRequest httpRequest,
                                                       @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification,
                                                       @Valid @RequestBody CreateAddressRequestDto addressDto) throws LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException {

        CreateAddressResponseDto createAddressResponseDto = new CreateAddressResponseDto();
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + addressDto.toString(), httpRequest);

        try {
            createAddressResponseDto = fiscalEntityService.createFiscalEntityAddress(fiscalEntityIdentification, addressDto, serviceContext);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Address created successfully", createAddressResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to update a fiscal entity address
     */
    @PutMapping("/{fiscalEntityIdentification}/addresses/{addressIdentification}")
    public ResponseEntity<?> updateFiscalEntityAddress(HttpServletRequest httpRequest,
                                                       @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification,
                                                       @PathVariable(value = "addressIdentification") String addressIdentification,
                                                       @Valid @RequestBody UpdateAddressRequestDto addressDto) throws AddressNotFoundException, LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + addressDto.toString(), httpRequest);

        try {
            fiscalEntityService.updateFiscalEntityAddress(fiscalEntityIdentification, addressIdentification, addressDto, serviceContext);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Address not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Address updated successfully"), HttpStatus.CREATED);

    }

    /*
     * Endpoint to delete a fiscal entity address
     */
    @DeleteMapping("/{fiscalEntityIdentification}/addresses/{addressIdentification}")
    public ResponseEntity<?> deleteFiscalEntityAddress(HttpServletRequest httpRequest,
                                                       @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification,
                                                       @PathVariable(value = "addressIdentification") String addressIdentification) throws AddressNotFoundException, LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            fiscalEntityService.deleteFiscalEntityAddress(fiscalEntityIdentification, addressIdentification, serviceContext);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Address not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Address deleted successfully"), HttpStatus.CREATED);

    }

    /*
     * Endpoint to get a fiscal entity address
     */
    @GetMapping("/{fiscalEntityIdentification}/addresses/{addressIdentification}")
    public ResponseEntity<?> getFiscalEntityAddress(HttpServletRequest httpRequest,
                                                    @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification,
                                                    @PathVariable(value = "addressIdentification") String addressIdentification) throws AddressNotFoundException, LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        GetAddressResponseDto getAddressResponseDto = new GetAddressResponseDto();

        try {
            getAddressResponseDto = fiscalEntityService.getFiscalEntityAddress(fiscalEntityIdentification, addressIdentification, serviceContext);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Address not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        logger.info("response body: " + getAddressResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Address retrieved successfully", getAddressResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to get all fiscal entity addresses
     */
    @GetMapping("/{fiscalEntityIdentification}/addresses")
    public ResponseEntity<?> getFiscalEntityAddress(HttpServletRequest httpRequest,
                                                    @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification,
                                                    @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                                    @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                                    @RequestParam(value = "addressIdentification", required = false) Optional<String> addressIdentification) throws LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        List<GetAddressResponseDto> getAddressResponseDto;

        try {
            getAddressResponseDto = fiscalEntityService.getAllFiscalEntityAddresses(fiscalEntityIdentification, limit, offset, addressIdentification, serviceContext);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Addresses retrieved successfully", getAddressResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to get all fiscal entity outbound payments
     */
    @GetMapping("/{fiscalEntityIdentification}/payments")
    public ResponseEntity<?> getFiscalEntityOutboundPayments(HttpServletRequest httpRequest,
                                                             @PathVariable(value = "fiscalEntityIdentification") String fiscalEntityIdentification,
                                                             @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                                             @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                                             @RequestParam(value = "outPaymentIdentification", required = false) Optional<String> outPaymentIdentification) throws LinkNotFoundException, FiscalEntityNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        List<GetOutPaymentResponseDto> getOutPaymentResponseDto;

        try {
            getOutPaymentResponseDto = fiscalEntityService.getAllOutboundPayments(fiscalEntityIdentification, limit, offset, outPaymentIdentification, serviceContext);
        } catch (FiscalEntityNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Fiscal entity not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Outbound payments retrieved successfully", getOutPaymentResponseDto), HttpStatus.CREATED);

    }

}