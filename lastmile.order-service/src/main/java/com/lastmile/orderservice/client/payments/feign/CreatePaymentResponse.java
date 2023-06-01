package com.lastmile.orderservice.client.payments.feign;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.orderservice.dto.payments.CreatePaymentResponseModel;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class CreatePaymentResponse {

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
    private CreatePaymentResponseModel data;

    @JsonProperty(value = JSON_VALUE_METADATA)
    private Object metadata;

    // no args constructor for use in serialization
    public CreatePaymentResponse() {
    }

    public CreatePaymentResponse(int code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public CreatePaymentResponse(int code, String message, CreatePaymentResponseModel data) {

        this(code, message);
        this.data = data;
        
    }

    public CreatePaymentResponse(int code, String message, CreatePaymentResponseModel data, Object metadata) {

        this(code, message);
        this.metadata = metadata;
        this.data = data;

    }

    public CreatePaymentResponseModel getData() {
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