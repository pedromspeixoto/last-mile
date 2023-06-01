package com.lastmile.paymentservice.client.easypay.dto.external;

import com.lastmile.paymentservice.client.easypay.enums.EasypayOutPaymentMethod;
import com.lastmile.paymentservice.client.easypay.enums.EasypayOutResponseStatus;

public class EasypayOutPaymentMethodResponseDto {

    private EasypayOutPaymentMethod type;

    private EasypayOutResponseStatus status;

    public EasypayOutPaymentMethod getType() {
        return this.type;
    }

    public void setType(EasypayOutPaymentMethod type) {
        this.type = type;
    }

    public EasypayOutResponseStatus getStatus() {
        return this.status;
    }

    public void setStatus(EasypayOutResponseStatus status) {
        this.status = status;
    }

}