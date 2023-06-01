package com.lastmile.paymentservice.client.easypay.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EasypayValuesDto {

    private Double requested;

    private Double paid;
    
    @JsonProperty("fixed_fee")
    private Double fixedFee;

    @JsonProperty("variable_fee")
    private Double variableFee;

    private Double tax;

    private Double transfer;

    public Double getRequested() {
        return this.requested;
    }

    public void setRequested(Double requested) {
        this.requested = requested;
    }

    public Double getPaid() {
        return this.paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    public Double getFixedFee() {
        return this.fixedFee;
    }

    public void setFixedFee(Double fixedFee) {
        this.fixedFee = fixedFee;
    }

    public Double getVariableFee() {
        return this.variableFee;
    }

    public void setVariableFee(Double variableFee) {
        this.variableFee = variableFee;
    }

    public Double getTax() {
        return this.tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getTransfer() {
        return this.transfer;
    }

    public void setTransfer(Double transfer) {
        this.transfer = transfer;
    }

}