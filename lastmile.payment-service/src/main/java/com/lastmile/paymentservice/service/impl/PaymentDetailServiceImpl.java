package com.lastmile.paymentservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lastmile.paymentservice.client.easypay.EasyPayAPIConnector;
import com.lastmile.paymentservice.client.easypay.dto.FrequentResponseDto;
import com.lastmile.paymentservice.domain.PaymentDetail;
import com.lastmile.paymentservice.dto.paymentdetails.CreatePaymentDetailRequestDto;
import com.lastmile.paymentservice.dto.paymentdetails.CreatePaymentDetailResponseDto;
import com.lastmile.paymentservice.dto.paymentdetails.GetPaymentDetailResponseDto;
import com.lastmile.paymentservice.dto.paymentdetails.UpdatePaymentDetailRequestDto;
import com.lastmile.paymentservice.enums.PaymentDetailStatus;
import com.lastmile.paymentservice.repository.PaymentDetailRepository;
import com.lastmile.paymentservice.service.PaymentDetailService;
import com.lastmile.paymentservice.service.exception.GenericException;
import com.lastmile.paymentservice.service.exception.payments.PaymentForbiddenException;
import com.lastmile.paymentservice.service.exception.paymentdetails.EasyPayException;
import com.lastmile.paymentservice.service.exception.paymentdetails.PaymentDetailNotFoundException;
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
public class PaymentDetailServiceImpl implements PaymentDetailService {

    private static final Integer DEFAULT_VALUE_LIMIT = 10;
    private static final Integer DEFAULT_VALUE_OFFSET = 0;

    private final PaymentDetailRepository paymentDetailRepository;
    private final EasyPayAPIConnector easyPayAPIConnector;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public PaymentDetailServiceImpl(final PaymentDetailRepository paymentDetailRepository,
            final EasyPayAPIConnector easyPayAPIConnector) {
        this.paymentDetailRepository = paymentDetailRepository;
        this.easyPayAPIConnector = easyPayAPIConnector;
    }

    @Override
    @Transactional(rollbackFor = { PaymentForbiddenException.class, GenericException.class, EasyPayException.class })
    public CreatePaymentDetailResponseDto createPaymentDetail(CreatePaymentDetailRequestDto paymentDetailDto,
            ServiceContext serviceContext) throws PaymentForbiddenException, GenericException, EasyPayException {

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isSameEntity(serviceContext,
                paymentDetailDto.getEntityIdentification(), paymentDetailDto.getEntityType().toString())) {
            throw new PaymentForbiddenException();
        }

        ModelMapper modelMapper = new ModelMapper();
        // map from DTO
        PaymentDetail paymentDetail = modelMapper.map(paymentDetailDto, PaymentDetail.class);
        // set payment detail id
        paymentDetail.setPaymentDetailIdentification(UUID.randomUUID().toString());

        FrequentResponseDto frequentResponseDto = new FrequentResponseDto();
        switch (paymentDetailDto.getExternalEntity()) {
            case EASYPAY:
                String paymentName = (null == paymentDetailDto.getPaymentName()) ? "" : paymentDetailDto.getPaymentName();
                String paymentEmail = (null == paymentDetailDto.getPaymentEmail()) ? "" : paymentDetailDto.getPaymentEmail();
                String phoneNumber = (null == paymentDetailDto.getPaymentPhoneNumber()) ? "" : paymentDetailDto.getPaymentPhoneNumber();
                String fiscalNumber = (null == paymentDetailDto.getPaymentFiscalNumber()) ? "" : paymentDetailDto.getPaymentFiscalNumber();
                try {
                    frequentResponseDto = easyPayAPIConnector.createFrequentPayment(paymentDetailDto.getEntityIdentification(),
                                                                                    paymentName,
                                                                                    paymentEmail,
                                                                                    phoneNumber,
                                                                                    fiscalNumber,
                                                                                    paymentDetail.getPaymentDetailIdentification(),
                                                                                    paymentDetailDto.getPaymentDetailType());
                    paymentDetail.setExternalEntityIdentification(frequentResponseDto.getExternalPaymentIdentification());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new EasyPayException(e.getMessage(), e.getCause());
                }
                paymentDetail.setStatus(PaymentDetailStatus.CREATED.toString());
                break;
            case NONE:
            default:
                paymentDetail.setStatus(PaymentDetailStatus.ACCEPTED.toString());
        }

        try {
            // save payment detail
            paymentDetailRepository.save(paymentDetail);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return new CreatePaymentDetailResponseDto(paymentDetail.getPaymentDetailIdentification(),
                frequentResponseDto.getPaymentGatewayUrl());

    }

