package com.lastmile.customerservice.service.impl;

import java.util.List;
import java.util.Optional;

import com.lastmile.customerservice.client.orders.OrdersBridge;
import com.lastmile.customerservice.domain.Customer;
import com.lastmile.customerservice.domain.CustomerUserLink;
import com.lastmile.customerservice.dto.orders.OrderRequestDto;
import com.lastmile.customerservice.dto.orders.OrderResponseDto;
import com.lastmile.customerservice.dto.orders.PostOrderResponseDto;
import com.lastmile.customerservice.enums.CustomerStatus;
import com.lastmile.customerservice.service.exception.GenericException;
import com.lastmile.customerservice.service.exception.InvalidApiKeyException;
import com.lastmile.customerservice.service.exception.InvalidPaymentDetailsException;
import com.lastmile.customerservice.service.exception.LinkNotFoundException;
import com.lastmile.customerservice.service.exception.OrderNotFoundException;
import com.lastmile.customerservice.service.exception.CustomerNotActiveException;
import com.lastmile.customerservice.service.exception.CustomerNotFoundException;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;
import com.lastmile.customerservice.repository.CustomerRepository;
import com.lastmile.customerservice.repository.CustomerUserLinkRepository;
import com.lastmile.customerservice.service.OrderService;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.validations.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final CustomerUserLinkRepository customerUserLinkRepository;
    private final OrdersBridge ordersBridge;

    public OrderServiceImpl(final CustomerRepository customerRepository, final CustomerUserLinkRepository customerUserLinkRepository,
                            final OrdersBridge ordersBridge) {
        this.customerRepository = customerRepository;
        this.customerUserLinkRepository = customerUserLinkRepository;
        this.ordersBridge = ordersBridge;
    }

    @Override
    @Transactional(rollbackFor = {FeignCommunicationException.class, GenericException.class, LinkNotFoundException.class})
    public PostOrderResponseDto create(String customerIdentification, OrderRequestDto orderDto, String apiKey, ServiceContext serviceContext) throws InvalidPaymentDetailsException, CustomerNotFoundException, FeignCommunicationException, InvalidApiKeyException, CustomerNotActiveException {

        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);

        // validate if customer exists
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // valdiate customer status
        if (!customer.get().getStatus().equals(CustomerStatus.ACTIVE.toString())) {
            throw new CustomerNotActiveException(customerIdentification);
        }

        // validate api key
        if (null != serviceContext.getApiKey() && !customer.get().getApiKey().equals(serviceContext.getApiKey())) {
            throw new InvalidApiKeyException(customerIdentification);
        }

        // validate payment details (active)
        if (null == customer.get().getActivePaymentDetailsId() || customer.get().getActivePaymentDetailsId().isEmpty()){
            throw new InvalidPaymentDetailsException(customerIdentification);
        }

        // try to create order in orders bridge
        PostOrderResponseDto postOrderResponseDto = new PostOrderResponseDto();
        try {
            postOrderResponseDto = ordersBridge.createNewOrder(customer.get(), orderDto, serviceContext);
        } catch (final Exception e) {
            throw new FeignCommunicationException(e.getMessage(), e.getCause());
        }

        return postOrderResponseDto;

    }

    @Override
    public List<OrderResponseDto> getCustomerOrders(String customerIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> status, Optional<String> assignedDriver, ServiceContext serviceContext) throws CustomerNotFoundException, FeignCommunicationException, LinkNotFoundException {
        List<OrderResponseDto> ordersList;

        // validate if customer exists
        if (!customerRepository.findByCustomerIdentification(customerIdentification).isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to fetch orders from orders bridge
        try {
            ordersList = ordersBridge.getOrders(customerIdentification, limit, offset, status, assignedDriver, serviceContext);
        } catch (final Exception e) {
            throw new FeignCommunicationException(e.getMessage(), e.getCause());
        }

        return ordersList;

    }

    @Override
    public OrderResponseDto getCustomerOrder(String customerIdentification, String orderIdentification, ServiceContext serviceContext) throws FeignCommunicationException, LinkNotFoundException, CustomerNotFoundException, OrderNotFoundException {

        Optional<OrderResponseDto> order;

        // validate if customer exists
        if (!customerRepository.findByCustomerIdentification(customerIdentification).isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to fetch order from orders bridge
        try {
            order = ordersBridge.getOrder(customerIdentification, orderIdentification, Optional.empty(), Optional.empty(), serviceContext);
        } catch (final Exception e) {
            throw new FeignCommunicationException(e.getMessage(), e.getCause());
        }

        // validate that order exists
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderIdentification);
        }

        return order.get();
    }

    @Override
    @Transactional(rollbackFor = {FeignCommunicationException.class, OrderNotFoundException.class, CustomerNotFoundException.class, LinkNotFoundException.class})
    public void updateCustomerOrder(String customerIdentification, String orderIdentification, OrderRequestDto order, ServiceContext serviceContext) throws FeignCommunicationException, OrderNotFoundException, CustomerNotFoundException, LinkNotFoundException {

        boolean orderFound;

        // validate if customer exists
        if (!customerRepository.findByCustomerIdentification(customerIdentification).isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to update order from orders bridge
        try {
            orderFound = ordersBridge.updateOrder(customerIdentification, orderIdentification, order, serviceContext);
        } catch (final Exception e) {
            throw new FeignCommunicationException(e.getMessage(), e.getCause());
        }

        if (!orderFound) {
            throw new OrderNotFoundException(orderIdentification);
        }

    }

    // validate if user that performed the request is linked to customer and is not admin
    private boolean isAccountLinkedToCustomer(ServiceContext serviceContext, String customerIdentification) {
        Optional<CustomerUserLink> customerUserLink = customerUserLinkRepository.findByUserIdentificationAndCustomerIdentification(serviceContext.getUserId(), customerIdentification);
        if (!customerUserLink.isPresent() && !Validator.isAdmin(serviceContext)) {
            return false;
        } else {
            return true;
        }
    }

}