package com.lastmile.paymentservice.dto.invoices;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.utils.enums.EntityType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_EMPTY)
public class CreateInvoiceRequestDto {

    @NotBlank(message = "Payment identification is mandatory")
    private String paymentIdentification;

    @NotBlank(message = "Entity identification is mandatory")
    private String entityIdentification;

    @NotNull(message = "Entity type is mandatory")
    private EntityType entityType;

    @NotBlank(message = "Invoice client name is mandatory")
    private String invoiceClientName;

    @NotBlank(message = "Invoice Fiscal number is mandatory")
    private String invoiceFiscalNumber;

    private String invoiceFullAddress;

    private String invoiceZipCode;

    public String getPaymentIdentification() {
        return this.paymentIdentification;
    }

    public void setPaymentIdentification(String paymentIdentification) {
        this.paymentIdentification = paymentIdentification;
    }

    public String getEntityIdentification() {
        return this.entityIdentification;
    }

    public void setEntityIdentification(String entityIdentification) {
        this.entityIdentification = entityIdentification;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getInvoiceClientName() {
        return this.invoiceClientName;
    }

    public void setInvoiceClientName(String invoiceClientName) {
        this.invoiceClientName = invoiceClientName;
    }

    public String getInvoiceFiscalNumber() {
        return this.invoiceFiscalNumber;
    }

    public void setInvoiceFiscalNumber(String invoiceFiscalNumber) {
        this.invoiceFiscalNumber = invoiceFiscalNumber;
    }

    public String getInvoiceFullAddress() {
        return this.invoiceFullAddress;
    }

    public void setInvoiceFullAddress(String invoiceFullAddress) {
        this.invoiceFullAddress = invoiceFullAddress;
    }

    public String getInvoiceZipCode() {
        return this.invoiceZipCode;
    }

    public void setInvoiceZipCode(String invoiceZipCode) {
        this.invoiceZipCode = invoiceZipCode;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}