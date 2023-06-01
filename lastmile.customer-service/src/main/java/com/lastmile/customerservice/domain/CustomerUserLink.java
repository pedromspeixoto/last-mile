package com.lastmile.customerservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "customers_users_link")
public class CustomerUserLink extends Auditable<String> {

    public CustomerUserLink() {
    }

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_identification")
    private String userIdentification;

    @Column(name = "customer_identification")
    private String customerIdentification;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserIdentification() {
        return this.userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public String getCustomerIdentification() {
        return this.customerIdentification;
    }

    public void setCustomerIdentification(String customerIdentification) {
        this.customerIdentification = customerIdentification;
    }

    public CustomerUserLink(String customerIdentification, String userIdentification) {
        this.userIdentification = userIdentification;
        this.customerIdentification = customerIdentification;
    }

}