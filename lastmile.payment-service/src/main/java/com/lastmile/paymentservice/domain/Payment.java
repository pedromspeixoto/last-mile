package com.lastmile.paymentservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "payments")
public class Payment extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_identification")
    private String paymentIdentification;

    @Column(name = "requester_entity_identification")
    private String requesterEntityIdentification;

    @Column(name = "requester_entity_type")
    private String requesterEntityType;

    @Column(name = "transaction_identification")
    private String transactionIdentification;

    @Column(name = "payment_details_id")
    private String paymentDetailsId;

    @Column(name = "payment_value")
    private Double paymentValue;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "external_payment_identification")
    private String externalPaymentIdentification;

    @Column(name = "status")
    private String status;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentIdentification() {
        return this.paymentIdentification;
    }

    public void setPaymentIdentification(String paymentIdentification) {
        this.paymentIdentification = paymentIdentification;
    }

    public String getRequesterEntityIdentification() {
        return this.requesterEntityIdentification;
    }

    public void setRequesterEntityIdentification(String requesterEntityIdentification) {
        this.requesterEntityIdentification = requesterEntityIdentification;
    }

    public String getRequesterEntityType() {
        return this.requesterEntityType;
    }

    public void setRequesterEntityType(String requesterEntityType) {
        this.requesterEntityType = requesterEntityType;
    }

    public String getTransactionIdentification() {
        return this.transactionIdentification;
    }

    public void setTransactionIdentification(String transactionIdentification) {
        this.transactionIdentification = transactionIdentification;
    }

    public String getPaymentDetailsId() {
        return this.paymentDetailsId;
    }

    public void setPaymentDetailsId(String paymentDetailsId) {
        this.paymentDetailsId = paymentDetailsId;
    }

    public Double getPaymentValue() {
        return this.paymentValue;
    }

    public void setPaymentValue(Double paymentValue) {
        this.paymentValue = paymentValue;
    }

    public String getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExternalPaymentIdentification() {
        return this.externalPaymentIdentification;
    }

    public void setExternalPaymentIdentification(String externalPaymentIdentification) {
        this.externalPaymentIdentification = externalPaymentIdentification;
    }

}
