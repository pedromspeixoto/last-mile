package com.lastmile.paymentservice.client.easypay.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EasypayCaptureSplitDto {

    @JsonProperty("split_key")
    private String splitKey;

    @JsonProperty("split_descriptive")
    private String splitDescriptive;

    private Double value;

    private EasypayCaptureAccountIdDto account;

    @JsonProperty("margin_value")
    private Double marginVaDouble;

    @JsonProperty("margin_account")
    private EasypayCaptureAccountIdDto marginAccount;

    public String getSplitKey() {
        return this.splitKey;
    }

    public void setSplitKey(String splitKey) {
        this.splitKey = splitKey;
    }

    public String getSplitDescriptive() {
        return this.splitDescriptive;
    }

    public void setSplitDescriptive(String splitDescriptive) {
        this.splitDescriptive = splitDescriptive;
    }

    public Double getValue() {
        return this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public EasypayCaptureAccountIdDto getAccount() {
        return this.account;
    }

    public void setAccount(EasypayCaptureAccountIdDto account) {
        this.account = account;
    }

    public Double getMarginVaDouble() {
        return this.marginVaDouble;
    }

    public void setMarginVaDouble(Double marginVaDouble) {
        this.marginVaDouble = marginVaDouble;
    }

    public EasypayCaptureAccountIdDto getMarginAccount() {
        return this.marginAccount;
    }

    public void setMarginAccount(EasypayCaptureAccountIdDto marginAccount) {
        this.marginAccount = marginAccount;
    }

}