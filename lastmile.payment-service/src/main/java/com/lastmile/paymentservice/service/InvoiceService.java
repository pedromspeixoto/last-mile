package com.lastmile.paymentservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.paymentservice.dto.invoices.CreateInvoiceRequestDto;
import com.lastmile.paymentservice.dto.invoices.CreateInvoiceResponseDto;
import com.lastmile.paymentservice.dto.invoices.GetInvoiceResponseDto;
import com.lastmile.paymentservice.dto.invoices.UpdateInvoiceRequestDto;
import com.lastmile.paymentservice.service.exception.GenericException;
import com.lastmile.paymentservice.service.exception.invoices.InvoiceNotFoundException;
import com.lastmile.paymentservice.service.exception.payments.PaymentForbiddenException;
import com.lastmile.utils.context.ServiceContext;

public interface InvoiceService {

    // create a new invoice
    CreateInvoiceResponseDto createInvoice(CreateInvoiceRequestDto createInvoiceRequestDto, ServiceContext serviceContext) throws GenericException;

    // get all invoices
    List<GetInvoiceResponseDto> getInvoices(Optional<Integer> limit, Optional<Integer> offset, Optional<String> entityIdentification, Optional<String> entityType, Optional<String> paymentIdentification, Optional<String> invoiceIdentification, ServiceContext serviceContext) throws PaymentForbiddenException, GenericException;

    // get invoice from invoice identification
    GetInvoiceResponseDto getInvoice(String invoiceIdentification, ServiceContext serviceContext) throws PaymentForbiddenException, InvoiceNotFoundException, GenericException;

    // delete invoice by invoice identification
    void deleteInvoice(String invoiceIdentification, ServiceContext serviceContext) throws PaymentForbiddenException, InvoiceNotFoundException, GenericException;

    // update invoice by invoice identification
    void updateInvoice(String invoiceIdentification, UpdateInvoiceRequestDto updateInvoiceRequestDto, ServiceContext serviceContext) throws PaymentForbiddenException, InvoiceNotFoundException, GenericException;

}