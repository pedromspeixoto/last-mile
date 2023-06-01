package com.lastmile.utils.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class SuccessResponse extends ApiResponse {

    // Json Properties
    private static final String JSON_VALUE_DATA = "data";
    private static final String JSON_VALUE_METADATA = "metadata";

    @JsonProperty(value = JSON_VALUE_DATA)
    private Object data;

    @JsonProperty(value = JSON_VALUE_METADATA)
    private Object metadata;

    public SuccessResponse() {
        super();
    }

    public SuccessResponse(int code, String message) {
        super(code, message);
    }

    public SuccessResponse(int code, String message, Object data) {

        super(code, message);
        this.data = data;
    }

    public SuccessResponse(int code, String message, Object data, Object metadata) {

        super(code, message);
        this.data = data;
        this.metadata = metadata;
    }

    public Object getData() {
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
