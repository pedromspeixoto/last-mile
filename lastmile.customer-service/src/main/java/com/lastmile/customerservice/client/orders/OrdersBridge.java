package com.lastmile.customerservice.client.orders;

import java.util.List;
import java.util.Optional;

import com.lastmile.customerservice.client.orders.feign.OrderServiceFeignClient;
import com.lastmile.customerservice.controller.response.OrderResponse;
import com.lastmile.customerservice.domain.Customer;
import com.lastmile.customerservice.dto.orders.OrderRequestDto;
import com.lastmile.customerservice.dto.orders.OrderResponseDto;
import com.lastmile.customerservice.dto.orders.PostOrderResponseDto;
import com.lastmile.customerservice.dto.orders.feign.OrderFeignRequestDto;
import com.lastmile.customerservice.dto.orders.feign.OrderFeignUpdateRequestDto;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.models.response.SuccessResponse;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class OrdersBridge {

    private static final String REQUEST_ORIGIN_INTERNAL = "internal";
    private static final String REQUEST_ORIGIN_ENTITY = "customer";

    @Autowired
    OrderServiceFeignClient orderServiceFeignClient;

    public PostOrderResponseDto createNewOrder(Customer customer, OrderRequestDto orderDto, ServiceContext serviceContext) throws FeignCommunicationException {

        PostOrderResponseDto postOrderResponseDto = new PostOrderResponseDto();
        ModelMapper modelMapper = new ModelMapper();

        try {

            ResponseEntity<SuccessResponse> orderResponse = orderServiceFeignClient.createOrder(serviceContext.getCorrelationId(),
                                                                                                serviceContext.getUserId(),
                                                                                                serviceContext.getPermissions(),
                                                                                                serviceContext.getRequestId(),
                                                                                                REQUEST_ORIGIN_INTERNAL,
                                                                                                REQUEST_ORIGIN_ENTITY,
                                                                                                customer.getCustomerIdentification(),
                                                                                                OrderFeignRequestDto.mapToFeignRequest(customer.getCustomerIdentification(),
                                                                                                                                       customer.getPublicName(),
                                                                                                                                       customer.getActivePaymentDetailsId(),
                                                                                                                                       orderDto));

            if ((orderResponse.getStatusCode() != HttpStatus.OK && orderResponse.getStatusCode() != HttpStatus.CREATED)) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + orderResponse.getStatusCode());
            }

            postOrderResponseDto = modelMapper.map(orderResponse.getBody().getData(), PostOrderResponseDto.class); 
    
        } catch (FeignException ex) {
            ex.printStackTrace();
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

        return postOrderResponseDto;
    }

    public Optional<OrderResponseDto> getOrder(String customerIdentification, String orderIdentification, Optional<String> status, Optional<String> assignedDriver, ServiceContext serviceContext) throws FeignCommunicationException {
        
        List<OrderResponseDto> ordersListResponse;
        Optional<OrderResponseDto> orderResponseDto = Optional.empty();

        try {

            ResponseEntity<OrderResponse> orderResponse = orderServiceFeignClient.getOrders(serviceContext.getCorrelationId(),
                                                                                            serviceContext.getUserId(),
                                                                                            serviceContext.getPermissions(),
                                                                                            serviceContext.getRequestId(),
                                                                                            REQUEST_ORIGIN_INTERNAL,
                                                                                            REQUEST_ORIGIN_ENTITY,
                                                                                            customerIdentification,
                                                                                            1,
                                                                                            0,
                                                                                            status.orElse(""),
                                                                                            orderIdentification,
                                                                                            customerIdentification,
                                                                                            assignedDriver.orElse(""));


            if ((orderResponse.getStatusCode() != HttpStatus.OK && orderResponse.getStatusCode() != HttpStatus.CREATED)) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + orderResponse.getStatusCode());
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

    public List<OrderResponseDto> getOrders(String customerIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> assignedDriver, ServiceContext serviceContext) throws FeignCommunicationException {
        List<OrderResponseDto> ordersListResponse;

        try {

            ResponseEntity<OrderResponse> orderResponse = orderServiceFeignClient.getOrders(serviceContext.getCorrelationId(),
                                                                                            serviceContext.getUserId(),
                                                                                            serviceContext.getPermissions(),
                                                                                            serviceContext.getRequestId(),
                                                                                            REQUEST_ORIGIN_INTERNAL,
                                                                                            REQUEST_ORIGIN_ENTITY,
                                                                                            customerIdentification,
                                                                                            limit.orElse(10),
                                                                                            offset.orElse(0),
                                                                                            status.orElse(""),
                                                                                            "",
                                                                                            customerIdentification,
                                                                                            assignedDriver.orElse(""));


            if ((orderResponse.getStatusCode() != HttpStatus.OK && orderResponse.getStatusCode() != HttpStatus.CREATED)) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + orderResponse.getStatusCode());
            }

            ordersListResponse = orderResponse.getBody().getData();
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

        return ordersListResponse;
    }

    public boolean updateOrder(String customerIdentification, String orderIdentification, OrderRequestDto orderDto, ServiceContext serviceContext) throws FeignCommunicationException {

        try {

            ResponseEntity<SuccessResponse> orderResponse = orderServiceFeignClient.updateOrder(serviceContext.getCorrelationId(),
                                                                                                serviceContext.getUserId(),
                                                                                                serviceContext.getPermissions(),
                                                                                                serviceContext.getRequestId(),
                                                                                                REQUEST_ORIGIN_INTERNAL,
                                                                                                REQUEST_ORIGIN_ENTITY,
                                                                                                customerIdentification,
                                                                                                orderIdentification,
                                                                                                OrderFeignUpdateRequestDto.mapToFeignRequest(orderDto));

            if (orderResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }

            if ((orderResponse.getStatusCode() != HttpStatus.OK && orderResponse.getStatusCode() != HttpStatus.CREATED)) {
                throw new FeignCommunicationException("Status code was not 200 or 201 - " + orderResponse.getStatusCode());
            }
    
        } catch (FeignException ex) {
            throw new FeignCommunicationException("order-service", ex);
        }

        return true;

    }

}