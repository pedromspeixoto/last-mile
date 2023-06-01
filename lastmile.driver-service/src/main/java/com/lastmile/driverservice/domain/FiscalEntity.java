package com.lastmile.driverservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "fiscal_entities")
public class FiscalEntity extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fiscal_entity_identification")
    private String fiscalEntityIdentification;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "fiscal_number")
    private String fiscalNumber;

    @Column(name = "bank_account_holder_name")
    private String bankAccountHolderName;

    @Column(name = "bank_account_iban")
    private String bankAccountIban;

    @Column(name = "bank_account_country_code")
    private String bankAccountCountryCode;

    @Column(name = "payment_frequency")
    private String paymentFrequency;

    @Column(name = "active_address_id")
    private String activeAddressId;

    @Column(name = "active_billing_address_id")
    private String activeBillingAddressId;

    @Column(name = "status")
    private String status;

    @Column(name = "entity_validated")
    private Boolean entityValidated;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFiscalEntityIdentification() {
        return this.fiscalEntityIdentification;
    }

    public void setFiscalEntityIdentification(String fiscalEntityIdentification) {
        this.fiscalEntityIdentification = fiscalEntityIdentification;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFiscalNumber() {
        return this.fiscalNumber;
    }

    public void setFiscalNumber(String fiscalNumber) {
        this.fiscalNumber = fiscalNumber;
    }

    public String getBankAccountHolderName() {
        return this.bankAccountHolderName;
    }

    public void setBankAccountHolderName(String bankAccountHolderName) {
        this.bankAccountHolderName = bankAccountHolderName;
    }

    public String getBankAccountIban() {
        return this.bankAccountIban;
    }

    public void setBankAccountIban(String bankAccountIban) {
        this.bankAccountIban = bankAccountIban;
    }

    public String getBankAccountCountryCode() {
        return this.bankAccountCountryCode;
    }

    public void setBankAccountCountryCode(String bankAccountCountryCode) {
        this.bankAccountCountryCode = bankAccountCountryCode;
    }

    public String getPaymentFrequency() {
        return this.paymentFrequency;
    }

    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public String getActiveAddressId() {
        return this.activeAddressId;
    }

    public void setActiveAddressId(String activeAddressId) {
        this.activeAddressId = activeAddressId;
    }

    public String getActiveBillingAddressId() {
        return this.activeBillingAddressId;
    }

    public void setActiveBillingAddressId(String activeBillingAddressId) {
        this.activeBillingAddressId = activeBillingAddressId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean isEntityValidated() {
        return this.entityValidated;
    }

    public Boolean getEntityValidated() {
        return this.entityValidated;
    }

    public void setEntityValidated(Boolean entityValidated) {
        this.entityValidated = entityValidated;
    }

}