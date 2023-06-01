package com.lastmile.paymentservice.dto.paymentdetails;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.paymentservice.enums.PaymentDetailStatus;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_EMPTY)
public class UpdatePaymentDetailRequestDto {

    private PaymentDetailStatus status;

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
