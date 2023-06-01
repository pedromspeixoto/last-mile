package com.lastmile.orderservice.domain;

import javax.persistence.*;

@Entity
@IdClass(OrderPropertiesID.class)
@Table(name = "order_properties")
public class OrderProperties {

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
