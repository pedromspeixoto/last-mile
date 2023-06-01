package com.lastmile.paymentservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.lastmile.paymentservice.dto.paymentdetails.CreatePaymentDetailRequestDto;
import com.lastmile.paymentservice.dto.paymentdetails.CreatePaymentDetailResponseDto;
import com.lastmile.paymentservice.dto.paymentdetails.GetPaymentDetailResponseDto;
import com.lastmile.paymentservice.dto.paymentdetails.UpdatePaymentDetailRequestDto;
import com.lastmile.paymentservice.service.PaymentDetailService;
import com.lastmile.paymentservice.service.exception.payments.PaymentForbiddenException;
import com.lastmile.paymentservice.service.exception.payments.PaymentNotFoundException;
import com.lastmile.paymentservice.service.exception.GenericException;
import com.lastmile.paymentservice.service.exception.paymentdetails.EasyPayException;
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
@RequestMapping("/details")
public class PaymentDetailController {

    private final PaymentDetailService paymentDetailService;
    private final CustomLogging logger;

    public PaymentDetailController(PaymentDetailService paymentDetailService, CustomLogging logger) {
        this.paymentDetailService = paymentDetailService;
        this.logger = logger;
    }

    /*
     * Endpoint to create new payment detail
     */
    @PostMapping("/create")
    public ResponseEntity<?> createNewPaymentDetail(HttpServletRequest httpRequest,
                                                    @Valid @RequestBody CreatePaymentDetailRequestDto paymentDetailRequestDto) throws EasyPayException, PaymentForbiddenException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        CreatePaymentDetailResponseDto createPaymentDetailResponseDto = new CreatePaymentDetailResponseDto();
        logger.info("request body: " + paymentDetailRequestDto.toString(), httpRequest);

        try {
            createPaymentDetailResponseDto = paymentDetailService.createPaymentDetail(paymentDetailRequestDto, serviceContext);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (EasyPayException ex) {
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
     * Endpoint to get all payment details
     */
    @GetMapping
    public ResponseEntity<?> getPaymentDetails(HttpServletRequest httpRequest,
                                               @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                               @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                               @RequestParam(value = "paymentDetailIdentification", required = false) Optional<String> paymentDetailIdentification,
                                               @RequestParam(value = "entityIdentification", required = false) Optional<String> entityIdentification,
                                               @RequestParam(value = "entityType", required = false) Optional<String> entityType) throws PaymentForbiddenException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        List<GetPaymentDetailResponseDto> paymentDetails;

        try {
            paymentDetails = paymentDetailService.getPaymentDetails(limit, offset, paymentDetailIdentification, entityIdentification, entityType, serviceContext);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching payment details", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Payment details retrieved successfully", paymentDetails), HttpStatus.OK);

    }

    /*
     * Endpoint to get individual payment detail info
     */
    @GetMapping("/{paymentDetailIdentification}")
    public ResponseEntity<?> getPaymentDetail(HttpServletRequest httpRequest,
                                              @PathVariable(value = "paymentDetailIdentification") String paymentDetailIdentification) throws PaymentDetailNotFoundException, PaymentForbiddenException, GenericException {

        GetPaymentDetailResponseDto paymentDetail;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            paymentDetail = paymentDetailService.getPaymentDetail(paymentDetailIdentification, serviceContext);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (PaymentDetailNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error fetching payment detail information.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + paymentDetail.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Payment detail retrieved successfully", paymentDetail), HttpStatus.OK);

    }

    /*
     * Endpoint to delete payment detail
     */
    @DeleteMapping("/{paymentDetailIdentification}")
    public ResponseEntity<?> deletePaymentDetail(HttpServletRequest httpRequest,
                                                 @PathVariable(value = "paymentDetailIdentification") String paymentDetailIdentification) throws PaymentForbiddenException, PaymentNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            paymentDetailService.deletePaymentDetail(paymentDetailIdentification, serviceContext);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (PaymentDetailNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment detail not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error deleting payment detail.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Payment detail deleted successfully"), HttpStatus.OK);

    }

    @PutMapping("/{paymentDetailIdentification}")
    public ResponseEntity<?> updatePayment(HttpServletRequest httpRequest,
                                           @PathVariable(value = "paymentDetailIdentification") String paymentDetailIdentification,
                                           @Valid @RequestBody UpdatePaymentDetailRequestDto updatePaymentDetailRequestDto) throws PaymentForbiddenException, PaymentDetailNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + updatePaymentDetailRequestDto.toString(), httpRequest);

        try {
            paymentDetailService.updatePaymentDetail(paymentDetailIdentification, updatePaymentDetailRequestDto, serviceContext);
        } catch (PaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (PaymentDetailNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Payment detail not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error updating payment detail.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Payment detail updated successfully"), HttpStatus.OK);

    }

}