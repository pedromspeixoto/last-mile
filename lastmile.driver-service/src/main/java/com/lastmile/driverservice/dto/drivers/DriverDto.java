package com.lastmile.driverservice.dto.drivers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.driverservice.dto.accounts.GetAccountDto;
import com.lastmile.driverservice.dto.documents.CriminalRecordResponseDto;
import com.lastmile.driverservice.dto.documents.DriverLicenseResponseDto;
import com.lastmile.driverservice.dto.documents.IdentificationCardResponseDto;
import com.lastmile.driverservice.dto.fiscalentities.FiscalEntityResponseDto;
import com.lastmile.driverservice.dto.vehicles.DriverVehicleResponseDto;
import com.lastmile.driverservice.enums.DriverStatus;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_EMPTY)
public class DriverDto {

    private String userIdentification;

    private GetAccountDto profile;

    private String driverIdentification;

    private FiscalEntityResponseDto fiscalEntity;

    private Double latitude;

    private Double longitude;

    private DriverLicenseResponseDto driverLicense;

    private CriminalRecordResponseDto documentCriminalRecord;

    private IdentificationCardResponseDto documentIdentificationCard;

    private DriverVehicleResponseDto activeVehicle;

    private DriverStatus status;

    private Integer driverRating;

    private Boolean entityValidated;

    public String getUserIdentification() {
        return this.userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public GetAccountDto getProfile() {
        return this.profile;
    }

    public void setProfile(GetAccountDto profile) {
        this.profile = profile;
    }

    public String getDriverIdentification() {
        return this.driverIdentification;
    }

    public void setDriverIdentification(String driverIdentification) {
        this.driverIdentification = driverIdentification;
    }

    public FiscalEntityResponseDto getFiscalEntity() {
        return this.fiscalEntity;
    }

    public void setFiscalEntity(FiscalEntityResponseDto fiscalEntity) {
        this.fiscalEntity = fiscalEntity;
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

    public DriverLicenseResponseDto getDriverLicense() {
        return this.driverLicense;
    }

    public void setDriverLicense(DriverLicenseResponseDto driverLicense) {
        this.driverLicense = driverLicense;
    }

    public CriminalRecordResponseDto getDocumentCriminalRecord() {
        return this.documentCriminalRecord;
    }

    public void setDocumentCriminalRecord(CriminalRecordResponseDto documentCriminalRecord) {
        this.documentCriminalRecord = documentCriminalRecord;
    }

    public IdentificationCardResponseDto getDocumentIdentificationCard() {
        return this.documentIdentificationCard;
    }

    public void setDocumentIdentificationCard(IdentificationCardResponseDto documentIdentificationCard) {
        this.documentIdentificationCard = documentIdentificationCard;
    }

    public DriverVehicleResponseDto getActiveVehicle() {
        return this.activeVehicle;
    }

    public void setActiveVehicle(DriverVehicleResponseDto activeVehicle) {
        this.activeVehicle = activeVehicle;
    }

    public DriverStatus getStatus() {
        return this.status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public Integer getDriverRating() {
        return this.driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
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

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}