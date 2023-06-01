package com.lastmile.paymentservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.paymentservice.client.easypay.dto.EasypayCallbackDto;
import com.lastmile.paymentservice.client.easypay.dto.external.EasypayPaymentCallbackDto;
import com.lastmile.paymentservice.dto.payments.CreatePaymentRequestDto;
import com.lastmile.paymentservice.dto.payments.CreatePaymentResponseDto;
import com.lastmile.paymentservice.dto.payments.GetPaymentResponseDto;
import com.lastmile.paymentservice.dto.payments.UpdatePaymentRequestDto;
import com.lastmile.paymentservice.service.exception.GenericException;
import com.lastmile.paymentservice.service.exception.paymentdetails.EasyPayException;
import com.lastmile.paymentservice.service.exception.paymentdetails.PaymentDetailNotFoundException;
import com.lastmile.paymentservice.service.exception.payments.PaymentForbiddenException;
import com.lastmile.paymentservice.service.exception.payments.PaymentNotFoundException;
import com.lastmile.utils.context.ServiceContext;

public interface PaymentService {

    // create a new payment
    CreatePaymentResponseDto createPayment(CreatePaymentRequestDto paymentDto, ServiceContext serviceContext) throws PaymentDetailNotFoundException, EasyPayException, GenericException;

    // get all paymentes
    List<GetPaymentResponseDto> getPayments(Optional<Integer> limit, Optional<Integer> offset, Optional<String> paymentIdentification, Optional<String> entityIdentification, Optional<String> entityType, ServiceContext serviceContext) throws PaymentForbiddenException, GenericException;

    // get payment from payment identification
    GetPaymentResponseDto getPayment(String paymentIdentification, ServiceContext serviceContext) throws PaymentForbiddenException, PaymentNotFoundException, GenericException;

    // delete payment by payment identification
    void deletePayment(String paymentIdentification, ServiceContext serviceContext) throws PaymentForbiddenException, PaymentNotFoundException, GenericException;

    // update payment by payment identification
    void updatePayment(String paymentIdentification, UpdatePaymentRequestDto updatePaymentRequestDto, ServiceContext serviceContext) throws PaymentForbiddenException, PaymentNotFoundException, GenericException;

    // easypay callback
    void easypayCallback(EasypayCallbackDto easyPayCallbackDto, ServiceContext serviceContext) throws PaymentNotFoundException, EasyPayException, PaymentDetailNotFoundException, GenericException;

    // easypay payment callback
    void easypayPaymentCallback(EasypayPaymentCallbackDto easyPayPaymentCallbackDto, ServiceContext serviceContext) throws PaymentNotFoundException, EasyPayException, PaymentDetailNotFoundException, GenericException;

}