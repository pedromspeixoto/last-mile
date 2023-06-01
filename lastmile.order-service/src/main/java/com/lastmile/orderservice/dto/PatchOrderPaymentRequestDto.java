package com.lastmile.orderservice.dto;

import javax.validation.constraints.NotNull;

import com.lastmile.utils.enums.payments.PaymentStatus;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class PatchOrderPaymentRequestDto {

    @NotNull(message = "Payment status must not be empty")
    private PaymentStatus paymentStatus;

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