package com.lastmile.customerservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.customerservice.dto.orders.OrderRequestDto;
import com.lastmile.customerservice.dto.orders.OrderResponseDto;
import com.lastmile.customerservice.dto.orders.PostOrderResponseDto;
import com.lastmile.customerservice.service.exception.GenericException;
import com.lastmile.customerservice.service.exception.InvalidApiKeyException;
import com.lastmile.customerservice.service.exception.InvalidPaymentDetailsException;
import com.lastmile.customerservice.service.exception.LinkNotFoundException;
import com.lastmile.customerservice.service.exception.OrderNotFoundException;
import com.lastmile.customerservice.service.exception.CustomerNotActiveException;
import com.lastmile.customerservice.service.exception.CustomerNotFoundException;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;

import com.lastmile.utils.context.ServiceContext;

public interface OrderService {

    // creates a new order
    PostOrderResponseDto create(String customerIdentification, OrderRequestDto order, String apiKey, ServiceContext serviceContext) throws InvalidPaymentDetailsException, CustomerNotFoundException, FeignCommunicationException, GenericException, InvalidApiKeyException, CustomerNotActiveException;

    // returns all customers' orders
    List<OrderResponseDto> getCustomerOrders(String customerIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> assignedDriver, ServiceContext serviceContext) throws CustomerNotFoundException, FeignCommunicationException, LinkNotFoundException;

    // return single order details from customer
    OrderResponseDto getCustomerOrder(String customerIdentification, String orderIdentification, ServiceContext serviceContext) throws FeignCommunicationException, LinkNotFoundException, CustomerNotFoundException, OrderNotFoundException;

    // update single order by customer identification and order identification
    void updateCustomerOrder(String customerIdentification, String orderIdentification, OrderRequestDto order, ServiceContext serviceContext) throws FeignCommunicationException, OrderNotFoundException, CustomerNotFoundException, LinkNotFoundException;

}