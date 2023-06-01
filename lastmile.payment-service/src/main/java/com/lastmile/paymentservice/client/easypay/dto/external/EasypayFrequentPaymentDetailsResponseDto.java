package com.lastmile.paymentservice.client.easypay.dto.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lastmile.paymentservice.client.easypay.enums.EasypayCurrency;

public class EasypayFrequentPaymentDetailsResponseDto {

    private List<EasypayAuthorisationsDto> authorisations;

    private List<EasypayTransactionsDto> transations;

    private String id;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("expiration_time")
    private String expirationTime;

    private EasypayCurrency currency;

    private EasypayCustomerDto customer;

    private String key;

    @JsonProperty("max_value")
    private String maxValue;

    @JsonProperty("min_value")
    private String minValue;

    @JsonProperty("unlimited_payments")
    private Boolean unlimitedPayments;

    private EasypayMethodDto method;

    public List<EasypayAuthorisationsDto> getAuthorisations() {
        return this.authorisations;
    }

    public void setAuthorisations(List<EasypayAuthorisationsDto> authorisations) {
        this.authorisations = authorisations;
    }

    public List<EasypayTransactionsDto> getTransations() {
        return this.transations;
    }

    public void setTransations(List<EasypayTransactionsDto> transations) {
        this.transations = transations;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getExpirationTime() {
        return this.expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public EasypayCurrency getCurrency() {
        return this.currency;
    }

    public void setCurrency(EasypayCurrency currency) {
        this.currency = currency;
    }

    public EasypayCustomerDto getCustomer() {
        return this.customer;
    }

    public void setCustomer(EasypayCustomerDto customer) {
        this.customer = customer;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMaxValue() {
        return this.maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getMinValue() {
        return this.minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
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

    public EasypayMethodDto getMethod() {
        return this.method;
    }

    public void setMethod(EasypayMethodDto method) {
        this.method = method;
    }

}