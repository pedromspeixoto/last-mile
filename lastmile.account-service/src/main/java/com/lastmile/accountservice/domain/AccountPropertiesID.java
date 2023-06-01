package com.lastmile.accountservice.domain;

import java.io.Serializable;

public class AccountPropertiesID implements Serializable {

    private static final long serialVersionUID = 1L;

    private String environment;

    private String property;

    private String value;

    public AccountPropertiesID() {
    }

    public AccountPropertiesID(String environment, String property, String value) {
        this.environment = environment;
        this.property = property;
        this.value = value;
    }


    public String getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}