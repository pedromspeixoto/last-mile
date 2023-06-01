package com.lastmile.paymentservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "out_payments_accounts")
public class OutPaymentAccount extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "out_payment_account_identification")
    private String outPaymentAccountIdentification;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "account_iban")
    private String accountIban;

    @Column(name = "account_email")
    private String accountEmail;

    @Column(name = "account_phone_number")
    private String accountPhoneNumber;

    @Column(name = "account_bank_account_country_code")
    private String accountBankAccountCountryCode;

    @Column(name = "account_payment_type")
    private String accountPaymentType;

    @Column(name = "external_entity")
    private String externalEntity;

    @Column(name = "external_entity_identification")
    private String externalEntityIdentification;

    @Column(name = "status")
    private String status;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountPaymentType() {
        return this.accountPaymentType;
    }

    public void setAccountPaymentType(String accountPaymentType) {
        this.accountPaymentType = accountPaymentType;
    }

    public String getOutPaymentAccountIdentification() {
        return this.outPaymentAccountIdentification;
    }

    public void setOutPaymentAccountIdentification(String outPaymentAccountIdentification) {
        this.outPaymentAccountIdentification = outPaymentAccountIdentification;
    }

    public String getAccountHolderName() {
        return this.accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountIban() {
        return this.accountIban;
    }

    public void setAccountIban(String accountIban) {
        this.accountIban = accountIban;
    }

    public String getAccountEmail() {
        return this.accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public String getAccountPhoneNumber() {
        return this.accountPhoneNumber;
    }

    public void setAccountPhoneNumber(String accountPhoneNumber) {
        this.accountPhoneNumber = accountPhoneNumber;
    }

    public String getAccountBankAccountCountryCode() {
        return this.accountBankAccountCountryCode;
    }

    public void setAccountBankAccountCountryCode(String accountBankAccountCountryCode) {
        this.accountBankAccountCountryCode = accountBankAccountCountryCode;
    }

    public String getExternalEntity() {
        return this.externalEntity;
    }

    public void setExternalEntity(String externalEntity) {
        this.externalEntity = externalEntity;
    }

    public String getExternalEntityIdentification() {
        return this.externalEntityIdentification;
    }

    public void setExternalEntityIdentification(String externalEntityIdentification) {
        this.externalEntityIdentification = externalEntityIdentification;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
