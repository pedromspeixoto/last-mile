package com.lastmile.orderengine.client.drivers.feign;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.orderengine.dto.controller.ApiResponse;
import com.lastmile.orderengine.dto.DriverResponseModel;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class DriverResponse extends ApiResponse {

    // Json Properties
    private static final String JSON_VALUE_DATA = "data";
    private static final String JSON_VALUE_METADATA = "metadata";

    @JsonProperty(value = JSON_VALUE_DATA)
    private List<DriverResponseModel> data;

    @JsonProperty(value = JSON_VALUE_METADATA)
    private Object metadata;

    public DriverResponse() {
        super();
    }

    public DriverResponse(int code, String message) {
        super(code, message);
    }

    public DriverResponse(int code, String message, List<DriverResponseModel> data) {

        super(code, message);
        this.data = data;
    }

    public DriverResponse(int code, String message, List<DriverResponseModel> data, Object metadata) {

        super(code, message);
        this.data = data;
        this.metadata = metadata;
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