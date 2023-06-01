package com.lastmile.orderservice.dto.payments;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.utils.enums.EntityType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_EMPTY)
public class CreateOutPaymentRequestDto {

    @NotBlank(message = "Transaction identification is mandatory")
    private String transactionIdentification;

    @NotBlank(message = "Requester entity identification is mandatory")
    private String requesterEntityIdentification;

    @NotNull(message = "Requester entity type is mandatory")
    private EntityType requesterEntityType;

    @NotNull(message = "Payment value is mandatory")
    private Double paymentValue;

    public String getTransactionIdentification() {
        return this.transactionIdentification;
    }

    public void setTransactionIdentification(String transactionIdentification) {
        this.transactionIdentification = transactionIdentification;
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

    public Double getPaymentValue() {
        return this.paymentValue;
    }

    public void setPaymentValue(Double paymentValue) {
        this.paymentValue = paymentValue;
    }

    public CreateOutPaymentRequestDto() {
    }

    public CreateOutPaymentRequestDto(String entityIdentification, EntityType entityType, String transactionIdentification, Double paymentValue) {
        this.requesterEntityIdentification = entityIdentification;
        this.requesterEntityType = entityType;
        this.transactionIdentification = transactionIdentification;
        this.paymentValue = paymentValue;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}