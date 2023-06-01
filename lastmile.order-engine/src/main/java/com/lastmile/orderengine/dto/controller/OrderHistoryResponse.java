package com.lastmile.orderengine.dto.controller;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.orderengine.dto.orders.OrderHistoryResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class OrderHistoryResponse extends ApiResponse {

    // Json Properties
    private static final String JSON_VALUE_DATA = "data";
    private static final String JSON_VALUE_METADATA = "metadata";

    @JsonProperty(value = JSON_VALUE_DATA)
    private List<OrderHistoryResponseDto> data;

    @JsonProperty(value = JSON_VALUE_METADATA)
    private Object metadata;

    public OrderHistoryResponse() {
        super();
    }

    public OrderHistoryResponse(int code, String message) {
        super(code, message);
    }

    public OrderHistoryResponse(int code, String message, List<OrderHistoryResponseDto> data) {

        super(code, message);
        this.data = data;
    }

    public OrderHistoryResponse(int code, String message, List<OrderHistoryResponseDto> data, Object metadata) {

        super(code, message);
        this.data = data;
        this.metadata = metadata;
    }

    public List<OrderHistoryResponseDto> getData() {
        return data;
    }

    public Object getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "SuccessResponse [data=" + data + ", metadata=" + metadata + "]";
    }

}
