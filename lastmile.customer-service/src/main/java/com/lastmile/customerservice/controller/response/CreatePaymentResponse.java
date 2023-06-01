package com.lastmile.customerservice.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.customerservice.dto.payments.CreatePaymentDetailResponseDto;
import com.lastmile.utils.models.response.ApiResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class CreatePaymentResponse extends ApiResponse {

    // Json Properties
    private static final String JSON_VALUE_DATA = "data";
    private static final String JSON_VALUE_METADATA = "metadata";

    @JsonProperty(value = JSON_VALUE_DATA)
    private CreatePaymentDetailResponseDto data;

    @JsonProperty(value = JSON_VALUE_METADATA)
    private Object metadata;

    public CreatePaymentResponse() {
        super();
    }

    public CreatePaymentResponse(int code, String message) {
        super(code, message);
    }

    public CreatePaymentResponse(int code, String message, CreatePaymentDetailResponseDto data) {

        super(code, message);
        this.data = data;
    }

    public CreatePaymentResponse(int code, String message, CreatePaymentDetailResponseDto data, Object metadata) {

        super(code, message);
        this.data = data;
        this.metadata = metadata;
    }

    public CreatePaymentDetailResponseDto getData() {
        return data;
    }

    public Object getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "CreateAddressResponse [data=" + data + ", metadata=" + metadata + "]";
    }

}