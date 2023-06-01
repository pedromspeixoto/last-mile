package com.lastmile.paymentservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.lastmile.paymentservice.dto.outpayments.CreateOutPaymentRequestDto;
import com.lastmile.paymentservice.dto.outpayments.CreateOutPaymentResponseDto;
import com.lastmile.paymentservice.dto.outpayments.GetOutPaymentResponseDto;
import com.lastmile.paymentservice.dto.outpayments.UpdateOutPaymentRequestDto;
import com.lastmile.paymentservice.service.exception.paymentdetails.EasyPayException;
import com.lastmile.paymentservice.service.OutPaymentService;
import com.lastmile.paymentservice.service.exception.outpayments.OutPaymentForbiddenException;
import com.lastmile.paymentservice.service.exception.outpayments.OutPaymentNotFoundException;
import com.lastmile.paymentservice.service.exception.outpayments.RequestEntityDetailsNotFoundException;
import com.lastmile.paymentservice.service.exception.FeignCommunicationException;
import com.lastmile.paymentservice.service.exception.GenericException;
import com.lastmile.paymentservice.service.exception.outpayments.NoActiveSourceAccountFoundException;
import com.lastmile.paymentservice.service.exception.outpayments.OutPaymentAlreadyProcessedException;
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
@RequestMapping("/outbound")
public class OutPaymentController {

    private final OutPaymentService outPaymentService;
    private final CustomLogging logger;

    public OutPaymentController(OutPaymentService outPaymentService, CustomLogging logger) {
        this.outPaymentService = outPaymentService;
        this.logger = logger;
    }

    /*
     * Endpoint to create new out payment
     */
    @PostMapping("/create")
    public ResponseEntity<?> createNewOutPayment(HttpServletRequest httpRequest,
                                                 @Valid @RequestBody CreateOutPaymentRequestDto outPaymentDto) throws PaymentDetailNotFoundException, EasyPayException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        CreateOutPaymentResponseDto createPaymentResponseDto = new CreateOutPaymentResponseDto();
        logger.info("request body: " + outPaymentDto.toString(), httpRequest);

        try {
            createPaymentResponseDto = outPaymentService.createOutPayment(outPaymentDto, serviceContext);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + createPaymentResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Out payment created successfully", createPaymentResponseDto), HttpStatus.CREATED);

    }

    /*
     * Endpoint to get all out payments
     */
    @GetMapping
    public ResponseEntity<?> getPayments(HttpServletRequest httpRequest,
                                         @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                         @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                         @RequestParam(value = "outPaymentIdentification", required = false) Optional<String> outPaymentIdentification,
                                         @RequestParam(value = "requesterEntityIdentification", required = false) Optional<String> requesterEntityIdentification,
                                         @RequestParam(value = "requesterEntityType", required = false) Optional<String> requesterEntityType) throws OutPaymentForbiddenException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        List<GetOutPaymentResponseDto> payments;

        try {
            payments = outPaymentService.getOutPayments(limit, offset, outPaymentIdentification, requesterEntityIdentification, requesterEntityType, serviceContext);
        } catch (OutPaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching outbound payments", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Outbound payments retrieved successfully", payments), HttpStatus.OK);

    }

    /*
     * Endpoint to get individual out payment info
     */
    @GetMapping("/{outPaymentIdentification}")
    public ResponseEntity<?> getPayment(HttpServletRequest httpRequest,
                                        @PathVariable(value = "outPaymentIdentification") String outPaymentIdentification) throws OutPaymentNotFoundException, OutPaymentForbiddenException, GenericException {

        GetOutPaymentResponseDto payment;
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            payment = outPaymentService.getOutPayment(outPaymentIdentification, serviceContext);
        } catch (OutPaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (OutPaymentNotFoundException ex) {
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
     * Endpoint to delete out payment
     */
    @DeleteMapping("/{outPaymentIdentification}")
    public ResponseEntity<?> deletePayment(HttpServletRequest httpRequest,
                                           @PathVariable(value = "outPaymentIdentification") String outPaymentIdentification) throws OutPaymentForbiddenException, OutPaymentNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            outPaymentService.deleteOutPayment(outPaymentIdentification, serviceContext);
        } catch (OutPaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (OutPaymentNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Outbound payment not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error deleting payment.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Outbound payment deleted successfully"), HttpStatus.OK);

    }

    @PutMapping("/{outPaymentIdentification}")
    public ResponseEntity<?> updatePayment(HttpServletRequest httpRequest,
                                           @PathVariable(value = "outPaymentIdentification") String outPaymentIdentification,
                                           @Valid @RequestBody UpdateOutPaymentRequestDto updateOutPaymentRequestDto) throws OutPaymentForbiddenException, OutPaymentNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);
        logger.info("request body: " + updateOutPaymentRequestDto.toString(), httpRequest);

        try {
            outPaymentService.updateOutPayment(outPaymentIdentification, updateOutPaymentRequestDto, serviceContext);
        } catch (OutPaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        } catch (OutPaymentNotFoundException ex) {
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
                new SuccessResponse(HttpStatus.OK.value(), "Outbound payment updated successfully"), HttpStatus.OK);

    }

    /*
     * Process single outbound payment
     */
    @PostMapping("/{outPaymentIdentification}/process")
    public ResponseEntity<?> processSingleOutboundPayment(HttpServletRequest httpRequest,
                                                          @PathVariable(value = "outPaymentIdentification") String outPaymentIdentification) throws OutPaymentAlreadyProcessedException, FeignCommunicationException, RequestEntityDetailsNotFoundException, OutPaymentNotFoundException, EasyPayException, PaymentDetailNotFoundException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            outPaymentService.processSingleOutPayment(outPaymentIdentification, serviceContext);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RequestEntityDetailsNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "No request entity details found to process this request.", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (OutPaymentAlreadyProcessedException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Payment already processed.", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (NoActiveSourceAccountFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "No source account found to process this request.", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (OutPaymentNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Outbound payment not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (EasyPayException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External service unavailable.", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Internal backend service unavailable.", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } 

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.ACCEPTED.value(), "Transaction created in external payments system"), HttpStatus.ACCEPTED);

    }

    /*
     * Process all outbound payments
     */
    @PostMapping("/process-all")
    public ResponseEntity<?> processAllOutboundPayments(HttpServletRequest httpRequest) throws OutPaymentForbiddenException, EasyPayException, GenericException {

        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            outPaymentService.processOutPayments(serviceContext);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EasyPayException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External service unavailable.", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (OutPaymentForbiddenException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perfom this action.", ex.getMessage()),
                    HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Batch ran successfully"), HttpStatus.OK);

    }

}