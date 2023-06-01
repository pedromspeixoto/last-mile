package com.lastmile.accountservice.domain;

import javax.persistence.*;

@Entity
@IdClass(AccountPropertiesID.class)
@Table(name = "account_properties")
public class AccountProperties {

    @Id
    @Column(name = "environment")
    private String environment;

    @Id
    @Column(name = "property")
    private String property;

    @Id
    @Column(name = "value")
    private String value;

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
