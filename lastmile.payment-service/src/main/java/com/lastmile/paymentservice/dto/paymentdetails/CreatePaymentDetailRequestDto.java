package com.lastmile.paymentservice.dto.paymentdetails;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.paymentservice.enums.PaymentDetailType;
import com.lastmile.paymentservice.enums.PaymentExternalEntities;
import com.lastmile.utils.validations.ValidEmail;
import com.lastmile.utils.validations.ValidPhoneNumber;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.lastmile.utils.enums.EntityType;

@JsonInclude(Include.NON_EMPTY)
public class CreatePaymentDetailRequestDto {

    @NotNull(message = "Payment detail type is mandatory")
    private PaymentDetailType paymentDetailType;

    @NotBlank(message = "Entity identification is mandatory")
    private String entityIdentification;

    @NotNull(message = "Entity type is mandatory")
    private EntityType entityType;

    private String paymentName;

    @ValidEmail
    private String paymentEmail;

    private String paymentFiscalNumber;

    @ValidPhoneNumber
    private String paymentPhoneNumber;

    private String paymentToken;

    @NotNull(message = "External entity is mandatory")
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

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
