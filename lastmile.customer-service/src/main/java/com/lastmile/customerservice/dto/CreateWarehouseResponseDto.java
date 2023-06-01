package com.lastmile.customerservice.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CreateWarehouseResponseDto {

    private String warehouseIdentification;

    public CreateWarehouseResponseDto() {
    }

    public CreateWarehouseResponseDto(String warehouseIdentification) {
        this.warehouseIdentification = warehouseIdentification;
    }

    public String getWarehouseIdentification() {
        return this.warehouseIdentification;
    }

    public void setWarehouseIdentification(String warehouseIdentification) {
        this.warehouseIdentification = warehouseIdentification;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}