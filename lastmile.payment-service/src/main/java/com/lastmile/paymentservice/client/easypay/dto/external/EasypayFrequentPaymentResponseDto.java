package com.lastmile.paymentservice.client.easypay.dto.external;

import java.util.List;

public class EasypayFrequentPaymentResponseDto {

    private String status;

    private String id;

    private List<String> message;

    private EasypayMethodDto method;

    private EasypayCustomerDto customer;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EasypayMethodDto getMethod() {
        return this.method;
    }

    public void setMethod(EasypayMethodDto method) {
        this.method = method;
    }

    public EasypayCustomerDto getCustomer() {
        return this.customer;
    }

    public void setCustomer(EasypayCustomerDto customer) {
        this.customer = customer;
    }

    public List<String> getMessage() {
        return this.message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

}