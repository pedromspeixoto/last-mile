package com.lastmile.customerservice.dto.orders;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.utils.enums.orders.FeeType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class PricingRequestDto {

    @NotBlank(message = "Order is mandatory")
    private Integer order;

    @NotBlank(message = "Fee name is mandatory")
    private String feeName;

    @NotNull(message = "Fee type is mandatory")
    private FeeType feeType;

    @NotNull(message = "Fee value is mandatory")
    private Double feeValue;

    private String referenceColumn;

    private String referenceColumnValue;

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getFeeName() {
        return this.feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public FeeType getFeeType() {
        return this.feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public Double getFeeValue() {
        return this.feeValue;
    }

    public void setFeeValue(Double feeValue) {
        this.feeValue = feeValue;
    }

    public String getReferenceColumn() {
        return this.referenceColumn;
    }

    public void setReferenceColumn(String referenceColumn) {
        this.referenceColumn = referenceColumn;
    }

    public String getReferenceColumnValue() {
        return this.referenceColumnValue;
    }

    public void setReferenceColumnValue(String referenceColumnValue) {
        this.referenceColumnValue = referenceColumnValue;
    }

    public PricingRequestDto() {
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}