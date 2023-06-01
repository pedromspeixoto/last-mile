package com.lastmile.paymentservice.client.easypay.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lastmile.paymentservice.client.easypay.enums.EasypayType;

public class EasypayPaymentTransactionDto {

    private String id;

    private String key;

    private EasypayType type;

    private String date;

    @JsonProperty("transfer_date")
    private String transferDate;

    @JsonProperty("document_number")
    private String documentNumber;

    private EasypayValuesDto values;

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

    public EasypayType getType() {
        return this.type;
    }

    public void setType(EasypayType type) {
        this.type = type;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTransferDate() {
        return this.transferDate;
    }

    public void setTransferDate(String transferDate) {
        this.transferDate = transferDate;
    }

    public String getDocumentNumber() {
        return this.documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public EasypayValuesDto getValues() {
        return this.values;
    }

    public void setValues(EasypayValuesDto values) {
        this.values = values;
    }

}