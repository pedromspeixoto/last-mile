package com.lastmile.customerservice.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreateNewApiKeyResponseDto {

    private String apiKey;

    private String privateKey;

    public CreateNewApiKeyResponseDto() {
    }

    public CreateNewApiKeyResponseDto(String apiKey, String privateKey) {
        this.apiKey = apiKey;
        this.privateKey = privateKey;
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