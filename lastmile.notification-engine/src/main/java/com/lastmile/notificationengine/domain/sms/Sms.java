package com.lastmile.notificationengine.domain.sms;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sms {

    @JsonProperty(value = "country_code", required = true)
    private String countryCode;

    @JsonProperty(value = "phone_number", required = true)
    private String phoneNumber;

    @JsonProperty(value = "from", required = false)
    private String from;

    @JsonProperty(value = "properties", required = false)
    private Map<String, Object> properties;

    public Sms() {

    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getFullNumber() {
        return countryCode + phoneNumber;
    }
}