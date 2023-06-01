package com.lastmile.paymentservice.dto.outpayments;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.paymentservice.enums.OutPaymentStatus;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_EMPTY)
public class UpdateOutPaymentRequestDto {

    private Double paymentValue;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date paymentScheduledDate;

    private OutPaymentStatus outPaymentStatus;

    public Double getPaymentValue() {
        return this.paymentValue;
    }

    public void setPaymentValue(Double paymentValue) {
        this.paymentValue = paymentValue;
    }

    public Date getPaymentScheduledDate() {
        return this.paymentScheduledDate;
    }

    public void setPaymentScheduledDate(Date paymentScheduledDate) {
        this.paymentScheduledDate = paymentScheduledDate;
    }

    public OutPaymentStatus getOutPaymentStatus() {
        return this.outPaymentStatus;
    }

    public void setOutPaymentStatus(OutPaymentStatus outPaymentStatus) {
        this.outPaymentStatus = outPaymentStatus;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}