package com.lastmile.paymentservice.dto.invoices;

import com.lastmile.utils.enums.EntityType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class GetInvoiceResponseDto {

    private String invoiceIdentification;

    private String paymentIdentification;

    private String entityIdentification;

    private EntityType entityType;

    private String invoiceClientName;

    private String invoiceFiscalNumber;

    private String invoiceFullAddress;

    private String invoiceZipCode;

    private String invoiceUrl;

    private String externalEntity;

    public String getInvoiceIdentification() {
        return this.invoiceIdentification;
    }

    public void setInvoiceIdentification(String invoiceIdentification) {
        this.invoiceIdentification = invoiceIdentification;
    }

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

    public String getInvoiceUrl() {
        return this.invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

    public String getExternalEntity() {
        return this.externalEntity;
    }

    public void setExternalEntity(String externalEntity) {
        this.externalEntity = externalEntity;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
