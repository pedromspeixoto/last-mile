package com.lastmile.driverservice.client.orders;

import java.util.List;
import java.util.Optional;

import com.lastmile.driverservice.client.orders.feign.OrderServiceFeignClient;
import com.lastmile.driverservice.controller.response.OrderPhotoResponse;
import com.lastmile.driverservice.controller.response.OrderResponse;
import com.lastmile.driverservice.dto.orders.AssignDriverToOrderRequestDto;
import com.lastmile.driverservice.dto.orders.OrderPhotoResponseDto;
import com.lastmile.driverservice.dto.orders.OrderResponseDto;
import com.lastmile.driverservice.dto.orders.OrderUpdateRequestDto;
import com.lastmile.driverservice.dto.orders.feign.OrderFeignUpdateRequestDto;
import com.lastmile.driverservice.service.exception.FeignCommunicationException;
import com.lastmile.utils.enums.orders.OrderPhotoType;
import com.lastmile.utils.models.response.SuccessResponse;
import com.lastmile.utils.context.ServiceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import feign.FeignException;

@Component
public class OrdersBridge {
    
    private static final String REQUEST_ORIGIN_INTERNAL = "internal";
    private static final String REQUEST_ORIGIN_ENTITY_DRIVER = "driver";    

    @Autowired
    OrderServiceFeignClient orderServiceFeignClient;

