package com.lastmile.paymentservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lastmile.paymentservice.domain.Invoice;
import com.lastmile.paymentservice.dto.invoices.CreateInvoiceRequestDto;
import com.lastmile.paymentservice.dto.invoices.CreateInvoiceResponseDto;
import com.lastmile.paymentservice.dto.invoices.GetInvoiceResponseDto;
import com.lastmile.paymentservice.dto.invoices.UpdateInvoiceRequestDto;
import com.lastmile.paymentservice.repository.InvoiceRepository;
import com.lastmile.paymentservice.service.InvoiceService;
import com.lastmile.paymentservice.service.exception.GenericException;
import com.lastmile.paymentservice.service.exception.payments.PaymentForbiddenException;
import com.lastmile.paymentservice.service.exception.invoices.InvoiceNotFoundException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.validations.Validator;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Configuration
public class InvoiceServiceImpl implements InvoiceService {

    private static final Integer DEFAULT_VALUE_LIMIT = 10;
    private static final Integer DEFAULT_VALUE_OFFSET = 0;

    private final InvoiceRepository invoiceRepository;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public InvoiceServiceImpl(final InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Transactional(rollbackFor = { GenericException.class })
    public CreateInvoiceResponseDto createInvoice(CreateInvoiceRequestDto invoiceDto, ServiceContext serviceContext) throws GenericException {

        ModelMapper modelMapper = new ModelMapper();
        // map from DTO
        Invoice invoice = modelMapper.map(invoiceDto, Invoice.class);

        try {
            // set invoice id
            invoice.setInvoiceIdentification(UUID.randomUUID().toString());
            // TO DO - communicate to external service to generate invoice
            // save invoice
            invoiceRepository.save(invoice);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return new CreateInvoiceResponseDto(invoice.getInvoiceIdentification());

    }

    @Override
    public List<GetInvoiceResponseDto> getInvoices(Optional<Integer> limit, Optional<Integer> offset, Optional<String> entityIdentification, Optional<String> entityType, Optional<String> paymentIdentification, Optional<String> invoiceIdentification, ServiceContext serviceContext) throws PaymentForbiddenException, GenericException {

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext, entityIdentification, entityType)) {
            throw new PaymentForbiddenException();
        }

        ModelMapper modelMapper = new ModelMapper();
        List<Invoice> invoices;

        Pageable pageable = PageRequest.of(offset.orElse(DEFAULT_VALUE_OFFSET), limit.orElse(DEFAULT_VALUE_LIMIT));

        try {
            // try to fetch invoices
            invoices = invoiceRepository.findAllInvoices(entityIdentification.orElse(""), entityType.orElse(""), paymentIdentification.orElse(""), invoiceIdentification.orElse(""),pageable);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return invoices.stream().map(payment -> modelMapper.map(payment, GetInvoiceResponseDto.class)).collect(Collectors.toList());

    }

    @Override
    public GetInvoiceResponseDto getInvoice(String invoiceIdentification, ServiceContext serviceContext) throws PaymentForbiddenException, InvoiceNotFoundException, GenericException {

        Optional<Invoice> invoice = invoiceRepository.findByInvoiceIdentification(invoiceIdentification);

        // validate if invoice exists
        if (!invoice.isPresent()) {
            throw new InvoiceNotFoundException(invoiceIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext, Optional.of(invoice.get().getEntityIdentification()), Optional.of(invoice.get().getEntityType()))) {
            throw new PaymentForbiddenException();
        }

        ModelMapper modelMapper = new ModelMapper();
        GetInvoiceResponseDto getInvoiceResponseDto = new GetInvoiceResponseDto();

        try {
            // try to map invoice to dto
            getInvoiceResponseDto = modelMapper.map(invoice.get(), GetInvoiceResponseDto.class);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return getInvoiceResponseDto;
    }

    @Override
    @Transactional(rollbackFor = { InvoiceNotFoundException.class, GenericException.class })
    public void deleteInvoice(String invoiceIdentification, ServiceContext serviceContext) throws PaymentForbiddenException, InvoiceNotFoundException, GenericException {

        Optional<Invoice> invoice = invoiceRepository.findByInvoiceIdentification(invoiceIdentification);

        // validate if invoice exists
        if (!invoice.isPresent()) {
            throw new InvoiceNotFoundException(invoiceIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext, Optional.of(invoice.get().getEntityIdentification()), Optional.of(invoice.get().getEntityType()))) {
            throw new PaymentForbiddenException();
        }

        try {
            invoiceRepository.deleteById(invoice.get().getId());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = { PaymentForbiddenException.class, InvoiceNotFoundException.class, GenericException.class })
    public void updateInvoice(String invoiceIdentification, UpdateInvoiceRequestDto updateInvoiceRequestDto, ServiceContext serviceContext) throws PaymentForbiddenException, InvoiceNotFoundException, GenericException {

        Optional<Invoice> invoice = invoiceRepository.findByInvoiceIdentification(invoiceIdentification);

        // validate if invoice exists
        if (!invoice.isPresent()) {
            throw new InvoiceNotFoundException(invoiceIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext, Optional.of(invoice.get().getEntityIdentification()), Optional.of(invoice.get().getEntityType()))) {
            throw new PaymentForbiddenException();
        }

        try {
            Invoice updatedInvoice = invoice.get();

            // entity identification
            if (updateInvoiceRequestDto.getEntityIdentification() != null && !updateInvoiceRequestDto.getEntityIdentification().toString().isEmpty()) {
                updatedInvoice.setEntityIdentification(updateInvoiceRequestDto.getEntityIdentification().toString());
            }
            // entity type
            if (updateInvoiceRequestDto.getEntityType() != null && !updateInvoiceRequestDto.getEntityType().toString().isEmpty()) {
                updatedInvoice.setEntityType(updateInvoiceRequestDto.getEntityType().toString());
            }
            // invoice client name
            if (updateInvoiceRequestDto.getInvoiceClientName() != null && !updateInvoiceRequestDto.getInvoiceClientName().isEmpty()) {
                updatedInvoice.setInvoiceClientName(updateInvoiceRequestDto.getInvoiceClientName());
            }
            // invoice fiscal number
            if (updateInvoiceRequestDto.getInvoiceFiscalNumber() != null && !updateInvoiceRequestDto.getInvoiceFiscalNumber().isEmpty()) {
                updatedInvoice.setInvoiceFiscalNumber(updateInvoiceRequestDto.getInvoiceFiscalNumber().toString());
            }
            // invoice zip code
            if (updateInvoiceRequestDto.getInvoiceZipCode() != null && !updateInvoiceRequestDto.getInvoiceZipCode().isEmpty()) {
                updatedInvoice.setInvoiceZipCode(updateInvoiceRequestDto.getInvoiceZipCode().toString());
            }
            // invoice url
            if (updateInvoiceRequestDto.getInvoiceUrl() != null && !updateInvoiceRequestDto.getInvoiceUrl().isEmpty()) {
                updatedInvoice.setInvoiceUrl(updateInvoiceRequestDto.getInvoiceUrl().toString());
            }
            // external entity
            if (updateInvoiceRequestDto.getExternalEntity() != null && !updateInvoiceRequestDto.getExternalEntity().isEmpty()) {
                updatedInvoice.setExternalEntity(updateInvoiceRequestDto.getExternalEntity().toString());
            }

            // update invoice
            invoiceRepository.save(updatedInvoice);

        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }
}