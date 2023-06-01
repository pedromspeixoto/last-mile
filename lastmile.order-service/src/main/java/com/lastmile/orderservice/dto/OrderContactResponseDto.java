package com.lastmile.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class OrderContactResponseDto {

    private String proxyPhoneNumber;

    public String getProxyPhoneNumber() {
        return this.proxyPhoneNumber;
    }

    public void setProxyPhoneNumber(String proxyPhoneNumber) {
        this.proxyPhoneNumber = proxyPhoneNumber;
    }

    public OrderContactResponseDto() {
    }

    public OrderContactResponseDto(String proxyPhoneNumber) {
        this.proxyPhoneNumber = proxyPhoneNumber;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}