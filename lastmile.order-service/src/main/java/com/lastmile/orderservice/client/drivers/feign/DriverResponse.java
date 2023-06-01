package com.lastmile.orderservice.client.drivers.feign;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.orderservice.dto.drivers.DriverResponseModel;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class DriverResponse {

    // Json Properties
    private static final String JSON_VALUE_ID = "id";
    private static final String JSON_VALUE_TIMESTAMP = "timestamp";
    private static final String JSON_VALUE_CODE = "code";
    private static final String JSON_VALUE_MESSAGE = "message";
    private static final String JSON_VALUE_DATA = "data";
    private static final String JSON_VALUE_METADATA = "metadata";

    @JsonProperty(value = JSON_VALUE_ID)
    private String id;

    @JsonProperty(value = JSON_VALUE_TIMESTAMP)
    private String timestamp;

    @JsonProperty(value = JSON_VALUE_CODE)
    private int code;

    @JsonProperty(value = JSON_VALUE_MESSAGE)
    private String message;

    @JsonProperty(value = JSON_VALUE_DATA)
    private List<DriverResponseModel> data;

    @JsonProperty(value = JSON_VALUE_METADATA)
    private Object metadata;

    // no args constructor for use in serialization
    public DriverResponse() {
    }

    public DriverResponse(int code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public DriverResponse(int code, String message, List<DriverResponseModel> data) {

        this(code, message);
        if (!data.isEmpty()) {
            this.data = data;
        }
        
    }

    public DriverResponse(int code, String message, List<DriverResponseModel> data, Object metadata) {

        this(code, message);
        this.metadata = metadata;
        if (!data.isEmpty()) {
            this.data = data;
        }

    }

    public List<DriverResponseModel> getData() {
        return data;
    }

    public Object getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "DriverResponse [data=" + data + ", metadata=" + metadata + "]";
    }

}