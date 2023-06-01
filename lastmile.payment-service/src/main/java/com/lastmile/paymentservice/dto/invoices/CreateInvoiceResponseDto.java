package com.lastmile.paymentservice.dto.invoices;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreateInvoiceResponseDto {

    private String invoiceIdentification;

    public CreateInvoiceResponseDto() {
    }

    public CreateInvoiceResponseDto(String invoiceIdentification) {
        this.invoiceIdentification = invoiceIdentification;
    }

    public String getInvoiceIdentification() {
        return this.invoiceIdentification;
    }

    public void setInvoiceIdentification(String invoiceIdentification) {
        this.invoiceIdentification = invoiceIdentification;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}