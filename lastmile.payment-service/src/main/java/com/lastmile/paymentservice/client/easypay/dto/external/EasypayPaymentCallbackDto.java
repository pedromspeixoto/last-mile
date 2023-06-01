package com.lastmile.paymentservice.client.easypay.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lastmile.paymentservice.client.easypay.enums.EasypayCurrency;
import com.lastmile.paymentservice.client.easypay.enums.EasypayMethodUpper;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class EasypayPaymentCallbackDto {

    private String id;

    private String value;

    private EasypayCurrency currency;

    private String key;

    @JsonProperty("expiration_time")
    private String expirationTime;

    private EasypayMethodUpper method;

    private EasypayCustomerDto customer;

    private EasypayCaptureAccountIdDto account;

    private EasypayPaymentTransactionDto transaction;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EasypayCurrency getCurrency() {
        return this.currency;
    }

    public void setCurrency(EasypayCurrency currency) {
        this.currency = currency;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExpirationTime() {
        return this.expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public EasypayMethodUpper getMethod() {
        return this.method;
    }

    public void setMethod(EasypayMethodUpper method) {
        this.method = method;
    }

    public EasypayCustomerDto getCustomer() {
        return this.customer;
    }

    public void setCustomer(EasypayCustomerDto customer) {
        this.customer = customer;
    }

    public EasypayCaptureAccountIdDto getAccount() {
        return this.account;
    }

    public void setAccount(EasypayCaptureAccountIdDto account) {
        this.account = account;
    }

    public EasypayPaymentTransactionDto getTransaction() {
        return this.transaction;
    }

    public void setTransaction(EasypayPaymentTransactionDto transaction) {
        this.transaction = transaction;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}