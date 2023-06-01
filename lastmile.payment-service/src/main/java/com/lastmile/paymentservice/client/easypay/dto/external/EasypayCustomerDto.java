package com.lastmile.paymentservice.client.easypay.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EasypayCustomerDto {

    private String id;

    private String name;

    private String email;

    private String phone;

    @JsonProperty("phone_indicative")
    private String phoneIndicative;

    @JsonProperty("fiscal_number")
    private String fiscalNumber;

    private String key;

    private String language;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhoneIndicative() {
        return this.phoneIndicative;
    }

    public void setPhoneIndicative(String phoneIndicative) {
        this.phoneIndicative = phoneIndicative;
    }

    public String getFiscalNumber() {
        return this.fiscalNumber;
    }

    public void setFiscalNumber(String fiscalNumber) {
        this.fiscalNumber = fiscalNumber;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}