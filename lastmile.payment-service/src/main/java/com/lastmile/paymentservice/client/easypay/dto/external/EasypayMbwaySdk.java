package com.lastmile.paymentservice.client.easypay.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EasypayMbwaySdk {

    @JsonProperty("purchase_token")
    private String purchaseToken;

    @JsonProperty("initial_timestamp")
    private String initialTimestamp;

    @JsonProperty("merchant_operation_id")
    private String merchantOperationId;

    private String payload;

    public String getPurchaseToken() {
        return this.purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public String getInitialTimestamp() {
        return this.initialTimestamp;
    }

    public void setInitialTimestamp(String initialTimestamp) {
        this.initialTimestamp = initialTimestamp;
    }

    public String getMerchantOperationId() {
        return this.merchantOperationId;
    }

    public void setMerchantOperationId(String merchantOperationId) {
        this.merchantOperationId = merchantOperationId;
    }

    public String getPayload() {
        return this.payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}