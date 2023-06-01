package com.lastmile.paymentservice.client.easypay.dto.external;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.lastmile.paymentservice.client.easypay.enums.EasypayOutPaymentMethod;
import com.lastmile.paymentservice.client.easypay.enums.EasypayOutPaymentType;
import com.lastmile.paymentservice.enums.OutPaymentType;
import com.lastmile.utils.validations.Validator;

public class EasypayOutPaymentRequestDto {

    private EasypayCustomerDto customer;

    private String key;

    private Double value;

    private EasypayOutPaymentType type;

    @JsonProperty("schedule_at")
    private String scheduleDate;

    private EasypayCaptureAccountIdDto account;

    @JsonProperty("out_account")
    private EasypayOutAccountDto outAccount;

    private EasypayOutPaymentMethod method;

    private String timestamp;

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

    public Double getValue() {
        return this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public EasypayOutPaymentType getType() {
        return this.type;
    }

    public void setType(EasypayOutPaymentType type) {
        this.type = type;
    }

    public String getScheduleDate() {
        return this.scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public EasypayCaptureAccountIdDto getAccount() {
        return this.account;
    }

    public void setAccount(EasypayCaptureAccountIdDto account) {
        this.account = account;
    }

    public EasypayOutAccountDto getOutAccount() {
        return this.outAccount;
    }

    public void setOutAccount(EasypayOutAccountDto outAccount) {
        this.outAccount = outAccount;
    }

    public EasypayOutPaymentMethod getMethod() {
        return this.method;
    }

    public void setMethod(EasypayOutPaymentMethod method) {
        this.method = method;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public EasypayOutPaymentRequestDto(String outPaymentIdentification,
                                       String sourceAccountIdentification,
                                       String sourceAccountExternalIdentification,
                                       String sourceAccountHolderName,
                                       String sourceAccountEmail,
                                       String sourceAccountPhoneNumber,
                                       String sourceAccountFiscalNumber,
                                       String targetAccountIdentification,
                                       String targetAccountHolderName,
                                       String targetAccountEmail,
                                       String targetAccountPhoneNumber,
                                       String targetAccountIban,
                                       String countryCode,
                                       Date scheduledDate,
                                       OutPaymentType method,
                                       Double value) {
        // customer
        this.customer = new EasypayCustomerDto();
        //this.customer.setId(sourceAccountExternalIdentification);
        this.customer.setKey(sourceAccountIdentification);
        this.customer.setName(sourceAccountHolderName);
        this.customer.setEmail(sourceAccountEmail);
        if (null != sourceAccountPhoneNumber && !sourceAccountPhoneNumber.isEmpty()) {

            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            PhoneNumber numberProto = new PhoneNumber();
            try {
                // phone must begin with '+'
                numberProto = phoneUtil.parse(sourceAccountPhoneNumber, "");
            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString()); 
            }
      
            this.customer.setPhoneIndicative(String.valueOf(numberProto.getCountryCode()));
            this.customer.setPhone(String.valueOf(numberProto.getNationalNumber()));
        }
        this.customer.setFiscalNumber(sourceAccountFiscalNumber);
        this.customer.setLanguage(countryCode);
        // type
        this.type = EasypayOutPaymentType.normal;
        // out account
        this.outAccount = new EasypayOutAccountDto();
        this.outAccount.setKey(targetAccountIdentification);
        this.outAccount.setAccountHolder(targetAccountHolderName);
        this.outAccount.setEmail(targetAccountEmail);
        if (null != targetAccountPhoneNumber && !targetAccountPhoneNumber.isEmpty()) {

            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            PhoneNumber numberProto = new PhoneNumber();
            try {
                // phone must begin with '+'
                numberProto = phoneUtil.parse(targetAccountPhoneNumber, "");
            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString()); 
            }
            this.outAccount.setPhone(String.valueOf(numberProto.getNationalNumber()));
        }
        this.outAccount.setIban(targetAccountIban);
        this.outAccount.setCountryCode(countryCode);
        // out payment method
        switch (method) {
            case TRANSFER:
            default:
                this.method = EasypayOutPaymentMethod.transfer;
                break;
        }
        // key
        this.key = outPaymentIdentification;
        // value
        this.value = value;
        // scheduled date
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (scheduledDate != null && !scheduledDate.toString().isEmpty() && Validator.isDateInTheFuture(scheduledDate)) {
            this.scheduleDate = dateFormatter.format(scheduledDate);
        } else {
            this.scheduleDate = null;
        }
        // timestamp
        this.timestamp = String.valueOf(Instant.now().getEpochSecond());
        System.out.println(this.timestamp);

    }

    public EasypayOutPaymentRequestDto() {
    }

}