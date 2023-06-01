package com.lastmile.orderengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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

}