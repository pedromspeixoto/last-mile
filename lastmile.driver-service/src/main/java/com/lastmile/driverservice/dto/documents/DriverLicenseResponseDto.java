package com.lastmile.driverservice.dto.documents;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DriverLicenseResponseDto {

    private String name;

    private String surname;

    private Date birthDate;

    private Date issueDate;

    private Date expiryDate;

    private String issuingAuthority;

    private String personalNumber;

    private String licenseNumber;

    private String licenseAddress;

    private byte[] documentFrontURL;

    private byte[] documentBackURL;

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

    public byte[] getDocumentFront() {
        return this.documentFrontURL;
    }

    public void setDocumentFront(byte[] documentFrontURL) {
        this.documentFrontURL = documentFrontURL;
    }

    public byte[] getDocumentBack() {
        return this.documentBackURL;
    }

    public void setDocumentBack(byte[] documentBackURL) {
        this.documentBackURL = documentBackURL;
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

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}