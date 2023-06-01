package com.lastmile.customerservice.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.customerservice.dto.addresses.CreateAddressRequestDto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class CreateWarehouseRequestDto {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;

    private CreateAddressRequestDto address;

    @NotBlank(message = "Latitude is mandatory")
    private Double latitude;

    @NotBlank(message = "Longitude is mandatory")
    private Double longitude;

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

    public CreateAddressRequestDto getAddress() {
        return this.address;
    }

    public void setAddress(CreateAddressRequestDto address) {
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

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}