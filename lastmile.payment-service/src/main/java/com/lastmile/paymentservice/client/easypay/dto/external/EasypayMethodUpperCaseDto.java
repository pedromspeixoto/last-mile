package com.lastmile.paymentservice.client.easypay.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lastmile.paymentservice.client.easypay.enums.EasypayMethodStatus;
import com.lastmile.paymentservice.client.easypay.enums.EasypayMethodUpper;

public class EasypayMethodUpperCaseDto {

    @JsonProperty("type")
    private EasypayMethodUpper methodType;

    @JsonProperty("sdd_mandate")
    private EasypaySddMandateDto sddMandate;

    private String entity;

    private String reference;

    @JsonProperty("url")
    private String easypayGatewayUrl;

    @JsonProperty("last_four")
    private String lastFourDigits;

    @JsonProperty("card_type")
    private String cardType;

    @JsonProperty("expiration_date")
    private String expirationDate;

    private EasypayMethodStatus status;

    private String alias;

    public EasypayMethodUpper getMethodType() {
        return this.methodType;
    }

    public void setMethodType(EasypayMethodUpper methodType) {
        this.methodType = methodType;
    }

    public EasypaySddMandateDto getSddMandate() {
        return this.sddMandate;
    }

    public void setSddMandate(EasypaySddMandateDto sddMandate) {
        this.sddMandate = sddMandate;
    }

    public String getEntity() {
        return this.entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getEasypayGatewayUrl() {
        return this.easypayGatewayUrl;
    }

    public void setEasypayGatewayUrl(String easypayGatewayUrl) {
        this.easypayGatewayUrl = easypayGatewayUrl;
    }

    public String getLastFourDigits() {
        return this.lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public String getCardType() {
        return this.cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public EasypayMethodStatus getStatus() {
        return this.status;
    }

    public void setStatus(EasypayMethodStatus status) {
        this.status = status;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}