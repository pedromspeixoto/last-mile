package com.lastmile.paymentservice.dto.payments;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.paymentservice.enums.PaymentType;
import com.lastmile.utils.enums.EntityType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_EMPTY)
public class CreatePaymentRequestDto {

    @NotBlank(message = "Requester entity identification is mandatory")
    private String requesterEntityIdentification;

    @NotNull(message = "Requester entity type is mandatory")
    private EntityType requesterEntityType;

    @NotBlank(message = "Transaction identification is mandatory")
    private String transactionIdentification;

    @NotBlank(message = "Payment details identification is mandatory")
    private String paymentDetailsId;

    @NotNull(message = "Payment value is mandatory")
    private Double paymentValue;

    @NotNull(message = "Payment Type is mandatory")
    private PaymentType paymentType;

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

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
