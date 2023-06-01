package com.lastmile.customerservice.service.impl;

import java.util.List;
import java.util.Optional;

import com.lastmile.customerservice.client.payments.PaymentBridge;
import com.lastmile.customerservice.domain.Customer;
import com.lastmile.customerservice.domain.CustomerUserLink;
import com.lastmile.customerservice.dto.payments.CreatePaymentDetailRequestDto;
import com.lastmile.customerservice.dto.payments.CreatePaymentDetailResponseDto;
import com.lastmile.customerservice.dto.payments.GetPaymentDetailResponseDto;
import com.lastmile.customerservice.dto.payments.UpdatePaymentDetailRequestDto;
import com.lastmile.customerservice.service.exception.LinkNotFoundException;
import com.lastmile.customerservice.service.exception.PaymentDetailNotFoundException;
import com.lastmile.customerservice.service.exception.CustomerNotFoundException;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;
import com.lastmile.customerservice.repository.CustomerRepository;
import com.lastmile.customerservice.repository.CustomerUserLinkRepository;
import com.lastmile.customerservice.service.PaymentService;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.validations.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final CustomerUserLinkRepository customerUserLinkRepository;
    private final PaymentBridge paymentBridge;

    public PaymentServiceImpl(final CustomerRepository customerRepository,
                              final CustomerUserLinkRepository customerUserLinkRepository,
                              final PaymentBridge paymentBridge) {
        this.customerRepository = customerRepository;
        this.customerUserLinkRepository = customerUserLinkRepository;
        this.paymentBridge = paymentBridge;
    }

    @Override
    @Transactional(rollbackFor = {LinkNotFoundException.class, CustomerNotFoundException.class, FeignCommunicationException.class})
    public CreatePaymentDetailResponseDto createCustomerPaymentDetail(String customerIdentification, CreatePaymentDetailRequestDto paymentDetailDto, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException {

        // validate if customer exists
        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to create paymentDetail
        CreatePaymentDetailResponseDto createPaymentDetailResponseDto = new CreatePaymentDetailResponseDto();
        try {
            String customerName = (customer.get().getPublicName() == null) ? "" : customer.get().getPublicName();
            String customerEmail = (customer.get().getCustomerEmail() == null) ? "" : customer.get().getCustomerEmail();
            String customerPhoneNumber =  (customer.get().getCustomerPhoneNumber() == null) ? "" : customer.get().getCustomerPhoneNumber();
            String customerNif =  (customer.get().getNif() == null) ? "" : customer.get().getNif();
            createPaymentDetailResponseDto = paymentBridge.createPaymentDetail(customerIdentification,
                                                                               EntityType.MARKETPLACE,
                                                                               customerName,
                                                                               customerEmail,
                                                                               customerPhoneNumber,
                                                                               customerNif,
                                                                               paymentDetailDto,
                                                                               serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }

        return createPaymentDetailResponseDto;
    }


    @Override
    @Transactional(rollbackFor = {PaymentDetailNotFoundException.class, LinkNotFoundException.class, CustomerNotFoundException.class, FeignCommunicationException.class})
    public void updateCustomerPaymentDetail(String customerIdentification, String paymentDetailIdentification, UpdatePaymentDetailRequestDto paymentDetailDto, ServiceContext serviceContext) throws PaymentDetailNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException {

        // validate if customer exists
        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to update paymentDetail
        try {
            paymentBridge.updatePaymentDetail(customerIdentification, EntityType.MARKETPLACE.toString(), paymentDetailDto, serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = {PaymentDetailNotFoundException.class, LinkNotFoundException.class, CustomerNotFoundException.class, FeignCommunicationException.class})
    public void deleteCustomerPaymentDetail(String customerIdentification, String paymentDetailIdentification, ServiceContext serviceContext) throws PaymentDetailNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException {
        
        // validate if customer exists
        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to delete paymentDetail
        try {
            paymentBridge.deletePaymentDetail(customerIdentification, EntityType.MARKETPLACE.toString(), serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }
    }

    @Override
    public GetPaymentDetailResponseDto getCustomerPaymentDetail(String customerIdentification, String paymentDetailIdentification, ServiceContext serviceContext) throws PaymentDetailNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException {
        // validate if customer exists
        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to get paymentDetail
        Optional<GetPaymentDetailResponseDto> getPaymentDetailResponseDto;
        try {
            getPaymentDetailResponseDto = paymentBridge.getPaymentDetail(customerIdentification, paymentDetailIdentification, serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }

        // validate that paymentDetail exists
        if (!getPaymentDetailResponseDto.isPresent()) {
            throw new PaymentDetailNotFoundException(paymentDetailIdentification);
        }

        return getPaymentDetailResponseDto.get();
    }

    @Override
    public List<GetPaymentDetailResponseDto> getAllCustomerPaymentDetails(String customerIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> paymentDetailIdentification, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException {
        // validate if customer exists
        Optional<Customer> customer = customerRepository.findByCustomerIdentification(customerIdentification);
        if (!customer.isPresent()) {
            throw new CustomerNotFoundException(customerIdentification);
        }

        // validate link
        if (!isAccountLinkedToCustomer(serviceContext, customerIdentification)) {
            throw new LinkNotFoundException(customerIdentification, serviceContext.getUserId());
        }

        // try to get payments
        List<GetPaymentDetailResponseDto> payments;
        try {
            payments = paymentBridge.getPaymentDetails(customerIdentification, limit, offset, paymentDetailIdentification, serviceContext);
        } catch (Exception ex) {
            throw new FeignCommunicationException(ex.getMessage());
        }

        return payments;
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