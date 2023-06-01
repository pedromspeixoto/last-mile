package com.lastmile.paymentservice.client.easypay.dto.external;

import java.util.List;

public class EasypayCaptureResponseDto {

    private String status;

    private List<String> message;

    private String id;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getMessage() {
        return this.message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

}