    @Override
    public List<GetPaymentDetailResponseDto> getPaymentDetails(Optional<Integer> limit, Optional<Integer> offset,
            Optional<String> paymentDetailIdentification, Optional<String> entityIdentification,
            Optional<String> entityType, ServiceContext serviceContext)
            throws PaymentForbiddenException, GenericException {

        // validate access
        if (!Validator.isAdmin(serviceContext)
                && !Validator.isEntityAllowed(serviceContext, entityIdentification, entityType)) {
            throw new PaymentForbiddenException();
        }

        ModelMapper modelMapper = new ModelMapper();
        List<PaymentDetail> paymentDetails;

        Pageable pageable = PageRequest.of(offset.orElse(DEFAULT_VALUE_OFFSET), limit.orElse(DEFAULT_VALUE_LIMIT));

        try {
            // try to fetch payment details
            paymentDetails = paymentDetailRepository.findAllPaymentDetails(entityIdentification.orElse(""),
                    entityType.orElse(""), paymentDetailIdentification.orElse(""), pageable);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return paymentDetails.stream()
                .map(paymentDetail -> modelMapper.map(paymentDetail, GetPaymentDetailResponseDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public GetPaymentDetailResponseDto getPaymentDetail(String paymentDetailIdentification,
            ServiceContext serviceContext)
            throws PaymentForbiddenException, PaymentDetailNotFoundException, GenericException {

        Optional<PaymentDetail> paymentDetail = paymentDetailRepository
                .findByPaymentDetailIdentification(paymentDetailIdentification);

        // validate if payment detail exists
        if (!paymentDetail.isPresent()) {
            throw new PaymentDetailNotFoundException(paymentDetailIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext,
                Optional.of(paymentDetail.get().getEntityIdentification()),
                Optional.of(paymentDetail.get().getEntityType()))) {
            throw new PaymentForbiddenException();
        }

        ModelMapper modelMapper = new ModelMapper();
        GetPaymentDetailResponseDto getPaymentDetailResponseDto = new GetPaymentDetailResponseDto();

        try {
            // try to map payment detail to dto
            getPaymentDetailResponseDto = modelMapper.map(paymentDetail.get(), GetPaymentDetailResponseDto.class);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return getPaymentDetailResponseDto;
    }

    @Override
    @Transactional(rollbackFor = { PaymentDetailNotFoundException.class, GenericException.class })
    public void deletePaymentDetail(String paymentDetailIdentification, ServiceContext serviceContext)
            throws PaymentForbiddenException, PaymentDetailNotFoundException, GenericException {

        Optional<PaymentDetail> paymentDetail = paymentDetailRepository
                .findByPaymentDetailIdentification(paymentDetailIdentification);

        // validate if payment detail exists
        if (!paymentDetail.isPresent()) {
            throw new PaymentDetailNotFoundException(paymentDetailIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext,
                Optional.of(paymentDetail.get().getEntityIdentification()),
                Optional.of(paymentDetail.get().getEntityType()))) {
            throw new PaymentForbiddenException();
        }

        try {
            paymentDetailRepository.deleteById(paymentDetail.get().getId());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = { PaymentForbiddenException.class, PaymentDetailNotFoundException.class,
            GenericException.class })
    public void updatePaymentDetail(String paymentDetailIdentification,
            UpdatePaymentDetailRequestDto updatePaymentDetailRequestDto, ServiceContext serviceContext)
            throws PaymentForbiddenException, PaymentDetailNotFoundException, GenericException {

        Optional<PaymentDetail> paymentDetail = paymentDetailRepository
                .findByPaymentDetailIdentification(paymentDetailIdentification);

        // validate if payment exists
        if (!paymentDetail.isPresent()) {
            throw new PaymentDetailNotFoundException(paymentDetailIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext,
                Optional.of(paymentDetail.get().getEntityIdentification()),
                Optional.of(paymentDetail.get().getEntityType()))) {
            throw new PaymentForbiddenException();
        }

        try {
            PaymentDetail updatedPaymentDetail = paymentDetail.get();

            // payment detail status
            if (updatePaymentDetailRequestDto.getStatus() != null
                    && !updatePaymentDetailRequestDto.getStatus().toString().isEmpty()) {
                updatedPaymentDetail.setStatus(updatePaymentDetailRequestDto.getStatus().toString());
            }

            // update payment
            paymentDetailRepository.save(updatedPaymentDetail);

        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

}