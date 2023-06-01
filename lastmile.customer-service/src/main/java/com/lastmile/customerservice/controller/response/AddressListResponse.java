package com.lastmile.customerservice.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.customerservice.dto.addresses.GetAddressResponseDto;
import com.lastmile.utils.models.response.ApiResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class AddressListResponse extends ApiResponse {

    // Json Properties
    private static final String JSON_VALUE_DATA = "data";
    private static final String JSON_VALUE_METADATA = "metadata";

    @JsonProperty(value = JSON_VALUE_DATA)
    private List<GetAddressResponseDto> data;

    @JsonProperty(value = JSON_VALUE_METADATA)
    private Object metadata;

    public AddressListResponse() {
        super();
    }

    public AddressListResponse(int code, String message) {
        super(code, message);
    }

    public AddressListResponse(int code, String message, List<GetAddressResponseDto> data) {

        super(code, message);
        this.data = data;
    }

    public AddressListResponse(int code, String message, List<GetAddressResponseDto> data, Object metadata) {

        super(code, message);
        this.data = data;
        this.metadata = metadata;
    }

    public List<GetAddressResponseDto> getData() {
        return data;
    }

    public Object getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "AddressListResponse [data=" + data + ", metadata=" + metadata + "]";
    }

}