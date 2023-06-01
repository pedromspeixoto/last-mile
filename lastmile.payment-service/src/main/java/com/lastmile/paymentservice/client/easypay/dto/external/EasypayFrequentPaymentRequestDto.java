package com.lastmile.paymentservice.client.easypay.dto.external;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.lastmile.paymentservice.client.easypay.enums.EasypayCurrency;
import com.lastmile.paymentservice.client.easypay.enums.EasypayMethod;
import com.lastmile.paymentservice.enums.PaymentDetailType;

public class EasypayFrequentPaymentRequestDto {

    @JsonProperty("expiration_time")
    private Date expirationTime;

    private EasypayCurrency currency;

    private EasypayCustomerDto customer;

    private String key;

    @JsonProperty("max_value")
    private Double maxValue;

    @JsonProperty("min_value")
    private Double minValue;

    @JsonProperty("unlimited_payments")
    private Boolean unlimitedPayments;

    private EasypayMethod method;

    @JsonProperty("sdd_mandate")
    private EasypaySddMandateDto sddMandate;

    public Date getExpirationTime() {
        return this.expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
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

    public Double getMaxValue() {
        return this.maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Double getMinValue() {
        return this.minValue;
    }

    public void setMinValue(Double minValue) {
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

    public EasypayFrequentPaymentRequestDto(String entityIdentification,
                                            String name,
                                            String email,
                                            String phoneNumber,
                                            String fiscalNumber,
                                            String paymentDetailIdentification,
                                            PaymentDetailType paymentDetailType) {
        
        // payment generic details
        this.currency = EasypayCurrency.EUR;
        this.key = paymentDetailIdentification;

        switch (paymentDetailType) {
            case CREDITCARD:
                this.method = EasypayMethod.cc;
                break;
            case MBWAY:
                this.method = EasypayMethod.mbw;
                break;
            case DEBIT:
                this.method = EasypayMethod.dd;
                break;
            default:
                this.method = EasypayMethod.cc;
        }
        
        // customer details
        this.customer = new EasypayCustomerDto();
        this.customer.setKey(entityIdentification);
        if (null != name && !name.isEmpty()) {
            this.customer.setName(name);
        }
        if (null != email && !email.isEmpty()) {
            this.customer.setEmail(email);
        }
        if (null != phoneNumber && !phoneNumber.isEmpty()) {

            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            PhoneNumber numberProto = new PhoneNumber();
            try {
                // phone must begin with '+'
                numberProto = phoneUtil.parse(phoneNumber, "");
            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString()); 
            }
      
            this.customer.setPhoneIndicative(String.valueOf(numberProto.getCountryCode()));
            this.customer.setPhone(String.valueOf(numberProto.getNationalNumber()));
        }
        if (null != fiscalNumber && !fiscalNumber.isEmpty()) {
            this.customer.setFiscalNumber(fiscalNumber);
        }

    }

    public EasypayFrequentPaymentRequestDto() {
    }

}