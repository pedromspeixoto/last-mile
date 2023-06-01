package com.lastmile.driverservice.dto.documents;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class IdentificationCardResponseDto {

    private String name;

    private String surname;

    private Date birthDate;

    private Date documentNumber;

    private Date documentExpiryDate;

    private byte[] documentFront;

    private byte[] documentBack;
    
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

    public Date getDocumentNumber() {
        return this.documentNumber;
    }

    public void setDocumentNumber(Date documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Date getDocumentExpiryDate() {
        return this.documentExpiryDate;
    }

    public void setDocumentExpiryDate(Date documentExpiryDate) {
        this.documentExpiryDate = documentExpiryDate;
    }

    public byte[] getDocumentFront() {
        return this.documentFront;
    }

    public void setDocumentFront(byte[] documentFront) {
        this.documentFront = documentFront;
    }

    public byte[] getDocumentBack() {
        return this.documentBack;
    }

    public void setDocumentBack(byte[] documentBack) {
        this.documentBack = documentBack;
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