package com.lastmile.driverservice.domain;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name = "document_identification_cards")
public class DocumentIdentificationCard extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identification_card_identification")
    private String identificationCardIdentification;

    @Column(name = "driver_identification")
    private String driverIdentification;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "document_expiry_date")
    private Date documentExpiryDate;

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

    public String getIdentificationCardIdentification() {
        return this.identificationCardIdentification;
    }

    public void setIdentificationCardIdentification(String identificationCardIdentification) {
        this.identificationCardIdentification = identificationCardIdentification;
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