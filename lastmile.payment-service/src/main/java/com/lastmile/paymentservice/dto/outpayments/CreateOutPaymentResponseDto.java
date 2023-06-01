package com.lastmile.paymentservice.dto.outpayments;

import com.lastmile.paymentservice.enums.OutPaymentStatus;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreateOutPaymentResponseDto {

    private String outPaymentIdentification;

    private OutPaymentStatus paymentStatus;

    public CreateOutPaymentResponseDto() {
    }

    public CreateOutPaymentResponseDto(String outPaymentIdentification, OutPaymentStatus paymentStatus) {
        this.outPaymentIdentification = outPaymentIdentification;
        this.paymentStatus = paymentStatus;
    }

    public String getOutPaymentIdentification() {
        return this.outPaymentIdentification;
    }

    public void setOutPaymentIdentification(String outPaymentIdentification) {
        this.outPaymentIdentification = outPaymentIdentification;
    }

    public OutPaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setPaymentStatus(OutPaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}