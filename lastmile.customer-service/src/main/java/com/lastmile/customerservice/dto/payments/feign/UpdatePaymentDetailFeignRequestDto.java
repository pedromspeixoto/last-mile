package com.lastmile.customerservice.dto.payments.feign;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.customerservice.dto.payments.UpdatePaymentDetailRequestDto;
import com.lastmile.customerservice.enums.payments.PaymentDetailStatus;

import org.modelmapper.ModelMapper;

@JsonInclude(Include.NON_EMPTY)
public class UpdatePaymentDetailFeignRequestDto {

    private PaymentDetailStatus status;

    public PaymentDetailStatus getStatus() {
        return this.status;
    }

    public void setStatus(PaymentDetailStatus status) {
        this.status = status;
    }

    public UpdatePaymentDetailFeignRequestDto() {
    }

    public static UpdatePaymentDetailFeignRequestDto mapToFeignRequest(UpdatePaymentDetailRequestDto updatePaymentDetailRequestDto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(updatePaymentDetailRequestDto, UpdatePaymentDetailFeignRequestDto.class);
    }


}