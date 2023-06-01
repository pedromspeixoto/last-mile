package com.lastmile.paymentservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lastmile.paymentservice.client.easypay.EasyPayAPIConnector;
import com.lastmile.paymentservice.client.easypay.dto.CreditCardFrequentDetailsResponseDto;
import com.lastmile.paymentservice.client.easypay.dto.EasypayCallbackDto;
import com.lastmile.paymentservice.client.easypay.dto.external.EasypayPaymentCallbackDto;
import com.lastmile.paymentservice.client.orders.OrdersBridge;
import com.lastmile.paymentservice.domain.OutPayment;
import com.lastmile.paymentservice.domain.Payment;
import com.lastmile.paymentservice.domain.PaymentDetail;
import com.lastmile.paymentservice.dto.payments.CreatePaymentRequestDto;
import com.lastmile.paymentservice.dto.payments.CreatePaymentResponseDto;
import com.lastmile.paymentservice.dto.payments.GetPaymentResponseDto;
import com.lastmile.paymentservice.dto.payments.UpdatePaymentRequestDto;
import com.lastmile.paymentservice.enums.OutPaymentStatus;
import com.lastmile.paymentservice.enums.PaymentDetailStatus;
import com.lastmile.paymentservice.enums.PaymentDetailType;
import com.lastmile.paymentservice.enums.PaymentStatus;
import com.lastmile.paymentservice.repository.OutPaymentRepository;
import com.lastmile.paymentservice.repository.PaymentDetailRepository;
import com.lastmile.paymentservice.repository.PaymentRepository;
import com.lastmile.paymentservice.service.PaymentService;
import com.lastmile.paymentservice.service.exception.GenericException;
import com.lastmile.paymentservice.service.exception.paymentdetails.EasyPayException;
import com.lastmile.paymentservice.service.exception.paymentdetails.PaymentDetailNotFoundException;
import com.lastmile.paymentservice.service.exception.payments.PaymentForbiddenException;
import com.lastmile.paymentservice.service.exception.payments.PaymentNotFoundException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.validations.Validator;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Configuration
public class PaymentServiceImpl implements PaymentService {

    private static final Integer DEFAULT_VALUE_LIMIT = 10;
    private static final Integer DEFAULT_VALUE_OFFSET = 0;

    private final PaymentRepository paymentRepository;
    private final OutPaymentRepository outPaymentRepository;
    private final PaymentDetailRepository paymentDetailRepository;
    private final EasyPayAPIConnector easyPayAPIConnector;
    private final OrdersBridge ordersBridge;

    Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public PaymentServiceImpl(final PaymentRepository paymentRepository,
                              final OutPaymentRepository outPaymentRepository,
                              final PaymentDetailRepository paymentDetailRepository,
                              final EasyPayAPIConnector easyPayAPIConnector,
                              final OrdersBridge ordersBridge) {
        this.paymentRepository = paymentRepository;
        this.paymentDetailRepository = paymentDetailRepository;
        this.easyPayAPIConnector = easyPayAPIConnector;
        this.ordersBridge = ordersBridge;
        this.outPaymentRepository = outPaymentRepository;
    }

    @Override
    @Transactional(rollbackFor = { GenericException.class, PaymentDetailNotFoundException.class, EasyPayException.class })
    public CreatePaymentResponseDto createPayment(CreatePaymentRequestDto paymentDto, ServiceContext serviceContext) throws PaymentDetailNotFoundException, EasyPayException, GenericException {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // map from DTO
        Payment payment = modelMapper.map(paymentDto, Payment.class);
        // set payment id
        payment.setPaymentIdentification(UUID.randomUUID().toString());

        Optional<PaymentDetail> paymentDetail = paymentDetailRepository.findByPaymentDetailIdentification(paymentDto.getPaymentDetailsId());

        if (!paymentDetail.isPresent()
            || !(paymentDetail.get().getStatus().equals(PaymentDetailStatus.ACCEPTED.toString())
            || ( null == paymentDetail.get().getExternalEntityIdentification() || paymentDetail.get().getExternalEntityIdentification().isEmpty()))) {
            throw new PaymentDetailNotFoundException(paymentDto.getPaymentDetailsId());
        }

        // process payment using payment details
        String externalPaymentIdentification = null;
        try {
            externalPaymentIdentification = easyPayAPIConnector.capturePayment(paymentDetail.get().getExternalEntityIdentification(), payment.getPaymentIdentification(), "Last Mile Order - Transaction Identification : " + paymentDto.getTransactionIdentification(), paymentDto.getPaymentValue().doubleValue());
        } catch (Exception e) {
            throw new EasyPayException(e.getMessage(), e.getCause());
        }

        try {
            // save payment
            payment.setStatus(PaymentStatus.CREATED.toString());
            payment.setExternalPaymentIdentification(externalPaymentIdentification);
            paymentRepository.save(payment);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return new CreatePaymentResponseDto(payment.getPaymentIdentification(), PaymentStatus.valueOf(payment.getStatus()));

    }

    @Override
    public List<GetPaymentResponseDto> getPayments(Optional<Integer> limit, Optional<Integer> offset, Optional<String> paymentIdentification, Optional<String> entityIdentification, Optional<String> entityType, ServiceContext serviceContext) throws PaymentForbiddenException, GenericException {

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext, entityIdentification, entityType)) {
            throw new PaymentForbiddenException();
        }

