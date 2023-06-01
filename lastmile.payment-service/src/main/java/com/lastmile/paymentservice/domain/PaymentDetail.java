package com.lastmile.paymentservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "payment_details")
public class PaymentDetail extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_detail_identification")
    private String paymentDetailIdentification;

    @Column(name = "payment_detail_type")
    private String paymentDetailType;

    @Column(name = "entity_identification")
    private String entityIdentification;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "payment_email")
    private String paymentEmail;

    @Column(name = "payment_fiscal_number")
    private String paymentFiscalNumber;

    @Column(name = "payment_phone_number")
    private String paymentPhoneNumber;

    @Column(name = "card_last_four_digits")
    private String cardLastFourDigits;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "card_expiry_date")
    private String cardExpiryDate;

    @Column(name = "payment_token")
    private String paymentToken;

    @Column(name = "external_entity")
    private String externalEntity;

    @Column(name = "external_entity_identification")
    private String externalEntityIdentification;

    @Column(name = "status")
    private String status;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentDetailIdentification() {
        return this.paymentDetailIdentification;
    }

    public void setPaymentDetailIdentification(String paymentDetailIdentification) {
        this.paymentDetailIdentification = paymentDetailIdentification;
    }

    public String getPaymentDetailType() {
        return this.paymentDetailType;
    }

    public void setPaymentDetailType(String paymentDetailType) {
        this.paymentDetailType = paymentDetailType;
    }

    public String getEntityIdentification() {
        return this.entityIdentification;
    }

    public void setEntityIdentification(String entityIdentification) {
        this.entityIdentification = entityIdentification;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getPaymentPhoneNumber() {
        return this.paymentPhoneNumber;
    }

    public void setPaymentPhoneNumber(String paymentPhoneNumber) {
        this.paymentPhoneNumber = paymentPhoneNumber;
    }

    public String getCardLastFourDigits() {
        return this.cardLastFourDigits;
    }

    public void setCardLastFourDigits(String cardLastFourDigits) {
        this.cardLastFourDigits = cardLastFourDigits;
    }

    public String getCardType() {
        return this.cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardExpiryDate() {
        return this.cardExpiryDate;
    }

    public void setCardExpiryDate(String cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }

    public String getPaymentToken() {
        return this.paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }

    public String getExternalEntity() {
        return this.externalEntity;
    }

    public void setExternalEntity(String externalEntity) {
        this.externalEntity = externalEntity;
    }

    public String getExternalEntityIdentification() {
        return this.externalEntityIdentification;
    }

    public void setExternalEntityIdentification(String externalEntityIdentification) {
        this.externalEntityIdentification = externalEntityIdentification;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentEmail() {
        return this.paymentEmail;
    }

    public void setPaymentEmail(String paymentEmail) {
        this.paymentEmail = paymentEmail;
    }

    public String getPaymentFiscalNumber() {
        return this.paymentFiscalNumber;
    }

    public void setPaymentFiscalNumber(String paymentFiscalNumber) {
        this.paymentFiscalNumber = paymentFiscalNumber;
    }

}
