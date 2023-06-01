package com.lastmile.customerservice.dto.payments;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreatePaymentDetailResponseDto {

    private String paymentDetailIdentification;

    private String paymentUrl;

    public CreatePaymentDetailResponseDto() {
    }

    public CreatePaymentDetailResponseDto(String paymentDetailIdentification, String paymentUrl) {
        this.paymentDetailIdentification = paymentDetailIdentification;
        this.paymentUrl = paymentUrl;
    }

    public String getPaymentDetailIdentification() {
        return this.paymentDetailIdentification;
    }

    public void setPaymentDetailIdentification(String paymentDetailIdentification) {
        this.paymentDetailIdentification = paymentDetailIdentification;
    }

    public String getPaymentUrl() {
        return this.paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}