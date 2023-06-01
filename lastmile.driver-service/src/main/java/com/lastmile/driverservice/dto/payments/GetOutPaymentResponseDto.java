package com.lastmile.driverservice.dto.payments;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.enums.payments.OutPaymentStatus;
import com.lastmile.utils.enums.payments.OutPaymentType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_EMPTY)
public class GetOutPaymentResponseDto {

    private String outPaymentIdentification;

    private String transactionIdentification;

    private String requesterIdentification;

    private EntityType requesterEntityType;

    private String requesterAccountHolderName;

    private String requesterIban;

    private String requesterEmail;

    private String requesterPhoneNumber;

    private String requesterBankAccountCountryCode;
    
    private Double paymentValue;

    private OutPaymentType paymentType;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date paymentScheduledDate;

    private String externalPaymentIdentification;

    private OutPaymentStatus status;

    public String getOutPaymentIdentification() {
        return this.outPaymentIdentification;
    }

    public void setOutPaymentIdentification(String outPaymentIdentification) {
        this.outPaymentIdentification = outPaymentIdentification;
    }

    public String getTransactionIdentification() {
        return this.transactionIdentification;
    }

    public void setTransactionIdentification(String transactionIdentification) {
        this.transactionIdentification = transactionIdentification;
    }

    public String getRequesterIdentification() {
        return this.requesterIdentification;
    }

    public void setRequesterIdentification(String requesterIdentification) {
        this.requesterIdentification = requesterIdentification;
    }

    public EntityType getRequesterEntityType() {
        return this.requesterEntityType;
    }

    public void setRequesterEntityType(EntityType requesterEntityType) {
        this.requesterEntityType = requesterEntityType;
    }

    public String getRequesterAccountHolderName() {
        return this.requesterAccountHolderName;
    }

    public void setRequesterAccountHolderName(String requesterAccountHolderName) {
        this.requesterAccountHolderName = requesterAccountHolderName;
    }

    public String getRequesterIban() {
        return this.requesterIban;
    }

    public void setRequesterIban(String requesterIban) {
        this.requesterIban = requesterIban;
    }

    public String getRequesterEmail() {
        return this.requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public String getRequesterPhoneNumber() {
        return this.requesterPhoneNumber;
    }

    public void setRequesterPhoneNumber(String requesterPhoneNumber) {
        this.requesterPhoneNumber = requesterPhoneNumber;
    }

    public String getRequesterBankAccountCountryCode() {
        return this.requesterBankAccountCountryCode;
    }

    public void setRequesterBankAccountCountryCode(String requesterBankAccountCountryCode) {
        this.requesterBankAccountCountryCode = requesterBankAccountCountryCode;
    }

    public Double getPaymentValue() {
        return this.paymentValue;
    }

    public void setPaymentValue(Double paymentValue) {
        this.paymentValue = paymentValue;
    }

    public OutPaymentType getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(OutPaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Date getPaymentScheduledDate() {
        return this.paymentScheduledDate;
    }

    public void setPaymentScheduledDate(Date paymentScheduledDate) {
        this.paymentScheduledDate = paymentScheduledDate;
    }

    public String getExternalPaymentIdentification() {
        return this.externalPaymentIdentification;
    }

    public void setExternalPaymentIdentification(String externalPaymentIdentification) {
        this.externalPaymentIdentification = externalPaymentIdentification;
    }

    public OutPaymentStatus getStatus() {
        return this.status;
    }

    public void setStatus(OutPaymentStatus status) {
        this.status = status;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}