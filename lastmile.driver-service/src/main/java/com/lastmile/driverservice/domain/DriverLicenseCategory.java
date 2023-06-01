package com.lastmile.driverservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "driver_license_categories")
public class DriverLicenseCategory extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_license_category_identification")
    private String driverLicenseCategoryIdentification;

    @Column(name = "driver_license_identification")
    private String driverLicenseIdentification;

    @Column(name = "category")
    private String category;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDriverLicenseCategoryIdentification() {
        return this.driverLicenseCategoryIdentification;
    }

    public void setDriverLicenseCategoryIdentification(String driverLicenseCategoryIdentification) {
        this.driverLicenseCategoryIdentification = driverLicenseCategoryIdentification;
    }

    public String getDriverLicenseIdentification() {
        return this.driverLicenseIdentification;
    }

    public void setDriverLicenseIdentification(String driverLicenseIdentification) {
        this.driverLicenseIdentification = driverLicenseIdentification;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}