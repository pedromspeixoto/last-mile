package com.lastmile.orderservice.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class OrderShortHistoryResponseDto {

    private Date pickedDate;

    private Date deliveredDate;

    public Date getPickedDate() {
        return this.pickedDate;
    }

    public void setPickedDate(Date pickedDate) {
        this.pickedDate = pickedDate;
    }

    public Date getDeliveredDate() {
        return this.deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public OrderShortHistoryResponseDto() {
    }

    public OrderShortHistoryResponseDto(Date pickedDate, Date deliveredDate) {
        this.pickedDate = pickedDate;
        this.deliveredDate = deliveredDate;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}