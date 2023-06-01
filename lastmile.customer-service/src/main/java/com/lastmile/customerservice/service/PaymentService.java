package com.lastmile.customerservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.customerservice.dto.payments.CreatePaymentDetailRequestDto;
import com.lastmile.customerservice.dto.payments.CreatePaymentDetailResponseDto;
import com.lastmile.customerservice.dto.payments.GetPaymentDetailResponseDto;
import com.lastmile.customerservice.dto.payments.UpdatePaymentDetailRequestDto;
import com.lastmile.customerservice.service.exception.GenericException;
import com.lastmile.customerservice.service.exception.LinkNotFoundException;
import com.lastmile.customerservice.service.exception.PaymentDetailNotFoundException;
import com.lastmile.customerservice.service.exception.CustomerNotFoundException;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;

import com.lastmile.utils.context.ServiceContext;

public interface PaymentService {

    // create customer payment detail
    public CreatePaymentDetailResponseDto createCustomerPaymentDetail(String customerIdentification, CreatePaymentDetailRequestDto paymentDetailDto, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException;

    // update customer payment detail
    public void updateCustomerPaymentDetail(String customerIdentification, String paymentDetailIdentification, UpdatePaymentDetailRequestDto paymentDetailDto, ServiceContext serviceContext) throws PaymentDetailNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException;

    // delete customer payment detail
    public void deleteCustomerPaymentDetail(String customerIdentification, String paymentDetailIdentification, ServiceContext serviceContext) throws PaymentDetailNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException;

    // get individual customer payment detail
    public GetPaymentDetailResponseDto getCustomerPaymentDetail(String customerIdentification, String paymentDetailIdentification, ServiceContext serviceContext) throws PaymentDetailNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException;

    // get all customer payments
    public List<GetPaymentDetailResponseDto> getAllCustomerPaymentDetails(String customerIdentification, Optional<Integer> limit, Optional<Integer> offset, Optional<String> paymentDetailIdentification, ServiceContext serviceContext) throws LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException;

}