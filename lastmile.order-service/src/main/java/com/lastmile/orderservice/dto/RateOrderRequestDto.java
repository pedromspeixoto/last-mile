package com.lastmile.orderservice.dto;

import com.lastmile.utils.validations.ValidRating;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class RateOrderRequestDto {

    @ValidRating
    private Integer rating;

    public Integer getRating() {
        return this.rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}