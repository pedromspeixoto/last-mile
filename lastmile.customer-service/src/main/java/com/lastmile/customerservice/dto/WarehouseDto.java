package com.lastmile.customerservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.customerservice.dto.addresses.GetAddressResponseDto;
import com.lastmile.customerservice.enums.WarehouseStatus;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class WarehouseDto {

    private String name;

    private String description;

    private GetAddressResponseDto address;

    private Double latitude;

    private Double longitude;

    private WarehouseStatus status;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GetAddressResponseDto getAddress() {
        return this.address;
    }

    public void setAddress(GetAddressResponseDto address) {
        this.address = address;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public WarehouseStatus getStatus() {
        return this.status;
    }

    public void setStatus(WarehouseStatus status) {
        this.status = status;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}