    public void assignDriverToOrder(ServiceContext serviceContext, String driverIdentification, String orderIdentification) throws FeignCommunicationException {

        AssignDriverToOrderRequestDto assignDriverToOrderRequestDto = new AssignDriverToOrderRequestDto();
        assignDriverToOrderRequestDto.setDriverIdentification(driverIdentification);

        try {

            ResponseEntity<SuccessResponse> orderResponse = orderServiceFeignClient.assignDriverToOrder(serviceContext.getCorrelationId(),
                                                                                                        serviceContext.getUserId(),
                                                                                                        serviceContext.getPermissions(),
                                                                                                        serviceContext.getRequestId(),
                                                                                                        REQUEST_ORIGIN_INTERNAL,
                                                                                                        REQUEST_ORIGIN_ENTITY_DRIVER,
                                                                                                        driverIdentification,
                                                                                                        assignDriverToOrderRequestDto, 
                                                                                                        orderIdentification);

            if ((orderResponse.getStatusCode() != HttpStatus.OK && orderResponse.getStatusCode() != HttpStatus.CREATED)) {
                throw new FeignCommunicationException("order-service");
            }

        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

    public Optional<OrderResponseDto> getOrder(String driverIdentification, String orderIdentification, Optional<String> status, Optional<String> requesterIdentification, ServiceContext serviceContext) throws FeignCommunicationException {
        
        List<OrderResponseDto> ordersListResponse;
        Optional<OrderResponseDto> orderResponseDto = Optional.empty();

        try {

            ResponseEntity<OrderResponse> orderResponse = orderServiceFeignClient.getOrders(serviceContext.getCorrelationId(),
                                                                                            serviceContext.getUserId(),
                                                                                            serviceContext.getPermissions(),
                                                                                            serviceContext.getRequestId(),
                                                                                            REQUEST_ORIGIN_INTERNAL,
                                                                                            REQUEST_ORIGIN_ENTITY_DRIVER,
                                                                                            driverIdentification,
                                                                                            1,
                                                                                            0,
                                                                                            status.orElse(""),
                                                                                            orderIdentification,
                                                                                            requesterIdentification.orElse(""),
                                                                                            driverIdentification);


            if ((orderResponse.getStatusCode() != HttpStatus.OK && orderResponse.getStatusCode() != HttpStatus.CREATED)) {
                throw new FeignCommunicationException("order-service");
            }

            ordersListResponse = orderResponse.getBody().getData();

            if (ordersListResponse.size() > 1 ) {
                throw new FeignCommunicationException("order-service returned more than one result");
            }

            orderResponseDto = Optional.of(orderResponse.getBody().getData().get(0));
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

        return orderResponseDto;
    }

    public OrderPhotoResponseDto getOrderPhoto(String driverIdentification, String orderIdentification, OrderPhotoType orderPhotoType, ServiceContext serviceContext) throws FeignCommunicationException {
        
        OrderPhotoResponseDto orderPhotoResponseDto = new OrderPhotoResponseDto();
        ResponseEntity<OrderPhotoResponse> orderPhotoResponse = null;

        try {
            switch (orderPhotoType) {
                case PICKUP:
                    orderPhotoResponse = orderServiceFeignClient.getOrderPickupPhoto(serviceContext.getCorrelationId(),
                                                                                     serviceContext.getUserId(),
                                                                                     serviceContext.getPermissions(),
                                                                                     serviceContext.getRequestId(),
                                                                                     REQUEST_ORIGIN_INTERNAL,
                                                                                     REQUEST_ORIGIN_ENTITY_DRIVER,
                                                                                     driverIdentification,
                                                                                     orderIdentification);
                    break;
                case DELIVERY:
                    orderPhotoResponse = orderServiceFeignClient.getOrderDeliveryPhoto(serviceContext.getCorrelationId(),
                                                                                       serviceContext.getUserId(),
                                                                                       serviceContext.getPermissions(),
                                                                                       serviceContext.getRequestId(),
                                                                                       REQUEST_ORIGIN_INTERNAL,
                                                                                       REQUEST_ORIGIN_ENTITY_DRIVER,
                                                                                       driverIdentification,
                                                                                       orderIdentification);
                    break;
            }

            if (orderPhotoResponse.getStatusCode() != HttpStatus.OK) {
                throw new FeignCommunicationException("order-service");
            }

            orderPhotoResponseDto = orderPhotoResponse.getBody().getData();
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

        return orderPhotoResponseDto;
    }

    public List<OrderResponseDto> getOrders(String driverIdentification, Optional<String> status, Optional<String> requesterIdentification, ServiceContext serviceContext) throws FeignCommunicationException {
        List<OrderResponseDto> ordersListResponse;

        try {

            ResponseEntity<OrderResponse> orderResponse = orderServiceFeignClient.getOrders(serviceContext.getCorrelationId(),
                                                                                            serviceContext.getUserId(),
                                                                                            serviceContext.getPermissions(),
                                                                                            serviceContext.getRequestId(),
                                                                                            REQUEST_ORIGIN_INTERNAL,
                                                                                            REQUEST_ORIGIN_ENTITY_DRIVER,
                                                                                            driverIdentification,
                                                                                            1,
                                                                                            0,
                                                                                            status.orElse(""),
                                                                                            "",
                                                                                            requesterIdentification.orElse(""),
                                                                                            driverIdentification);


            if ((orderResponse.getStatusCode() != HttpStatus.OK && orderResponse.getStatusCode() != HttpStatus.CREATED)) {
                throw new FeignCommunicationException("order-service");
            }

            ordersListResponse = orderResponse.getBody().getData();
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

        return ordersListResponse;
    }

    public boolean updateOrder(String driverIdentification, String orderIdentification, OrderUpdateRequestDto orderDto, ServiceContext serviceContext) throws FeignCommunicationException {

        try {
            ResponseEntity<SuccessResponse> orderResponse = orderServiceFeignClient.updateOrder(serviceContext.getCorrelationId(),
                                                                                                serviceContext.getUserId(),
                                                                                                serviceContext.getPermissions(),
                                                                                                serviceContext.getRequestId(),
                                                                                                REQUEST_ORIGIN_INTERNAL,
                                                                                                REQUEST_ORIGIN_ENTITY_DRIVER,
                                                                                                driverIdentification,
                                                                                                orderIdentification,
                                                                                                OrderFeignUpdateRequestDto.mapToFeignRequest(orderDto));
            if (orderResponse.getStatusCode() != HttpStatus.OK && orderResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("order-service");
            }

        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex.getCause());
        }
        return true;

    }

    public void upsertPhoto(String driverIdentification, String orderIdentification, OrderPhotoType orderPhotoType, MultipartFile photo, ServiceContext serviceContext) throws FeignCommunicationException {
        
        ResponseEntity<SuccessResponse> orderPhotoResponse = null;

        try {
            switch (orderPhotoType) {
                case PICKUP:
                    orderPhotoResponse = orderServiceFeignClient.upsertOrderPickupPhoto(serviceContext.getCorrelationId(),
                                                                                        serviceContext.getUserId(),
                                                                                        serviceContext.getPermissions(),
                                                                                        serviceContext.getRequestId(),
                                                                                        REQUEST_ORIGIN_INTERNAL,
                                                                                        REQUEST_ORIGIN_ENTITY_DRIVER,
                                                                                        driverIdentification,
                                                                                        orderIdentification,
                                                                                        photo);
                    break;
                case DELIVERY:
                    orderPhotoResponse = orderServiceFeignClient.upsertOrderDeliveryPhoto(serviceContext.getCorrelationId(),
                                                                                        serviceContext.getUserId(),
                                                                                        serviceContext.getPermissions(),
                                                                                        serviceContext.getRequestId(),
                                                                                        REQUEST_ORIGIN_INTERNAL,
                                                                                        REQUEST_ORIGIN_ENTITY_DRIVER,
                                                                                        driverIdentification,
                                                                                        orderIdentification,
                                                                                        photo);
                    break;
            }

            if (orderPhotoResponse.getStatusCode() != HttpStatus.OK && orderPhotoResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new FeignCommunicationException("order-service");
            }

        } catch (FeignException ex) {
            throw new FeignCommunicationException("order-service", ex);
        }

    }


}