package com.lastmile.driverservice.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.driverservice.dto.orders.OrderPhotoResponseDto;
import com.lastmile.utils.models.response.ApiResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class OrderPhotoResponse extends ApiResponse {

    // Json Properties
    private static final String JSON_VALUE_DATA = "data";
    private static final String JSON_VALUE_METADATA = "metadata";

    @JsonProperty(value = JSON_VALUE_DATA)
    private OrderPhotoResponseDto data;

    @JsonProperty(value = JSON_VALUE_METADATA)
    private Object metadata;

    public OrderPhotoResponse() {
        super();
    }

    public OrderPhotoResponse(int code, String message) {
        super(code, message);
    }

    public OrderPhotoResponse(int code, String message, OrderPhotoResponseDto data) {

        super(code, message);
        this.data = data;
    }

    public OrderPhotoResponse(int code, String message, OrderPhotoResponseDto data, Object metadata) {

        super(code, message);
        this.data = data;
        this.metadata = metadata;
    }

    public OrderPhotoResponseDto getData() {
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
