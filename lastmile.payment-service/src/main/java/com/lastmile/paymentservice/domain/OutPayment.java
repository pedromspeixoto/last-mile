package com.lastmile.paymentservice.domain;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "out_payments")
public class OutPayment extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "out_payment_identification")
    private String outPaymentIdentification;

    @Column(name = "source_account_identification")
    private String sourceAccountIdentification;

    @Column(name = "transaction_identification")
    private String transactionIdentification;

    @Column(name = "requester_identification")
    private String requesterEntityIdentification;

    @Column(name = "requester_entity_type")
    private String requesterEntityType;

    @Column(name = "requester_account_holder_name")
    private String requesterAccountHolderName;

    @Column(name = "requester_iban")
    private String requesterIban;

    @Column(name = "requester_email")
    private String requesterEmail;

    @Column(name = "requester_phone_number")
    private String requesterPhoneNumber;

    @Column(name = "requester_bank_account_country_code")
    private String requesterBankAccountCountryCode;
    
    @Column(name = "payment_value")
    private Double paymentValue;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "payment_scheduled_date")
    private Date paymentScheduledDate;

    @Column(name = "external_payment_identification")
    private String externalPaymentIdentification;

    @Column(name = "status")
    private String status;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOutPaymentIdentification() {
        return this.outPaymentIdentification;
    }

    public void setOutPaymentIdentification(String outPaymentIdentification) {
        this.outPaymentIdentification = outPaymentIdentification;
    }

    public String getSourceAccountIdentification() {
        return this.sourceAccountIdentification;
    }

    public void setSourceAccountIdentification(String sourceAccountIdentification) {
        this.sourceAccountIdentification = sourceAccountIdentification;
    }

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

    public String getRequesterEntityType() {
        return this.requesterEntityType;
    }

    public void setRequesterEntityType(String requesterEntityType) {
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

    public String getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(String paymentType) {
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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
