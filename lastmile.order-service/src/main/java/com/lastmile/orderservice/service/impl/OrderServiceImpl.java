package com.lastmile.orderservice.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lastmile.orderservice.enums.OrderStatus;
import com.lastmile.orderservice.enums.RequesterEntityType;
import com.lastmile.orderservice.enums.FeeType;
import com.lastmile.orderservice.enums.OrderHistoryActions;
import com.lastmile.orderservice.enums.OrderPriority;
import com.lastmile.orderservice.rabbitmq.EventPublisher;
import com.lastmile.orderservice.client.drivers.DriversBridge;
import com.lastmile.orderservice.client.google.GoogleConnector;
import com.lastmile.orderservice.client.google.dto.distancematrix.GoogleEstimateResponseDto;
import com.lastmile.orderservice.client.payments.PaymentsBridge;
import com.lastmile.orderservice.client.twilio.TwilioClient;
import com.lastmile.orderservice.domain.BasePricing;
import com.lastmile.orderservice.domain.CustomPricing;
import com.lastmile.orderservice.domain.Order;
import com.lastmile.orderservice.domain.OrderHistory;
import com.lastmile.orderservice.dto.drivers.AssignDriverToOrderRequestDto;
import com.lastmile.orderservice.dto.drivers.DriverResponseModel;
import com.lastmile.orderservice.dto.OrderContactResponseDto;
import com.lastmile.orderservice.dto.EstimateRequestDto;
import com.lastmile.orderservice.dto.EstimateResponseDto;
import com.lastmile.orderservice.dto.LinkUserToOrderRequestDto;
import com.lastmile.orderservice.dto.OrderFollowResponseDto;
import com.lastmile.orderservice.dto.OrderHistoryResponseDto;
import com.lastmile.orderservice.dto.OrderPhotoResponseDto;
import com.lastmile.orderservice.dto.OrderRequestDto;
import com.lastmile.orderservice.dto.OrderResponseDto;
import com.lastmile.orderservice.dto.OrderShortHistoryResponseDto;
import com.lastmile.orderservice.dto.OrderUpdateRequestDto;
import com.lastmile.orderservice.dto.PatchOrderPaymentRequestDto;
import com.lastmile.orderservice.dto.PostOrderResponseDto;
import com.lastmile.orderservice.dto.RateOrderRequestDto;
import com.lastmile.orderservice.dto.payments.CreatePaymentResponseModel;
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
import com.lastmile.utils.clients.aws.AWSS3Client;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.enums.drivers.DriverStatus;
import com.lastmile.utils.validations.Validator;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.twilio.twiml.TwiML;
import com.lastmile.orderservice.repository.BasePricingRepository;
import com.lastmile.orderservice.repository.CustomPricingRepository;
import com.lastmile.orderservice.repository.OrderCoverageRepository;
import com.lastmile.orderservice.repository.OrderHistoryRepository;
import com.lastmile.orderservice.repository.OrderPropertiesRepository;
import com.lastmile.orderservice.repository.OrderRepository;
import com.lastmile.orderservice.service.OrderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@Service
@Configuration
public class OrderServiceImpl implements OrderService {

    @Value("${spring.profiles.active}")
    private String environment;

    @Value("${rabbitmq.routing-key.order-top-priority}")
    private String ORDER_TOP_PRIORITY_ROUTING_KEY;

    @Value("${rabbitmq.routing-key.order-medium-priority}")
    private String ORDER_MEDIUM_PRIORITY_ROUTING_KEY;

    @Value("${rabbitmq.routing-key.order-low-priority}")
    private String ORDER_LOW_PRIORITY_ROUTING_KEY;

    @Value("${lastmile.nearest.radius}")
    private Integer LAST_MILE_NEAREST_RADIUS;

    private static final String ORDERS_PICKUP_S3_PATH = "orders/pickup/";
    private static final String ORDERS_DELIVERY_S3_PATH = "orders/delivery/";
    private static final String DRIVERS_FEE_PROPERTY_NAME = "drivers_percentage_fee";
    private static final String SURGE_FEE_PROPERTY_NAME = "current_surge_fee";
    private static final String AVG_DRIVER_ASSIGN_TIME_PROPERTY_NAME = "average_driver_assign_time";
    private static final String ORDER_TIMEOUT_PROPERTY_NAME = "order_timeout_value";

    Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final EventPublisher eventPublisher;
    private final DriversBridge driversBridge;
    private final PaymentsBridge paymentsBridge;
    private final GoogleConnector googleConnector;
    private final AWSS3Client awsS3Client;
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderCoverageRepository orderCoverageRepository;
    private final BasePricingRepository basePricingRepository;
    private final CustomPricingRepository customPricingRepository;
    private final OrderPropertiesRepository orderPropertiesRepository;
    private final TwilioClient twilioClient;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public OrderServiceImpl(final OrderRepository orderRepository,
                            final OrderHistoryRepository orderHistoryRepository,
                            final AWSS3Client awsS3Client,
                            final EventPublisher eventPublisher,
                            final DriversBridge driversBridge,
                            final PaymentsBridge paymentsBridge,
                            final GoogleConnector googleConnector,
                            final TwilioClient twilioClient,
                            final OrderCoverageRepository orderCoverageRepository,
                            final BasePricingRepository basePricingRepository,
                            final CustomPricingRepository customPricingRepository,
                            final OrderPropertiesRepository orderPropertiesRepository) {
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderCoverageRepository = orderCoverageRepository;
        this.awsS3Client = awsS3Client;
        this.eventPublisher = eventPublisher;
        this.driversBridge = driversBridge;
        this.paymentsBridge = paymentsBridge;
        this.googleConnector = googleConnector;
        this.twilioClient = twilioClient;
        this.basePricingRepository = basePricingRepository;
        this.customPricingRepository = customPricingRepository;
        this.orderPropertiesRepository = orderPropertiesRepository;
    }

