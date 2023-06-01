package com.lastmile.driverservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "drivers")
public class Driver extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_identification")
    private String userIdentification;

    @Column(name = "driver_identification")
    private String driverIdentification;

    @Column(name = "fiscal_entity_identification")
    private String fiscalEntityIdentification;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "driver_rating")
    private Integer driverRating;

    @Column(name = "status")
    private String status;

    @Column(name = "entity_validated")
    private Boolean entityValidated;

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

    public String getDriverIdentification() {
        return this.driverIdentification;
    }

    public void setDriverIdentification(String driverIdentification) {
        this.driverIdentification = driverIdentification;
    }

    public String getFiscalEntityIdentification() {
        return this.fiscalEntityIdentification;
    }

    public void setFiscalEntityIdentification(String fiscalEntityIdentification) {
        this.fiscalEntityIdentification = fiscalEntityIdentification;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getDriverRating() {
        return this.driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean isEntityValidated() {
        return this.entityValidated;
    }

    public Boolean getEntityValidated() {
        return this.entityValidated;
    }

    public void setEntityValidated(Boolean entityValidated) {
        this.entityValidated = entityValidated;
    }

}