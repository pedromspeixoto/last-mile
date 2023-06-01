package com.lastmile.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class RabbitOrderEvent {

    @JsonProperty("order")
    private RabbitOrderModel order;

    @JsonProperty("order")
    public RabbitOrderModel getOrder() {
        return order;
    }

    @JsonProperty("order")
    public void setOrder(RabbitOrderModel order) {
        this.order = order;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}