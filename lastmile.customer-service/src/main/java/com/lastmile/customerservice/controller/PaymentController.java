package com.lastmile.customerservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.lastmile.customerservice.dto.payments.CreatePaymentDetailRequestDto;
import com.lastmile.customerservice.dto.payments.CreatePaymentDetailResponseDto;
import com.lastmile.customerservice.dto.payments.GetPaymentDetailResponseDto;
import com.lastmile.customerservice.dto.payments.UpdatePaymentDetailRequestDto;
import com.lastmile.customerservice.service.PaymentService;
import com.lastmile.customerservice.service.exception.PaymentDetailNotFoundException;
import com.lastmile.customerservice.service.exception.CustomerNotFoundException;
import com.lastmile.customerservice.service.exception.FeignCommunicationException;
import com.lastmile.customerservice.service.exception.GenericException;
import com.lastmile.customerservice.service.exception.LinkNotFoundException;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.models.response.ErrorResponse;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;
    private final CustomLogging logger;

    public PaymentController(PaymentService paymentService,
                             CustomLogging logger) {
        this.paymentService = paymentService;
        this.logger = logger;
    }

    /*
     * Endpoint to create a customer payment detail
     */
    @PostMapping("/{customerIdentification}/payment-details")
    public ResponseEntity<?> createCustomerPaymentDetail(HttpServletRequest httpRequest,
                                                       @PathVariable(value = "customerIdentification") String customerIdentification,
                                                       @Valid @RequestBody CreatePaymentDetailRequestDto paymentDto) throws LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException {

        CreatePaymentDetailResponseDto createPaymentDetailResponseDto = new CreatePaymentDetailResponseDto();
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + paymentDto.toString(), httpRequest);

        try {
            createPaymentDetailResponseDto = paymentService.createCustomerPaymentDetail(customerIdentification, paymentDto, serviceContext);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        logger.info("response body: " + createPaymentDetailResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Payment detail created successfully", createPaymentDetailResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to update a customer payment detail
     */
    @PutMapping("/{customerIdentification}/payment-details/{paymentDetailIdentification}")
    public ResponseEntity<?> updateCustomerPaymentDetail(HttpServletRequest httpRequest,
                                                       @PathVariable(value = "customerIdentification") String customerIdentification,
                                                       @PathVariable(value = "paymentDetailIdentification") String paymentDetailIdentification,
                                                       @Valid @RequestBody UpdatePaymentDetailRequestDto paymentDto) throws PaymentDetailNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + paymentDto.toString(), httpRequest);

        try {
            paymentService.updateCustomerPaymentDetail(customerIdentification, paymentDetailIdentification, paymentDto, serviceContext);
        } catch (PaymentDetailNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment detail not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Payment detail updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to delete a customer payment detail
     */
    @DeleteMapping("/{customerIdentification}/payments-details/{paymentDetailIdentification}")
    public ResponseEntity<?> deleteCustomerPaymentDetail(HttpServletRequest httpRequest,
                                                       @PathVariable(value = "customerIdentification") String customerIdentification,
                                                       @PathVariable(value = "paymentDetailIdentification") String paymentDetailIdentification) throws PaymentDetailNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            paymentService.deleteCustomerPaymentDetail(customerIdentification, paymentDetailIdentification, serviceContext);
        } catch (PaymentDetailNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment detail not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Payment detail deleted successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to get a customer payment
     */
    @GetMapping("/{customerIdentification}/payment-details/{paymentDetailIdentification}")
    public ResponseEntity<?> getCustomerPaymentDetail(HttpServletRequest httpRequest,
                                                    @PathVariable(value = "customerIdentification") String customerIdentification,
                                                    @PathVariable(value = "paymentDetailIdentification") String paymentDetailIdentification) throws PaymentDetailNotFoundException, LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        GetPaymentDetailResponseDto getPaymentDetailResponseDto = new GetPaymentDetailResponseDto();

        try {
            getPaymentDetailResponseDto = paymentService.getCustomerPaymentDetail(customerIdentification, paymentDetailIdentification, serviceContext);
        } catch (PaymentDetailNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment detail not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        logger.info("response body: " + getPaymentDetailResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Payment detail retrieved successfully", getPaymentDetailResponseDto), HttpStatus.OK);

    }

    /*
     * Endpoint to get all customer payment details
     */
    @GetMapping("/{customerIdentification}/payment-details/")
    public ResponseEntity<?> getCustomerPaymentDetail(HttpServletRequest httpRequest,
                                                    @PathVariable(value = "customerIdentification") String customerIdentification,
                                                    @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                                    @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                                    @RequestParam(value = "paymentIdentification", required = false) Optional<String> paymentIdentification) throws LinkNotFoundException, CustomerNotFoundException, FeignCommunicationException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        List<GetPaymentDetailResponseDto> getPaymentDetailResponseDto;

        try {
            getPaymentDetailResponseDto = paymentService.getAllCustomerPaymentDetails(customerIdentification, limit, offset, paymentIdentification, serviceContext);
        } catch (CustomerNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Customer not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (LinkNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error calling external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Payment details retrieved successfully", getPaymentDetailResponseDto), HttpStatus.OK);

    }
}