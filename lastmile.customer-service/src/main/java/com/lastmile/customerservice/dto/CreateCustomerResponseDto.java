package com.lastmile.customerservice.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreateCustomerResponseDto {

    private String customerIdentification;

    private String apiKey;

    private String privateKey;

    public CreateCustomerResponseDto() {
    }
    public CreateCustomerResponseDto(String customerIdentification, String apiKey, String privateKey) {
        this.customerIdentification = customerIdentification;
        this.apiKey = apiKey;
        this.privateKey = privateKey;
    }

    public String getCustomerIdentification() {
        return this.customerIdentification;
    }

    public void setCustomerIdentification(String customerIdentification) {
        this.customerIdentification = customerIdentification;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}