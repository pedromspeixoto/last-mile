package com.lastmile.driverservice.dto.documents;

import java.util.Date;

public class IdentificationCardUpsertRequestDto {

    private String name;

    private String surname;

    private Date birthDate;

    private String documentNumber;

    private Date documentExpiryDate;

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

    public String getDocumentNumber() {
        return this.documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Date getDocumentExpiryDate() {
        return this.documentExpiryDate;
    }

    public void setDocumentExpiryDate(Date documentExpiryDate) {
        this.documentExpiryDate = documentExpiryDate;
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