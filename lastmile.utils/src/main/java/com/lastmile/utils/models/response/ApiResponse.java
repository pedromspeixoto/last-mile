package com.lastmile.utils.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(Include.NON_NULL)
public abstract class ApiResponse {

    // Json Properties
    private static final String JSON_VALUE_ID = "id";
    private static final String JSON_VALUE_TIMESTAMP = "timestamp";
    private static final String JSON_VALUE_CODE = "code";
    private static final String JSON_VALUE_MESSAGE = "message";

    @JsonProperty(value = JSON_VALUE_ID)
    private String id;

    @JsonProperty(value = JSON_VALUE_TIMESTAMP)
    private String timestamp;

    @JsonProperty(value = JSON_VALUE_CODE)
    private int code;

    @JsonProperty(value = JSON_VALUE_MESSAGE)
    private String message;

    protected ApiResponse() {

        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now().toString();
    }

    public ApiResponse(int code, String message) {

        this();
        this.code = code;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ApiResponse [id=" + id + ", timestamp=" + timestamp + ", code=" + code + ", message=" + message + "]";
    }

}