package com.lastmile.customerservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.lastmile.customerservice.dto.addresses.CreateAddressRequestDto;
import com.lastmile.customerservice.dto.addresses.CreateAddressResponseDto;
import com.lastmile.customerservice.dto.addresses.GetAddressResponseDto;
import com.lastmile.customerservice.dto.addresses.UpdateAddressRequestDto;
import com.lastmile.customerservice.service.AddressService;
import com.lastmile.customerservice.service.exception.AddressNotFoundException;
import com.lastmile.customerservice.service.exception.CustomerNotFoundException;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;
import com.lastmile.customerservice.service.exception.GenericException;
import com.lastmile.customerservice.service.exception.LinkNotFoundException;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.models.response.ErrorResponse;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class AddressController {

    private final AddressService addressService;
    private final CustomLogging logger;

    public AddressController(AddressService addressService,
                             CustomLogging logger) {
        this.addressService = addressService;
        this.logger = logger;
    }
    /*
     * Endpoint to create a customer address
     */
    @PostMapping("/{customerIdentification}/addresses")
    public ResponseEntity<?> createCustomerAddress(HttpServletRequest httpRequest,
                                                       @PathVariable(value = "customerIdentification") String customerIdentification,
                                                       @Valid @RequestBody CreateAddressRequestDto addressDto) throws LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException {

        CreateAddressResponseDto createAddressResponseDto = new CreateAddressResponseDto();
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + addressDto.toString(), httpRequest);

        try {
            createAddressResponseDto = addressService.createCustomerAddress(customerIdentification, addressDto, serviceContext);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
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
     * Endpoint to update a customer address
     */
    @PutMapping("/{customerIdentification}/addresses/{addressIdentification}")
    public ResponseEntity<?> updateCustomerAddress(HttpServletRequest httpRequest,
                                                       @PathVariable(value = "customerIdentification") String customerIdentification,
                                                       @PathVariable(value = "addressIdentification") String addressIdentification,
                                                       @Valid @RequestBody UpdateAddressRequestDto addressDto) throws AddressNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + addressDto.toString(), httpRequest);

        try {
            addressService.updateCustomerAddress(customerIdentification, addressIdentification, addressDto, serviceContext);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Address not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
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
     * Endpoint to delete a customer address
     */
    @DeleteMapping("/{customerIdentification}/addresses/{addressIdentification}")
    public ResponseEntity<?> deleteCustomerAddress(HttpServletRequest httpRequest,
                                                       @PathVariable(value = "customerIdentification") String customerIdentification,
                                                       @PathVariable(value = "addressIdentification") String addressIdentification) throws AddressNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            addressService.deleteCustomerAddress(customerIdentification, addressIdentification, serviceContext);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Address not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
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
     * Endpoint to get a customer address
     */
    @GetMapping("/{customerIdentification}/addresses/{addressIdentification}")
    public ResponseEntity<?> getCustomerAddress(HttpServletRequest httpRequest,
                                                    @PathVariable(value = "customerIdentification") String customerIdentification,
                                                    @PathVariable(value = "addressIdentification") String addressIdentification) throws AddressNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        GetAddressResponseDto getAddressResponseDto = new GetAddressResponseDto();

        try {
            getAddressResponseDto = addressService.getCustomerAddress(customerIdentification, addressIdentification, serviceContext);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Address not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
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

        logger.info("response body: " +  getAddressResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Address retrieved successfully", getAddressResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to get all customer addresses
     */
    @GetMapping("/{customerIdentification}/addresses/")
    public ResponseEntity<?> getCustomerAddress(HttpServletRequest httpRequest,
                                                    @PathVariable(value = "customerIdentification") String customerIdentification,
                                                    @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                                    @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                                    @RequestParam(value = "addressIdentification", required = false) Optional<String> addressIdentification) throws LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        List<GetAddressResponseDto> getAddressResponseDto;

        try {
            getAddressResponseDto = addressService.getAllCustomerAddresses(customerIdentification, limit, offset, addressIdentification, serviceContext);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
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
}