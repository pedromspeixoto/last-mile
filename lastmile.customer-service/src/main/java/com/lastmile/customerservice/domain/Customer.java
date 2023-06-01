package com.lastmile.customerservice.domain;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.persistence.*;
import javax.xml.bind.DatatypeConverter;

@Entity
@Table(name = "customers")
public class Customer extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_identification")
    private String customerIdentification;

    @Column(name = "name")
    private String name;

    @Column(name = "public_name")
    private String publicName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_phone_number")
    private String customerPhoneNumber;

    @Column(name = "customer_website")
    private String customerWebsite;

    @Column(name = "nif")
    private String nif;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "private_key")
    private String privateKey;

    @Column(name = "status")
    private String status;

    @Column(name = "active_address_id")
    private String activeAddressId;

    @Column(name = "active_billing_address_id")
    private String activeBillingAddressId;

    @Column(name = "active_payment_details_id")
    private String activePaymentDetailsId;

    @Column(name = "customer_callback_url")
    private String customerCallbackUrl;

    @Column(name = "entity_validated")
    private Boolean entityValidated;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerIdentification() {
        return this.customerIdentification;
    }

    public void setCustomerIdentification(String customerIdentification) {
        this.customerIdentification = customerIdentification;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicName() {
        return this.publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    public String getCustomerEmail() {
        return this.customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhoneNumber() {
        return this.customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerWebsite() {
        return this.customerWebsite;
    }

    public void setCustomerWebsite(String customerWebsite) {
        this.customerWebsite = customerWebsite;
    }

    public String getNif() {
        return this.nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        byte[] encoded = secretKey.getEncoded();
        this.apiKey = DatatypeConverter.printHexBinary(encoded).toLowerCase();
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        byte[] encoded = secretKey.getEncoded();
        this.privateKey = DatatypeConverter.printHexBinary(encoded).toLowerCase();
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActiveAddressId() {
        return this.activeAddressId;
    }

    public void setActiveAddressId(String activeAddressId) {
        this.activeAddressId = activeAddressId;
    }

    public String getActiveBillingAddressId() {
        return this.activeBillingAddressId;
    }

    public void setActiveBillingAddressId(String activeBillingAddressId) {
        this.activeBillingAddressId = activeBillingAddressId;
    }

    public String getActivePaymentDetailsId() {
        return this.activePaymentDetailsId;
    }

    public void setActivePaymentDetailsId(String activePaymentDetailsId) {
        this.activePaymentDetailsId = activePaymentDetailsId;
    }

    public String getCustomerCallbackUrl() {
        return this.customerCallbackUrl;
    }

    public void setCustomerCallbackUrl(String customerCallbackUrl) {
        this.customerCallbackUrl = customerCallbackUrl;
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

}