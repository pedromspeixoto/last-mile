package com.lastmile.paymentservice.dto.payments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.paymentservice.enums.PaymentStatus;
import com.lastmile.paymentservice.enums.PaymentType;
import com.lastmile.utils.enums.EntityType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class GetPaymentResponseDto {

    private String paymentIdentification;

    private String requesterEntityIdentification;

    private EntityType requesterEntityType;

    private String transactionIdentification;

    private String paymentDetailsId;

    private Double paymentValue;

    private PaymentType paymentType;

    private PaymentStatus status;

    private String externalPaymentIdentification;

    public String getPaymentIdentification() {
        return this.paymentIdentification;
    }

    public void setPaymentIdentification(String paymentIdentification) {
        this.paymentIdentification = paymentIdentification;
    }

    public String getRequesterEntityIdentification() {
        return this.requesterEntityIdentification;
    }

    public void setRequesterEntityIdentification(String requesterEntityIdentification) {
        this.requesterEntityIdentification = requesterEntityIdentification;
    }

    public EntityType getRequesterEntityType() {
        return this.requesterEntityType;
    }

    public void setRequesterEntityType(EntityType requesterEntityType) {
        this.requesterEntityType = requesterEntityType;
    }

    public String getTransactionIdentification() {
        return this.transactionIdentification;
    }

    public void setTransactionIdentification(String transactionIdentification) {
        this.transactionIdentification = transactionIdentification;
    }

    public String getPaymentDetailsId() {
        return this.paymentDetailsId;
    }

    public void setPaymentDetailsId(String paymentDetailsId) {
        this.paymentDetailsId = paymentDetailsId;
    }

    public Double getPaymentValue() {
        return this.paymentValue;
    }

    public void setPaymentValue(Double paymentValue) {
        this.paymentValue = paymentValue;
    }

    public PaymentType getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getExternalPaymentIdentification() {
        return this.externalPaymentIdentification;
    }

    public void setExternalPaymentIdentification(String externalPaymentIdentification) {
        this.externalPaymentIdentification = externalPaymentIdentification;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
