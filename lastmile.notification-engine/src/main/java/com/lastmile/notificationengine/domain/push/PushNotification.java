package com.lastmile.notificationengine.domain.push;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PushNotification {

    @JsonProperty("to")
    private String to;

    @JsonProperty("device_token")
    private String deviceToken;

    @JsonProperty("notification_title")
    private String notificationTitle;

    @JsonProperty("notification_text")
    private String notificationText;

    @JsonProperty("notification_name")
    private String notificationName;

    @JsonProperty("notification_image_url")
    private String notificationImageUrl;

    @JsonProperty("notification_data")
    private Map<String, String> data;

    public PushNotification() {
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDeviceToken() {
        return this.deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getNotificationTitle() {
        return this.notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationText() {
        return this.notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getNotificationName() {
        return this.notificationName;
    }

    public void setNotificationName(String notificationName) {
        this.notificationName = notificationName;
    }

    public Map<String,String> getData() {
        return this.data;
    }

    public void setData(Map<String,String> data) {
        this.data = data;
    }

    public String getNotificationImageUrl() {
        return this.notificationImageUrl;
    }

    public void setNotificationImageUrl(String notificationImageUrl) {
        this.notificationImageUrl = notificationImageUrl;
    }

}