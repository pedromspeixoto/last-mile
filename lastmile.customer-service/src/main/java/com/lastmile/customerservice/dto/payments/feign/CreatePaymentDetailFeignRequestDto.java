package com.lastmile.customerservice.dto.payments.feign;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.customerservice.dto.payments.CreatePaymentDetailRequestDto;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.customerservice.enums.payments.PaymentDetailType;
import com.lastmile.customerservice.enums.payments.PaymentExternalEntities;

import org.modelmapper.ModelMapper;

@JsonInclude(Include.NON_EMPTY)
public class CreatePaymentDetailFeignRequestDto {

    @NotNull(message = "Payment detail type is mandatory")
    private PaymentDetailType paymentDetailType;

    @NotBlank(message = "Entity identification is mandatory")
    private String entityIdentification;

    @NotNull(message = "Entity type is mandatory")
    private EntityType entityType;

    private String paymentName;

    private String paymentEmail;

    private String paymentFiscalNumber;
    
    private String paymentPhoneNumber;

    private String paymentToken;

    @NotBlank(message = "External entity is mandatory")
    private PaymentExternalEntities externalEntity;

    public PaymentDetailType getPaymentDetailType() {
        return this.paymentDetailType;
    }

    public void setPaymentDetailType(PaymentDetailType paymentDetailType) {
        this.paymentDetailType = paymentDetailType;
    }

    public String getEntityIdentification() {
        return this.entityIdentification;
    }

    public void setEntityIdentification(String entityIdentification) {
        this.entityIdentification = entityIdentification;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getPaymentPhoneNumber() {
        return this.paymentPhoneNumber;
    }

    public void setPaymentPhoneNumber(String paymentPhoneNumber) {
        this.paymentPhoneNumber = paymentPhoneNumber;
    }

    public String getPaymentToken() {
        return this.paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }

    public PaymentExternalEntities getExternalEntity() {
        return this.externalEntity;
    }

    public void setExternalEntity(PaymentExternalEntities externalEntity) {
        this.externalEntity = externalEntity;
    }

    public String getPaymentName() {
        return this.paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getPaymentEmail() {
        return this.paymentEmail;
    }

    public void setPaymentEmail(String paymentEmail) {
        this.paymentEmail = paymentEmail;
    }

    public String getPaymentFiscalNumber() {
        return this.paymentFiscalNumber;
    }

    public void setPaymentFiscalNumber(String paymentFiscalNumber) {
        this.paymentFiscalNumber = paymentFiscalNumber;
    }

    public CreatePaymentDetailFeignRequestDto() {
    }

    public static CreatePaymentDetailFeignRequestDto mapToFeignRequest(CreatePaymentDetailRequestDto createPaymentDetailRequestDto,
                                                                       String entityIdentification,
                                                                       EntityType entityType,
                                                                       String customerName, 
                                                                       String customerPhoneNumber,
                                                                       String customerEmail,
                                                                       String customerFiscalNumber) {

        ModelMapper modelMapper = new ModelMapper();
        CreatePaymentDetailFeignRequestDto createPaymentDetailFeignRequestDto = modelMapper.map(createPaymentDetailRequestDto, CreatePaymentDetailFeignRequestDto.class);

        createPaymentDetailFeignRequestDto.setEntityType(entityType);
        createPaymentDetailFeignRequestDto.setEntityIdentification(entityIdentification);
        createPaymentDetailFeignRequestDto.setPaymentName(customerName);
        createPaymentDetailFeignRequestDto.setPaymentFiscalNumber(customerFiscalNumber);
        createPaymentDetailFeignRequestDto.setPaymentEmail(customerEmail);
        createPaymentDetailFeignRequestDto.setPaymentPhoneNumber(customerPhoneNumber);

        return createPaymentDetailFeignRequestDto;

    }


}