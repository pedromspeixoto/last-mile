package com.lastmile.paymentservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.lastmile.paymentservice.client.easypay.dto.EasypayCallbackDto;
import com.lastmile.paymentservice.client.easypay.dto.external.EasypayPaymentCallbackDto;
import com.lastmile.paymentservice.dto.payments.CreatePaymentRequestDto;
import com.lastmile.paymentservice.dto.payments.CreatePaymentResponseDto;
import com.lastmile.paymentservice.dto.payments.GetPaymentResponseDto;
import com.lastmile.paymentservice.dto.payments.UpdatePaymentRequestDto;
import com.lastmile.paymentservice.service.exception.paymentdetails.EasyPayException;
import com.lastmile.paymentservice.service.PaymentService;
import com.lastmile.paymentservice.service.exception.payments.PaymentForbiddenException;
import com.lastmile.paymentservice.service.exception.payments.PaymentNotFoundException;
import com.lastmile.paymentservice.service.exception.GenericException;
import com.lastmile.paymentservice.service.exception.paymentdetails.PaymentDetailNotFoundException;
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

    public PaymentController(PaymentService paymentService, CustomLogging logger) {
        this.paymentService = paymentService;
        this.logger = logger;
    }

    /*
     * Endpoint to create new payment
     */
    @PostMapping("/create")
    public ResponseEntity<?> createNewPayment(HttpServletRequest httpRequest,
                                              @Valid @RequestBody CreatePaymentRequestDto paymentDto) throws PaymentDetailNotFoundException, EasyPayException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        CreatePaymentResponseDto createPaymentResponseDto = new CreatePaymentResponseDto();
        logger.info("request body: " + paymentDto.toString(), httpRequest);

        try {
            createPaymentResponseDto = paymentService.createPayment(paymentDto, serviceContext);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (PaymentDetailNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Invalid payment details", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (EasyPayException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External service unavailable", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        logger.info("response body: " + createPaymentResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Payment created successfully", createPaymentResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to get all payments
     */
    @GetMapping
    public ResponseEntity<?> getPayments(HttpServletRequest httpRequest,
                                         @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                         @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                         @RequestParam(value = "paymentIdentification", required = false) Optional<String> paymentIdentification,
                                         @RequestParam(value = "requesterEntityIdentification", required = false) Optional<String> requesterEntityIdentification,
                                         @RequestParam(value = "requesterEntityType", required = false) Optional<String> requesterEntityType) throws PaymentForbiddenException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        List<GetPaymentResponseDto> payments;

        try {
            payments = paymentService.getPayments(limit, offset, paymentIdentification, requesterEntityIdentification, requesterEntityType, serviceContext);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching payments", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Payments retrieved successfully", payments), HttpStatus.OK);

    }

    /*
     * Endpoint to get individual payment info
     */
    @GetMapping("/{paymentIdentification}")
    public ResponseEntity<?> getPayment(HttpServletRequest httpRequest,
                                        @PathVariable(value = "paymentIdentification") String paymentIdentification) throws PaymentNotFoundException, PaymentForbiddenException, GenericException {

        GetPaymentResponseDto payment;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            payment = paymentService.getPayment(paymentIdentification, serviceContext);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (PaymentNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error fetching payment information.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + payment.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Payment retrieved successfully", payment), HttpStatus.OK);

    }

    /*
     * Endpoint to delete payment
     */
    @DeleteMapping("/{paymentIdentification}")
    public ResponseEntity<?> deletePayment(HttpServletRequest httpRequest,
                                           @PathVariable(value = "paymentIdentification") String paymentIdentification) throws PaymentForbiddenException, PaymentNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            paymentService.deletePayment(paymentIdentification, serviceContext);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (PaymentNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error deleting payment.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Payment deleted successfully"), HttpStatus.OK);

    }

    @PutMapping("/{paymentIdentification}")
    public ResponseEntity<?> updatePayment(HttpServletRequest httpRequest,
                                           @PathVariable(value = "paymentIdentification") String paymentIdentification,
                                           @Valid @RequestBody UpdatePaymentRequestDto updatePaymentRequestDto) throws PaymentForbiddenException, PaymentNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + updatePaymentRequestDto.toString(), httpRequest);

        try {
            paymentService.updatePayment(paymentIdentification, updatePaymentRequestDto, serviceContext);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (PaymentNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error updating payment.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Payment updated successfully"), HttpStatus.OK);

    }

    /*
     * Easypay callback endpoint
     */
    @PostMapping("/easypay/callback")
    public ResponseEntity<?> easypayCallback(HttpServletRequest httpRequest,
                                             @Valid @RequestBody EasypayCallbackDto easyPayCallbackDto) throws PaymentNotFoundException, EasyPayException, PaymentDetailNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + easyPayCallbackDto.toString(), httpRequest);

        try {
            paymentService.easypayCallback(easyPayCallbackDto, serviceContext);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (PaymentDetailNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment detail not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (PaymentNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (EasyPayException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External service unavailable.", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } 

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Callback processed sucessfully"), HttpStatus.OK);

    }

    /*
     * Easypay callback endpoint
     */
    @PostMapping("/easypay/payment-callback")
    public ResponseEntity<?> easypayPaymentCallback(HttpServletRequest httpRequest,
                                                    @Valid @RequestBody EasypayPaymentCallbackDto easyPayPaymentCallbackDto) throws PaymentNotFoundException, EasyPayException, PaymentDetailNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + easyPayPaymentCallbackDto.toString(), httpRequest);

        try {
            paymentService.easypayPaymentCallback(easyPayPaymentCallbackDto, serviceContext);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (PaymentDetailNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment detail not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (PaymentNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (EasyPayException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External service unavailable.", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } 

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Callback processed sucessfully"), HttpStatus.OK);

    }

}