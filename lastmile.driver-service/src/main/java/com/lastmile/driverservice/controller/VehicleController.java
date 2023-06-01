package com.lastmile.driverservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lastmile.driverservice.dto.vehicles.AddVehicleRequestDto;
import com.lastmile.driverservice.dto.vehicles.AddVehicleResponseDto;
import com.lastmile.driverservice.dto.vehicles.DriverVehicleResponseDto;
import com.lastmile.driverservice.dto.vehicles.UpdateVehicleStatusRequestDto;
import com.lastmile.driverservice.dto.vehicles.ValidateVehicleRequestDto;
import com.lastmile.driverservice.dto.documents.VehicleRegistrationUpsertRequestDto;
import com.lastmile.driverservice.dto.vehicles.VehicleResponseDto;
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
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.models.response.ErrorResponse;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class VehicleController {

    private final VehicleService vehicleService;
    private final CustomLogging logger;

    public VehicleController(VehicleService vehicleService, CustomLogging logger) {
        this.vehicleService = vehicleService;
        this.logger = logger;
    }

    @PostMapping("/vehicles/refresh")
    public ResponseEntity<?> refreshVehicles(HttpServletRequest httpRequest) throws DriverForbiddenException, ExternalServerException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            vehicleService.refreshVehicles(serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                    "You do not have permission to perform this request", ex.getMessage()), HttpStatus.FORBIDDEN);
        } catch (ExternalServerException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "Error refreshing vehicles - external service is unavailable", ex.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error refreshing vehicles", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.ACCEPTED.value(), "Triggered thread to refresh vehicles successfully"),
                HttpStatus.ACCEPTED);

    }

    @GetMapping("/vehicles/search")
    public ResponseEntity<?> searchVehicles(@RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                            @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                            @RequestParam(name = "make", required = false) Optional<String> make,
                                            @RequestParam(name = "model", required = false) Optional<String> model,
                                            @RequestParam(name = "category", required = false) Optional<String> category,
                                            @RequestParam(name = "year", required = false) Optional<String> year) throws GenericException {

        List<VehicleResponseDto> vehiclesResponseDto;

        try {
            vehiclesResponseDto = vehicleService.searchVehicles(limit, offset, make, model, category, year);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error retrieving vehicles", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Vehicles retrieved successfully", vehiclesResponseDto),
                HttpStatus.OK);

    }

    @PatchMapping("/vehicles/{vehicleIdentification}/status")
    public ResponseEntity<?> patchVehicleStatus(HttpServletRequest httpRequest,
                                                @Valid @RequestBody UpdateVehicleStatusRequestDto updateVehicleStatusRequestDto,
                                                @PathVariable(value = "vehicleIdentification") String vehicleIdentification) throws EntityNotValidatedException, DriverForbiddenException, GenericException, VehicleNotFoundException, VehicleAlreadyActiveException {
        
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + updateVehicleStatusRequestDto.toString(), httpRequest);

        try {
			vehicleService.updateVehicleStatus(vehicleIdentification, updateVehicleStatusRequestDto, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error updating vehicle status", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (VehicleNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Vehicle not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (VehicleAlreadyActiveException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Driver already has an active vehicle", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (EntityNotValidatedException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Entity is not validated", ex.getMessage()),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Vehicle status updated successfully"), HttpStatus.OK);

    }

    @PatchMapping("/vehicles/{vehicleIdentification}/validate")
    public ResponseEntity<?> patchVehicleValidated(HttpServletRequest httpRequest,
                                                   @Valid @RequestBody ValidateVehicleRequestDto validateVehicleRequestDto,
                                                   @PathVariable(value = "vehicleIdentification") String vehicleIdentification) throws DriverForbiddenException, GenericException, VehicleNotFoundException {
        
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + validateVehicleRequestDto.toString(), httpRequest);

        try {
			vehicleService.validateVehicle(vehicleIdentification, validateVehicleRequestDto, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error validating vehicle", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (VehicleNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Vehicle not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Vehicle validated successfully"), HttpStatus.OK);

    }

    @GetMapping("/vehicles/{vehicleIdentification}")
    public ResponseEntity<?> getVehicle(HttpServletRequest httpRequest,
                                        @PathVariable(value = "vehicleIdentification") String vehicleIdentification) throws DriverForbiddenException, GenericException, VehicleNotFoundException, VehicleAlreadyActiveException {
        
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        DriverVehicleResponseDto driverVehicleResponseDto = new DriverVehicleResponseDto();

        try {
			driverVehicleResponseDto = vehicleService.getVehicle(vehicleIdentification, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error adding vehicles", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (VehicleNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Vehicle not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        }

        logger.info("response body: " + driverVehicleResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Vehicle retrieved successfully", driverVehicleResponseDto), HttpStatus.OK);

    }

    @PutMapping("/vehicles/{vehicleIdentification}")
    public ResponseEntity<?> updateVehicle(HttpServletRequest httpRequest,
                                           @Valid @RequestBody AddVehicleRequestDto addVehicleStatusRequestDto,
                                           @PathVariable(value = "vehicleIdentification") String vehicleIdentification) throws DriverForbiddenException, GenericException, VehicleNotFoundException, VehicleAlreadyActiveException {
        
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + addVehicleStatusRequestDto.toString(), httpRequest);

        try {
			vehicleService.updateVehicle(vehicleIdentification, addVehicleStatusRequestDto, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error adding vehicles", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (VehicleNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Vehicle not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Vehicle status updated successfully"), HttpStatus.OK);

    }

    @PutMapping("/vehicles/{vehicleIdentification}/vehicle-registration")
    public ResponseEntity<?> upsertVehicleRegistration(HttpServletRequest httpRequest,
                                                       @PathVariable(value = "vehicleIdentification") String vehicleIdentification,
                                                       @RequestPart(value = "documentFront", required = false) MultipartFile documentFront,
                                                       @RequestPart(value = "documentBack", required = false) MultipartFile documentBack,
                                                       @RequestPart(value = "requestBody", required = false) String vehicleRegistrationUpsertRequestString) throws DriverForbiddenException, ExternalServerException, VehicleNotFoundException, GenericException {

        ObjectMapper objectMapper = new ObjectMapper();
        VehicleRegistrationUpsertRequestDto vehicleRegistrationUpsertRequestDto = new VehicleRegistrationUpsertRequestDto();
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            if (null != vehicleRegistrationUpsertRequestString && !vehicleRegistrationUpsertRequestString.isEmpty()) {
                vehicleRegistrationUpsertRequestDto = objectMapper.readValue(vehicleRegistrationUpsertRequestString, VehicleRegistrationUpsertRequestDto.class);
            }
        } catch (Exception ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Could not map request body to DTO", ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            vehicleService.upsertVehicleRegistration(vehicleIdentification, documentFront, documentBack, vehicleRegistrationUpsertRequestDto, serviceContext);
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
        } catch (VehicleNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Vehicle not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error upserting vehicle registration license", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver updated successfully"), HttpStatus.OK);

    }

    @DeleteMapping("/vehicles/{vehicleIdentification}/vehicle-registration")
    public ResponseEntity<?> deleteVehicleRegistration(HttpServletRequest httpRequest,
                                                       @PathVariable(value = "vehicleIdentification") String vehicleIdentification) throws DocumentNotFoundException, ExternalServerException, VehicleNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            vehicleService.deleteVehicleRegistration(vehicleIdentification, serviceContext);
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
        } catch (VehicleNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Vehicle not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting vehicle license registration", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Vehicle registration license removed successfully"), HttpStatus.OK);

    }

    @PostMapping("/{driverIdentification}/vehicles")
    public ResponseEntity<?> addVehicle(HttpServletRequest httpRequest,
                                        @Valid @RequestBody AddVehicleRequestDto addVehicleRequestDto,
                                        @PathVariable(value = "driverIdentification") String driverIdentification) throws DriverForbiddenException, GenericException, DriverNotFoundException {
        
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        AddVehicleResponseDto addVehicleResponseDto = new AddVehicleResponseDto();
        logger.info("request body: " + addVehicleRequestDto.toString(), httpRequest);

        try {
            addVehicleResponseDto = vehicleService.addVehicle(driverIdentification, addVehicleRequestDto, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error adding vehicles", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Vehicle added to driver successfully", addVehicleResponseDto), HttpStatus.CREATED);

    }

    @DeleteMapping("/{driverIdentification}/vehicles/{vehicleIdentification}")
    public ResponseEntity<?> removeVehicle(HttpServletRequest httpRequest,
                                           @PathVariable(value = "driverIdentification") String driverIdentification,
                                           @PathVariable(value = "vehicleIdentification") String vehicleIdentification) throws DriverForbiddenException, GenericException, VehicleNotFoundException, DriverNotFoundException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            vehicleService.removeVehicle(driverIdentification, vehicleIdentification, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error deleting vehicle", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (VehicleNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Vehicle not found", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Vehicle deleted successfully"), HttpStatus.OK);

    }

    @GetMapping("/{driverIdentification}/vehicles")
    public ResponseEntity<?> getVehicles(HttpServletRequest httpRequest,
                                         @PathVariable(value = "driverIdentification") String driverIdentification) throws DriverForbiddenException, GenericException, DriverNotFoundException {

        List<DriverVehicleResponseDto> vehiclesListDto;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            vehiclesListDto = vehicleService.getVehicles(driverIdentification, serviceContext);
        } catch (DriverForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error retrieving vehicles", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Vehicles retrieved successfully", vehiclesListDto),
                HttpStatus.OK);

    }

}