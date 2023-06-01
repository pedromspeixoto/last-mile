package com.lastmile.addressservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.lastmile.addressservice.dto.CreateAddressRequestDto;
import com.lastmile.addressservice.dto.CreateAddressResponseDto;
import com.lastmile.addressservice.dto.GetAddressResponseDto;
import com.lastmile.addressservice.dto.UpdateAddressRequestDto;
import com.lastmile.addressservice.service.AddressService;
import com.lastmile.addressservice.service.exception.AddressForbiddenException;
import com.lastmile.addressservice.service.exception.AddressNotFoundException;
import com.lastmile.addressservice.service.exception.GenericException;
import com.lastmile.utils.models.response.ErrorResponse;
import com.lastmile.utils.models.response.SuccessResponse;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.logs.CustomLogging;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class AddressController {

    private final AddressService addressService;
    private final CustomLogging logger;

    public AddressController(AddressService addressservice,
                             CustomLogging logger) {
        this.addressService = addressservice;
        this.logger = logger;
    }

    /*
     * Endpoint to create new address
     */
    @PostMapping("/create")
    public ResponseEntity<?> createNewAddress(HttpServletRequest httpRequest,
                                              @Valid @RequestBody CreateAddressRequestDto addressDto) throws GenericException {

        logger.info("request body: " + addressDto.toString(), httpRequest);
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        CreateAddressResponseDto createAddressResponseDto = new CreateAddressResponseDto();

        try {
            createAddressResponseDto = addressService.createAddress(addressDto, serviceContext);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + createAddressResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Address created successfully", createAddressResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to get all addresses
     */
    @GetMapping
    public ResponseEntity<?> getAddresses(HttpServletRequest httpRequest,
                                          @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                          @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                          @RequestParam(value = "addressIdentification", required = false) Optional<String> addressIdentification,
                                          @RequestParam(value = "entityIdentification", required = false) Optional<String> entityIdentification,
                                          @RequestParam(value = "entityType", required = false) Optional<String> entityType) throws AddressForbiddenException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        List<GetAddressResponseDto> addresses;

        try {
            addresses = addressService.getAddresses(limit, offset, addressIdentification, entityIdentification, entityType, serviceContext);
        } catch (AddressForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching addresses", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Addresses retrieved successfully", addresses), HttpStatus.OK);

    }

    /*
     * Endpoint to get individual address info
     */
    @GetMapping("/{addressIdentification}")
    public ResponseEntity<?> getAddress(HttpServletRequest httpRequest,
                                        @PathVariable(value = "addressIdentification") String addressIdentification) throws AddressNotFoundException, AddressForbiddenException, GenericException {

        GetAddressResponseDto address;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            address = addressService.getAddress(addressIdentification, serviceContext);
        } catch (AddressForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Address not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);

        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error fetching address information.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }

        logger.info("response body: " + address.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Address retrieved successfully", address), HttpStatus.OK);

    }

    /*
     * Endpoint to delete address
     */
    @DeleteMapping("/{addressIdentification}")
    public ResponseEntity<?> deleteAddress(HttpServletRequest httpRequest,
                                           @PathVariable(value = "addressIdentification") String addressIdentification) throws AddressForbiddenException, AddressNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            addressService.deleteAddress(addressIdentification, serviceContext);
        } catch (AddressForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Address not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error deleting address.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Address deleted successfully"), HttpStatus.OK);

    }

    @PutMapping("/{addressIdentification}")
    public ResponseEntity<?> updateAddress(HttpServletRequest httpRequest,
                                           @PathVariable(value = "addressIdentification") String addressIdentification,
                                           @Valid @RequestBody UpdateAddressRequestDto updateAddressRequestDto) throws AddressForbiddenException, AddressNotFoundException, GenericException {

        logger.info("request body: " + updateAddressRequestDto.toString(), httpRequest);
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            addressService.updateAddress(addressIdentification, updateAddressRequestDto, serviceContext);
        } catch (AddressForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Address not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error updating address.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Address updated successfully"), HttpStatus.OK);

    }

}