    @Override
    public PostOrderResponseDto create(final OrderRequestDto orderDto, ServiceContext serviceContext) throws PricingException, NoCoverageException, InvalidScheduledDateException, OrderForbiddenException, GenericException, PaymentProcessException {

        // check access - allow internal, admin or orders with order type
        if (!Validator.isAdmin(serviceContext) && orderDto.getRequesterEntityType() == RequesterEntityType.MARKETPLACE && !Validator.isInternalCommunication(serviceContext)) {
            throw new OrderForbiddenException();
        }

        // check coverage for pickup and destination
        if (!hasCoverage(orderDto.getPickupLatitude(), orderDto.getPickupLongitude())
                || !hasCoverage(orderDto.getDestinationLatitude(), orderDto.getDestinationLongitude())) {
            throw new NoCoverageException();
        }

        // set basic order info
        OrderHistory newOrderHistoryEntry = new OrderHistory();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Order newOrder = modelMapper.map(orderDto, Order.class);
        newOrder.setOrderIdentification(UUID.randomUUID().toString());
        newOrder.setIsOrderTrackingActive(true);

        // generate unique short order ID
        try {
            String sixRandomNumber = "";
            while (true) {
                sixRandomNumber = RandomStringUtils.randomNumeric(6);
                if (!orderRepository.findByShortOrderIdentification(sixRandomNumber).isPresent()) {
                    newOrder.setShortOrderIdentification(sixRandomNumber);
                    break;
                }
            }
        } catch (final Exception e) {
            newOrder.setStatus(OrderStatus.FAILED_ON_CREATE.toString());
            orderRepository.save(newOrder);
            throw new GenericException(e.getMessage(), e.getCause());
        }

        // set status and scheduled date (if that is the case)
        if (null != newOrder.getScheduledDate() && !newOrder.getScheduledDate().toString().isEmpty()) {
            newOrder.setStatus(OrderStatus.SCHEDULED.toString());
        } else {
            newOrder.setStatus(OrderStatus.PENDING.toString());
        }

        // reverse geo calculation of pickup and delivery address
        try {
            newOrder.setPickupAddress(googleConnector.reverseGeocoding(orderDto.getPickupLatitude(), orderDto.getPickupLongitude()));
            newOrder.setDestinationAddress(googleConnector.reverseGeocoding(orderDto.getDestinationLatitude(), orderDto.getDestinationLongitude()));
        } catch (Exception e) {

        }

        // calculate estimates and pricing
        EstimateRequestDto estimateRequestDto = new EstimateRequestDto(orderDto.getRequesterIdentification(),
                                                                       orderDto.getRequesterEntityType(),
                                                                       orderDto.getPickupLatitude(),
                                                                       orderDto.getPickupLongitude(),
                                                                       orderDto.getDestinationLatitude(),
                                                                       orderDto.getDestinationLongitude(),
                                                                       orderDto.getPriority(),
                                                                       orderDto.getScheduledDate(),
                                                                       orderDto.getOrderValue(),
                                                                       orderDto.getOrderType());

        // get driver delivery fee
        Double driverDeliveryFee = 0.85;
        try {
            driverDeliveryFee = Double.valueOf(orderPropertiesRepository.findByEnvironmentAndProperty(environment, DRIVERS_FEE_PROPERTY_NAME).get().getValue());
        } catch (Exception e) {
            newOrder.setStatus(OrderStatus.ESTIMATE_FAILED.toString());
            orderRepository.save(newOrder);
            throw new GenericException(e.getMessage(), e.getCause());
        }

        // process estimate
        try {
            EstimateResponseDto estimateResponseDto = this.calculateEstimate(estimateRequestDto, serviceContext);
            newOrder.setDeliveryFeeValue(estimateResponseDto.getEstimatedDeliveryFee());
            newOrder.setDriverFeeValue(estimateResponseDto.getEstimatedDeliveryFee() * driverDeliveryFee);
            newOrder.setPickupEta(estimateResponseDto.getPickupEta());
            newOrder.setDeliveryEta(estimateResponseDto.getDeliveryEta());
            newOrder.setEstimatedDistance(estimateResponseDto.getEstimatedDeliveryDistance() + estimateResponseDto.getEstimatedPickupDistance());
        } catch (Exception e) {
            newOrder.setStatus(OrderStatus.ESTIMATE_FAILED.toString());
            orderRepository.save(newOrder);
            throw new PricingException(e.getMessage(), e.getCause());
        }

        // try to process payment for order
        try {
            CreatePaymentResponseModel paymentResponse = paymentsBridge.createPayment(serviceContext,
                                                                                      orderDto,
                                                                                      newOrder.getOrderIdentification(),
                                                                                      newOrder.getDeliveryFeeValue());
            newOrder.setPaymentStatus(paymentResponse.getPaymentStatus().toString());
            orderRepository.save(newOrder);
        } catch (Exception e) {
            newOrder.setStatus(OrderStatus.PAYMENT_FAILED.toString());
            orderRepository.save(newOrder);
            throw new PaymentProcessException(e.getMessage(), e.getCause());
        }

        // publish to rabbitmq if top priority order
        if (orderDto.getPriority().equals(OrderPriority.HIGH) && !newOrder.getStatus().equals(OrderStatus.SCHEDULED.toString())) {
            // publish to RabbitMQ
            try {
                eventPublisher.sendOrderMessage(ORDER_TOP_PRIORITY_ROUTING_KEY,
                                                newOrder.getOrderIdentification(),
                                                newOrder.getPickupLatitude(),
                                                newOrder.getPickupLongitude(),
                                                newOrder.getDestinationLatitude(),
                                                newOrder.getDestinationLongitude(),
                                                newOrder.getOrderValue());
                newOrder.setStatus(OrderStatus.PUBLISHED.toString());
            } catch (final Exception e) {
                newOrder.setStatus(OrderStatus.FAILED_ON_CREATE.toString());
                orderRepository.save(newOrder);
                throw new GenericException(e.getMessage(), e.getCause());
            }
        }

        // add to history repo
        newOrderHistoryEntry.setOrderIdentification(newOrder.getOrderIdentification());
        newOrderHistoryEntry.setAssignedDriver(null);
        newOrderHistoryEntry.setOrderAction(newOrder.getStatus());
        orderHistoryRepository.save(newOrderHistoryEntry);

        return new PostOrderResponseDto(newOrder.getOrderIdentification());

    }

