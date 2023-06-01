package com.lastmile.customerservice.dto.payments;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.customerservice.enums.payments.PaymentDetailType;
import com.lastmile.customerservice.enums.payments.PaymentExternalEntities;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_EMPTY)
public class CreatePaymentDetailRequestDto {

    @NotNull(message = "Payment detail type is mandatory")
    private PaymentDetailType paymentDetailType;

    @NotNull(message = "External entity is mandatory")
    private PaymentExternalEntities externalEntity;

    public PaymentDetailType getPaymentDetailType() {
        return this.paymentDetailType;
    }

    public void setPaymentDetailType(PaymentDetailType paymentDetailType) {
        this.paymentDetailType = paymentDetailType;
    }

    public PaymentExternalEntities getExternalEntity() {
        return this.externalEntity;
    }

    public void setExternalEntity(PaymentExternalEntities externalEntity) {
        this.externalEntity = externalEntity;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
