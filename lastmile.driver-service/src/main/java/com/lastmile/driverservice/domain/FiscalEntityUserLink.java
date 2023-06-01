package com.lastmile.driverservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "fiscal_entities_users_link")
public class FiscalEntityUserLink extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_identification")
    private String userIdentification;

    @Column(name = "fiscal_entity_identification")
    private String fiscalEntityIdentification;

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

    public String getFiscalEntityIdentification() {
        return this.fiscalEntityIdentification;
    }

    public void setFiscalEntityIdentification(String fiscalEntityIdentification) {
        this.fiscalEntityIdentification = fiscalEntityIdentification;
    }

    public FiscalEntityUserLink() {
    }

    public FiscalEntityUserLink(String fiscalEntityIdentification, String userIdentification) {
        this.userIdentification = userIdentification;
        this.fiscalEntityIdentification = fiscalEntityIdentification;
    }

}