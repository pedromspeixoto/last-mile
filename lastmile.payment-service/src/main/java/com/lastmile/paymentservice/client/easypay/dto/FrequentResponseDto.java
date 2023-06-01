package com.lastmile.paymentservice.client.easypay.dto;

import com.lastmile.paymentservice.client.easypay.dto.external.EasypayFrequentPaymentResponseDto;

public class FrequentResponseDto {

    private String externalPaymentIdentification;

    private String paymentGatewayUrl;

    public String getExternalPaymentIdentification() {
        return this.externalPaymentIdentification;
    }

    public void setExternalPaymentIdentification(String externalPaymentIdentification) {
        this.externalPaymentIdentification = externalPaymentIdentification;
    }

    public String getPaymentGatewayUrl() {
        return this.paymentGatewayUrl;
    }

    public void setPaymentGatewayUrl(String paymentGatewayUrl) {
        this.paymentGatewayUrl = paymentGatewayUrl;
    }

    public FrequentResponseDto() {
    }

    public FrequentResponseDto(EasypayFrequentPaymentResponseDto easypayFrequentPaymentResponseDto) {
        this.externalPaymentIdentification = easypayFrequentPaymentResponseDto.getId();
        this.paymentGatewayUrl = easypayFrequentPaymentResponseDto.getMethod().getEasypayGatewayUrl();
    }

}