package com.lastmile.driverservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "document_vehicle_license_registrations")
public class DocumentVehicleLicenseRegistration extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_license_reg_identification")
    private String vehicleLicenseRegIdentification;

    @Column(name = "vehicle_identification")
    private String vehicleIdentification;

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

    public String getVehicleLicenseRegIdentification() {
        return this.vehicleLicenseRegIdentification;
    }

    public void setVehicleLicenseRegIdentification(String vehicleLicenseRegIdentification) {
        this.vehicleLicenseRegIdentification = vehicleLicenseRegIdentification;
    }

    public String getVehicleIdentification() {
        return this.vehicleIdentification;
    }

    public void setVehicleIdentification(String vehicleIdentification) {
        this.vehicleIdentification = vehicleIdentification;
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