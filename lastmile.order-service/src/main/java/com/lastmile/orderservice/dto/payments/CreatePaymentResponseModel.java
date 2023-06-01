package com.lastmile.orderservice.dto.payments;

import com.lastmile.utils.enums.payments.PaymentStatus;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreatePaymentResponseModel {

    private String paymentIdentification;

    private PaymentStatus paymentStatus;

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