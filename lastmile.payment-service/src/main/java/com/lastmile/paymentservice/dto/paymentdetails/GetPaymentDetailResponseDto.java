package com.lastmile.paymentservice.dto.paymentdetails;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.paymentservice.enums.PaymentDetailStatus;
import com.lastmile.paymentservice.enums.PaymentDetailType;
import com.lastmile.paymentservice.enums.PaymentExternalEntities;
import com.lastmile.utils.enums.EntityType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class GetPaymentDetailResponseDto {

    private String paymentDetailIdentification;

    private PaymentDetailType paymentDetailType;

    private String entityIdentification;

    private EntityType entityType;

    private String paymentPhoneNumber;

    private String cardLastFourDigits;

    private String cardType;

    private String cardExpiryDate;

    private String paymentToken;

    private PaymentExternalEntities externalEntity;

    private String externalEntityIdentification;

    private PaymentDetailStatus status;

    public String getPaymentDetailIdentification() {
        return this.paymentDetailIdentification;
    }

    public void setPaymentDetailIdentification(String paymentDetailIdentification) {
        this.paymentDetailIdentification = paymentDetailIdentification;
    }

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

    public String getCardLastFourDigits() {
        return this.cardLastFourDigits;
    }

    public void setCardLastFourDigits(String cardLastFourDigits) {
        this.cardLastFourDigits = cardLastFourDigits;
    }

    public String getCardType() {
        return this.cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardExpiryDate() {
        return this.cardExpiryDate;
    }

    public void setCardExpiryDate(String cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
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

    public String getExternalEntityIdentification() {
        return this.externalEntityIdentification;
    }

    public void setExternalEntityIdentification(String externalEntityIdentification) {
        this.externalEntityIdentification = externalEntityIdentification;
    }

    public PaymentDetailStatus getStatus() {
        return this.status;
    }

    public void setStatus(PaymentDetailStatus status) {
        this.status = status;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
