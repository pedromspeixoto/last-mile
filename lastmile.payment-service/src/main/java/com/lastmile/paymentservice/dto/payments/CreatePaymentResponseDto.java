package com.lastmile.paymentservice.dto.payments;

import com.lastmile.paymentservice.enums.PaymentStatus;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreatePaymentResponseDto {

    private String paymentIdentification;

    private PaymentStatus paymentStatus;

    public CreatePaymentResponseDto() {
    }

    public CreatePaymentResponseDto(String paymentIdentification, PaymentStatus paymentStatus) {
        this.paymentIdentification = paymentIdentification;
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentIdentification() {
        return this.paymentIdentification;
    }

    public void setPaymentIdentification(String paymentIdentification) {
        this.paymentIdentification = paymentIdentification;
    }

    public PaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}