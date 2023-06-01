package com.lastmile.driverservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.lastmile.driverservice.dto.orders.AssignOrderRequestDto;
import com.lastmile.driverservice.dto.orders.OrderActionRequestDto;
import com.lastmile.driverservice.dto.orders.OrderPhotoResponseDto;
import com.lastmile.driverservice.dto.orders.OrderResponseDto;
import com.lastmile.driverservice.service.DriverService;
import com.lastmile.driverservice.service.OrderService;
import com.lastmile.driverservice.service.exception.DriverForbiddenException;
import com.lastmile.driverservice.service.exception.DriverNotFoundException;
import com.lastmile.driverservice.service.exception.DriverStatusInvalidException;
import com.lastmile.driverservice.service.exception.FeignCommunicationException;
import com.lastmile.driverservice.service.exception.GenericException;
import com.lastmile.driverservice.service.exception.OrderNotFoundException;
import com.lastmile.utils.enums.orders.OrderPhotoType;
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.models.response.ErrorResponse;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    private final DriverService driverService;
    private final CustomLogging logger;

    public OrderController(OrderService orderService,
                           DriverService driverService,
                           CustomLogging logger) {
        this.orderService = orderService;
        this.driverService = driverService;
        this.logger = logger;
    }

    /*
     * Endpoint to get all driver orders
     */
    @GetMapping("/{driverIdentification}/orders")
    public ResponseEntity<?> getDriverOrders(HttpServletRequest httpRequest,
                                             @PathVariable(value = "driverIdentification") String driverIdentification,
                                             @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                             @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                             @RequestParam(value = "status", required = false) Optional<String> status,
                                             @RequestParam(value = "requesterIdentification", required = false) Optional<String> requesterIdentification) throws DriverNotFoundException, FeignCommunicationException, DriverForbiddenException {

        List<OrderResponseDto> orders;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orders = orderService.getDriverOrders(driverIdentification, limit, offset, status, requesterIdentification, serviceContext);
        } catch (DriverNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Driver not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (DriverForbiddenException ex) {
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
                new SuccessResponse(HttpStatus.OK.value(), "Driver orders retrieved successfully", orders), HttpStatus.OK);

    }

    /*
     * Endpoint to get individual driver order
     */
    @GetMapping("/{driverIdentification}/orders/{orderIdentification}")
    public ResponseEntity<?> getDriverOrder(HttpServletRequest httpRequest,
                                            @PathVariable(value = "driverIdentification") String driverIdentification,
                                            @PathVariable(value = "orderIdentification") String orderIdentification) throws FeignCommunicationException, OrderNotFoundException, DriverNotFoundException, DriverForbiddenException {

        OrderResponseDto order;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            order = orderService.getDriverOrder(driverIdentification, orderIdentification, serviceContext);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Unexpected error performing order action.", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
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
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found exception", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        }

        logger.info("response body: " + order.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver order retrieved successfully", order), HttpStatus.OK);

    }

    /*
     * Endpoint to get individual driver order pickup photo
     */
    @GetMapping("/{driverIdentification}/orders/{orderIdentification}/pickup-photo")
    public ResponseEntity<?> getDriverOrderPickupPhoto(HttpServletRequest httpRequest,
                                                       @PathVariable(value = "driverIdentification") String driverIdentification,
                                                       @PathVariable(value = "orderIdentification") String orderIdentification) throws FeignCommunicationException, OrderNotFoundException, DriverNotFoundException, DriverForbiddenException {

        OrderPhotoResponseDto orderPhotoResponseDto;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orderPhotoResponseDto = orderService.getDriverOrderPhoto(driverIdentification, orderIdentification, OrderPhotoType.PICKUP, serviceContext);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Unexpected error performing order action.", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
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
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found exception", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver order pickup photo retrieved successfully", orderPhotoResponseDto), HttpStatus.OK);

    }

    /*
     * Endpoint to get individual driver order delivery photo
     */
    @GetMapping("/{driverIdentification}/orders/{orderIdentification}/delivery-photo")
    public ResponseEntity<?> getDriverOrderDeliveryPhoto(HttpServletRequest httpRequest,
                                                         @PathVariable(value = "driverIdentification") String driverIdentification,
                                                         @PathVariable(value = "orderIdentification") String orderIdentification) throws FeignCommunicationException, OrderNotFoundException, DriverNotFoundException, DriverForbiddenException {

        OrderPhotoResponseDto orderPhotoResponseDto;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orderPhotoResponseDto = orderService.getDriverOrderPhoto(driverIdentification, orderIdentification, OrderPhotoType.DELIVERY, serviceContext);
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
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver order delivery photo retrieved successfully", orderPhotoResponseDto), HttpStatus.OK);

    }

    /*
     * Endpoint to assign order to driver - protected for internal services only
     */
    @PutMapping("/{driverIdentification}/orders")
    public ResponseEntity<?> assignOrderToDriver(HttpServletRequest httpRequest,
                                                 @PathVariable(value = "driverIdentification") String driverIdentification,
                                                 @Valid @RequestBody AssignOrderRequestDto assignOrderRequestDto) throws DriverStatusInvalidException, DriverForbiddenException, DriverNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + assignOrderRequestDto.toString(), httpRequest);

        try {
            driverService.assignOrderToDriver(driverIdentification, assignOrderRequestDto.getOrderIdentification(), serviceContext);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Unexpected error assigning order to driver.", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
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
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error assigning order to driver", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DriverStatusInvalidException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Invalid driver status", ex.getMessage()),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order assigned to driver successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to handle order actions
     */
    @PutMapping("/{driverIdentification}/orders/{orderIdentification}")
    public ResponseEntity<?> manageOrder(HttpServletRequest httpRequest,
                                         @PathVariable(value = "driverIdentification") String driverIdentification,
                                         @PathVariable(value = "orderIdentification") String orderIdentification,
                                         @Valid @RequestBody OrderActionRequestDto orderActionRequestDto) throws DriverForbiddenException, DriverNotFoundException, GenericException {


        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + orderActionRequestDto.toString(), httpRequest);

        try {
            driverService.manageOrder(driverIdentification, orderIdentification, orderActionRequestDto, serviceContext);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Unexpected error performing order action.", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
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
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error performing order action", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order action performed successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to upsert individual driver order pickup photo
     */
    @RequestMapping(value = "/{driverIdentification}/orders/{orderIdentification}/pickup-photo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upsertDriverOrderPickupPhoto(HttpServletRequest httpRequest,
                                                            @PathVariable(value = "driverIdentification") String driverIdentification,
                                                            @PathVariable(value = "orderIdentification") String orderIdentification,
                                                            @RequestPart(value = "pickupPhoto", required = true) MultipartFile pickupPhoto) throws FeignCommunicationException, OrderNotFoundException, DriverNotFoundException, DriverForbiddenException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orderService.upsertDriverOrderPhoto(driverIdentification, orderIdentification, OrderPhotoType.PICKUP, pickupPhoto, serviceContext);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Unexpected error performing order action.", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
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
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found exception", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver order pickup photo updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to upsert individual driver order delivery photo
     */
    @RequestMapping(value = "/{driverIdentification}/orders/{orderIdentification}/delivery-photo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upsertDriverOrderDeliveryPhoto(HttpServletRequest httpRequest,
                                                            @PathVariable(value = "driverIdentification") String driverIdentification,
                                                            @PathVariable(value = "orderIdentification") String orderIdentification,
                                                            @RequestPart(value = "deliveryPhoto", required = true) MultipartFile deliveryPhoto) throws FeignCommunicationException, OrderNotFoundException, DriverNotFoundException, DriverForbiddenException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orderService.upsertDriverOrderPhoto(driverIdentification, orderIdentification, OrderPhotoType.DELIVERY, deliveryPhoto, serviceContext);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Unexpected error performing order action.", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
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
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found exception", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver order delivery photo updated successfully"), HttpStatus.OK);

    }

}