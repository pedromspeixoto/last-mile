package com.lastmile.paymentservice.client.easypay.dto;

import java.text.ParseException;

import com.lastmile.paymentservice.client.easypay.dto.external.EasypayFrequentPaymentDetailsResponseDto;
import com.lastmile.paymentservice.enums.CardType;

public class CreditCardFrequentDetailsResponseDto {

    private String lastFourDigits;

    private CardType cardType;

    private String expirationDate;

    public String getLastFourDigits() {
        return this.lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public CardType getCardType() {
        return this.cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public CreditCardFrequentDetailsResponseDto() {
    }

    public CreditCardFrequentDetailsResponseDto(EasypayFrequentPaymentDetailsResponseDto easypayFrequentDetailsPaymentResponseDto)
            throws ParseException {
        this.cardType = CardType.valueOf(easypayFrequentDetailsPaymentResponseDto.getMethod().getCardType().toString().toUpperCase());
        this.lastFourDigits = easypayFrequentDetailsPaymentResponseDto.getMethod().getLastFourDigits();
        this.expirationDate = easypayFrequentDetailsPaymentResponseDto.getMethod().getExpirationDate();
    }

}