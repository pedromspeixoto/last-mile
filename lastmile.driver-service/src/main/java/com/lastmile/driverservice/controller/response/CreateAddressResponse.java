package com.lastmile.driverservice.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.driverservice.dto.addresses.CreateAddressResponseDto;
import com.lastmile.utils.models.response.ApiResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class CreateAddressResponse extends ApiResponse {

    // Json Properties
    private static final String JSON_VALUE_DATA = "data";
    private static final String JSON_VALUE_METADATA = "metadata";

    @JsonProperty(value = JSON_VALUE_DATA)
    private CreateAddressResponseDto data;

    @JsonProperty(value = JSON_VALUE_METADATA)
    private Object metadata;

    public CreateAddressResponse() {
        super();
    }

    public CreateAddressResponse(int code, String message) {
        super(code, message);
    }

    public CreateAddressResponse(int code, String message, CreateAddressResponseDto data) {

        super(code, message);
        this.data = data;
    }

    public CreateAddressResponse(int code, String message, CreateAddressResponseDto data, Object metadata) {

        super(code, message);
        this.data = data;
        this.metadata = metadata;
    }

    public CreateAddressResponseDto getData() {
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