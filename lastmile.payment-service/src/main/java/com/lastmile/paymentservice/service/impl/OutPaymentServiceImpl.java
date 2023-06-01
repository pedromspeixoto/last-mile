package com.lastmile.paymentservice.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lastmile.paymentservice.client.drivers.DriversBridge;
import com.lastmile.paymentservice.client.easypay.EasyPayAPIConnector;
import com.lastmile.paymentservice.client.easypay.dto.OutPaymentResponseDto;
import com.lastmile.paymentservice.domain.OutPayment;
import com.lastmile.paymentservice.domain.OutPaymentAccount;
import com.lastmile.paymentservice.dto.drivers.FiscalEntityResponseDto;
import com.lastmile.paymentservice.dto.outpayments.CreateOutPaymentRequestDto;
import com.lastmile.paymentservice.dto.outpayments.CreateOutPaymentResponseDto;
import com.lastmile.paymentservice.dto.outpayments.GetOutPaymentResponseDto;
import com.lastmile.paymentservice.dto.outpayments.UpdateOutPaymentRequestDto;
import com.lastmile.paymentservice.enums.OutPaymentAccountStatus;
import com.lastmile.paymentservice.enums.OutPaymentStatus;
import com.lastmile.paymentservice.enums.OutPaymentType;
import com.lastmile.paymentservice.enums.PaymentExternalEntities;
import com.lastmile.paymentservice.repository.OutPaymentAccountRepository;
import com.lastmile.paymentservice.repository.OutPaymentRepository;
import com.lastmile.paymentservice.service.OutPaymentService;
import com.lastmile.paymentservice.service.exception.FeignCommunicationException;
import com.lastmile.paymentservice.service.exception.GenericException;
import com.lastmile.paymentservice.service.exception.outpayments.NoActiveSourceAccountFoundException;
import com.lastmile.paymentservice.service.exception.outpayments.OutPaymentAlreadyProcessedException;
import com.lastmile.paymentservice.service.exception.outpayments.OutPaymentForbiddenException;
import com.lastmile.paymentservice.service.exception.outpayments.OutPaymentNotFoundException;
import com.lastmile.paymentservice.service.exception.outpayments.RequestEntityDetailsNotFoundException;
import com.lastmile.paymentservice.service.exception.paymentdetails.EasyPayException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.EntityType;
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
public class OutPaymentServiceImpl implements OutPaymentService {

    private static final Integer DEFAULT_VALUE_LIMIT = 10;
    private static final Integer DEFAULT_VALUE_OFFSET = 0;

    private final OutPaymentRepository outPaymentRepository;
    private final OutPaymentAccountRepository outPaymentAccountRepository;
    private final EasyPayAPIConnector easyPayAPIConnector;
    private final DriversBridge driversBridge;

    Logger logger = LoggerFactory.getLogger(OutPaymentServiceImpl.class);

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public OutPaymentServiceImpl(final OutPaymentRepository outPaymentRepository,
            final EasyPayAPIConnector easyPayAPIConnector,
            final OutPaymentAccountRepository outPaymentAccountRepository, final DriversBridge driversBridge) {
        this.outPaymentRepository = outPaymentRepository;
        this.outPaymentAccountRepository = outPaymentAccountRepository;
        this.easyPayAPIConnector = easyPayAPIConnector;
        this.driversBridge = driversBridge;
    }

