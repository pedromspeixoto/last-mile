package com.lastmile.customerservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.lastmile.customerservice.dto.CreateCustomerRequestDto;
import com.lastmile.customerservice.dto.CreateCustomerResponseDto;
import com.lastmile.customerservice.dto.CreateNewApiKeyResponseDto;
import com.lastmile.customerservice.dto.CreateWarehouseRequestDto;
import com.lastmile.customerservice.dto.CreateWarehouseResponseDto;
import com.lastmile.customerservice.dto.CustomerDto;
import com.lastmile.customerservice.dto.CustomerUserResponseDto;
import com.lastmile.customerservice.dto.LinkAccountCustomerRequestDto;
import com.lastmile.customerservice.dto.PatchCustomerValidatedRequestDto;
import com.lastmile.customerservice.dto.UpdateCustomerRequestDto;
import com.lastmile.customerservice.dto.UpdateWarehouseRequestDto;
import com.lastmile.customerservice.dto.WarehouseDto;
import com.lastmile.customerservice.service.CustomerService;
import com.lastmile.customerservice.service.exception.AddressNotFoundException;
import com.lastmile.customerservice.service.exception.CustomerInvalidStatusException;
import com.lastmile.customerservice.service.exception.CustomerNotFoundException;
import com.lastmile.customerservice.service.exception.EntityNotValidatedException;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;
import com.lastmile.customerservice.service.exception.GenericException;
import com.lastmile.customerservice.service.exception.LinkNotFoundException;
import com.lastmile.customerservice.service.exception.PaymentDetailNotFoundException;
import com.lastmile.customerservice.service.exception.WarehouseNotFoundException;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.models.response.ErrorResponse;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class CustomerController {

    private final CustomerService customerService;
    private final CustomLogging logger;

    public CustomerController(CustomerService customerService,
                              CustomLogging logger) {
        this.customerService = customerService;
        this.logger = logger;
    }

    /*
     * Endpoint to create a new customer
     */
    @PostMapping("/create")
    public ResponseEntity<?> createNewCustomer(HttpServletRequest httpRequest,
                                              @Valid @RequestBody CreateCustomerRequestDto customer) throws FeignCommunicationException, GenericException {

        CreateCustomerResponseDto customerIdentification = new CreateCustomerResponseDto();
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + customer.toString(), httpRequest);

        try {
            customerIdentification = customerService.create(serviceContext, customer);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "Error calling external service", ex.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Unexpected error creating customer", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + customerIdentification.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(new SuccessResponse(HttpStatus.CREATED.value(),"Customer created successfully", customerIdentification), 
                                                   HttpStatus.CREATED);

    }

    /*
     * Endpoint to get all registered customers with filter by status
     */
    @GetMapping
    public ResponseEntity<?> getCustomers(HttpServletRequest httpRequest,
                                          @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                          @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                          @RequestParam(value = "status", required = false) Optional<String> status,
                                          @RequestParam(value = "userIdentification", required = false) Optional<String> userIdentification) throws LinkNotFoundException, GenericException {

        List<CustomerDto> customers;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            customers = customerService.getCustomers(limit, offset, status, userIdentification, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                    "You do not have permission to perform this action", ex.getMessage()), HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Unexpected error retrieving customers", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Customers retrieved successfully", customers),
                HttpStatus.OK);

    }

    /*
     * Endpoint to get customer information by customer identification
     */
    @GetMapping("/{customerIdentification}")
    public ResponseEntity<?> getCustomer(HttpServletRequest httpRequest,
                                         @PathVariable(value = "customerIdentification") String customerIdentification) throws LinkNotFoundException, CustomerNotFoundException, GenericException {

        CustomerDto customerDto;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            customerDto = customerService.getCustomer(customerIdentification, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                    "You do not have permission to perform this action", ex.getMessage()), HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Unexpected error retrieving customer", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + customerDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Customer retrieved successfully", customerDto),
                HttpStatus.OK);

    }

    /*
     * Endpoint to delete customer by customer_identification - protected for ADMINS
     * only
     */
    @DeleteMapping("/{customerIdentification}")
    public ResponseEntity<?> deleteCustomer(HttpServletRequest httpRequest,
                                            @PathVariable(value = "customerIdentification") String customerIdentification) throws GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            customerService.deleteCustomer(customerIdentification, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                    "You do not have permission to perform this action", ex.getMessage()), HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Unexpected error deleting customer", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Customer deleted successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to update customer
     */
    @PutMapping("/{customerIdentification}")
    public ResponseEntity<?> updateCustomer(HttpServletRequest httpRequest,
                                            @PathVariable(value = "customerIdentification") String customerIdentification,
                                            @RequestBody UpdateCustomerRequestDto customer) throws CustomerInvalidStatusException, PaymentDetailNotFoundException, LinkNotFoundException, CustomerNotFoundException, GenericException, FeignCommunicationException, AddressNotFoundException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + customer.toString(), httpRequest);

        try {
            customerService.updateCustomer(customerIdentification, customer, serviceContext);
        } catch (EntityNotValidatedException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Customer not validated", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (CustomerInvalidStatusException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Invalid status transition - please ensure that customer has an active payment method", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External service unavailable", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Address not found", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (PaymentDetailNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Payment detail not found", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error updating customer", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Customer updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to validate customer
     */
    @PatchMapping("/{customerIdentification}/validate")
    public ResponseEntity<?> patchCustomerValidated(HttpServletRequest httpRequest,
                                                    @PathVariable(value = "customerIdentification") String customerIdentification,
                                                    @Valid @RequestBody PatchCustomerValidatedRequestDto patchCustomerValidatedRequestDto) throws LinkNotFoundException, CustomerNotFoundException, GenericException, CustomerInvalidStatusException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + patchCustomerValidatedRequestDto.toString(), httpRequest);

        try {
            customerService.patchCustomerValidated(customerIdentification, patchCustomerValidatedRequestDto, serviceContext);
        } catch (CustomerInvalidStatusException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Invalid status transition", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error updating customer status", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Customer status updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to update customer
     */
    @PostMapping("/{customerIdentification}/apikey")
    public ResponseEntity<?> createNewCustomerApiKey(HttpServletRequest httpRequest,
                                                     @PathVariable(value = "customerIdentification") String customerIdentification) throws LinkNotFoundException, CustomerNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        CreateNewApiKeyResponseDto createNewApiKeyResponseDto = new CreateNewApiKeyResponseDto();

        try {
            createNewApiKeyResponseDto = customerService.createNewCustomerApiKey(customerIdentification, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error creating new api key for customer", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + createNewApiKeyResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "New api key created successfully", createNewApiKeyResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to add link from account to customer
     */
    @PutMapping("/{customerIdentification}/users")
    public ResponseEntity<?> addLinkAccountToCustomer(HttpServletRequest httpRequest,
                                                      @PathVariable(value = "customerIdentification") String customerIdentification,
                                                      @RequestBody LinkAccountCustomerRequestDto linkAccountDto) throws CustomerNotFoundException, GenericException {
        
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + linkAccountDto.toString(), httpRequest);
        
        try {
            customerService.addLinkUserToCustomer(customerIdentification, linkAccountDto, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error linking account to customer", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Account linked to customer successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to remove link from account to customer
     */
    @DeleteMapping("/{customerIdentification}/users")
    public ResponseEntity<?> removeLinkAccountFromCustomer(HttpServletRequest httpRequest,
                                                          @PathVariable(value = "customerIdentification") String customerIdentification,
                                                          @RequestBody LinkAccountCustomerRequestDto linkAccountDto) throws LinkNotFoundException, CustomerNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            customerService.removeLinkUserToCustomer(customerIdentification, linkAccountDto, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error removing link", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Account link removed successfully"), HttpStatus.OK);
    }                                                                                                                                        

    /*
     * Endpoint to add new warehouse to customer
     */
    @PostMapping("/{customerIdentification}/warehouses")
    public ResponseEntity<?> addWarehouseCustomer(HttpServletRequest httpRequest, 
                                                  @PathVariable(value = "customerIdentification") String customerIdentification,
                                                  @RequestBody CreateWarehouseRequestDto createWarehouseRequestDto) throws CustomerNotFoundException, LinkNotFoundException, GenericException, FeignCommunicationException {
                                                    
        // set service context
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + createWarehouseRequestDto.toString(), httpRequest);
        CreateWarehouseResponseDto warehouseIdentification = new CreateWarehouseResponseDto();

        try {
            warehouseIdentification = customerService.addWarehouseCustomer(customerIdentification, createWarehouseRequestDto, serviceContext);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External service unavailable", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error adding warehouse", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + warehouseIdentification.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Warehouse created successfully", warehouseIdentification), HttpStatus.CREATED);
    }

    /*
     * Endpoint to update warehouse information
    */
    @PutMapping("/{customerIdentification}/warehouses/{warehouseIdentification}")
    public ResponseEntity<?> updateWarehouse(HttpServletRequest httpRequest,
                                             @PathVariable(value = "customerIdentification") String customerIdentification,
                                             @PathVariable(value = "warehouseIdentification") String warehouseIdentification,
                                             @RequestBody UpdateWarehouseRequestDto updateWarehouseRequestDto) throws CustomerNotFoundException, WarehouseNotFoundException, LinkNotFoundException, GenericException, FeignCommunicationException, AddressNotFoundException {
                                                    
        // set service context
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + updateWarehouseRequestDto.toString(), httpRequest);

        try {
            customerService.updateWarehouse(customerIdentification, warehouseIdentification, updateWarehouseRequestDto, serviceContext);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External service unavailable", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Address not found", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (WarehouseNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Warehouse not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error adding warehouse", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Warehouse created successfully"), HttpStatus.OK);
    }

    /*
     * Endpoint to delete warehouse from customer
    */
    @DeleteMapping("/{customerIdentification}/warehouses/{warehouseIdentification}")
    public ResponseEntity<?> removeWarehouse(HttpServletRequest httpRequest,
                                             @PathVariable(value = "customerIdentification") String customerIdentification,
                                             @PathVariable(value = "warehouseIdentification") String warehouseIdentification,
                                             @RequestBody CreateWarehouseRequestDto createWarehouseRequestDto) throws CustomerNotFoundException, WarehouseNotFoundException, LinkNotFoundException, GenericException {
                                                    
        // set service context
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            customerService.removeWarehouse(customerIdentification, warehouseIdentification, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (WarehouseNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Warehouse not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error removing warehouse", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Warehouse removed successfully"), HttpStatus.OK);
    }

    /*
     * Endpoint to retrieve all warehouses with filters
    */
    @GetMapping("/warehouses")
    public ResponseEntity<?> getWarehouses(HttpServletRequest httpRequest,
                                          @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                          @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                          @RequestParam(value = "status", required = false) Optional<String> status,
                                          @RequestParam(value = "customerIdentification") Optional<String> customerIdentification) throws CustomerNotFoundException, LinkNotFoundException, GenericException {
                                                    
        // set service context
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            customerService.getWarehouses(limit, offset, status, customerIdentification, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error retrieving warehouse", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Warehouse retrieved successfully"), HttpStatus.OK);
    }

    /*
     * Endpoint to retrieve single warehouse 
    */
    @GetMapping("/{customerIdentification}/warehouses/{warehouseIdentification}")
    public ResponseEntity<?> getWarehouse(HttpServletRequest httpRequest,
                                          @PathVariable(value = "customerIdentification") String customerIdentification,
                                          @PathVariable(value = "warehouseIdentification") String warehouseIdentification) throws WarehouseNotFoundException, CustomerNotFoundException, LinkNotFoundException, GenericException {
                                                    
        // set service context
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        WarehouseDto warehouseDto = new WarehouseDto();

        try {
            warehouseDto = customerService.getWarehouse(customerIdentification, warehouseIdentification, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (WarehouseNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Warehouse not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error retrieving warehouse", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + warehouseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Warehouse retrieved successfully", warehouseDto), HttpStatus.OK);
    }

    /*
     * Endpoint to get customer information by customer identification
     */
    @GetMapping("/{customerIdentification}/users")
    public ResponseEntity<?> getCustomerUsers(HttpServletRequest httpRequest,
                                              @PathVariable(value = "customerIdentification") String customerIdentification) throws LinkNotFoundException, CustomerNotFoundException, GenericException {

        List<CustomerUserResponseDto> customerUserResponseDto;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            customerUserResponseDto = customerService.getCustomerUsers(customerIdentification, serviceContext);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                    "You do not have permission to perform this action", ex.getMessage()), HttpStatus.FORBIDDEN);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Unexpected error retrieving customer users", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Customer users retrieved successfully", customerUserResponseDto),
                HttpStatus.OK);

    }

}