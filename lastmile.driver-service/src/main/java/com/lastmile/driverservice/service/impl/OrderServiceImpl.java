package com.lastmile.driverservice.service.impl;

import java.util.List;
import java.util.Optional;

import com.lastmile.driverservice.client.orders.OrdersBridge;
import com.lastmile.driverservice.dto.orders.OrderPhotoResponseDto;
import com.lastmile.driverservice.dto.orders.OrderResponseDto;
import com.lastmile.driverservice.service.exception.OrderNotFoundException;
import com.lastmile.driverservice.service.exception.DriverForbiddenException;
import com.lastmile.driverservice.service.exception.DriverNotFoundException;
import com.lastmile.driverservice.service.exception.FeignCommunicationException;
import com.lastmile.driverservice.repository.DriverRepository;
import com.lastmile.driverservice.service.OrderService;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.orders.OrderPhotoType;
import com.lastmile.utils.validations.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OrderServiceImpl implements OrderService {

    Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final DriverRepository driverRepository;
    private final OrdersBridge ordersBridge;

    public OrderServiceImpl(final DriverRepository driverRepository, final OrdersBridge ordersBridge) {
        this.driverRepository = driverRepository;
        this.ordersBridge = ordersBridge;
    }

    @Override
    public List<OrderResponseDto> getDriverOrders(String driverIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> requesterIdentification, ServiceContext serviceContext) throws DriverNotFoundException, FeignCommunicationException, DriverForbiddenException {
        List<OrderResponseDto> ordersList;

        // validate if driver exists
        if (!driverRepository.findByDriverIdentification(driverIdentification).isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user is linked to driver account
        if (!Validator.isAdmin(serviceContext) && !driverRepository.findByUserIdentification(serviceContext.getUserId()).isPresent()) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        // try to fetch orders from orders bridge
        try {
            ordersList = ordersBridge.getOrders(driverIdentification, status, requesterIdentification, serviceContext);
        } catch (final Exception e) {
            throw new FeignCommunicationException("order-service", e.getCause());
        }

        return ordersList;

    }

    @Override
    public OrderResponseDto getDriverOrder(String driverIdentification, String orderIdentification, ServiceContext serviceContext) throws DriverForbiddenException, FeignCommunicationException, DriverNotFoundException, OrderNotFoundException {

        Optional<OrderResponseDto> order;

        // validate if driver exists
        if (!driverRepository.findByDriverIdentification(driverIdentification).isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user is linked to driver account
        if (!driverRepository.findByUserIdentification(serviceContext.getUserId()).isPresent()) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        // try to fetch order from orders bridge
        try {
            order = ordersBridge.getOrder(driverIdentification, orderIdentification, Optional.empty(), Optional.empty(), serviceContext);
        } catch (final Exception e) {
            throw new FeignCommunicationException("order-service", e.getCause());
        }

        // validate that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        return order.get();
    }

    @Override
    public OrderPhotoResponseDto getDriverOrderPhoto(String driverIdentification, String orderIdentification, OrderPhotoType orderPhotoType, ServiceContext serviceContext) throws DriverForbiddenException, FeignCommunicationException, DriverNotFoundException, OrderNotFoundException {

        OrderPhotoResponseDto orderPhotoDto = new OrderPhotoResponseDto();

        // validate if driver exists
        if (!driverRepository.findByDriverIdentification(driverIdentification).isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user is linked to driver account
        if (!driverRepository.findByUserIdentification(serviceContext.getUserId()).isPresent()) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        // try to fetch order from orders bridge
        try {
            switch (orderPhotoType) {
                case PICKUP:
                    orderPhotoDto = ordersBridge.getOrderPhoto(driverIdentification, orderIdentification, orderPhotoType, serviceContext);
                    break;
                case DELIVERY:
                    orderPhotoDto = ordersBridge.getOrderPhoto(driverIdentification, orderIdentification, orderPhotoType, serviceContext);
                    break;
            }
        } catch (final Exception e) {
            throw new FeignCommunicationException("order-service", e.getCause());
        }

        return orderPhotoDto;
    }

    @Override
    @Transactional(rollbackFor = {FeignCommunicationException.class, DriverForbiddenException.class, DriverNotFoundException.class, OrderNotFoundException.class})
    public void upsertDriverOrderPhoto(String driverIdentification, String orderIdentification, OrderPhotoType orderPhotoType, MultipartFile photo, ServiceContext serviceContext) throws FeignCommunicationException, DriverForbiddenException, DriverNotFoundException, OrderNotFoundException {

        // validate if driver exists
        if (!driverRepository.findByDriverIdentification(driverIdentification).isPresent()) {
            throw new DriverNotFoundException(driverIdentification);
        }

        // validate that user is linked to driver account
        if (!driverRepository.findByUserIdentification(serviceContext.getUserId()).isPresent()) {
            throw new DriverForbiddenException(serviceContext.getUserId());
        }

        // try to fetch order from orders bridge
        try {
            switch (orderPhotoType) {
                case PICKUP:
                    ordersBridge.upsertPhoto(driverIdentification, orderIdentification, orderPhotoType, photo, serviceContext);
                    break;
                case DELIVERY:
                    ordersBridge.upsertPhoto(driverIdentification, orderIdentification, orderPhotoType, photo, serviceContext);
                    break;
            }
        } catch (final Exception e) {
            throw new FeignCommunicationException("order-service", e.getCause());
        }

    }


}