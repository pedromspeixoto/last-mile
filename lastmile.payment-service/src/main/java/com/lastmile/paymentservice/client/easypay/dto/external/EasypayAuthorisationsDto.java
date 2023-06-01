package com.lastmile.paymentservice.client.easypay.dto.external;

import com.lastmile.paymentservice.client.easypay.enums.EasypayAuthorisationStatus;

public class EasypayAuthorisationsDto {

    private String id;

    private String key;

    private EasypayAuthorisationStatus status;

    private Double value;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public EasypayAuthorisationStatus getStatus() {
        return this.status;
    }

    public void setStatus(EasypayAuthorisationStatus status) {
        this.status = status;
    }

    public Double getValue() {
        return this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}