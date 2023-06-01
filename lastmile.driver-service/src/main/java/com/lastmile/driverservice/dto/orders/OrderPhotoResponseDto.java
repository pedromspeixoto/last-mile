package com.lastmile.driverservice.dto.orders;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class OrderPhotoResponseDto {

    private byte[] photo;

    public OrderPhotoResponseDto() {
    }

    public OrderPhotoResponseDto(byte[] photo) {
        this.photo = photo;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public OrderPhotoResponseDto photo(byte[] photo) {
        this.photo = photo;
        return this;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}