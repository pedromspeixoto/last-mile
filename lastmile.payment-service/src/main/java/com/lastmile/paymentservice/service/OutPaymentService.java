package com.lastmile.paymentservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.paymentservice.dto.outpayments.CreateOutPaymentRequestDto;
import com.lastmile.paymentservice.dto.outpayments.CreateOutPaymentResponseDto;
import com.lastmile.paymentservice.dto.outpayments.GetOutPaymentResponseDto;
import com.lastmile.paymentservice.dto.outpayments.UpdateOutPaymentRequestDto;
import com.lastmile.paymentservice.service.exception.FeignCommunicationException;
import com.lastmile.paymentservice.service.exception.GenericException;
import com.lastmile.paymentservice.service.exception.paymentdetails.EasyPayException;
import com.lastmile.paymentservice.service.exception.outpayments.NoActiveSourceAccountFoundException;
import com.lastmile.paymentservice.service.exception.outpayments.OutPaymentAlreadyProcessedException;
import com.lastmile.paymentservice.service.exception.outpayments.OutPaymentForbiddenException;
import com.lastmile.paymentservice.service.exception.outpayments.OutPaymentNotFoundException;
import com.lastmile.paymentservice.service.exception.outpayments.RequestEntityDetailsNotFoundException;
import com.lastmile.utils.context.ServiceContext;

public interface OutPaymentService {

    // create a new out payment
    CreateOutPaymentResponseDto createOutPayment(CreateOutPaymentRequestDto outPaymentDto, ServiceContext serviceContext) throws GenericException;

    // get all out payments
    List<GetOutPaymentResponseDto> getOutPayments(Optional<Integer> limit, Optional<Integer> offset, Optional<String> outPaymentIdentification, Optional<String> entityIdentification, Optional<String> entityType, ServiceContext serviceContext) throws OutPaymentForbiddenException, GenericException;

    // get out payment from out payment identification
    GetOutPaymentResponseDto getOutPayment(String outPaymentIdentification, ServiceContext serviceContext) throws OutPaymentForbiddenException, OutPaymentNotFoundException, GenericException;

    // delete out payment by out payment identification
    void deleteOutPayment(String outPaymentIdentification, ServiceContext serviceContext) throws OutPaymentForbiddenException, OutPaymentNotFoundException, GenericException;

    // update out payment by out payment identification
    void updateOutPayment(String outPaymentIdentification, UpdateOutPaymentRequestDto updateOutPaymentRequestDto, ServiceContext serviceContext) throws OutPaymentForbiddenException, OutPaymentNotFoundException, GenericException;

    // process single out payment
    void processSingleOutPayment(String outPaymentIdentification, ServiceContext serviceContext) throws OutPaymentAlreadyProcessedException, FeignCommunicationException, RequestEntityDetailsNotFoundException, NoActiveSourceAccountFoundException, OutPaymentNotFoundException, GenericException, EasyPayException;

    // process out payments (batch)
    void processOutPayments(ServiceContext serviceContext) throws OutPaymentForbiddenException, GenericException, EasyPayException;

}