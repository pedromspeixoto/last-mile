package com.lastmile.driverservice.domain;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name = "document_driver_licenses")
public class DocumentDriverLicense extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_license_identification")
    private String driverLicenseIdentification;

    @Column(name = "driver_identification")
    private String driverIdentification;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "issue_date")
    private Date issueDate;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "issuing_authority")
    private String issuingAuthority;

    @Column(name = "personal_number")
    private String personalNumber;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "license_address")
    private String licenseAddress;

    @Column(name = "document_front_file_id")
    private String documentFrontFileId;

    @Column(name = "document_back_file_id")
    private String documentBackFileId;

    @Column(name = "validated")
    private boolean validated;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDriverLicenseIdentification() {
        return this.driverLicenseIdentification;
    }

    public void setDriverLicenseIdentification(String driverLicenseIdentification) {
        this.driverLicenseIdentification = driverLicenseIdentification;
    }

    public String getDriverIdentification() {
        return this.driverIdentification;
    }

    public void setDriverIdentification(String driverIdentification) {
        this.driverIdentification = driverIdentification;
    }

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

    public String getDocumentFrontFileId() {
        return this.documentFrontFileId;
    }

    public void setDocumentFrontFileId(String documentFrontFileId) {
        this.documentFrontFileId = documentFrontFileId;
    }

    public String getDocumentBackFileId() {
        return this.documentBackFileId;
    }

    public void setDocumentBackFileId(String documentBackFileId) {
        this.documentBackFileId = documentBackFileId;
    }

    public boolean isValidated() {
        return this.validated;
    }

    public boolean getValidated() {
        return this.validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

}