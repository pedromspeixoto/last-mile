package com.lastmile.paymentservice.client.easypay.dto.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EasypayOutPaymentResponseDto {

    private String status;

    private List<String> message;

    private String id;

    private EasypayOutPaymentMethodResponseDto method;

    @JsonProperty("out_account")
    private EasypayOutAccountResponseIdDto outAccount;

    private EasypayOutAccountResponseIdDto account;

    private EasypayOutAccountCustomerIdDto customer;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getMessage() {
        return this.message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EasypayOutPaymentMethodResponseDto getMethod() {
        return this.method;
    }

    public void setMethod(EasypayOutPaymentMethodResponseDto method) {
        this.method = method;
    }

    public EasypayOutAccountResponseIdDto getOutAccount() {
        return this.outAccount;
    }

    public void setOutAccount(EasypayOutAccountResponseIdDto outAccount) {
        this.outAccount = outAccount;
    }

    public EasypayOutAccountResponseIdDto getAccount() {
        return this.account;
    }

    public void setAccount(EasypayOutAccountResponseIdDto account) {
        this.account = account;
    }

    public EasypayOutAccountCustomerIdDto getCustomer() {
        return this.customer;
    }

    public void setCustomer(EasypayOutAccountCustomerIdDto customer) {
        this.customer = customer;
    }

}