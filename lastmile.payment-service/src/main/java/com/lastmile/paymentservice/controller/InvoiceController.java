package com.lastmile.paymentservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.lastmile.paymentservice.dto.invoices.CreateInvoiceRequestDto;
import com.lastmile.paymentservice.dto.invoices.CreateInvoiceResponseDto;
import com.lastmile.paymentservice.dto.invoices.GetInvoiceResponseDto;
import com.lastmile.paymentservice.dto.invoices.UpdateInvoiceRequestDto;
import com.lastmile.paymentservice.service.InvoiceService;
import com.lastmile.paymentservice.service.exception.payments.PaymentForbiddenException;
import com.lastmile.paymentservice.service.exception.invoices.InvoiceNotFoundException;
import com.lastmile.paymentservice.service.exception.GenericException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.models.response.ErrorResponse;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final CustomLogging logger;

    public InvoiceController(InvoiceService invoiceService, CustomLogging logger) {
        this.invoiceService = invoiceService;
        this.logger = logger;
    }

    /*
     * Endpoint to create new invoice
     */
    @PostMapping("/create")
    public ResponseEntity<?> createNewInvoice(HttpServletRequest httpRequest,
                                              @Valid @RequestBody CreateInvoiceRequestDto invoiceDto) throws GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        CreateInvoiceResponseDto createInvoiceResponseDto = new CreateInvoiceResponseDto();
        logger.info("request body: " + invoiceDto.toString(), httpRequest);

        try {
            createInvoiceResponseDto = invoiceService.createInvoice(invoiceDto, serviceContext);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + createInvoiceResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Invoice created successfully", createInvoiceResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to get all invoices
     */
    @GetMapping
    public ResponseEntity<?> getInvoices(HttpServletRequest httpRequest,
                                         @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                         @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                         @RequestParam(value = "entityIdentification", required = false) Optional<String> entityIdentification,
                                         @RequestParam(value = "entityType", required = false) Optional<String> entityType,
                                         @RequestParam(value = "paymentIdentification", required = false) Optional<String> paymentIdentification,
                                         @RequestParam(value = "invoiceIdentification", required = false) Optional<String> invoiceIdentification) throws PaymentForbiddenException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        List<GetInvoiceResponseDto> invoices;

        try {
            invoices = invoiceService.getInvoices(limit, offset, entityIdentification, entityType, paymentIdentification, invoiceIdentification, serviceContext);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching invoices", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Invoices retrieved successfully", invoices), HttpStatus.OK);

    }

    /*
     * Endpoint to get individual invoice
     */
    @GetMapping("/{invoiceIdentification}")
    public ResponseEntity<?> getInvoice(HttpServletRequest httpRequest,
                                        @PathVariable(value = "invoiceIdentification") String invoiceIdentification) throws InvoiceNotFoundException, PaymentForbiddenException, GenericException {

        GetInvoiceResponseDto invoice;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            invoice = invoiceService.getInvoice(invoiceIdentification, serviceContext);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (InvoiceNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Invoice not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error fetching invoice information.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + invoice.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Invoice retrieved successfully", invoice), HttpStatus.OK);

    }

    /*
     * Endpoint to delete invoice
     */
    @DeleteMapping("/{invoiceIdentification}")
    public ResponseEntity<?> deleteInvoice(HttpServletRequest httpRequest,
                                           @PathVariable(value = "invoiceIdentification") String invoiceIdentification) throws PaymentForbiddenException, InvoiceNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            invoiceService.deleteInvoice(invoiceIdentification, serviceContext);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (InvoiceNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Invoice not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error deleting invoice.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Invoice deleted successfully"), HttpStatus.OK);

    }

    @PutMapping("/{invoiceIdentification}")
    public ResponseEntity<?> updateInvoice(HttpServletRequest httpRequest,
                                           @PathVariable(value = "invoiceIdentification") String invoiceIdentification,
                                           @Valid @RequestBody UpdateInvoiceRequestDto updateInvoiceRequestDto) throws PaymentForbiddenException, InvoiceNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + updateInvoiceRequestDto.toString(), httpRequest);

        try {
            invoiceService.updateInvoice(invoiceIdentification, updateInvoiceRequestDto, serviceContext);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (InvoiceNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Invoice not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error updating invoice.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Invoice updated successfully"), HttpStatus.OK);

    }

}