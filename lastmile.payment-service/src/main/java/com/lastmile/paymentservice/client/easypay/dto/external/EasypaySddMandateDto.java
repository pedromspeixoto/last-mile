package com.lastmile.paymentservice.client.easypay.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EasypaySddMandateDto {
    private String id;

    private String iban;

    private String key;

    private String name;

    private String email;

    private String phone;

    @JsonProperty("account_holder")
    private String accountHolder;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("max_num_debits")
    private String maxNumDebits;

    @JsonProperty("reference_adc")
    private String referenceAdc;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIban() {
        return this.iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAccountHolder() {
        return this.accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMaxNumDebits() {
        return this.maxNumDebits;
    }

    public void setMaxNumDebits(String maxNumDebits) {
        this.maxNumDebits = maxNumDebits;
    }

    public String getReferenceAdc() {
        return this.referenceAdc;
    }

    public void setReferenceAdc(String referenceAdc) {
        this.referenceAdc = referenceAdc;
    }

}