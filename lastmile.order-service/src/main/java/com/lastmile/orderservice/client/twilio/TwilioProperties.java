package com.lastmile.orderservice.client.twilio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioProperties {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.proxy.service-name}")
    private String serviceName;

    @Value("${twilio.proxy.service-sid}")
    private String serviceSid;

    @Value("${twilio.proxy.session-timeout}")
    private Integer sessionTimeout;

    @Value("${twilio.twiml.app-name}")
    private String twimlAppName;

    public String getAccountSid() {
        return this.accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceSid() {
        return this.serviceSid;
    }

    public void setServiceSid(String serviceSid) {
        this.serviceSid = serviceSid;
    }

    public Integer getSessionTimeout() {
        return this.sessionTimeout;
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getTwimlAppName() {
        return this.twimlAppName;
    }

    public void setTwimlAppName(String twimlAppName) {
        this.twimlAppName = twimlAppName;
    }

}