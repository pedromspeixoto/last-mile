package com.lastmile.accountservice.client.payments.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.accountservice.dto.payments.GetPaymentDetailResponseDto;
import com.lastmile.utils.models.response.ApiResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class PaymentResponse extends ApiResponse {

    // Json Properties
    private static final String JSON_VALUE_DATA = "data";
    private static final String JSON_VALUE_METADATA = "metadata";

    @JsonProperty(value = JSON_VALUE_DATA)
    private GetPaymentDetailResponseDto data;

    @JsonProperty(value = JSON_VALUE_METADATA)
    private Object metadata;

    public PaymentResponse() {
        super();
    }

    public PaymentResponse(int code, String message) {
        super(code, message);
    }

    public PaymentResponse(int code, String message, GetPaymentDetailResponseDto data) {

        super(code, message);
        this.data = data;
    }

    public PaymentResponse(int code, String message, GetPaymentDetailResponseDto data, Object metadata) {

        super(code, message);
        this.data = data;
        this.metadata = metadata;
    }

    public GetPaymentDetailResponseDto getData() {
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