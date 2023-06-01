package com.lastmile.driverservice.dto.orders;

import com.lastmile.utils.enums.orders.OrderAction;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class OrderActionRequestDto {
        
    private OrderAction orderAction;

    public OrderAction getOrderAction() {
        return this.orderAction;
    }

    public void setOrderAction(OrderAction orderAction) {
        this.orderAction = orderAction;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}