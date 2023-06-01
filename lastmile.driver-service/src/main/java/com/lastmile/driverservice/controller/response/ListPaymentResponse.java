package com.lastmile.driverservice.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.driverservice.dto.payments.GetOutPaymentResponseDto;
import com.lastmile.utils.models.response.ApiResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class ListPaymentResponse extends ApiResponse {

    // Json Properties
    private static final String JSON_VALUE_DATA = "data";
    private static final String JSON_VALUE_METADATA = "metadata";

    @JsonProperty(value = JSON_VALUE_DATA)
    private List<GetOutPaymentResponseDto> data;

    @JsonProperty(value = JSON_VALUE_METADATA)
    private Object metadata;

    public ListPaymentResponse() {
        super();
    }

    public ListPaymentResponse(int code, String message) {
        super(code, message);
    }

    public ListPaymentResponse(int code, String message, List<GetOutPaymentResponseDto> data) {

        super(code, message);
        this.data = data;
    }

    public ListPaymentResponse(int code, String message, List<GetOutPaymentResponseDto> data, Object metadata) {

        super(code, message);
        this.data = data;
        this.metadata = metadata;
    }

    public List<GetOutPaymentResponseDto> getData() {
        return data;
    }

    public Object getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "OrderResponse [data=" + data + ", metadata=" + metadata + "]";
    }

}