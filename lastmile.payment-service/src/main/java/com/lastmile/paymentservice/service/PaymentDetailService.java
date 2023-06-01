package com.lastmile.paymentservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.paymentservice.dto.paymentdetails.CreatePaymentDetailRequestDto;
import com.lastmile.paymentservice.dto.paymentdetails.CreatePaymentDetailResponseDto;
import com.lastmile.paymentservice.dto.paymentdetails.GetPaymentDetailResponseDto;
import com.lastmile.paymentservice.dto.paymentdetails.UpdatePaymentDetailRequestDto;
import com.lastmile.paymentservice.service.exception.GenericException;
import com.lastmile.paymentservice.service.exception.paymentdetails.EasyPayException;
import com.lastmile.paymentservice.service.exception.paymentdetails.PaymentDetailNotFoundException;
import com.lastmile.paymentservice.service.exception.payments.PaymentForbiddenException;
import com.lastmile.utils.context.ServiceContext;

public interface PaymentDetailService {

    // create a new payment detail entry
    CreatePaymentDetailResponseDto createPaymentDetail(CreatePaymentDetailRequestDto paymentDetailsDto, ServiceContext serviceContext) throws EasyPayException, PaymentForbiddenException, GenericException;

    // get all payment details
    List<GetPaymentDetailResponseDto> getPaymentDetails(Optional<Integer> limit, Optional<Integer> offset, Optional<String> paymentDetailIdentification, Optional<String> entityIdentification, Optional<String> entityType, ServiceContext serviceContext) throws PaymentForbiddenException, GenericException;

    // get payment detail from payment detail identification
    GetPaymentDetailResponseDto getPaymentDetail(String paymentDetailIdentification, ServiceContext serviceContext) throws PaymentForbiddenException, PaymentDetailNotFoundException, GenericException;

    // delete payment detail by payment identification
    void deletePaymentDetail(String paymentDetailIdentification, ServiceContext serviceContext) throws PaymentForbiddenException, PaymentDetailNotFoundException, GenericException;

    // update payment detail by payment identification
    void updatePaymentDetail(String paymentDetailIdentification, UpdatePaymentDetailRequestDto updatePaymentDetailRequestDto, ServiceContext serviceContext) throws PaymentForbiddenException, PaymentDetailNotFoundException, GenericException;

}