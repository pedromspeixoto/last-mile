package com.lastmile.orderservice.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.lastmile.orderservice.dto.drivers.AssignDriverToOrderRequestDto;
import com.lastmile.orderservice.dto.OrderContactResponseDto;
import com.lastmile.orderservice.dto.EstimateRequestDto;
import com.lastmile.orderservice.dto.EstimateResponseDto;
import com.lastmile.orderservice.dto.LinkUserToOrderRequestDto;
import com.lastmile.orderservice.dto.OrderFollowResponseDto;
import com.lastmile.orderservice.dto.OrderHistoryResponseDto;
import com.lastmile.orderservice.dto.OrderPhotoResponseDto;
import com.lastmile.orderservice.dto.OrderRequestDto;
import com.lastmile.orderservice.dto.OrderResponseDto;
import com.lastmile.orderservice.dto.OrderUpdateRequestDto;
import com.lastmile.orderservice.dto.PatchOrderPaymentRequestDto;
import com.lastmile.orderservice.dto.PostOrderResponseDto;
import com.lastmile.orderservice.dto.RateOrderRequestDto;
import com.lastmile.orderservice.service.OrderService;
import com.lastmile.orderservice.service.exception.OrderNotFoundException;
import com.lastmile.orderservice.service.exception.OrderPhoneNumberNotFoundException;
import com.lastmile.orderservice.service.exception.PaymentProcessException;
import com.lastmile.orderservice.service.exception.PricingException;
import com.lastmile.orderservice.service.exception.StatusTransitionNotAllowedException;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.twilio.twiml.TwiML;
import com.lastmile.orderservice.service.exception.ExternalServerException;
import com.lastmile.orderservice.service.exception.FeignCommunicationException;
import com.lastmile.orderservice.service.exception.GenericException;
import com.lastmile.orderservice.service.exception.GoogleAPIException;
import com.lastmile.orderservice.service.exception.InvalidScheduledDateException;
import com.lastmile.orderservice.service.exception.MissingFieldException;
import com.lastmile.orderservice.service.exception.NoAssignedDriverException;
import com.lastmile.orderservice.service.exception.NoCoverageException;
import com.lastmile.orderservice.service.exception.NoEstimateAvailableException;
import com.lastmile.orderservice.service.exception.OrderForbiddenException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.logs.CustomLogging;
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
    private final CustomLogging logger;

    public OrderController(OrderService orderService, CustomLogging logger) {
        this.orderService = orderService;
        this.logger = logger;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewOrder(HttpServletRequest httpRequest,
                                            @Valid @RequestBody OrderRequestDto order) throws PricingException, NoCoverageException, InvalidScheduledDateException, PaymentProcessException, GenericException, OrderForbiddenException {

        PostOrderResponseDto postOrderResponseDto;
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + order.toString(), httpRequest);

        try {
            postOrderResponseDto = orderService.create(order, serviceContext);
        } catch (PricingException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Error calculating pricing", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (NoCoverageException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "No coverage in the selected area", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (InvalidScheduledDateException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Invalid scheduled date - please ensure that the date is in the future and not in the next 30 minutes", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (PaymentProcessException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Error processing payment", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error creating order", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + postOrderResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Order created successfully", postOrderResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to get all registered orders
     */
    @GetMapping
    public ResponseEntity<?> getOrders(HttpServletRequest httpRequest,
                                       @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                       @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                       @RequestParam(value = "status", required = false) Optional<String> status,
                                       @RequestParam(value = "orderIdentification", required = false) Optional<String> orderIdentification,
                                       @RequestParam(value = "requesterIdentification", required = false) Optional<String> requesterIdentification,
                                       @RequestParam(value = "ownerIdentification", required = false) Optional<String> ownerIdentification,
                                       @RequestParam(value = "assignedDriver", required = false) Optional<String> driverIdentification) throws OrderForbiddenException, GenericException {

        List<OrderResponseDto> ordersDto;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            ordersDto = orderService.getOrders(limit, offset, status, orderIdentification, requesterIdentification, driverIdentification, ownerIdentification, serviceContext);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving orders", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Orders retrieved successfully", ordersDto), HttpStatus.OK);

    }

    @GetMapping("/{orderIdentification}")
    public ResponseEntity<?> getOrder(HttpServletRequest httpRequest,
                                      @PathVariable(value = "orderIdentification") String orderIdentification,
                                      @RequestParam(defaultValue = "false") Boolean includeOrderHistory) throws OrderNotFoundException, OrderForbiddenException, GenericException {

        OrderResponseDto order;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            order = orderService.getOrder(orderIdentification, includeOrderHistory, serviceContext);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving order", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + order.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order retrieved successfully", order), HttpStatus.OK);

    }

    @GetMapping("/{orderIdentification}/history")
    public ResponseEntity<?> getOrderHistory(HttpServletRequest httpRequest,
                                             @RequestParam(value = "startDate", required = false) Optional<Date> startDate,
                                             @RequestParam(value = "endDate", required = false) Optional<Date> endDate,
                                             @RequestParam(value = "driverIdentification", required = false) Optional<String> driverIdentification,
                                             @PathVariable(value = "orderIdentification") String orderIdentification) throws OrderNotFoundException, OrderForbiddenException, GenericException {

        List<OrderHistoryResponseDto> orderHistoryList;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orderHistoryList = orderService.getOrderHistory(orderIdentification, startDate, endDate, driverIdentification, serviceContext);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order history not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving order history", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order history retrieved successfully", orderHistoryList), HttpStatus.OK);

    }

    @GetMapping("/{orderIdentification}/pickup-photo")
    public ResponseEntity<?> getOrderPickupPhoto(HttpServletRequest httpRequest,
                                                @PathVariable(value = "orderIdentification") String orderIdentification) throws OrderNotFoundException, OrderForbiddenException, GenericException {

        OrderPhotoResponseDto orderPickupPhotoResponseDto;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orderPickupPhotoResponseDto = orderService.getOrderPickupPhoto(orderIdentification, serviceContext);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving order pickup photo", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order pickup photo retrieved successfully", orderPickupPhotoResponseDto), HttpStatus.OK);

    }

    @GetMapping("/{orderIdentification}/delivery-photo")
    public ResponseEntity<?> getOrderDeliveryPhoto(HttpServletRequest httpRequest,
                                                @PathVariable(value = "orderIdentification") String orderIdentification) throws OrderNotFoundException, OrderForbiddenException, GenericException {

        OrderPhotoResponseDto orderDeliveryPhotoResponseDto;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orderDeliveryPhotoResponseDto = orderService.getOrderDeliveryPhoto(orderIdentification, serviceContext);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving order delivery photo", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order delivery photo retrieved successfully", orderDeliveryPhotoResponseDto), HttpStatus.OK);

    }

    /*
     * Endpoint to update order
     */
    @PutMapping("/{orderIdentification}")
    public ResponseEntity<?> updateOrder(HttpServletRequest httpRequest,
                                         @PathVariable(value = "orderIdentification") String orderIdentification,
                                         @Valid @RequestBody OrderUpdateRequestDto order) throws MissingFieldException, InvalidScheduledDateException, FeignCommunicationException, OrderForbiddenException, StatusTransitionNotAllowedException, OrderNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + order.toString(), httpRequest);

        try {
            orderService.updateOrder(orderIdentification, order, serviceContext);
        } catch (InvalidScheduledDateException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Invalid scheduled date - please ensure that the date is in the future and not in the next 30 minutes", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External service unavailable", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (StatusTransitionNotAllowedException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Status transition not allowed", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (MissingFieldException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Missing field", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating order", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to upsert pickup photo
     */
    @RequestMapping(value = "/{orderIdentification}/pickup-photo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upsertOrderPickupPhoto(HttpServletRequest httpRequest,
                                                    @PathVariable(value = "orderIdentification") String orderIdentification,
                                                    @RequestPart(value = "pickupPhoto", required = true) MultipartFile pickupPhoto) throws OrderForbiddenException, ExternalServerException, OrderNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orderService.upsertPickupPhoto(orderIdentification, pickupPhoto, serviceContext);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (ExternalServerException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error deleting/uploading file to AWS S3 Bucket", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error upserting pickup photo", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Pickup photo updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to upsert delivery photo
     */
    @RequestMapping(value = "/{orderIdentification}/delivery-photo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upsertOrderDeliveryPhoto(HttpServletRequest httpRequest,
                                                      @PathVariable(value = "orderIdentification") String orderIdentification,
                                                      @RequestPart(value = "deliveryPhoto", required = true) MultipartFile deliveryPhoto) throws OrderForbiddenException, ExternalServerException, OrderNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orderService.upsertDeliveryPhoto(orderIdentification, deliveryPhoto, serviceContext);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (ExternalServerException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error deleting/uploading file to AWS S3 Bucket", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error upserting delivery photo", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Delivery photo updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to patch order payment information
     */
    @PatchMapping("/{orderIdentification}/payment-status")
    public ResponseEntity<?> patchOrderPayment(HttpServletRequest httpRequest,
                                               @PathVariable(value = "orderIdentification") String orderIdentification,
                                               @Valid @RequestBody PatchOrderPaymentRequestDto patchOrderPaymentDto) throws OrderForbiddenException, OrderNotFoundException, GenericException {
        
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + patchOrderPaymentDto.toString(), httpRequest);

        try {
            orderService.patchOrderPayment(orderIdentification, patchOrderPaymentDto, serviceContext);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating order", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to assign order to driver - protected for internal services only
     */
    @PatchMapping("/{orderIdentification}/driver")
    public ResponseEntity<?> assignDriverToOrder(HttpServletRequest httpRequest,
                                                 @PathVariable(value = "orderIdentification") String orderIdentification,
                                                 @Valid @RequestBody AssignDriverToOrderRequestDto assignDriverToOrderRequestDto) throws OrderForbiddenException, OrderNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + assignDriverToOrderRequestDto.toString(), httpRequest);

        try {
            orderService.assignDriverToOrder(orderIdentification, assignDriverToOrderRequestDto, serviceContext);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);

        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating order", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver assigned to order successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to get estimate for a order
     */
    @PostMapping("/estimate")
    public ResponseEntity<?> calculateEstimate(HttpServletRequest httpRequest,
                                               @Valid @RequestBody EstimateRequestDto estimateRequestDto) throws PricingException, NoCoverageException, FeignCommunicationException, GoogleAPIException, GenericException, UnirestException, NoEstimateAvailableException {

        EstimateResponseDto etaResponseDto;
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + estimateRequestDto.toString(), httpRequest);

        try {
            etaResponseDto = orderService.calculateEstimate(estimateRequestDto, serviceContext);
        } catch (PricingException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Error calculating pricing", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }  catch (NoCoverageException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Go Mile does not have coverage in the selected coordinates", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Feign service unavailable", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (GoogleAPIException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External service unavailable", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (GenericException | UnirestException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "No estimate is available for the input parameters", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (NoEstimateAvailableException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "No estimate available for the provided coordinates", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        logger.info("response body: " + etaResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Estimate calculated successfully", etaResponseDto), HttpStatus.OK);

    }

    /*
     * Endpoint to track order by order identification
     */
    @GetMapping("/{orderIdentification}/track")
    public ResponseEntity<?> trackOrder(HttpServletRequest httpRequest,
                                        @PathVariable(value = "orderIdentification") String orderIdentification) throws OrderNotFoundException, OrderForbiddenException, GenericException {

        OrderFollowResponseDto orderFollowResponseDto;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orderFollowResponseDto = orderService.trackOrder(orderIdentification, serviceContext);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving order", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External service unavailable", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        logger.info("response body: " + orderFollowResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order retrieved successfully", orderFollowResponseDto), HttpStatus.OK);

    }

    /*
     * Endpoint to track order by phone number and short order id (no auth)
     */
    @GetMapping("/track")
    public ResponseEntity<?> trackOrderByShortIdAndPhoneNumber(HttpServletRequest httpRequest,
                                                               @RequestParam(name = "phoneNumber") String phoneNumber,
                                                               @RequestParam(name = "shortOrderId") String shortOrderId) 
        throws OrderForbiddenException, FeignCommunicationException, OrderNotFoundException, GenericException {

        OrderFollowResponseDto order = new OrderFollowResponseDto();
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            order = orderService.trackOrderByShortIdAndPhoneNumber(phoneNumber, shortOrderId, serviceContext);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving order", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External service unavailable", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        }

        logger.info("response body: " + order.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order retrieved successfully", order), HttpStatus.OK);

    }

    /*
     * Endpoint to create a link between user identification and order (update owner)
     */
    @PostMapping("/link")
    public ResponseEntity<?> linkUserToOrder(HttpServletRequest httpRequest,
                                             @Valid @RequestBody LinkUserToOrderRequestDto linkUserToOrderRequestDto) throws OrderForbiddenException, OrderNotFoundException, GenericException { 

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + linkUserToOrderRequestDto.toString(), httpRequest);

        try {
            orderService.linkUserToOrder(linkUserToOrderRequestDto, serviceContext);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving order", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order updated successfully"), HttpStatus.OK);

    }


    /*
     * Endpoint to rate order
     */
    @PatchMapping("/rating")
    public ResponseEntity<?> rateOrderWithoutAuth(@RequestParam(name = "phoneNumber") String phoneNumber,
                                                  @RequestParam(name = "shortOrderId") String shortOrderId,
                                                  @Valid @RequestBody RateOrderRequestDto rateOrderRequestDto) throws OrderNotFoundException, GenericException {

        logger.info("request body: " + rateOrderRequestDto.toString());

        try {
            orderService.rateOrderWithoutAuth(phoneNumber, shortOrderId, rateOrderRequestDto);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error rating order", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order rated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to rate order by order id
     */
    @PatchMapping("/{orderIdentification}/rating")
    public ResponseEntity<?> rateOrder(HttpServletRequest httpRequest,
                                       @PathVariable(value = "orderIdentification") String orderIdentification,
                                       @Valid @RequestBody RateOrderRequestDto rateOrderRequestDto) throws OrderForbiddenException, OrderNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + rateOrderRequestDto.toString(), httpRequest);

        try {
            orderService.rateOrder(orderIdentification, rateOrderRequestDto, serviceContext);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error rating order", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Order rated successfully"), HttpStatus.OK);

    }

    @PostMapping("/{orderIdentification}/contact-driver")
    public ResponseEntity<?> contactOrderAssignedDriver(HttpServletRequest httpRequest,
                                                        @PathVariable(value = "orderIdentification") String orderIdentification) throws FeignCommunicationException, NoAssignedDriverException, OrderNotFoundException, OrderForbiddenException, GenericException {

        OrderContactResponseDto contactDriverResponseDto;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            contactDriverResponseDto = orderService.contactDriver(orderIdentification, serviceContext);
        } catch (NoAssignedDriverException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "No assigned driver found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        }  catch (OrderNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (ExternalServerException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External Server Exception", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Feign communication exception", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error contacting driver", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + contactDriverResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Driver phone number retrieved successfully", contactDriverResponseDto), HttpStatus.OK);

    }

    @PostMapping(value = "/twilio-handle-voice")
    public void handleTwilioVoiceCall(HttpServletRequest httpRequest,
                                      HttpServletResponse httpResponse) throws FeignCommunicationException, GenericException, IOException, OrderPhoneNumberNotFoundException, GenericException {

        // get parameters from request (to and from)
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        String from = httpRequest.getParameter("From");
        String to = httpRequest.getParameter("To");

        // twiml object
        TwiML twimlResponse;
        try {
            httpResponse.setContentType(MediaType.TEXT_XML_VALUE);
            twimlResponse = orderService.handleTwilioVoiceCall(from, to, serviceContext);
            httpResponse.getWriter().write(twimlResponse.toXml());
        } catch (OrderPhoneNumberNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            httpResponse.setContentType(MediaType.TEXT_XML_VALUE);
            httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
            httpResponse.getWriter().write(ex.getMessage());
        } catch (FeignCommunicationException | GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            httpResponse.setContentType(MediaType.TEXT_XML_VALUE);
            httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            httpResponse.getWriter().write(ex.getMessage());
        }

    }

    @GetMapping(value = "/coverage")
    public ResponseEntity<?> hasCoverage(HttpServletRequest httpRequest,
                                         @RequestParam(value = "latitude", required = true) Double latitude,
                                         @RequestParam(value = "longitude", required = true) Double longitude) {

        // check coverage
        try {
            Boolean hasCoverage = orderService.hasCoverage(latitude, longitude);
            if (hasCoverage) {
                return new ResponseEntity<SuccessResponse>(
                    new SuccessResponse(HttpStatus.OK.value(), "Go Mile has coverage in the selected coordinates"), HttpStatus.OK);    
            } else {
                return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Go Mile does not have coverage in the selected coordinates"), HttpStatus.NOT_FOUND);    
            }
        } catch (Exception e) {
            logger.error("error message: " + e.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error getting order coverage"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/process-scheduled-orders")
    public ResponseEntity<?> processScheduledOrders(HttpServletRequest httpRequest) throws GenericException, OrderForbiddenException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orderService.processScheduledOrders(serviceContext);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error running batch", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Batch ran successfully"), HttpStatus.OK);

    }

    @PostMapping("/process-assigned-orders")
    public ResponseEntity<?> processAssignedOrders(HttpServletRequest httpRequest) throws GenericException, OrderForbiddenException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            orderService.processAssignedOrders(serviceContext);
        } catch (OrderForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error running batch", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Batch ran successfully"), HttpStatus.OK);

    }

}