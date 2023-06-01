package com.lastmile.driverservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "document_criminal_records")
public class DocumentCriminalRecord extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "criminal_record_identification")
    private String criminalRecordIdentification;

    @Column(name = "driver_identification")
    private String driverIdentification;

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

    public String getCriminalRecordIdentification() {
        return this.criminalRecordIdentification;
    }

    public void setCriminalRecordIdentification(String criminalRecordIdentification) {
        this.criminalRecordIdentification = criminalRecordIdentification;
    }

    public String getDriverIdentification() {
        return this.driverIdentification;
    }

    public void setDriverIdentification(String driverIdentification) {
        this.driverIdentification = driverIdentification;
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