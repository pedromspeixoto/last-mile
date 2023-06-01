package com.lastmile.paymentservice.client.easypay.dto;

import com.lastmile.paymentservice.client.easypay.dto.external.EasypayOutPaymentResponseDto;
import com.lastmile.paymentservice.enums.OutPaymentStatus;

public class OutPaymentResponseDto {

    private String externalPaymentIdentification;

    private OutPaymentStatus outPaymentStatus;

    public String getExternalPaymentIdentification() {
        return this.externalPaymentIdentification;
    }

    public void setExternalPaymentIdentification(String externalPaymentIdentification) {
        this.externalPaymentIdentification = externalPaymentIdentification;
    }

    public OutPaymentStatus getOutPaymentStatus() {
        return this.outPaymentStatus;
    }

    public void setOutPaymentStatus(OutPaymentStatus outPaymentStatus) {
        this.outPaymentStatus = outPaymentStatus;
    }

    public OutPaymentResponseDto() {
    }

    public OutPaymentResponseDto(EasypayOutPaymentResponseDto easypayOutPaymentResponseDto) {
        this.externalPaymentIdentification = easypayOutPaymentResponseDto.getId();
        if (null != easypayOutPaymentResponseDto.getMethod()) {
            switch (easypayOutPaymentResponseDto.getMethod().getStatus()) {
                case delayed:
                    this.outPaymentStatus = OutPaymentStatus.SCHEDULED;
                    break;
                case success:
                    this.outPaymentStatus= OutPaymentStatus.PAID;
                    break;
                case pending:
                    this.outPaymentStatus = OutPaymentStatus.CREATED;
                    break;
                case deleted:
                    this.outPaymentStatus = OutPaymentStatus.CANCELLED;
                    break;
                default:
                    this.outPaymentStatus = OutPaymentStatus.FAILED;
            }
        }
    }

}