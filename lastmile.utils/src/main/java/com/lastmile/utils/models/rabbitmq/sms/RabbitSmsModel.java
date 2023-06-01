package com.lastmile.utils.models.rabbitmq.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class RabbitSmsModel {

    @JsonProperty("properties")
    Map<String, Object> properties;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("phone_number")
    private String phoneNumber;

    public RabbitSmsModel() {
        super();
    }

    @JsonProperty("country_code")
    public String getCountryCode() {
        return countryCode;
    }

    @JsonProperty("country_code")
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @JsonProperty("phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty("phone_number")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("properties")
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonProperty("properties")
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}