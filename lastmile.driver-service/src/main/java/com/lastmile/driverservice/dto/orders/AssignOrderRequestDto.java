package com.lastmile.driverservice.dto.orders;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AssignOrderRequestDto {

    private String orderIdentification;

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