    @Override
    @Transactional(rollbackFor = { GenericException.class })
    public CreateOutPaymentResponseDto createOutPayment(CreateOutPaymentRequestDto outPaymentDto,
            ServiceContext serviceContext) throws GenericException {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // map from DTO
        OutPayment outPayment = modelMapper.map(outPaymentDto, OutPayment.class);
        // set payment id
        outPayment.setOutPaymentIdentification(UUID.randomUUID().toString());

        try {
            // save out payment
            outPayment.setStatus(OutPaymentStatus.PENDING.toString());
            outPaymentRepository.save(outPayment);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return new CreateOutPaymentResponseDto(outPayment.getOutPaymentIdentification(),
                OutPaymentStatus.valueOf(outPayment.getStatus()));

    }

    @Override
    public List<GetOutPaymentResponseDto> getOutPayments(Optional<Integer> limit, Optional<Integer> offset,
            Optional<String> outPaymentIdentification, Optional<String> entityIdentification,
            Optional<String> entityType, ServiceContext serviceContext)
            throws OutPaymentForbiddenException, GenericException {

        // validate access
        if (!Validator.isAdmin(serviceContext)
                && !Validator.isEntityAllowed(serviceContext, entityIdentification, entityType)
                && !Validator.isFiscalEntity(serviceContext)) {
            throw new OutPaymentForbiddenException();
        }

        ModelMapper modelMapper = new ModelMapper();
        List<OutPayment> outPayments;

        Pageable pageable = PageRequest.of(offset.orElse(DEFAULT_VALUE_OFFSET), limit.orElse(DEFAULT_VALUE_LIMIT));

        try {
            // try to fetch out payments
            outPayments = outPaymentRepository.findAllOutPayments(entityIdentification.orElse(""),
                    entityType.orElse(""), outPaymentIdentification.orElse(""), pageable);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return outPayments.stream().map(outPayment -> modelMapper.map(outPayment, GetOutPaymentResponseDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public GetOutPaymentResponseDto getOutPayment(String outPaymentIdentification, ServiceContext serviceContext)
            throws OutPaymentForbiddenException, OutPaymentNotFoundException, GenericException {

        Optional<OutPayment> outPayment = outPaymentRepository.findByOutPaymentIdentification(outPaymentIdentification);

        // validate if out payment exists
        if (!outPayment.isPresent()) {
            throw new OutPaymentNotFoundException(outPaymentIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext,
                Optional.of(outPayment.get().getRequesterEntityIdentification()),
                Optional.of(outPayment.get().getRequesterEntityType()))) {
            throw new OutPaymentForbiddenException();
        }

        ModelMapper modelMapper = new ModelMapper();
        GetOutPaymentResponseDto getOutPaymentResponseDto = new GetOutPaymentResponseDto();

        try {
            // try to map out payment to dto
            getOutPaymentResponseDto = modelMapper.map(outPayment.get(), GetOutPaymentResponseDto.class);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return getOutPaymentResponseDto;
    }

    @Override
    @Transactional(rollbackFor = { OutPaymentNotFoundException.class, GenericException.class })
    public void deleteOutPayment(String outPaymentIdentification, ServiceContext serviceContext)
            throws OutPaymentForbiddenException, OutPaymentNotFoundException, GenericException {

        Optional<OutPayment> outPayment = outPaymentRepository.findByOutPaymentIdentification(outPaymentIdentification);

        // validate if payment exists
        if (!outPayment.isPresent()) {
            throw new OutPaymentNotFoundException(outPaymentIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext,
                Optional.of(outPayment.get().getRequesterEntityIdentification()),
                Optional.of(outPayment.get().getRequesterEntityType()))) {
            throw new OutPaymentForbiddenException();
        }

        try {
            outPaymentRepository.deleteById(outPayment.get().getId());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = { OutPaymentForbiddenException.class, OutPaymentNotFoundException.class,
            GenericException.class })
    public void updateOutPayment(String outPaymentIdentification, UpdateOutPaymentRequestDto updateOutPaymentRequestDto,
            ServiceContext serviceContext)
            throws OutPaymentForbiddenException, OutPaymentNotFoundException, GenericException {

        Optional<OutPayment> outPayment = outPaymentRepository.findByOutPaymentIdentification(outPaymentIdentification);

        // validate if out payment exists
        if (!outPayment.isPresent()) {
            throw new OutPaymentNotFoundException(outPaymentIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext,
                Optional.of(outPayment.get().getRequesterEntityIdentification()),
                Optional.of(outPayment.get().getRequesterEntityType()))) {
            throw new OutPaymentForbiddenException();
        }

        try {
            OutPayment updatedOutPayment = outPayment.get();

            // payment scheduled date
            if (updateOutPaymentRequestDto.getPaymentScheduledDate() != null
                    && !updateOutPaymentRequestDto.getPaymentScheduledDate().toString().isEmpty()) {
                updatedOutPayment.setPaymentType(updateOutPaymentRequestDto.getPaymentScheduledDate().toString());
            }
            // payment value
            if (updateOutPaymentRequestDto.getPaymentValue() != null
                    && !Double.isNaN(updateOutPaymentRequestDto.getPaymentValue())) {
                updatedOutPayment.setPaymentValue(updateOutPaymentRequestDto.getPaymentValue());
            }
            // payment status
            if (updateOutPaymentRequestDto.getOutPaymentStatus() != null
                    && !updateOutPaymentRequestDto.getOutPaymentStatus().toString().isEmpty()) {
                updatedOutPayment.setStatus(updateOutPaymentRequestDto.getOutPaymentStatus().toString());
            }

            // update payment
            outPaymentRepository.save(updatedOutPayment);

        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    public void processSingleOutPayment(String outPaymentIdentification, ServiceContext serviceContext)
            throws RequestEntityDetailsNotFoundException, NoActiveSourceAccountFoundException,
            OutPaymentNotFoundException, GenericException, EasyPayException, FeignCommunicationException, OutPaymentAlreadyProcessedException {

        Optional<OutPayment> outPayment = outPaymentRepository.findByOutPaymentIdentification(outPaymentIdentification);

        // validate if out payment exists
        if (!outPayment.isPresent()) {
            throw new OutPaymentNotFoundException(outPaymentIdentification);
        }

        // validate status
        if (!outPayment.get().getStatus().equals(OutPaymentStatus.FAILED.toString())
                && !outPayment.get().getStatus().equals(OutPaymentStatus.PENDING.toString())
                && !outPayment.get().getStatus().equals(OutPaymentStatus.SCHEDULED.toString())
                && !outPayment.get().getStatus().equals(OutPaymentStatus.CANCELLED.toString())) {
            throw new OutPaymentAlreadyProcessedException(outPayment.get().getOutPaymentIdentification());
        }

        OutPayment updatedOutPayment = outPayment.get();

        // fetch request entity details (if not filled already)
        if (null == updatedOutPayment.getRequesterIban() || updatedOutPayment.getRequesterIban().isEmpty()) {
            switch (EntityType.valueOf(outPayment.get().getRequesterEntityType())) {
                // if driver, fetch fiscal entity information
                case DRIVER:
                    try {
                        FiscalEntityResponseDto fiscalEntity = driversBridge.getDriverFiscalEntity(serviceContext, outPayment.get().getRequesterEntityIdentification(), outPaymentIdentification);
                        updatedOutPayment.setRequesterAccountHolderName(fiscalEntity.getBankAccountHolderName());
                        updatedOutPayment.setRequesterBankAccountCountryCode("PT");
                        updatedOutPayment.setRequesterEmail(fiscalEntity.getEmail());
                        updatedOutPayment.setRequesterIban(fiscalEntity.getBankAccountIban());
                        updatedOutPayment.setRequesterPhoneNumber(fiscalEntity.getPhoneNumber());
                        outPaymentRepository.save(updatedOutPayment);
                    } catch (FeignCommunicationException e) {
                        updatedOutPayment.setStatus(OutPaymentStatus.FAILED.toString());
                        outPaymentRepository.save(updatedOutPayment);        
                        throw new FeignCommunicationException("driver-service", e.getCause());
                    }
                    break;
                default:
                    throw new RequestEntityDetailsNotFoundException(outPayment.get().getOutPaymentIdentification());
            }
        }

        // fetch source account to be used
        Optional<OutPaymentAccount> outPaymentAccount = outPaymentAccountRepository.findByAccountBankAccountCountryCodeAndStatus(outPayment.get().getRequesterBankAccountCountryCode(), OutPaymentAccountStatus.ACTIVE.toString());
        if (!outPaymentAccount.isPresent()) {
            updatedOutPayment.setStatus(OutPaymentStatus.FAILED.toString());
            outPaymentRepository.save(updatedOutPayment);
            throw new NoActiveSourceAccountFoundException(outPayment.get().getRequesterBankAccountCountryCode());
        }
        updatedOutPayment.setSourceAccountIdentification(outPaymentAccount.get().getOutPaymentAccountIdentification());
        updatedOutPayment.setPaymentType(outPaymentAccount.get().getAccountPaymentType().toString());

        // call external entity to process request
        OutPaymentResponseDto outPaymentResponseDto = new OutPaymentResponseDto();
        switch (PaymentExternalEntities.valueOf(outPaymentAccount.get().getExternalEntity())) {
            case EASYPAY:
                try {
                    outPaymentResponseDto = easyPayAPIConnector.createOutPayment(outPaymentIdentification,
                                                                                 outPayment.get().getSourceAccountIdentification(),
                                                                                 outPaymentAccount.get().getExternalEntityIdentification(),
                                                                                 outPaymentAccount.get().getAccountHolderName(),
                                                                                 outPaymentAccount.get().getAccountEmail(),
                                                                                 outPaymentAccount.get().getAccountPhoneNumber(),
                                                                                 outPaymentAccount.get().getAccountIban(),
                                                                                 outPayment.get().getRequesterEntityIdentification(),
                                                                                 outPayment.get().getRequesterAccountHolderName(),
                                                                                 outPayment.get().getRequesterEmail(),
                                                                                 outPayment.get().getRequesterPhoneNumber(),
                                                                                 outPayment.get().getRequesterIban(),
                                                                                 outPayment.get().getRequesterBankAccountCountryCode(),
                                                                                 outPayment.get().getPaymentScheduledDate(),
                                                                                 OutPaymentType.valueOf(outPaymentAccount.get().getAccountPaymentType()),
                                                                                 outPayment.get().getPaymentValue());
                    updatedOutPayment.setExternalPaymentIdentification(outPaymentResponseDto.getExternalPaymentIdentification());
                    updatedOutPayment.setStatus(outPaymentResponseDto.getOutPaymentStatus().toString());
                } catch (Exception e) {
                    updatedOutPayment.setStatus(OutPaymentStatus.FAILED.toString());
                    outPaymentRepository.save(updatedOutPayment);
                    throw new EasyPayException(e.getMessage(), e.getCause());
                }
                break;
            case NONE:
            default:
                return;
        }

        // update out payment
        try {
            updatedOutPayment.setExternalPaymentIdentification(outPaymentResponseDto.getExternalPaymentIdentification());
            updatedOutPayment.setStatus(outPaymentResponseDto.getOutPaymentStatus().toString());
            outPaymentRepository.save(updatedOutPayment);
        } catch (Exception ex) {
            updatedOutPayment.setStatus(OutPaymentStatus.FAILED.toString());
            outPaymentRepository.save(updatedOutPayment);
            throw new GenericException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void processOutPayments(ServiceContext serviceContext) throws OutPaymentForbiddenException, GenericException, EasyPayException {

        if (!Validator.isAdmin(serviceContext) && !Validator.isBatch(serviceContext)) {
            throw new OutPaymentForbiddenException();
        }

        // fetch all payments to fill payment details
        List<OutPayment> paymentsWoutScheduledDateList = outPaymentRepository.getPaymentsWithoutScheduledDate();

        // iterate through payments without details
        for (OutPayment outPayment : paymentsWoutScheduledDateList) {
            // fetch request entity details
            if (null == outPayment.getRequesterIban() || outPayment.getRequesterIban().isEmpty()) {
                switch (EntityType.valueOf(outPayment.getRequesterEntityType())) {
                    // if driver, fetch fiscal entity information
                    case DRIVER:
                        try {
                            FiscalEntityResponseDto fiscalEntity = driversBridge.getDriverFiscalEntity(serviceContext, outPayment.getRequesterEntityIdentification(), outPayment.getOutPaymentIdentification());                 
                            switch (fiscalEntity.getPaymentFrequency()) {
                                case MONTHLY:
                                    outPayment.setPaymentScheduledDate(this.firstDayOfNextMonth());
                                    break;
                                case DAILY:
                                default:
                                    // set scheduled date for now
                                    outPayment.setPaymentScheduledDate(new Date());
                                    break;
                            }
                            outPayment.setRequesterAccountHolderName(fiscalEntity.getBankAccountHolderName());
                            outPayment.setRequesterBankAccountCountryCode("PT");
                            outPayment.setRequesterEmail(fiscalEntity.getEmail());
                            outPayment.setRequesterIban(fiscalEntity.getBankAccountIban());
                            outPayment.setRequesterPhoneNumber(fiscalEntity.getPhoneNumber());
                            outPaymentRepository.save(outPayment);
                        } catch (FeignCommunicationException e) {
                            logger.error("error processing outbound payments batch for outbound payment: " + outPayment.getOutPaymentIdentification() + ". error message: " + e.getMessage());
                            outPayment.setStatus(OutPaymentStatus.FAILED.toString());
                            outPaymentRepository.save(outPayment);        
                        }
                        break;
                    default:
                        logger.error("error processing outbound payments batch for outbound payment: " + outPayment.getOutPaymentIdentification() + ". error message: the type of entity was not recognized");
                        outPayment.setStatus(OutPaymentStatus.FAILED.toString());
                        outPaymentRepository.save(outPayment);        
                }
            }
        }

        // fetch all payments to be processed
        List<OutPayment> paymentsToBeProcessed = outPaymentRepository.getPaymentsToBeProcessed();

        // iterate through payments without details
        for (OutPayment outPayment : paymentsToBeProcessed) {
            // fetch source account to be used
            Optional<OutPaymentAccount> outPaymentAccount = outPaymentAccountRepository.findByAccountBankAccountCountryCodeAndStatus(outPayment.getRequesterBankAccountCountryCode(), OutPaymentAccountStatus.ACTIVE.toString());
            if (!outPaymentAccount.isPresent()) {
                logger.error("error processing outbound payments batch for outbound payment: " + outPayment.getOutPaymentIdentification() + ". error message: no gomile bank account found to process this request");
                outPayment.setStatus(OutPaymentStatus.FAILED.toString());
                outPaymentRepository.save(outPayment);
            } else {
                outPayment.setSourceAccountIdentification(outPaymentAccount.get().getOutPaymentAccountIdentification());
                outPayment.setPaymentType(outPaymentAccount.get().getAccountPaymentType().toString());
                // call easypay to process each request
                OutPaymentResponseDto outPaymentResponseDto = new OutPaymentResponseDto();
                switch (PaymentExternalEntities.valueOf(outPaymentAccount.get().getExternalEntity())) {
                    case EASYPAY:
                        try {
                            outPaymentResponseDto = easyPayAPIConnector.createOutPayment(outPayment.getOutPaymentIdentification(),
                                                                                         outPayment.getSourceAccountIdentification(),
                                                                                         outPaymentAccount.get().getExternalEntityIdentification(),
                                                                                         outPaymentAccount.get().getAccountHolderName(),
                                                                                         outPaymentAccount.get().getAccountEmail(),
                                                                                         outPaymentAccount.get().getAccountPhoneNumber(),
                                                                                         outPaymentAccount.get().getAccountIban(),
                                                                                         outPayment.getRequesterEntityIdentification(),
                                                                                         outPayment.getRequesterAccountHolderName(),
                                                                                         outPayment.getRequesterEmail(),
                                                                                         outPayment.getRequesterPhoneNumber(),
                                                                                         outPayment.getRequesterIban(),
                                                                                         outPayment.getRequesterBankAccountCountryCode(),
                                                                                         outPayment.getPaymentScheduledDate(),
                                                                                         OutPaymentType.valueOf(outPaymentAccount.get().getAccountPaymentType()),
                                                                                         outPayment.getPaymentValue());
                            outPayment.setExternalPaymentIdentification(outPaymentResponseDto.getExternalPaymentIdentification());
                            outPayment.setStatus(outPaymentResponseDto.getOutPaymentStatus().toString());
                            outPaymentRepository.save(outPayment);
                        } catch (Exception e) {
                            logger.error("error processing outbound payments batch for outbound payment: " + outPayment.getOutPaymentIdentification() + ". error message: " + e.getMessage());
                            outPayment.setStatus(OutPaymentStatus.FAILED.toString());
                            outPaymentRepository.save(outPayment);
                        }
                        break;
                    case NONE:
                    default:
                        return;
                }
            }
        }
    }

    public Date firstDayOfNextMonth() {
        return Date.from(LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth())
                                        .atStartOfDay()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant());
    }
}