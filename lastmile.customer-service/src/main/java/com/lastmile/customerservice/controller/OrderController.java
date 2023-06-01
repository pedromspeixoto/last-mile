package com.lastmile.customerservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.lastmile.customerservice.dto.orders.OrderRequestDto;
import com.lastmile.customerservice.dto.orders.OrderResponseDto;
import com.lastmile.customerservice.dto.orders.PostOrderResponseDto;
import com.lastmile.customerservice.service.OrderService;
import com.lastmile.customerservice.service.exception.CustomerNotActiveException;
import com.lastmile.customerservice.service.exception.CustomerNotFoundException;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;
import com.lastmile.customerservice.service.exception.GenericException;
import com.lastmile.customerservice.service.exception.InvalidApiKeyException;
import com.lastmile.customerservice.service.exception.InvalidPaymentDetailsException;
import com.lastmile.customerservice.service.exception.LinkNotFoundException;
import com.lastmile.customerservice.service.exception.OrderNotFoundException;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.models.response.ErrorResponse;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    private final CustomLogging logger;

    public OrderController(OrderService orderService,
                           CustomLogging logger) {
        this.orderService = orderService;
        this.logger = logger;
    }

    /*
     * Endpoint to create a new order
     */
    @PostMapping("/{customerIdentification}/orders")
    public ResponseEntity<?> createOrder(HttpServletRequest httpRequest,
                                         @RequestHeader(value = "X-Api-Key") String apiKey,
                                         @PathVariable(value = "customerIdentification") String customerIdentification,
                                         @Valid @RequestBody OrderRequestDto order) throws InvalidApiKeyException, CustomerNotFoundException, FeignCommunicationException, GenericException, CustomerNotActiveException, InvalidPaymentDetailsException {

        PostOrderResponseDto postOrderResponseDto = new PostOrderResponseDto();
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + order.toString(), httpRequest);

        try {
            postOrderResponseDto = orderService.create(customerIdentification, order, apiKey, serviceContext);
        } catch (CustomerNotActiveException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Customer is not active", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (InvalidApiKeyException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid authorization parameters entered", ex.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        } catch (InvalidPaymentDetailsException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Customer does not have an active payment method configured", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error creating order", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + postOrderResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Order created successfully", postOrderResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to get all customer orders
     */
    @GetMapping("/{customerIdentification}/orders")
    public ResponseEntity<?> getCustomerOrders(HttpServletRequest httpRequest,
                                               @PathVariable(value = "customerIdentification") String customerIdentification,
                                               @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                               @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                               @RequestParam(value = "status", required = false) Optional<String> status,
                                               @RequestParam(value = "assignedDriver", required = false) Optional<String> assignedDriver) throws CustomerNotFoundException, FeignCommunicationException, LinkNotFoundException {

        List<OrderResponseDto> orders;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orders = orderService.getCustomerOrders(customerIdentification, limit, offset, status, assignedDriver, serviceContext);
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
                new SuccessResponse(HttpStatus.OK.value(), "Customer orders retrieved successfully", orders), HttpStatus.OK);

    }

    /*
     * Endpoint to get individual customer order
     */
    @GetMapping("/{customerIdentification}/orders/{orderIdentification}")
    public ResponseEntity<?> getCustomerOrder(HttpServletRequest httpRequest,
                                             @PathVariable(value = "customerIdentification") String customerIdentification,
                                             @PathVariable(value = "orderIdentification") String orderIdentification) throws FeignCommunicationException, OrderNotFoundException, CustomerNotFoundException, LinkNotFoundException {

        OrderResponseDto order;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            order = orderService.getCustomerOrder(customerIdentification, orderIdentification, serviceContext);
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
        }

        logger.info("response body: " + order.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Customer retrieved successfully", order), HttpStatus.OK);

    }

    /*
     * Endpoint to update customer order
     */
    @PutMapping("/{customerIdentification}/orders/{orderIdentification}")
    public ResponseEntity<?> updateCustomerOrder(HttpServletRequest httpRequest,
                                                @PathVariable(value = "customerIdentification") String customerIdentification,
                                                @PathVariable(value = "orderIdentification") String orderIdentification,
                                                @Valid @RequestBody OrderRequestDto order) throws FeignCommunicationException, OrderNotFoundException, CustomerNotFoundException, LinkNotFoundException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + order.toString(), httpRequest);

        try {
            orderService.updateCustomerOrder(customerIdentification, orderIdentification, order, serviceContext);
        } catch (OrderNotFoundException | CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not found", ex.getMessage()),
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
                new SuccessResponse(HttpStatus.OK.value(), "Customer updated successfully"), HttpStatus.OK);

    }

}