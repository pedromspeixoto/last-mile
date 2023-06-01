package com.lastmile.driverservice.dto.documents;

import java.util.Date;

public class DriverLicenseUpsertRequestDto {

    private String name;

    private String surname;

    private Date birthDate;

    private Date issueDate;

    private Date expiryDate;

    private String issuingAuthority;

    private String personalNumber;

    private String licenseNumber;

    private String licenseAddress;

    private Boolean validated;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getIssueDate() {
        return this.issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getIssuingAuthority() {
        return this.issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getPersonalNumber() {
        return this.personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getLicenseNumber() {
        return this.licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseAddress() {
        return this.licenseAddress;
    }

    public void setLicenseAddress(String licenseAddress) {
        this.licenseAddress = licenseAddress;
    }

    public Boolean isValidated() {
        return this.validated;
    }

    public Boolean getValidated() {
        return this.validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

}