    @Override
    public List<OrderResponseDto> getOrders(Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> orderIdentification, Optional<String> requesterIdentification, Optional<String> driverIdentification, Optional<String> ownerIdentification, ServiceContext serviceContext) throws OrderForbiddenException, GenericException {

        // check access - allow internal, admin or requests where the user id header matches the requester identification
        if (!Validator.isAdmin(serviceContext) && !Validator.isSameEntity(serviceContext, requesterIdentification, driverIdentification, ownerIdentification)) {
            throw new OrderForbiddenException();
        }

        ModelMapper modelMapper = new ModelMapper();
        Pageable pageable = PageRequest.of(offset.orElse(Constants.DEFAULT_VALUE_OFFSET), limit.orElse(Constants.DEFAULT_VALUE_LIMIT));
        List<Order> orders;

        try {
            orders = orderRepository.findAllOrders(status.orElse(""), orderIdentification.orElse(""), requesterIdentification.orElse(""), driverIdentification.orElse(""), ownerIdentification.orElse("") ,pageable);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return orders.stream()
                     .map(order -> modelMapper.map(order, OrderResponseDto.class))
                     .collect(Collectors.toList());

    }

    @Override
    public OrderResponseDto getOrder(String orderIdentification, Boolean includeOrderHistory, ServiceContext serviceContext) throws OrderNotFoundException, OrderForbiddenException, GenericException {

        Optional<Order> order = orderRepository.findByOrderIdentification(orderIdentification);
        ModelMapper modelMapper = new ModelMapper();

        // check that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        // validate if order has assigned driver
        Optional<String> assignedDriver;
        if (null == order.get().getAssignedDriver()) {
            assignedDriver = Optional.empty();
        } else {
            assignedDriver = Optional.of(order.get().getAssignedDriver());
        }

        // validate access
        if (!Validator.isAdmin(serviceContext)
                && !Validator.isSameEntity(serviceContext, Optional.of(order.get().getRequesterIdentification()), assignedDriver, Optional.of(order.get().getOwnerIdentification()))
                && !(serviceContext.getUserId().equals(order.get().getOwnerIdentification()))) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        // fetch order
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        try {
            order = orderRepository.findByOrderIdentification(orderIdentification);
            orderResponseDto = modelMapper.map(order.get(), OrderResponseDto.class);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        // fetch order history
        if (includeOrderHistory) {
            try {
                Date pickedAt = null;
                Date deliveredAt = null;
                Optional<OrderHistory> orderHistoryPicked = orderHistoryRepository.findByOrderIdentificationAndOrderAction(orderIdentification,
                                                                                                                           OrderHistoryActions.PICKED_UP.toString());
                if (orderHistoryPicked.isPresent()) {
                    pickedAt = orderHistoryPicked.get().getCreatedDate();
                }
                Optional<OrderHistory> orderHistoryDelivered = orderHistoryRepository.findByOrderIdentificationAndOrderAction(orderIdentification,
                                                                                                                              OrderHistoryActions.FINALIZED.toString());

                if (orderHistoryDelivered.isPresent()) {
                    deliveredAt = orderHistoryDelivered.get().getCreatedDate();
                }
                orderResponseDto.setHistory(new OrderShortHistoryResponseDto(pickedAt, deliveredAt));
            } catch (final Exception e) {
                throw new GenericException(e.getMessage(), e.getCause());
            }
        }

        return orderResponseDto;

    }

    @Override
    public List<OrderHistoryResponseDto> getOrderHistory(String orderIdentification, Optional<Date> startDate, Optional<Date> endDate, Optional<String> driverIdentification, ServiceContext serviceContext) throws OrderNotFoundException, OrderForbiddenException, GenericException {

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isInternalCommunication(serviceContext)) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        // default dates
        Calendar pastDefaultCalendar = Calendar.getInstance();
        pastDefaultCalendar.set(1900, 1, 1);
        Calendar futureDefaultCalendar = Calendar.getInstance();
        futureDefaultCalendar.set(2999, 1, 1);

        List<OrderHistory> ordersHistoryList = orderHistoryRepository.getOrderHistoryWithFilters(orderIdentification,
                                                                                                 startDate.orElse(pastDefaultCalendar.getTime()),
                                                                                                 endDate.orElse(futureDefaultCalendar.getTime()),
                                                                                                 driverIdentification.orElse(""));
        ModelMapper modelMapper = new ModelMapper();

        // fetch order
        List<OrderHistoryResponseDto> orderHistoryResponseDtoList = null;
        try {
            orderHistoryResponseDtoList = ordersHistoryList.stream()
                                                           .map(orderHistory -> modelMapper.map(orderHistory, OrderHistoryResponseDto.class))
                                                           .collect(Collectors.toList());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return orderHistoryResponseDtoList;

    }

    @Override
    public OrderPhotoResponseDto getOrderPickupPhoto(String orderIdentification, ServiceContext serviceContext) throws OrderNotFoundException, OrderForbiddenException, GenericException {

        Optional<Order> order = orderRepository.findByOrderIdentification(orderIdentification);

        // check that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        // validate if order has assigned driver
        Optional<String> assignedDriver;
        if (null == order.get().getAssignedDriver()) {
            assignedDriver = Optional.empty();
        } else {
            assignedDriver = Optional.of(order.get().getAssignedDriver());
        }

        // validate access
        if (!Validator.isAdmin(serviceContext)
                && !Validator.isSameEntity(serviceContext, Optional.of(order.get().getRequesterIdentification()), assignedDriver, Optional.of(order.get().getOwnerIdentification()))
                && !(serviceContext.getUserId().equals(order.get().getOwnerIdentification()))) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        // fetch order pickup photo
        OrderPhotoResponseDto orderPickupPhotoResponseDto = new OrderPhotoResponseDto();
        try {
            if (null != order.get().getPickupPhoto() && !order.get().getPickupPhoto().isEmpty()) {
                String documentPath = ORDERS_PICKUP_S3_PATH + orderIdentification + "/";
                orderPickupPhotoResponseDto.setPhoto(awsS3Client.downloadFile(documentPath + order.get().getPickupPhoto()));
            }
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return orderPickupPhotoResponseDto;

    }

    @Override
    public OrderPhotoResponseDto getOrderDeliveryPhoto(String orderIdentification, ServiceContext serviceContext) throws OrderNotFoundException, OrderForbiddenException, GenericException {

        Optional<Order> order = orderRepository.findByOrderIdentification(orderIdentification);

        // check that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        // validate if order has assigned driver
        Optional<String> assignedDriver;
        if (null == order.get().getAssignedDriver()) {
            assignedDriver = Optional.empty();
        } else {
            assignedDriver = Optional.of(order.get().getAssignedDriver());
        }

        // validate access
        if (!Validator.isAdmin(serviceContext)
                && !Validator.isSameEntity(serviceContext, Optional.of(order.get().getRequesterIdentification()), assignedDriver, Optional.of(order.get().getOwnerIdentification()))
                && !(serviceContext.getUserId().equals(order.get().getOwnerIdentification()))) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        // fetch order delivery photo
        OrderPhotoResponseDto orderDeliveryPhotoResponseDto= new OrderPhotoResponseDto();
        try {
            if (null != order.get().getDeliveryPhoto() && !order.get().getDeliveryPhoto().isEmpty()) {
                String documentPath = ORDERS_DELIVERY_S3_PATH + orderIdentification + "/";
                orderDeliveryPhotoResponseDto.setPhoto(awsS3Client.downloadFile(documentPath + order.get().getDeliveryPhoto()));
            }
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return orderDeliveryPhotoResponseDto;

    }

    @Override
    @Transactional(rollbackFor = { StatusTransitionNotAllowedException.class, OrderForbiddenException.class, OrderNotFoundException.class, GenericException.class, FeignCommunicationException.class })
    public void updateOrder(String orderIdentification, OrderUpdateRequestDto orderUpdateDto, ServiceContext serviceContext) throws MissingFieldException, InvalidScheduledDateException, FeignCommunicationException, OrderForbiddenException, StatusTransitionNotAllowedException, OrderNotFoundException, GenericException {

        Optional<Order> order = orderRepository.findByOrderIdentification(orderIdentification);

        // check that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        // validate if order has assigned driver
        Optional<String> assignedDriver;
        if (null == order.get().getAssignedDriver()) {
            assignedDriver = Optional.empty();
        } else {
            assignedDriver = Optional.of(order.get().getAssignedDriver());
        }

        // validate if order has owner
        Optional<String> orderOwner;
        if (null == order.get().getOwnerIdentification()) {
            orderOwner = Optional.empty();
        } else {
            orderOwner = Optional.of(order.get().getOwnerIdentification());
        }

        // validate access and status - order can only be updated if not finalized
        if (!Validator.isAdmin(serviceContext)
                && !Validator.isSameEntity(serviceContext, Optional.of(order.get().getRequesterIdentification()), assignedDriver, orderOwner)
                && !(serviceContext.getUserId().equals(orderOwner.orElse("")))) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        // validate status - order can only be updated if not finalized
        if (!Validator.isAdmin(serviceContext)
                && order.get().getStatus().equals(OrderStatus.FINALIZED.toString())) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        Order newOrder = new Order();
        OrderHistory newOrderHistoryEntry = new OrderHistory();
        boolean reQueue = false;

        newOrder = order.get();
        newOrderHistoryEntry.setOrderIdentification(orderIdentification);

        // requester city
        if (orderUpdateDto.getRequesterCity() != null && !orderUpdateDto.getRequesterCity().isEmpty()) {
            newOrder.setRequesterCity(orderUpdateDto.getRequesterCity());
        }
        // requester zip code
        if (orderUpdateDto.getRequesterZipCode() != null && !orderUpdateDto.getRequesterZipCode().isEmpty()) {
            newOrder.setRequesterZipCode(orderUpdateDto.getRequesterZipCode());
        }
        // pickup latitude
        if (null != orderUpdateDto.getPickupLatitude() && !Double.isNaN(orderUpdateDto.getPickupLatitude())) {
            newOrder.setPickupLatitude(orderUpdateDto.getPickupLatitude());
        }
        // pickup longitude
        if (null != orderUpdateDto.getPickupLongitude() && !Double.isNaN(orderUpdateDto.getPickupLongitude())) {
            newOrder.setPickupLongitude(orderUpdateDto.getPickupLongitude());
        }
        // destination country
        if (orderUpdateDto.getDestinationCountry() != null && !orderUpdateDto.getDestinationCountry().isEmpty()) {
            newOrder.setDestinationCountry(orderUpdateDto.getDestinationCountry());
        }
        // destination city
        if (orderUpdateDto.getDestinationCity() != null && !orderUpdateDto.getDestinationCity().isEmpty()) {
            newOrder.setDestinationCity(orderUpdateDto.getDestinationCity());
        }
        // destination zip code
        if (orderUpdateDto.getDestinationZipCode() != null && !orderUpdateDto.getDestinationZipCode().isEmpty()) {
            newOrder.setDestinationZipCode(orderUpdateDto.getDestinationZipCode());
        }
        // destination latitude
        if (null != orderUpdateDto.getDestinationLatitude() && !Double.isNaN(orderUpdateDto.getDestinationLatitude())) {
            newOrder.setDestinationLatitude(orderUpdateDto.getDestinationLatitude());
        }
        // destination longitude
        if (null != orderUpdateDto.getDestinationLongitude() && !Double.isNaN(orderUpdateDto.getDestinationLongitude())) {
            newOrder.setDestinationLongitude(orderUpdateDto.getDestinationLongitude());
        }
        // priority
        if (orderUpdateDto.getPriority() != null && !orderUpdateDto.getPriority().toString().isEmpty()) {
            newOrder.setPriority(orderUpdateDto.getPriority().toString());
        }
        // assigned driver
        if (orderUpdateDto.getAssignedDriver() != null && !orderUpdateDto.getAssignedDriver().isEmpty()) {
            newOrder.setAssignedDriver(orderUpdateDto.getAssignedDriver());
        }
        // delivery eta
        if (null != orderUpdateDto.getDeliveryEta() && !orderUpdateDto.getDeliveryEta().toString().isEmpty()) {
            newOrder.setDeliveryEta(orderUpdateDto.getDeliveryEta());
        }
        // scheduled date
        if (orderUpdateDto.getIsOrderTrackActive() != null && !orderUpdateDto.getIsOrderTrackActive().toString().isEmpty()) {
            newOrder.setIsOrderTrackingActive(orderUpdateDto.getIsOrderTrackActive());
        }
        // scheduled date
        if (orderUpdateDto.getScheduledDate() != null && !orderUpdateDto.getScheduledDate().toString().isEmpty() && null != newOrder.getScheduledDate() && !newOrder.getScheduledDate().toString().isEmpty()) {
            // validate status
            if (!newOrder.getStatus().equals(OrderStatus.SCHEDULED.toString())) {
                throw new StatusTransitionNotAllowedException(newOrder.getStatus(), OrderStatus.SCHEDULED.toString());
            }
            // validate scheduled date (in the future and not in the next 30 minutes)
            if (!Validator.isDateInTheFuture(orderUpdateDto.getScheduledDate(), Long.valueOf(30))) {
                throw new InvalidScheduledDateException(orderUpdateDto.getScheduledDate().toString());
            }
            newOrder.setScheduledDate(orderUpdateDto.getScheduledDate());
        }

        if (orderUpdateDto.getOrderStatus() != null && !orderUpdateDto.getOrderStatus().toString().isEmpty()) {
            // check order status logic
            OrderStatus oldStatus = OrderStatus.valueOf(newOrder.getStatus());
            OrderStatus newStatus = orderUpdateDto.getOrderStatus();
            Optional<OrderHistory> oldOrderHistoryEntry;

            switch(newStatus) {
                case PENDING:
                    newOrder.setStatus(orderUpdateDto.getOrderStatus().toString());
                    newOrder.setAssignedDriver(null);
                    newOrderHistoryEntry.setOrderAction(OrderHistoryActions.CANCELLED.toString());
                    newOrderHistoryEntry.setAssignedDriver(null);
                    reQueue = true;
                    break;
                case ASSIGNED:
                    if (oldStatus != OrderStatus.PENDING) {
                        throw new StatusTransitionNotAllowedException(oldStatus.toString(), newStatus.toString());
                    }
                    newOrder.setStatus(orderUpdateDto.getOrderStatus().toString());
                    newOrderHistoryEntry.setOrderAction(OrderHistoryActions.ASSIGNED.toString());
                    newOrderHistoryEntry.setAssignedDriver(newOrder.getAssignedDriver());
                    break;
                case ACCEPTED:
                    if (oldStatus != OrderStatus.ASSIGNED) {
                        throw new StatusTransitionNotAllowedException(oldStatus.toString(), newStatus.toString());
                    }
                    newOrder.setStatus(orderUpdateDto.getOrderStatus().toString());
                    newOrderHistoryEntry.setOrderAction(OrderHistoryActions.ACCEPTED.toString());
                    newOrderHistoryEntry.setAssignedDriver(newOrder.getAssignedDriver());
                    break;
                case REJECTED:
                    if (oldStatus != OrderStatus.ASSIGNED) {
                        throw new StatusTransitionNotAllowedException(oldStatus.toString(), newStatus.toString());
                    }
                    // set status as PENDING and re-publish in the queue
                    newOrderHistoryEntry.setOrderAction(OrderHistoryActions.REJECTED.toString());
                    newOrderHistoryEntry.setAssignedDriver(newOrder.getAssignedDriver());
                    newOrder.setStatus(OrderStatus.PENDING.toString());
                    newOrder.setAssignedDriver(null);
                    reQueue = true;
                    break;
                case IN_TRANSIT:
                    if (oldStatus != OrderStatus.ACCEPTED) {
                        throw new StatusTransitionNotAllowedException(oldStatus.toString(), newStatus.toString());
                    }
                    if (null == newOrder.getPickupPhoto() || newOrder.getPickupPhoto().isEmpty()) {
                        throw new MissingFieldException("pickup photo", newOrder.getOrderIdentification());
                    }
                    // set effective pickup time
                    oldOrderHistoryEntry = orderHistoryRepository.getLatestOrderEntryByActionAndDriver(orderIdentification,
                                                                                                       OrderHistoryActions.ACCEPTED.toString(),
                                                                                                       assignedDriver.get());
                    if (oldOrderHistoryEntry.isPresent()) {
                        Long acceptedTime = oldOrderHistoryEntry.get().getCreatedDate().getTime() / 1000;
                        Long pickupTime = new Date().getTime() / 1000;
                        Integer effectivePickupTime = pickupTime.intValue() - acceptedTime.intValue();
                        newOrder.setEffectivePickupTime(effectivePickupTime);
                    }
                    newOrderHistoryEntry.setOrderAction(OrderHistoryActions.PICKED_UP.toString());
                    newOrderHistoryEntry.setAssignedDriver(newOrder.getAssignedDriver());
                    newOrder.setStatus(orderUpdateDto.getOrderStatus().toString());
                    break;
                case FINALIZED:
                    if (oldStatus != OrderStatus.IN_TRANSIT) {
                        throw new StatusTransitionNotAllowedException(oldStatus.toString(), newStatus.toString());
                    }
                    if (null == newOrder.getDeliveryPhoto() || newOrder.getDeliveryPhoto().isEmpty()) {
                        throw new MissingFieldException("delivery photo", newOrder.getOrderIdentification());
                    }
                    // set effective delivery time
                    oldOrderHistoryEntry = orderHistoryRepository.getLatestOrderEntryByActionAndDriver(orderIdentification,
                                                                                                       OrderHistoryActions.PICKED_UP.toString(),
                                                                                                       assignedDriver.get());
                    if (oldOrderHistoryEntry.isPresent()) {
                        Long pickedUpTime = oldOrderHistoryEntry.get().getCreatedDate().getTime() / 1000;;
                        Long deliveryTime = new Date().getTime() / 1000;
                        Integer effectiveDeliveryTime = deliveryTime.intValue() - pickedUpTime.intValue();
                        newOrder.setEffectiveDeliveryTime(effectiveDeliveryTime);
                    }
                    newOrderHistoryEntry.setOrderAction(OrderHistoryActions.FINALIZED.toString());
                    newOrderHistoryEntry.setAssignedDriver(newOrder.getAssignedDriver());
                    newOrder.setStatus(orderUpdateDto.getOrderStatus().toString());
                    newOrder.setIsOrderTrackingActive(false);
                    //create payment notification to driver/fiscal entity
                    try {
                        paymentsBridge.createOutPayment(serviceContext, newOrder.getAssignedDriver(), EntityType.DRIVER, newOrder.getOrderIdentification(), newOrder.getDriverFeeValue());
                    } catch (Exception e) {
                        throw new FeignCommunicationException("payment-service", e.getCause());
                    }
                    // set phone number as not in use if exits
                    if (null != newOrder.getOrderExtVoiceSession() && newOrder.getOrderExtVoiceSession().isEmpty()) {
                        try {
                            twilioClient.setNumberInUse(newOrder.getOrderExtVoiceSession(), Boolean.FALSE);
                        } catch (Exception e) {
                            logger.info("error setting phone number " + newOrder.getOrderExtVoiceSession() +  " as not in use for order " + newOrder.getOrderIdentification());
                        }
                    }
                    break;
                default:
                    break;
            }

        }

        orderRepository.save(newOrder);

        // save to history
        orderHistoryRepository.save(newOrderHistoryEntry);

        if (reQueue) {
            // publish to RabbitMQ
            eventPublisher.sendOrderMessage(ORDER_TOP_PRIORITY_ROUTING_KEY, newOrder.getOrderIdentification(),
                                            newOrder.getPickupLatitude(), newOrder.getPickupLongitude(),
                                            newOrder.getDestinationLatitude(), newOrder.getDestinationLongitude(),
                                            newOrder.getOrderValue());
        }

    }

    @Override
    @Transactional(rollbackFor = {ExternalServerException.class, OrderNotFoundException.class, GenericException.class})
    public void upsertPickupPhoto(String orderIdentification, MultipartFile pickupPhoto, ServiceContext serviceContext) throws OrderForbiddenException, ExternalServerException, OrderNotFoundException, GenericException {

        Optional<Order> order = orderRepository.findByOrderIdentification(orderIdentification);

        // validate that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        // validate if order has assigned driver
        Optional<String> assignedDriver;
        if (null == order.get().getAssignedDriver()) {
            assignedDriver = Optional.empty();
        } else {
            assignedDriver = Optional.of(order.get().getAssignedDriver());
        }

        // validate if order has owner
        Optional<String> orderOwner;
        if (null == order.get().getOwnerIdentification()) {
            orderOwner = Optional.empty();
        } else {
            orderOwner = Optional.of(order.get().getOwnerIdentification());
        }

        // validate access
        if (!Validator.isAdmin(serviceContext)
                && !Validator.isSameEntity(serviceContext, Optional.of(order.get().getRequesterIdentification()), assignedDriver, orderOwner)
                && !(serviceContext.getUserId().equals(orderOwner.orElse("")))) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        String documentPath = ORDERS_PICKUP_S3_PATH + orderIdentification + "/";
        Order newOrder = order.get();

        // delete from aws current pickup photo (if it exists)
        if (null != newOrder.getPickupPhoto() && !newOrder.getPickupPhoto().isEmpty()) {
            if (!awsS3Client.deleteFile(documentPath + newOrder.getPickupPhoto())) {
                throw new ExternalServerException("Error deleting file from AWS S3");
            }
        }

        // upload new pickup photo to aws
        String pickupPhotoId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(pickupPhoto.getOriginalFilename());

        // upload to AWS
        if (!awsS3Client.uploadFile(pickupPhoto, documentPath + pickupPhotoId)){
            throw new ExternalServerException("Error uploading file to AWS S3");
        }

        newOrder.setPickupPhoto(pickupPhotoId);

        try {
            orderRepository.save(newOrder);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {ExternalServerException.class, OrderNotFoundException.class, GenericException.class})
    public void upsertDeliveryPhoto(String orderIdentification, MultipartFile deliveryPhoto, ServiceContext serviceContext) throws OrderForbiddenException, ExternalServerException, OrderNotFoundException, GenericException {

        Optional<Order> order = orderRepository.findByOrderIdentification(orderIdentification);

        // validate that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        // validate if order has assigned driver
        Optional<String> assignedDriver;
        if (null == order.get().getAssignedDriver()) {
            assignedDriver = Optional.empty();
        } else {
            assignedDriver = Optional.of(order.get().getAssignedDriver());
        }

        // validate if order has owner
        Optional<String> orderOwner;
        if (null == order.get().getOwnerIdentification()) {
            orderOwner = Optional.empty();
        } else {
            orderOwner = Optional.of(order.get().getOwnerIdentification());
        }

        // validate access
        if (!Validator.isAdmin(serviceContext)
                && !Validator.isSameEntity(serviceContext, Optional.of(order.get().getRequesterIdentification()), assignedDriver, orderOwner)
                && !(serviceContext.getUserId().equals(orderOwner.orElse("")))) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        String documentPath = ORDERS_DELIVERY_S3_PATH + orderIdentification + "/";
        Order newOrder = order.get();

        // delete from aws current delivery photo (if it exists)
        if (null != newOrder.getDeliveryPhoto() && !newOrder.getDeliveryPhoto().isEmpty()) {
            if (!awsS3Client.deleteFile(documentPath + newOrder.getDeliveryPhoto())) {
                throw new ExternalServerException("Error deleting file from AWS S3");
            }
        }

        // upload new delivery photo to aws
        String deliveryPhotoId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(deliveryPhoto.getOriginalFilename());

        // upload to AWS
        if (!awsS3Client.uploadFile(deliveryPhoto, documentPath + deliveryPhotoId)){
            throw new ExternalServerException("Error uploading file to AWS S3");
        }

        newOrder.setDeliveryPhoto(deliveryPhotoId);

        try {
            orderRepository.save(newOrder);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = { OrderForbiddenException.class, OrderNotFoundException.class, GenericException.class })
    public void patchOrderPayment(String orderIdentification, PatchOrderPaymentRequestDto patchOrderPaymentDto, ServiceContext serviceContext) throws OrderForbiddenException, OrderNotFoundException, GenericException {

        Optional<Order> order = orderRepository.findByOrderIdentification(orderIdentification);

        // check that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        // validate that it is admin or internal communication
        if (!Validator.isAdmin(serviceContext) && !Validator.isInternalCommunication(serviceContext)) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        // update order
        Order updatedOrder = order.get();
        try {
            updatedOrder.setPaymentStatus(patchOrderPaymentDto.getPaymentStatus().toString());
            orderRepository.save(updatedOrder);
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = { OrderForbiddenException.class, OrderNotFoundException.class, GenericException.class })
    public void linkUserToOrder(LinkUserToOrderRequestDto linkUserToOrderRequestDto, ServiceContext serviceContext) throws OrderForbiddenException, OrderNotFoundException, GenericException {
        Optional<Order> order = orderRepository.findByRequesterPhoneNumberAndShortOrderIdentification(linkUserToOrderRequestDto.getPhoneNumber(), linkUserToOrderRequestDto.getShortOrderId());

        // validate that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(linkUserToOrderRequestDto.getShortOrderId());
        }

        // validate that user is trying to add his own user id (except if is admin)
        if (!Validator.isAdmin(serviceContext)
                && null != serviceContext.getUserId()
                && !serviceContext.getUserId().equals(linkUserToOrderRequestDto.getUserIdentification())){
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        // update owner
        Order updatedOrder = order.get();
        try {
            updatedOrder.setOwnerIdentification(linkUserToOrderRequestDto.getUserIdentification());
        } catch (Exception e){
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = { OrderForbiddenException.class, OrderNotFoundException.class, GenericException.class })
    public void assignDriverToOrder(String orderIdentification, AssignDriverToOrderRequestDto assignDriverToOrderRequestDto, ServiceContext serviceContext) throws OrderForbiddenException, OrderNotFoundException, GenericException {

        Optional<Order> order = orderRepository.findByOrderIdentification(orderIdentification);
        OrderHistory newOrderHistoryEntry = new OrderHistory();

        // check that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isInternalCommunication(serviceContext)){
            throw new OrderForbiddenException();
        }

        // set driver anonymous phone number

        try {
            Order updatedOrder = order.get();
            updatedOrder.setStatus(OrderStatus.ASSIGNED.toString());
            updatedOrder.setAssignedDriver(assignDriverToOrderRequestDto.getDriverIdentification());
            orderRepository.save(updatedOrder);

            // save history
            newOrderHistoryEntry.setOrderIdentification(orderIdentification);
            newOrderHistoryEntry.setAssignedDriver(assignDriverToOrderRequestDto.getDriverIdentification());
            newOrderHistoryEntry.setOrderAction(OrderHistoryActions.ASSIGNED.toString());
            orderHistoryRepository.save(newOrderHistoryEntry);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    public EstimateResponseDto calculateEstimate(EstimateRequestDto estimateRequestDto, ServiceContext serviceContext) throws PricingException, NoCoverageException, FeignCommunicationException, GoogleAPIException, GenericException, UnirestException, NoEstimateAvailableException {

        List<DriverResponseModel> driverList;
        DriverResponseModel driver = new DriverResponseModel();

        // validate if estimate is within valid range of operation
        // check coverage for pickup
        if (!hasCoverage(estimateRequestDto.getPickupLatitude(), estimateRequestDto.getPickupLongitude())
                || !hasCoverage(estimateRequestDto.getDestinationLatitude(), estimateRequestDto.getDestinationLongitude())) {
            throw new NoCoverageException();
        }

        try {
            driverList = driversBridge.getDrivers(serviceContext,
                                                  estimateRequestDto.getPickupLatitude(),
                                                  estimateRequestDto.getPickupLongitude(),
                                                  LAST_MILE_NEAREST_RADIUS,
                                                  1,
                                                  DriverStatus.AVAILABLE);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex.getCause());
        }

        if (!driverList.isEmpty()) {
            driver = driverList.get(0);
        } else {
            throw new NoEstimateAvailableException("No available drivers found");
        }

        // calculate estimates
        GoogleEstimateResponseDto pickupEstimate = new GoogleEstimateResponseDto();
        GoogleEstimateResponseDto deliveryEstimate = new GoogleEstimateResponseDto();
        try {
            // get estimate from driver location to pickup point
            pickupEstimate = googleConnector.getEstimate(driver.getLatitude(), driver.getLongitude(),
                                                         estimateRequestDto.getPickupLatitude(), estimateRequestDto.getPickupLongitude());
            // get estimate from pickup point to destination
            deliveryEstimate = googleConnector.getEstimate(estimateRequestDto.getPickupLatitude(), estimateRequestDto.getPickupLongitude(),
                                                           estimateRequestDto.getDestinationLatitude(), estimateRequestDto.getDestinationLongitude());
        } catch (Exception ex) {
            throw new GoogleAPIException(ex.getCause());
        }

        // calculate pricing estimate
        ModelMapper modelMapper = new ModelMapper();
        Order order = modelMapper.map(estimateRequestDto, Order.class);
        if (null == order.getRequesterIdentification()) order.setRequesterIdentification("");

        // set estimated distance
        order.setEstimatedDistance(deliveryEstimate.getDistance());

        Double pricingValue = 0.0;
        try {
            pricingValue = calculatePricing(order);
        } catch (Exception e) {
            throw new PricingException(e.getMessage(), e.getCause());
        }

        return new EstimateResponseDto(pickupEstimate.getEta(),
                                       deliveryEstimate.getEta(),
                                       pickupEstimate.getDistance(),
                                       deliveryEstimate.getDistance(),
                                       pricingValue);

    }

    @Override
    public OrderFollowResponseDto trackOrderByShortIdAndPhoneNumber(String phoneNumber, String shortOrderId, ServiceContext serviceContext) throws FeignCommunicationException, OrderForbiddenException, OrderNotFoundException, GenericException {

        Optional<Order> order = orderRepository.findByRequesterPhoneNumberAndShortOrderIdentification(phoneNumber, shortOrderId);

        // validate that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(shortOrderId);
        }

        // validate that tracking is active
        if (!Validator.isAdmin(serviceContext) && !order.get().getIsOrderTrackingActive()) {
            throw new OrderForbiddenException(shortOrderId);
        }

        ModelMapper modelMapper = new ModelMapper();
        OrderFollowResponseDto orderDto = new OrderFollowResponseDto();
        orderDto = modelMapper.map(order.get(), OrderFollowResponseDto.class);

        // try to fetch assigned driver details (if order has assigned driver)
        DriverResponseModel assignedDriver = new DriverResponseModel();
        if (null != order.get().getAssignedDriver() && !order.get().getAssignedDriver().isEmpty()) {
            try {
                assignedDriver = driversBridge.getDriver(serviceContext, order.get().getAssignedDriver(), true);
                // set driver details
                orderDto.setCurrentLatitude(assignedDriver.getLatitude());
                orderDto.setCurrentLongitude(assignedDriver.getLongitude());
                orderDto.setDriverName(assignedDriver.getProfile().getFullName());
                orderDto.setDriverPhoto(assignedDriver.getProfile().getProfilePicture());
                orderDto.setDriverRating(assignedDriver.getDriverRating());
                // vehicle details
                if (null != assignedDriver.getActiveVehicle().getLicensePlate() && !assignedDriver.getActiveVehicle().getLicensePlate().isEmpty()) {
                    orderDto.setDriverVehicleLicensePlate(assignedDriver.getActiveVehicle().getLicensePlate());
                }
                String vehicleMake = "";
                if (null != assignedDriver.getActiveVehicle().getMake() && !assignedDriver.getActiveVehicle().getMake().isEmpty()) {
                    vehicleMake = assignedDriver.getActiveVehicle().getMake();
                }
                String vehicleModel = "";
                if (null != assignedDriver.getActiveVehicle().getModel() && !assignedDriver.getActiveVehicle().getModel().isEmpty()) {
                    vehicleModel = assignedDriver.getActiveVehicle().getModel();
                }
                // set vehicle description
                orderDto.setDriverVehicleDescription(vehicleMake + " " + vehicleModel);
            } catch(Exception e) {
                throw new GenericException(e.getMessage(), e.getCause());
            }
        }

        return orderDto;

    }

    @Override
    public OrderFollowResponseDto trackOrder(String orderIdentification, ServiceContext serviceContext) throws FeignCommunicationException, OrderForbiddenException, OrderNotFoundException, GenericException {

        Optional<Order> order = orderRepository.findByOrderIdentification(orderIdentification);

        // validate that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        // validate that tracking is active
        if (!Validator.isAdmin(serviceContext) 
                && null != order.get().getOwnerIdentification() 
                && !order.get().getOwnerIdentification().equals(serviceContext.getUserId())) {
            throw new OrderForbiddenException(orderIdentification);
        }

        ModelMapper modelMapper = new ModelMapper();
        OrderFollowResponseDto orderDto = new OrderFollowResponseDto();
        orderDto = modelMapper.map(order.get(), OrderFollowResponseDto.class);

        // try to fetch assigned driver details (if order has assigned driver)
        DriverResponseModel assignedDriver = new DriverResponseModel();
        if (null != order.get().getAssignedDriver() && !order.get().getAssignedDriver().isEmpty()) {
            try {
                assignedDriver = driversBridge.getDriver(serviceContext, order.get().getAssignedDriver(), true);
                // set driver details
                orderDto.setCurrentLatitude(assignedDriver.getLatitude());
                orderDto.setCurrentLongitude(assignedDriver.getLongitude());
                orderDto.setDriverName(assignedDriver.getProfile().getFullName());
                orderDto.setDriverPhoto(assignedDriver.getProfile().getProfilePicture());
                orderDto.setDriverRating(assignedDriver.getDriverRating());
                // vehicle details
                if (null != assignedDriver.getActiveVehicle().getLicensePlate() && !assignedDriver.getActiveVehicle().getLicensePlate().isEmpty()) {
                    orderDto.setDriverVehicleLicensePlate(assignedDriver.getActiveVehicle().getLicensePlate());
                }
                String vehicleMake = "";
                if (null != assignedDriver.getActiveVehicle().getMake() && !assignedDriver.getActiveVehicle().getMake().isEmpty()) {
                    vehicleMake = assignedDriver.getActiveVehicle().getMake();
                }
                String vehicleModel = "";
                if (null != assignedDriver.getActiveVehicle().getModel() && !assignedDriver.getActiveVehicle().getModel().isEmpty()) {
                    vehicleModel = assignedDriver.getActiveVehicle().getModel();
                }
                // set vehicle description
                orderDto.setDriverVehicleDescription(vehicleMake + " " + vehicleModel);
            } catch(Exception e) {
                throw new GenericException(e.getMessage(), e.getCause());
            }
        }

        return orderDto;

    }

    @Override
    @Transactional(rollbackFor = { OrderNotFoundException.class, GenericException.class })
    public void rateOrder(String orderIdentification, RateOrderRequestDto rateOrderRequestDto, ServiceContext serviceContext) throws OrderForbiddenException, OrderNotFoundException, GenericException {

        Optional<Order> order = orderRepository.findByOrderIdentification(orderIdentification);

        // check that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        // validate if order has owner
        Optional<String> orderOwner;
        if (null == order.get().getOwnerIdentification()) {
            orderOwner = Optional.empty();
        } else {
            orderOwner = Optional.of(order.get().getOwnerIdentification());
        }

        // validate access
        if (!Validator.isAdmin(serviceContext)
                && !(serviceContext.getUserId().equals(orderOwner.orElse("")))) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        Order updatedOrder = order.get();

        try {
            updatedOrder.setOrderRating(rateOrderRequestDto.getRating());
            orderRepository.save(updatedOrder);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }


    @Override
    @Transactional(rollbackFor = { OrderNotFoundException.class, GenericException.class })
    public void rateOrderWithoutAuth(String phoneNumber, String shortOrderId, RateOrderRequestDto rateOrderRequestDto) throws OrderNotFoundException, GenericException {

        Optional<Order> order = orderRepository.findByRequesterPhoneNumberAndShortOrderIdentification(phoneNumber, shortOrderId);

        // check that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(shortOrderId);
        }

        Order updatedOrder = order.get();

        try {
            updatedOrder.setOrderRating(rateOrderRequestDto.getRating());
            orderRepository.save(updatedOrder);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = { OrderNotFoundException.class, GenericException.class, ExternalServerException.class, NoAssignedDriverException.class, FeignCommunicationException.class,   })
    public OrderContactResponseDto contactDriver(String orderIdentification, ServiceContext serviceContext) throws ExternalServerException, NoAssignedDriverException, FeignCommunicationException, OrderForbiddenException, OrderNotFoundException, GenericException {

        Optional<Order> order = orderRepository.findByOrderIdentification(orderIdentification);

        // check that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        // check order status
        if (!order.get().getStatus().equals(OrderStatus.ACCEPTED.toString())
            && !order.get().getStatus().equals(OrderStatus.ASSIGNED.toString())
            && !order.get().getStatus().equals(OrderStatus.IN_TRANSIT.toString())) {
            throw new OrderNotFoundException(orderIdentification);
        }

        // validate access (must be driver or owner)
        if (null == order.get().getOwnerIdentification()
                || order.get().getOwnerIdentification().isEmpty()
                || !(serviceContext.getUserId().equals(order.get().getOwnerIdentification()))) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        // check if order has assigned driver
        if (null == order.get().getAssignedDriver() || order.get().getAssignedDriver().isEmpty()) {
            throw new NoAssignedDriverException(orderIdentification);
        }

        // if session exists, return the existing one, if not, create a new one
        OrderContactResponseDto orderContactResponseDto = new OrderContactResponseDto();
        if (null != order.get().getOrderExtVoiceSession() && !order.get().getOrderExtVoiceSession().isEmpty()) {
            orderContactResponseDto = new OrderContactResponseDto(order.get().getOrderExtVoiceSession());
        } else {
            String availablePhoneNumber;
            try {
                // get available phone number from twilio
                availablePhoneNumber = twilioClient.getAvailabeNumberFromPool();
            } catch (Exception e) {
                throw new ExternalServerException(e.getMessage(), e.getCause());
            }
            Order updatedOrder = order.get();
            try {
                updatedOrder.setOrderExtVoiceSession(availablePhoneNumber);
                orderRepository.save(updatedOrder);
            } catch (Exception e) {
                throw new GenericException(e.getMessage(), e.getCause());
            }
            orderContactResponseDto = new OrderContactResponseDto(availablePhoneNumber);
        }

        return orderContactResponseDto;
    }

    @Override
    public TwiML handleTwilioVoiceCall(String incomingPhoneNumber, String outgoingAnonymousPhoneNumber, ServiceContext serviceContext) throws FeignCommunicationException, OrderPhoneNumberNotFoundException, GenericException {

        List<Order> orderList = orderRepository.findByActivePhoneNumber(outgoingAnonymousPhoneNumber);

        // check that phone number exists for this order
        if (orderList.isEmpty()) {
            throw new OrderPhoneNumberNotFoundException(outgoingAnonymousPhoneNumber);
        }

        if (orderList.size() > 1) {
            throw new GenericException("handleTwilioVoiceCall. more than one phone number matched this request. phone number: " + outgoingAnonymousPhoneNumber);
        }

        Order order = orderList.get(0);
        String outgoingNumber = null;

        // try to fetch assigned driver details (if order has assigned driver)
        DriverResponseModel assignedDriverResponseModel = new DriverResponseModel();
        if (null != order.getAssignedDriver() && !order.getAssignedDriver().isEmpty()) {
            try {
                assignedDriverResponseModel = driversBridge.getDriver(serviceContext, order.getAssignedDriver(), true);
            } catch(Exception e) {
                throw new FeignCommunicationException("driver-service", e.getMessage(), e.getCause());
            }
        } else {
            throw new GenericException("handleTwilioVoiceCall. no driver matched this request");
        }

        // get real outgoing phone number - if driver is making the call, contact the order requester
        if (incomingPhoneNumber.equals(assignedDriverResponseModel.getProfile().getPhoneNumber())) {
            outgoingNumber = order.getRequesterPhoneNumber();
        // if it is not the driver making the call, contact the driver
        } else {
            outgoingNumber = assignedDriverResponseModel.getProfile().getPhoneNumber();
        }

        // get twilio response
        TwiML twilioResponse;
        try {
            twilioResponse = twilioClient.startVoiceCall(outgoingAnonymousPhoneNumber, outgoingNumber);
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return twilioResponse;

    }

    @Override
    public Boolean hasCoverage(Double latitude, Double longitude) {
        if (orderCoverageRepository.getOrderCoverage(latitude, longitude).isEmpty()) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    @Override
    public void processScheduledOrders(ServiceContext serviceContext) throws OrderForbiddenException, GenericException {
        // verify if admin or batch
        if (!Validator.isAdmin(serviceContext) && ! Validator.isBatch(serviceContext)) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        // get all scheduled orders for the next 30 minutes
        Integer averageAssignTime = Integer.valueOf(orderPropertiesRepository.findByEnvironmentAndProperty(environment, AVG_DRIVER_ASSIGN_TIME_PROPERTY_NAME).get().getValue());
        List<Order> ordersList = orderRepository.getOrdersToBeProcessed(averageAssignTime);

        // iterate through orders and publish them to rabbitmq (if top priority)
        for (Order order : ordersList) {
            if (order.getPriority().equals(OrderPriority.HIGH.toString())) {
                try {
                    eventPublisher.sendOrderMessage(ORDER_TOP_PRIORITY_ROUTING_KEY,
                                                    order.getOrderIdentification(),
                                                    order.getPickupLatitude(),
                                                    order.getPickupLongitude(),
                                                    order.getDestinationLatitude(),
                                                    order.getDestinationLongitude(),
                                                    order.getOrderValue());
                } catch (final Exception e) {
                    order.setStatus(OrderStatus.FAILED_ON_CREATE.toString());
                    orderRepository.save(order);
                }
                // update order
                order.setStatus(OrderStatus.PUBLISHED.toString());
                orderRepository.save(order);
                // add to history repo
                OrderHistory newOrderHistoryEntry = new OrderHistory();
                newOrderHistoryEntry.setOrderIdentification(order.getOrderIdentification());
                newOrderHistoryEntry.setOrderAction(order.getStatus());
                orderHistoryRepository.save(newOrderHistoryEntry);
            }
        }
    }

    @Override
    public void processAssignedOrders(ServiceContext serviceContext) throws OrderForbiddenException, GenericException {
        // verify if admin or batch
        if (!Validator.isAdmin(serviceContext) && ! Validator.isBatch(serviceContext)) {
            throw new OrderForbiddenException(serviceContext.getUserId());
        }

        // get all orders that were assigned and no processed
        Integer orderTimeoutValue = Integer.valueOf(orderPropertiesRepository.findByEnvironmentAndProperty(environment, ORDER_TIMEOUT_PROPERTY_NAME).get().getValue());
        List<Order> ordersList = orderRepository.getOrdersAssignedAndNotProcessed(orderTimeoutValue);

        // iterate through orders and re-publish them to rabbitmq
        for (Order order : ordersList) {
            try {
                eventPublisher.sendOrderMessage(ORDER_TOP_PRIORITY_ROUTING_KEY,
                                                order.getOrderIdentification(),
                                                order.getPickupLatitude(),
                                                order.getPickupLongitude(),
                                                order.getDestinationLatitude(),
                                                order.getDestinationLongitude(),
                                                order.getOrderValue());
            } catch (final Exception e) {
                order.setStatus(OrderStatus.FAILED_ON_CREATE.toString());
                orderRepository.save(order);
            }
            // update order
            order.setStatus(OrderStatus.PUBLISHED.toString());
            orderRepository.save(order);
            // add to history repo
            OrderHistory newOrderHistoryEntry = new OrderHistory();
            newOrderHistoryEntry.setOrderIdentification(order.getOrderIdentification());
            newOrderHistoryEntry.setOrderAction(order.getStatus());
            orderHistoryRepository.save(newOrderHistoryEntry);
        }

    }

    private Double calculatePricing(Order order) {

        Double pricingValue = 0.0;
        List<BasePricing> pricingRulesList;

        // check if entity has any custom pricing configuration
        if (customPricingRepository.findByEntityIdentification(order.getRequesterIdentification()).isPresent()) {
            List<CustomPricing> customPricingRulesList = customPricingRepository.getCustomPricingConfiguration(order.getRequesterIdentification());
            ModelMapper modelMapper = new ModelMapper();
            pricingRulesList = customPricingRulesList.stream()
                                                     .map(rule -> modelMapper.map(rule, BasePricing.class))
                                                     .collect(Collectors.toList());
        } else {
            pricingRulesList = basePricingRepository.getBasePricingConfiguration();
        }

        // apply pricing
        for (BasePricing rule : pricingRulesList) {
            Object value = null;
            switch (FeeType.valueOf(rule.getFeeType())) {
                case FIXED:
                    pricingValue += rule.getFeeValue();
                    break;
                case MULTIPLIER:
                    value = order.invokeGetter(rule.getReferenceColumn());
                    Double feeValue = rule.getFeeValue() * this.asDouble(value);
                    if (feeValue > rule.getFeeCap()) feeValue = rule.getFeeCap();
                    pricingValue += feeValue;
                    break;
                case EQUALS:
                    value = order.invokeGetter(rule.getReferenceColumn());
                    if (rule.getReferenceColumnValue().equals(value.toString())) pricingValue += rule.getFeeValue();
                    break;
                case SURGE:
                    Double surgeFee = Double.valueOf(orderPropertiesRepository.findByEnvironmentAndProperty(environment, SURGE_FEE_PROPERTY_NAME).get().getValue());
                    pricingValue = pricingValue * surgeFee;
                    break;
            }
        }

        return pricingValue;
    }

    Double asDouble(Object o) {
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        return null;
    }

}