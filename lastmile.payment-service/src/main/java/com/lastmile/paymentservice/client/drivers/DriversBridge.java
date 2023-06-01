package com.lastmile.paymentservice.client.drivers;

import com.lastmile.paymentservice.client.drivers.feign.FiscalEntityResponse;
import com.lastmile.paymentservice.client.drivers.feign.DriverServiceFeignClient;
import com.lastmile.paymentservice.dto.drivers.FiscalEntityResponseDto;
import com.lastmile.paymentservice.service.exception.FeignCommunicationException;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.context.ServiceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class DriversBridge {

    @Autowired
    DriverServiceFeignClient driverServiceFeignClient;

    public FiscalEntityResponseDto getDriverFiscalEntity(ServiceContext serviceContext, String driverIdentification, String outPaymentIdentification) throws FeignCommunicationException {

        try {
            ResponseEntity<FiscalEntityResponse> driverResponse = driverServiceFeignClient.getDriverFiscalEntity(serviceContext.getCorrelationId(),
                                                                                                                 serviceContext.getUserId(),
                                                                                                                 serviceContext.getPermissions(),
                                                                                                                 serviceContext.getRequestId(),
                                                                                                                 Constants.REQUEST_ORIGIN_INTERNAL,
                                                                                                                 Constants.REQUEST_ORIGIN_ENTITY_OUTBOUND_PAYMENT,
                                                                                                                 outPaymentIdentification,
                                                                                                                 driverIdentification);
            return driverResponse.getBody().getData();
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

}