        ModelMapper modelMapper = new ModelMapper();
        List<Payment> payments;

        Pageable pageable = PageRequest.of(offset.orElse(DEFAULT_VALUE_OFFSET), limit.orElse(DEFAULT_VALUE_LIMIT));

        try {
            // try to fetch payments
            payments = paymentRepository.findAllPayments(entityIdentification.orElse(""), entityType.orElse(""), paymentIdentification.orElse(""), pageable);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return payments.stream().map(payment -> modelMapper.map(payment, GetPaymentResponseDto.class)).collect(Collectors.toList());

    }

    @Override
    public GetPaymentResponseDto getPayment(String paymentIdentification, ServiceContext serviceContext) throws PaymentForbiddenException, PaymentNotFoundException, GenericException {

        Optional<Payment> payment = paymentRepository.findByPaymentIdentification(paymentIdentification);

        // validate if payment exists
        if (!payment.isPresent()) {
            throw new PaymentNotFoundException(paymentIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext, Optional.of(payment.get().getRequesterEntityIdentification()), Optional.of(payment.get().getRequesterEntityType()))) {
            throw new PaymentForbiddenException();
        }

        ModelMapper modelMapper = new ModelMapper();
        GetPaymentResponseDto getPaymentResponseDto = new GetPaymentResponseDto();

        try {
            // try to map payment to dto
            getPaymentResponseDto = modelMapper.map(payment.get(), GetPaymentResponseDto.class);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return getPaymentResponseDto;
    }

    @Override
    @Transactional(rollbackFor = { PaymentNotFoundException.class, GenericException.class })
    public void deletePayment(String paymentIdentification, ServiceContext serviceContext) throws PaymentForbiddenException, PaymentNotFoundException, GenericException {

        Optional<Payment> payment = paymentRepository.findByPaymentIdentification(paymentIdentification);

        // validate if payment exists
        if (!payment.isPresent()) {
            throw new PaymentNotFoundException(paymentIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext, Optional.of(payment.get().getRequesterEntityIdentification()), Optional.of(payment.get().getRequesterEntityType()))) {
            throw new PaymentForbiddenException();
        }

        try {
            paymentRepository.deleteById(payment.get().getId());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = { PaymentForbiddenException.class, PaymentNotFoundException.class, GenericException.class })
    public void updatePayment(String paymentIdentification, UpdatePaymentRequestDto updatePaymentRequestDto, ServiceContext serviceContext) throws PaymentForbiddenException, PaymentNotFoundException, GenericException {

        Optional<Payment> payment = paymentRepository.findByPaymentIdentification(paymentIdentification);

        // validate if payment exists
        if (!payment.isPresent()) {
            throw new PaymentNotFoundException(paymentIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext, Optional.of(payment.get().getRequesterEntityIdentification()), Optional.of(payment.get().getRequesterEntityType()))) {
            throw new PaymentForbiddenException();
        }

        try {
            Payment updatedPayment = payment.get();

            // payment type
            if (updatePaymentRequestDto.getPaymentType() != null && !updatePaymentRequestDto.getPaymentType().toString().isEmpty()) {
                updatedPayment.setPaymentType(updatePaymentRequestDto.getPaymentType().toString());
            }
            // payment details id
            if (updatePaymentRequestDto.getPaymentDetailsId() != null && !updatePaymentRequestDto.getPaymentDetailsId().isEmpty()) {
                updatedPayment.setPaymentDetailsId(updatePaymentRequestDto.getPaymentDetailsId());
            }
            // payment value
            if (updatePaymentRequestDto.getPaymentValue() != null && !Double.isNaN(updatePaymentRequestDto.getPaymentValue())) {
                updatedPayment.setPaymentValue(updatePaymentRequestDto.getPaymentValue());
            }
            // payment status
            if (updatePaymentRequestDto.getPaymentStatus() != null && !updatePaymentRequestDto.getPaymentStatus().toString().isEmpty()) {
                updatedPayment.setStatus(updatePaymentRequestDto.getPaymentStatus().toString());
            }

            // update payment
            paymentRepository.save(updatedPayment);

        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }


    @Override
    @Transactional(rollbackFor = { GenericException.class, PaymentDetailNotFoundException.class, EasyPayException.class })
    public void easypayCallback(EasypayCallbackDto easyPayCallbackDto, ServiceContext serviceContext) throws PaymentNotFoundException, PaymentDetailNotFoundException, GenericException, EasyPayException {

        switch (easyPayCallbackDto.getType()) {
            case capture:
                Optional<Payment> payment = paymentRepository.findByExternalPaymentIdentification(easyPayCallbackDto.getId());

                if (!payment.isPresent()) {
                    throw new PaymentDetailNotFoundException(easyPayCallbackDto.getId());
                }

                Payment updatedPayment = payment.get();
                // get status
                switch (easyPayCallbackDto.getStatus()) {
                    case success:
                        updatedPayment.setStatus(PaymentStatus.ACCEPTED.toString());
                        break;
                    case failed:
                        updatedPayment.setStatus(PaymentStatus.DENIED.toString());
                        break;
                    default:
                }

                // update order
                try {
                    ordersBridge.patchOrderPaymentStatus(payment.get().getTransactionIdentification(),
                                                         payment.get().getPaymentIdentification(),
                                                         PaymentStatus.valueOf(payment.get().getStatus()),
                                                         serviceContext);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new GenericException(e.getMessage(), e.getCause());
                }

                // save in db
                paymentRepository.save(updatedPayment);

                break;
            case out_payment:
                Optional<OutPayment> outPayment = outPaymentRepository.findByExternalPaymentIdentification(easyPayCallbackDto.getId());

                if (!outPayment.isPresent()) {
                    throw new PaymentDetailNotFoundException(easyPayCallbackDto.getId());
                }

                OutPayment updatedOutPayment = outPayment.get();
                // get status
                switch (easyPayCallbackDto.getStatus()) {
                    case success:
                        updatedOutPayment.setStatus(OutPaymentStatus.PAID.toString());
                        break;
                    case failed:
                        updatedOutPayment.setStatus(OutPaymentStatus.FAILED.toString());
                        break;
                    default:
                }

                outPaymentRepository.save(updatedOutPayment);

                break;
            case frequent_create:
                Optional<PaymentDetail> paymentDetail = paymentDetailRepository.findByExternalEntityIdentification(easyPayCallbackDto.getId());

                if (!paymentDetail.isPresent()) {
                    throw new PaymentDetailNotFoundException(easyPayCallbackDto.getId());
                }

                PaymentDetail updatedPaymentDetail = paymentDetail.get();
                // get status
                switch (easyPayCallbackDto.getStatus()) {
                    case success:
                        updatedPaymentDetail.setStatus(PaymentDetailStatus.ACCEPTED.toString());
                        break;
                    case failed:
                        updatedPaymentDetail.setStatus(PaymentDetailStatus.FAILED.toString());
                        break;
                    default:
                }

                // update payment details
                switch (PaymentDetailType.valueOf(paymentDetail.get().getPaymentDetailType())) {
                    case CREDITCARD:
                        CreditCardFrequentDetailsResponseDto creditCardDto = new CreditCardFrequentDetailsResponseDto();
                        // call payment details
                        try {
                            creditCardDto = easyPayAPIConnector.getCreditCardFrequentPaymentDetails(easyPayCallbackDto.getId());
                        } catch (Exception e) {
                            throw new EasyPayException(e.getMessage(), e.getCause());
                        }
                        updatedPaymentDetail.setCardLastFourDigits(creditCardDto.getLastFourDigits());
                        updatedPaymentDetail.setCardExpiryDate(creditCardDto.getExpirationDate());
                        updatedPaymentDetail.setCardType(creditCardDto.getCardType().toString());
                        break;
                    case MBWAY:
                    case DEBIT:
                    default:
                }

                // save in db
                paymentDetailRepository.save(updatedPaymentDetail);

                break;
            default:
                throw new GenericException("Callback type not recognized");
        }
    }

    @Override
    @Transactional(rollbackFor = { GenericException.class, PaymentDetailNotFoundException.class, EasyPayException.class })
    public void easypayPaymentCallback(EasypayPaymentCallbackDto easyPayPaymentCallbackDto, ServiceContext serviceContext) throws PaymentNotFoundException, PaymentDetailNotFoundException, GenericException, EasyPayException {
        logger.info("Received easypay callback. Body: " + easyPayPaymentCallbackDto.toString());
    }

}