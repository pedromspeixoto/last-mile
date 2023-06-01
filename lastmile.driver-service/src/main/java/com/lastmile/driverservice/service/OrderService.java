package com.lastmile.driverservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.driverservice.dto.orders.OrderPhotoResponseDto;
import com.lastmile.driverservice.dto.orders.OrderResponseDto;
import com.lastmile.driverservice.service.exception.DriverForbiddenException;
import com.lastmile.driverservice.service.exception.OrderNotFoundException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.orders.OrderPhotoType;

import org.springframework.web.multipart.MultipartFile;

import com.lastmile.driverservice.service.exception.DriverNotFoundException;
import com.lastmile.driverservice.service.exception.FeignCommunicationException;

public interface OrderService {

    // returns all drivers' orders
    List<OrderResponseDto> getDriverOrders(String driverIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> requesterIdentification, ServiceContext serviceContext) throws DriverNotFoundException, FeignCommunicationException, DriverForbiddenException;

    // return single order details from driver
    OrderResponseDto getDriverOrder(String driverIdentification, String orderIdentification, ServiceContext serviceContext) throws FeignCommunicationException, DriverForbiddenException, DriverNotFoundException, OrderNotFoundException;

    // return single order photo from driver
    OrderPhotoResponseDto getDriverOrderPhoto(String driverIdentification, String orderIdentification, OrderPhotoType orderPhotoType, ServiceContext serviceContext) throws FeignCommunicationException, DriverForbiddenException, DriverNotFoundException, OrderNotFoundException;

    // upsert order photo
    void upsertDriverOrderPhoto(String driverIdentification, String orderIdentification, OrderPhotoType orderPhotoType, MultipartFile photo, ServiceContext serviceContext) throws FeignCommunicationException, DriverForbiddenException, DriverNotFoundException, OrderNotFoundException;

}