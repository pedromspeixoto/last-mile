package com.lastmile.accountservice.domain;

import javax.persistence.*;

import com.lastmile.utils.enums.notifications.PushNotificationsExternalEntities;

@Entity
@Table(name = "account_devices")
public class AccountDevice extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_identification")
    private String userIdentification;

    @Column(name = "external_entity")
    private String externalEntity;

    @Column(name = "external_entity_token")
    private String externalEntityToken;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserIdentification() {
        return this.userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public String getExternalEntity() {
        return this.externalEntity;
    }

    public void setExternalEntity(PushNotificationsExternalEntities externalEntity) {
        this.externalEntity = externalEntity.toString();
    }

    public String getExternalEntityToken() {
        return this.externalEntityToken;
    }

    public void setExternalEntityToken(String externalEntityToken) {
        this.externalEntityToken = externalEntityToken;
    }

    public AccountDevice(String userIdentification, PushNotificationsExternalEntities externalEntity, String externalEntityToken) {
        this.userIdentification = userIdentification;
        this.externalEntity = externalEntity.toString();
        this.externalEntityToken = externalEntityToken;
    }

    public AccountDevice() {
    }

}
