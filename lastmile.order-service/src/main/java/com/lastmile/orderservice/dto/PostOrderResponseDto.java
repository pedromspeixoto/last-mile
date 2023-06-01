package com.lastmile.orderservice.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class PostOrderResponseDto {

    private String orderIdentification;

    public PostOrderResponseDto() {
    }

    public PostOrderResponseDto(String orderIdentification) {
        this.orderIdentification = orderIdentification;
    }

    public String getOrderIdentification() {
        return this.orderIdentification;
    }

    public void setOrderIdentification(String orderIdentification) {
        this.orderIdentification = orderIdentification;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}