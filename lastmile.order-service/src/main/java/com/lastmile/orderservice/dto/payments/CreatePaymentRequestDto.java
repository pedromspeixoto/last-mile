package com.lastmile.orderservice.dto.payments;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.orderservice.dto.OrderRequestDto;
import com.lastmile.orderservice.enums.RequesterEntityType;
import com.lastmile.utils.enums.payments.PaymentType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_EMPTY)
public class CreatePaymentRequestDto {

    @NotBlank(message = "Requester entity identification is mandatory")
    private String requesterEntityIdentification;

    @NotNull(message = "Requester entity type is mandatory")
    private RequesterEntityType requesterEntityType;

    @NotBlank(message = "Transaction identification is mandatory")
    private String transactionIdentification;

    @NotBlank(message = "Payment details identification is mandatory")
    private String paymentDetailsId;

    @NotBlank(message = "Payment value is mandatory")
    private Double paymentValue;

    @NotNull(message = "Payment Type is mandatory")
    private PaymentType paymentType;

    public String getRequesterEntityIdentification() {
        return this.requesterEntityIdentification;
    }

    public void setRequesterEntityIdentification(String requesterEntityIdentification) {
        this.requesterEntityIdentification = requesterEntityIdentification;
    }

    public RequesterEntityType getRequesterEntityType() {
        return this.requesterEntityType;
    }

    public void setRequesterEntityType(RequesterEntityType requesterEntityType) {
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

    public CreatePaymentRequestDto() {
    }

    public CreatePaymentRequestDto(OrderRequestDto orderRequestDto, String orderIdentification, Double paymentValue) {

        switch (orderRequestDto.getRequesterEntityType()) {
            case ACCOUNT:
                this.paymentType = PaymentType.DEFERRED;
                break;
            case MARKETPLACE:
                this.paymentType = PaymentType.DIRECT;
                break;
            default:
                return;
        }

        this.paymentDetailsId = orderRequestDto.getPaymentDetailsId();
        this.requesterEntityType = orderRequestDto.getRequesterEntityType();
        this.paymentValue = paymentValue;
        this.requesterEntityIdentification = orderRequestDto.getRequesterIdentification();
        this.transactionIdentification = orderIdentification;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}