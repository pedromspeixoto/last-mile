package com.lastmile.orderservice.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.lastmile.orderservice.dto.OrderUpdateRequestDto;
import com.lastmile.orderservice.dto.PatchOrderPaymentRequestDto;
import com.lastmile.orderservice.dto.PostOrderResponseDto;
import com.lastmile.orderservice.dto.RateOrderRequestDto;
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
import com.lastmile.orderservice.service.exception.OrderNotFoundException;
import com.lastmile.orderservice.service.exception.OrderPhoneNumberNotFoundException;
import com.lastmile.orderservice.service.exception.PaymentProcessException;
import com.lastmile.orderservice.service.exception.PricingException;
import com.lastmile.orderservice.service.exception.StatusTransitionNotAllowedException;
import com.lastmile.utils.context.ServiceContext;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.twilio.twiml.TwiML;

import org.springframework.web.multipart.MultipartFile;

public interface OrderService {

    // create new order
    PostOrderResponseDto create(OrderRequestDto order, ServiceContext serviceContext) throws PricingException, NoCoverageException, InvalidScheduledDateException, PaymentProcessException, OrderForbiddenException, GenericException;

    // get orders with filters
    List<OrderResponseDto> getOrders(Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> orderIdentification, Optional<String> requesterIdentification, Optional<String> driverIdentification, Optional<String> ownerIdentification, ServiceContext serviceContext) throws OrderForbiddenException, GenericException;

    // get individual order details
    OrderResponseDto getOrder(String orderIdentification, Boolean includeOrderHistory, ServiceContext serviceContext) throws OrderForbiddenException, OrderNotFoundException, GenericException;

    // get individual order history
    List<OrderHistoryResponseDto> getOrderHistory(String orderIdentification, Optional<Date> startDate, Optional<Date> endDate, Optional<String> driverIdentification, ServiceContext serviceContext) throws OrderNotFoundException, OrderForbiddenException, GenericException;

    // get pickup photo by order id
    OrderPhotoResponseDto getOrderPickupPhoto(String orderIdentification, ServiceContext serviceContext) throws OrderForbiddenException, OrderNotFoundException, GenericException;

    // get delivery photo by order id
    OrderPhotoResponseDto getOrderDeliveryPhoto(String orderIdentification, ServiceContext serviceContext) throws OrderForbiddenException, OrderNotFoundException, GenericException;

    // track order by phonenumber and short id - tracking
    OrderFollowResponseDto trackOrderByShortIdAndPhoneNumber(String phoneNumber, String shortOrderId, ServiceContext serviceContext) throws FeignCommunicationException, OrderForbiddenException, OrderNotFoundException, GenericException;

    // track order by order identification - tracking
    OrderFollowResponseDto trackOrder(String orderIdentification, ServiceContext serviceContext) throws FeignCommunicationException, OrderForbiddenException, OrderNotFoundException, GenericException;

    // update order information
    void updateOrder(String orderIdentification, OrderUpdateRequestDto order, ServiceContext serviceContext) throws MissingFieldException, InvalidScheduledDateException, FeignCommunicationException, OrderForbiddenException, StatusTransitionNotAllowedException, OrderNotFoundException, GenericException;

    // upsert pickup photo
    void upsertPickupPhoto(String userIdentification, MultipartFile pickupPhoto, ServiceContext serviceContext) throws OrderForbiddenException, ExternalServerException, OrderNotFoundException, GenericException;

    //  upsert delivery photo
    void upsertDeliveryPhoto(String userIdentification, MultipartFile deliveryPhoto, ServiceContext serviceContext) throws OrderForbiddenException, ExternalServerException, OrderNotFoundException, GenericException;

    // patch order payment information
    void patchOrderPayment(String orderIdentification, PatchOrderPaymentRequestDto patchOrderPaymentDto, ServiceContext serviceContext) throws OrderForbiddenException, OrderNotFoundException, GenericException;

    // link user to order
    void linkUserToOrder(LinkUserToOrderRequestDto linkUserToOrderRequestDto, ServiceContext serviceContext) throws OrderForbiddenException, OrderNotFoundException, GenericException;

    // patch order assigned driver
    void assignDriverToOrder(String orderIdentification, AssignDriverToOrderRequestDto assignDriverToOrderRequestDto, ServiceContext serviceContext) throws OrderForbiddenException, OrderNotFoundException, GenericException;

    // patch order rating without auth
    void rateOrderWithoutAuth(String phoneNumber, String shortOrderId, RateOrderRequestDto rateOrderRequestDto) throws OrderNotFoundException, GenericException;

    // patch order rating without auth
    void rateOrder(String orderIdentification, RateOrderRequestDto rateOrderRequestDto, ServiceContext serviceContext) throws OrderForbiddenException, OrderNotFoundException, GenericException;

    // get estimate for order
    EstimateResponseDto calculateEstimate(EstimateRequestDto estimateRequestDto, ServiceContext serviceContext) throws PricingException, NoCoverageException, FeignCommunicationException, GoogleAPIException, GenericException, UnirestException, NoEstimateAvailableException;

    // contact driver by driver identification
    OrderContactResponseDto contactDriver(String orderIdentification, ServiceContext serviceContext) throws ExternalServerException, NoAssignedDriverException, FeignCommunicationException, OrderForbiddenException, OrderNotFoundException, GenericException;

    // handle twilio voice call
    TwiML handleTwilioVoiceCall(String incomingPhoneNumber, String outgoingAnonymousPhoneNumber, ServiceContext serviceContext) throws FeignCommunicationException, OrderPhoneNumberNotFoundException, GenericException;

    // get order coverage
    Boolean hasCoverage(Double latitude, Double longitude);

    // process scheduled orders
    void processScheduledOrders(ServiceContext serviceContext) throws OrderForbiddenException, GenericException;

    // process assigned orders
    void processAssignedOrders(ServiceContext serviceContext) throws OrderForbiddenException, GenericException;

}