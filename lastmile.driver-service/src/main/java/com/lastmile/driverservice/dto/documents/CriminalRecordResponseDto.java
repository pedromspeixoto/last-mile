package com.lastmile.driverservice.dto.documents;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CriminalRecordResponseDto {

    private byte[] documentFront;

    private byte[] documentBack;

    private Boolean validated;

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

    public Boolean isValidated() {
        return this.validated;
    }

    public Boolean getValidated() {
        return this.validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}