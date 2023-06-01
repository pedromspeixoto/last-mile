package com.lastmile.paymentservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "invoices")
public class Invoice extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_identification")
    private String invoiceIdentification;

    @Column(name = "payment_identification")
    private String paymentIdentification;

    @Column(name = "entity_identification")
    private String entityIdentification;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "invoice_client_name")
    private String invoiceClientName;

    @Column(name = "invoice_fiscal_number")
    private String invoiceFiscalNumber;

    @Column(name = "invoice_full_address")
    private String invoiceFullAddress;

    @Column(name = "invoice_zip_code")
    private String invoiceZipCode;

    @Column(name = "invoice_url")
    private String invoiceUrl;

    @Column(name = "external_entity")
    private String externalEntity;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getEntityType() {
        return this.entityType;
    }

    public void setEntityType(String entityType) {
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

}
