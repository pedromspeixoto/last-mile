package com.lastmile.paymentservice.client.easypay.dto.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lastmile.paymentservice.client.easypay.enums.EasypayMethod;

public class EasypayCaptureRequestDto {

    @JsonProperty("transaction_key")
    private String transactionKey;

    @JsonProperty("capture_date")
    private String captureDate;

    private EasypayCaptureAccountIdDto account;

    private List<EasypayCaptureSplitDto> splits;

    @JsonProperty("mbway_sdk")
    private EasypayMbwaySdk mbwaySdk;

    private String descriptive;

    private Double value;

    @JsonProperty("unlimited_payments")
    private Boolean unlimitedPayments;

    private EasypayMethod method;

    @JsonProperty("sdd_mandate")
    private EasypaySddMandateDto sddMandate;

    public String getTransactionKey() {
        return this.transactionKey;
    }

    public void setTransactionKey(String transactionKey) {
        this.transactionKey = transactionKey;
    }

    public String getCaptureDate() {
        return this.captureDate;
    }

    public void setCaptureDate(String captureDate) {
        this.captureDate = captureDate;
    }

    public EasypayCaptureAccountIdDto getAccount() {
        return this.account;
    }

    public void setAccount(EasypayCaptureAccountIdDto account) {
        this.account = account;
    }

    public List<EasypayCaptureSplitDto> getSplits() {
        return this.splits;
    }

    public void setSplits(List<EasypayCaptureSplitDto> splits) {
        this.splits = splits;
    }

    public EasypayMbwaySdk getMbwaySdk() {
        return this.mbwaySdk;
    }

    public void setMbwaySdk(EasypayMbwaySdk mbwaySdk) {
        this.mbwaySdk = mbwaySdk;
    }

    public String getDescriptive() {
        return this.descriptive;
    }

    public void setDescriptive(String descriptive) {
        this.descriptive = descriptive;
    }

    public Double getValue() {
        return this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Boolean isUnlimitedPayments() {
        return this.unlimitedPayments;
    }

    public Boolean getUnlimitedPayments() {
        return this.unlimitedPayments;
    }

    public void setUnlimitedPayments(Boolean unlimitedPayments) {
        this.unlimitedPayments = unlimitedPayments;
    }

    public EasypayMethod getMethod() {
        return this.method;
    }

    public void setMethod(EasypayMethod method) {
        this.method = method;
    }

    public EasypaySddMandateDto getSddMandate() {
        return this.sddMandate;
    }

    public void setSddMandate(EasypaySddMandateDto sddMandate) {
        this.sddMandate = sddMandate;
    }

    public EasypayCaptureRequestDto(String paymentIdentification, String descriptive, Double transactionValue) {
        this.descriptive = descriptive;
        this.transactionKey = paymentIdentification;
        this.value = transactionValue;
    }

    public EasypayCaptureRequestDto() {
    }

}