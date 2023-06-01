package com.lastmile.paymentservice.dto.outpayments;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.validations.ValidCountryCode;
import com.lastmile.utils.validations.ValidEmail;
import com.lastmile.utils.validations.ValidIban;
import com.lastmile.utils.validations.ValidPhoneNumber;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_EMPTY)
public class CreateOutPaymentRequestDto {

    @NotBlank(message = "Transaction identification is mandatory")
    private String transactionIdentification;

    @NotBlank(message = "Requester entity identification is mandatory")
    private String requesterEntityIdentification;

    @NotNull(message = "Requester entity type is mandatory")
    private EntityType requesterEntityType;

    private String requesterAccountHolderName;

    @ValidIban
    private String requesterAccountIban;

    @ValidEmail
    private String requesterAccountEmail;

    @ValidPhoneNumber
    private String requesterAccountPhoneNumber;

    @ValidCountryCode
    private String requesterAccountBankAccountCountryCode;

    @NotNull(message = "Payment value is mandatory")
    private Double paymentValue;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date paymentScheduledDate;

    public String getTransactionIdentification() {
        return this.transactionIdentification;
    }

    public void setTransactionIdentification(String transactionIdentification) {
        this.transactionIdentification = transactionIdentification;
    }

    public String getRequesterEntityIdentification() {
        return this.requesterEntityIdentification;
    }

    public void setRequesterEntityIdentification(String requesterEntityIdentification) {
        this.requesterEntityIdentification = requesterEntityIdentification;
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

    public String getRequesterAccountIban() {
        return this.requesterAccountIban;
    }

    public void setRequesterAccountIban(String requesterAccountIban) {
        this.requesterAccountIban = requesterAccountIban;
    }

    public String getRequesterAccountEmail() {
        return this.requesterAccountEmail;
    }

    public void setRequesterAccountEmail(String requesterAccountEmail) {
        this.requesterAccountEmail = requesterAccountEmail;
    }

    public String getRequesterAccountPhoneNumber() {
        return this.requesterAccountPhoneNumber;
    }

    public void setRequesterAccountPhoneNumber(String requesterAccountPhoneNumber) {
        this.requesterAccountPhoneNumber = requesterAccountPhoneNumber;
    }

    public String getRequesterAccountBankAccountCountryCode() {
        return this.requesterAccountBankAccountCountryCode;
    }

    public void setRequesterAccountBankAccountCountryCode(String requesterAccountBankAccountCountryCode) {
        this.requesterAccountBankAccountCountryCode = requesterAccountBankAccountCountryCode;
    }

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

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
