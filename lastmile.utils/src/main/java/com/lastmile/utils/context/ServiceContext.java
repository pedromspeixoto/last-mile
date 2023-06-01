package com.lastmile.utils.context;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.lastmile.utils.constants.Constants;

@JsonInclude(Include.NON_NULL)
public class ServiceContext {

    @JsonProperty(Constants.JSON_VALUE_REQUEST_ID)
    private String requestId;

    @JsonProperty(Constants.JSON_VALUE_CORRELATION_ID)
    private String correlationId;

    @JsonProperty(Constants.JSON_VALUE_USER_ID)
    private String userId;

    @JsonProperty(Constants.JSON_VALUE_PERMISSIONS)
    private String permissions;

    @JsonProperty(Constants.JSON_VALUE_TIMESTAMP)
    private LocalDateTime timestamp;

    @JsonProperty(Constants.JSON_VALUE_REQUEST_ORIGIN)
    private String requestOrigin;

    @JsonProperty(Constants.JSON_VALUE_REQUEST_ENTITY)
    private String requestEntity;

    @JsonProperty(Constants.JSON_VALUE_REQUEST_ENTITY_ID)
    private String requestEntityId;

    @JsonProperty(Constants.JSON_VALUE_API_KEY)
    private String apiKey;

    public ServiceContext(String requestId, String correlationId, String userId, String permissions, String timestamp, 
                          String requestOrigin, String requestEntity, String requestEntityId, String apiKey) {

        this.requestId = requestId;
        this.correlationId = correlationId;
        this.userId = userId;
        this.permissions = permissions;

        if (null != requestOrigin) {
            this.requestOrigin = requestOrigin;
        }

        if (null != requestEntity) {
            this.requestEntity = requestEntity;
        }

        if (null != requestEntityId) {
            this.requestEntityId = requestEntityId;
        }

        if (null != apiKey) {
            this.apiKey = apiKey;
        }

        if (null != timestamp) {
            this.timestamp = LocalDateTime.parse(timestamp);
        }

    }

    public ServiceContext(HttpServletRequest httpRequest) {

        if (null == httpRequest) {
            return;
        }

        this.requestId = httpRequest.getHeader(Constants.JSON_VALUE_REQUEST_ID);
        this.correlationId = httpRequest.getHeader(Constants.JSON_VALUE_CORRELATION_ID);
        this.userId = httpRequest.getHeader(Constants.JSON_VALUE_USER_ID);
        this.permissions = httpRequest.getHeader(Constants.JSON_VALUE_PERMISSIONS);

        if (null != httpRequest.getHeader(Constants.JSON_VALUE_REQUEST_ORIGIN)) {
            this.requestOrigin = httpRequest.getHeader(Constants.JSON_VALUE_REQUEST_ORIGIN);
        }

        if (null != httpRequest.getHeader(Constants.JSON_VALUE_REQUEST_ENTITY)) {
            this.requestEntity = httpRequest.getHeader(Constants.JSON_VALUE_REQUEST_ENTITY);
        }

        if (null != httpRequest.getHeader(Constants.JSON_VALUE_REQUEST_ORIGIN)) {
            this.requestEntityId = httpRequest.getHeader(Constants.JSON_VALUE_REQUEST_ENTITY_ID);
        }

        if (null != httpRequest.getHeader(Constants.JSON_VALUE_API_KEY)) {
            this.apiKey = httpRequest.getHeader(Constants.JSON_VALUE_API_KEY);
        }

        if (null != httpRequest.getHeader(Constants.JSON_VALUE_TIMESTAMP)) {
            this.timestamp = LocalDateTime.parse(httpRequest.getHeader(Constants.JSON_VALUE_TIMESTAMP));
        }

    }

    public String getRequestId() {
        return requestId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPermissions() {
        return permissions;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
	public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestOrigin() {
        return this.requestOrigin;
    }

    public void setRequestOrigin(String requestOrigin) {
        this.requestOrigin = requestOrigin;
    }

    public String getRequestEntity() {
        return this.requestEntity;
    }

    public void setRequestEntity(String requestEntity) {
        this.requestEntity = requestEntity;
    }

    public String getRequestEntityId() {
        return this.requestEntityId;
    }

    public void setRequestEntityId(String requestEntityId) {
        this.requestEntityId = requestEntityId;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this).build();
    }
}