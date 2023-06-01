package com.lastmile.orderservice.client.twilio.domain;

import javax.persistence.*;

@Entity
@Table(name = "twilio_phone_numbers")
public class TwilioPhoneNumber {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "twilio_phone_number_sid")
    private String twilioPhoneNumberSid;

    @Column(name = "twilio_phone_number")
    private String twilioPhoneNumber;

    @Column(name = "in_use")
    private Boolean inUse;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTwilioPhoneNumberSid() {
        return this.twilioPhoneNumberSid;
    }

    public void setTwilioPhoneNumberSid(String twilioPhoneNumberSid) {
        this.twilioPhoneNumberSid = twilioPhoneNumberSid;
    }

    public String getTwilioPhoneNumber() {
        return this.twilioPhoneNumber;
    }

    public void setTwilioPhoneNumber(String twilioPhoneNumber) {
        this.twilioPhoneNumber = twilioPhoneNumber;
    }

    public Boolean isInUse() {
        return this.inUse;
    }

    public Boolean getInUse() {
        return this.inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }

    public TwilioPhoneNumber() {
    }

    public TwilioPhoneNumber(String twilioPhoneNumberSid, String twilioPhoneNumber, Boolean inUse) {
        this.twilioPhoneNumberSid = twilioPhoneNumberSid;
        this.twilioPhoneNumber = twilioPhoneNumber;
        this.inUse = inUse;
    }

}